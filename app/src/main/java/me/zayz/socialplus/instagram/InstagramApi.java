package me.zayz.socialplus.instagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import me.zayz.socialplus.R;
import me.zayz.socialplus.models.InstagramMedia;
import me.zayz.socialplus.models.InstagramProfile;
import me.zayz.socialplus.models.InstagramPublicUser;
import me.zayz.socialplus.models.InstagramUser;
import me.zayz.socialplus.utils.RequestUtil;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Makes API_URL calls to Instagram
 */
class InstagramApi {

    private static final String API_URL = "https://api.instagram.com/v1";

    private static final String PARAM_ACCESS_TOKEN = "?access_token=";

    private static final String SELF = "/users/self";
    //private static final String USER = "/users/";

    private static final String RECENT_MEDIA = "/media/recent";
    private static final String FOLLOWS = "/follows";
    private static final String FOLLOWED_BY = "/followed-by";
    //private static final String RELATIONSHIP = "/relationship";

    private Context mContext;
    private RequestQueue mQueue;

    /**
     * Instantiates new object.
     *
     * @param context Context
     */
    InstagramApi(Context context) {

        mContext = context;
        mQueue = Volley.newRequestQueue(context);
    }

    /**
     * Gets image from provided URL.
     *
     * @param url      URL to receive image
     * @param listener Response listener
     * @param error    Error listener
     */
    void getImage(String url, Response.Listener<Bitmap> listener, Response.ErrorListener error) {

        ImageRequest request = new ImageRequest(url, listener, 80, 80,
                ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, error);

        mQueue.add(request);
    }

    /**
     * Gets own user data.
     *
     * @param callback Callback for when request is finished
     */
    void getSelf(final String accessToken, final Callback callback) {

        final InstagramUser instagramUser = new InstagramUser();

        String url = API_URL + SELF + PARAM_ACCESS_TOKEN + accessToken;

        RequestUtil.get(mQueue, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        final InstagramProfile profile = new InstagramProfile();

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject data = (JSONObject) obj.get("data");

                            profile.accessToken = accessToken;

                            profile.id = data.getString(
                                    InstagramProfile.ID);
                            profile.username = data.getString(
                                    InstagramProfile.USERNAME);
                            profile.profilePicture = data.getString(
                                    InstagramProfile.PROFILE_PICTURE);
                            profile.fullName = data.getString(
                                    InstagramProfile.FULL_NAME);
                            profile.bio = data.getString(
                                    InstagramProfile.BIO);
                            profile.website = data.getString(
                                    InstagramProfile.WEBSITE);
                            profile.isBusiness = data.getBoolean(
                                    InstagramProfile.IS_BUSINESS);

                        } catch (JSONException je) {
                            Log.e(mContext.getString(R.string.app_name), je.getMessage());
                            callback.onError();
                            return;
                        }

                        final CountDownLatch latch = new CountDownLatch(3);

                        // Recent posts
                        getMedia(instagramUser, accessToken, latch);

                        // Follows
                        getFollows(instagramUser, accessToken, latch);

                        // Followed by
                        getFollowedBy(instagramUser, accessToken, latch);

                        final Handler mainThread = new Handler(mContext.getMainLooper());

                        new Thread(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    latch.await(10, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                    Log.e(mContext.getString(R.string.app_name), e.getMessage());
                                    callback.onError();
                                    return;
                                }
                                mainThread.post(new Runnable() {

                                    @Override
                                    public void run() {

                                        callback.onFinish(profile, instagramUser);
                                    }
                                });
                            }
                        }).start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error == null || error.networkResponse == null) {
                            return;
                        }

                        String body;
                        final String statusCode = String.valueOf(error.networkResponse.statusCode);

                        try {
                            body = new String(error.networkResponse.data, "UTF-8");

                            Log.e("Social Plus", statusCode + ": " + body);
                        } catch (UnsupportedEncodingException e) {
                            Log.e("InstagramApi", "UnsupportedEncodingException");
                        }

                        callback.onError();
                    }
                }
        );
    }

    /**
     * Gets own user media posts.
     *
     * @param latch Latch for asynchronous countdown
     */
    private void getMedia(final InstagramUser instagramUser, String accessToken,
                          final CountDownLatch latch) {

        String url = API_URL + SELF + RECENT_MEDIA + PARAM_ACCESS_TOKEN + accessToken;

        RequestUtil.get(mQueue, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray data = obj.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject media = (JSONObject) data.get(i);
                                String id = media.getString("id");

                                String type = media.getString("type");
                                boolean isVideo = type.equals("video");

                                JSONObject images = media.getJSONObject("images");
                                JSONObject thumbnail = images.getJSONObject("thumbnail");
                                String image = thumbnail.getString("url");

                                String createdTime = media.getString("created_time");

                                String link = media.getString("link");

                                JSONObject likes = media.getJSONObject("likes");
                                long likesCount = likes.getLong("count");

                                JSONObject comments = media.getJSONObject("comments");
                                long commentsCount = comments.getLong("count");

                                JSONArray tagsArray = media.getJSONArray("tags");
                                List<String> tags = new ArrayList<>();
                                for (int j = 0; j < tagsArray.length(); j++) {
                                    String tag = tagsArray.getString(j);
                                    tags.add(tag);
                                }

                                JSONArray usersInPhoto = media.getJSONArray(
                                        "users_in_photo");
                                List<InstagramPublicUser> users = new ArrayList<>();
                                for (int j = 0; j < usersInPhoto.length(); j++) {
                                    JSONObject userInPhoto = usersInPhoto.getJSONObject(j);
                                    JSONObject userInfo = userInPhoto.getJSONObject("user");

                                    String userId = userInfo.getString("id");
                                    String fullName = userInfo.getString("full_name");
                                    String profilePicture = userInfo.getString(
                                            "profile_picture");
                                    String username = userInfo.getString("username");

                                    InstagramPublicUser user = new InstagramPublicUser(userId,
                                            fullName, profilePicture, username);

                                    users.add(user);
                                }

                                instagramUser.media.add(new InstagramMedia(
                                        id, isVideo, image, likesCount, commentsCount,
                                        createdTime, tags, users, link
                                ));
                            }
                        } catch (JSONException je) {
                            Log.e("Media", je.getMessage());
                        }

                        latch.countDown();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Media", error.getMessage());
                    }
                }
        );
    }

    /**
     * Gets own user follows.
     *
     * @param latch Latch for asynchronous countdown
     */
    private void getFollows(final InstagramUser instagramUser, String accessToken,
                            final CountDownLatch latch) {

        String url = API_URL + SELF + FOLLOWS + PARAM_ACCESS_TOKEN + accessToken;

        RequestUtil.get(mQueue, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray data = (JSONArray) obj.get("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject follows = (JSONObject) data.get(i);

                                InstagramPublicUser publicUser = new InstagramPublicUser();
                                publicUser.id = follows.getString(
                                        "id");
                                publicUser.fullName = follows.getString(
                                        "full_name");
                                publicUser.profilePicture = follows.getString(
                                        "profile_picture");
                                publicUser.username = "@" + follows.getString(
                                        "username");

                                instagramUser.follows.add(publicUser);
                            }
                        } catch (JSONException je) {
                            Log.e("Follows", je.getMessage());
                        }

                        latch.countDown();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Follows", error.getMessage());
                    }
                }
        );
    }

    /**
     * Gets own user followed by's.
     *
     * @param latch Latch for asynchronous countdown
     */
    private void getFollowedBy(final InstagramUser instagramUser, String accessToken,
                               final CountDownLatch latch) {

        String url = API_URL + SELF + FOLLOWED_BY + PARAM_ACCESS_TOKEN + accessToken;

        RequestUtil.get(mQueue, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray data = (JSONArray) obj.get("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject followedBy = (JSONObject) data.get(i);

                                InstagramPublicUser publicUser = new InstagramPublicUser();
                                publicUser.id = followedBy.getString(
                                        "id");
                                publicUser.fullName = followedBy.getString(
                                        "full_name");
                                publicUser.profilePicture = followedBy.getString(
                                        "profile_picture");
                                publicUser.username = "@" + followedBy.getString(
                                        "username");

                                instagramUser.followedBy.add(publicUser);
                            }
                        } catch (JSONException je) {
                            Log.e("Followed By", je.getMessage());
                        }

                        latch.countDown();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Followed By", error.getMessage());
                    }
                }
        );
    }

    /**
     * Callback interface to return user after request.
     */
    public interface Callback {

        void onFinish(InstagramProfile profile, InstagramUser currentInstagramUser);

        void onError();
    }

    /*
    public interface CheckUserCallback {

        void onFinish(InstagramPublicUser user);

        void onError(InstagramPublicUser user);
    }

    public static class BlockedAsyncTask extends AsyncTask<Void, Void, Void> {

        private InstagramApi api;
        private List<InstagramPublicUser> users;
        private InstagramProfile profile;
        private InstagramStats stats;
        private CountDownLatch latch;

        BlockedAsyncTask(InstagramApi api, InstagramStats stats, InstagramProfile profile,
                         List<InstagramPublicUser> users, CountDownLatch latch) {
            this.api = api;
            this.stats = stats;
            this.profile = profile;
            this.users = users;
            this.latch = latch;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (final InstagramPublicUser publicUser : users) {
                api.checkUserBlocked(users.get(0), profile.accessToken,
                        new InstagramApi.CheckUserCallback() {

                            @Override
                            public void onFinish(InstagramPublicUser publicUser) {

                                stats.blocked.add(publicUser);
                                Log.e("TEST", publicUser.fullName);
                                latch.countDown();
                            }

                            @Override
                            public void onError(InstagramPublicUser publicUser) {
                                Log.e("TEST", "Nope");
                                latch.countDown();
                            }
                        });
            }

            return null;
        }
    }
    */
}
