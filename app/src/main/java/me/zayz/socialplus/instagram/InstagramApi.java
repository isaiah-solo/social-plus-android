package me.zayz.socialplus.instagram;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import me.zayz.socialplus.R;
import me.zayz.socialplus.SocialPlusCallback;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Makes API calls to Instagram
 */
class InstagramApi {
    private Context mContext;
    private InstagramUser mUser;
    private RequestQueue mQueue;

    private static final String INIT_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API = "https://api.instagram.com/v1";

    private static final String PARAM_ACCESS_TOKEN = "?access_token=";

    private static final String SELF = "/users/self";

    private static final String RECENT_MEDIA = "/media/recent";
    private static final String FOLLOWS = "/follows";
    private static final String FOLLOWED_BY = "/followed-by";

    /*
    private static final String LIKED = "/liked";
    private static final String SEARCH = "/search";
    private static final String REQUESTEDBY = "/requested-by";
    private static final String RELATIONSHIP = "/relationship";
    */

    /**
     * Instantiate new object.
     */
    InstagramApi(Context context) {
        mContext = context;
        mUser = new InstagramUser();
        mQueue = Volley.newRequestQueue(context);
    }

    /**
     * Instantiate new object.
     */
    InstagramApi(Context context, InstagramUser user) {
        mContext = context;
        mUser = user;
        mQueue = Volley.newRequestQueue(context);
    }

    InstagramUser getUser() {
        return mUser;
    }

    void getAccessToken(String code, final SocialPlusCallback callback) {
        String clientId = mContext.getString(R.string.auth_client_id);
        String clientSecret = mContext.getString(R.string.auth_client_secret);
        String redirectUri = mContext.getString(R.string.auth_redirect_uri);

        Map<String, String> formData = new HashMap<>();
        formData.put("client_id", clientId);
        formData.put("client_secret", clientSecret);
        formData.put("grant_type", "authorization_code");
        formData.put("redirect_uri", redirectUri);
        formData.put("code", code);

        post(INIT_URL, formData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            String accessToken = (String) obj.get("access_token");
                            JSONObject user = (JSONObject) obj.get("user");

                            mUser.id = (String) user.get("id");
                            mUser.username = (String) user.get("username");
                            mUser.profilePicture = (String) user.get("profile_picture");
                            mUser.fullName = (String) user.get("full_name");
                            mUser.bio = (String) user.get("bio");
                            mUser.website = (String) user.get("website");
                            mUser.isBusiness = (Boolean) user.get("is_business");
                            mUser.accessToken = accessToken;

                            callback.onFinish();

                        } catch (JSONException je) {
                            Log.e("Instagram Access Token", je.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("AGH", "FAILED FOR SOME REASON");
                    }
                }
        );
    }

    void getSelf(final SocialPlusCallback callback) {
        get(SELF + PARAM_ACCESS_TOKEN + mUser.accessToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject data = (JSONObject) obj.get("data");
                            JSONObject counts = (JSONObject) data.get("counts");

                            mUser.numberOfMedia = counts.getInt("media");
                            mUser.numberOfFollows = counts.getInt("follows");
                            mUser.numberOfFollowedBy = counts.getInt("followed_by");

                        } catch (JSONException je) {
                            Log.e("Instagram Access Token", je.getMessage());
                        }

                        final CountDownLatch latch = new CountDownLatch(3);

                        // Recent posts
                        getMedia(latch);

                        // Follows
                        getFollows(latch);

                        // Followed by
                        getFollowedBy(latch);

                        final Handler mainThread = new Handler(mContext.getMainLooper());

                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    latch.await(10, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mainThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onFinish();
                                    }
                                });
                            }
                        }).start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("AGH", "FAILED FOR SOME REASON");
                    }
                }
        );
    }

    private void getMedia(final CountDownLatch latch) {
        get(SELF + RECENT_MEDIA + PARAM_ACCESS_TOKEN + mUser.accessToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray data = (JSONArray) obj.get("data");

                            for (int i = 0; i < mUser.numberOfMedia; i++) {
                                JSONObject media = (JSONObject) data.get(i);
                                String id = media.getString("id");

                                mUser.media.add(id);
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

    private void getFollows(final CountDownLatch latch) {
        get(SELF + FOLLOWS + PARAM_ACCESS_TOKEN + mUser.accessToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray data = (JSONArray) obj.get("data");

                            for (int i = 0; i < mUser.numberOfFollows; i++) {
                                JSONObject follows = (JSONObject) data.get(i);
                                String id = follows.getString("id");

                                mUser.follows.add(id);
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

    private void getFollowedBy(final CountDownLatch latch) {
        get(SELF + FOLLOWED_BY + PARAM_ACCESS_TOKEN + mUser.accessToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray data = (JSONArray) obj.get("data");

                            for (int i = 0; i < mUser.numberOfFollowedBy; i++) {
                                JSONObject followedBy = (JSONObject) data.get(i);
                                String id = followedBy.getString("id");

                                mUser.followedBy.add(id);
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

    private void get(String url, Response.Listener<String> response,
                     Response.ErrorListener error) {
        request(Request.Method.GET, API + url, response, error);
    }

    /*
    void get(String url, Map<String, String> formData,
             Response.Listener<String> response, Response.ErrorListener error) {
        request(Request.Method.GET, API + url, formData, response, error);
    }

    void post(String url, Response.Listener<String> response,
              Response.ErrorListener error) {
        request(Request.Method.POST, url, response, error);
    }
    */

    private void post(String url, Map<String, String> formData,
                      Response.Listener<String> response, Response.ErrorListener error) {
        request(Request.Method.POST, url, formData, response, error);
    }

    private void request(int method, String url, Response.Listener<String> response,
                         Response.ErrorListener error) {
        StringRequest stringRequest = new StringRequest(method, url, response, error);

        mQueue.add(stringRequest);
    }

    private void request(int method, String url, final Map<String, String> formData,
                         Response.Listener<String> response, Response.ErrorListener error) {
        StringRequest stringRequest = new StringRequest(method, url, response, error) {
            @Override
            protected Map<String, String> getParams() {
                return formData;
            }
        };

        mQueue.add(stringRequest);
    }

    /*
    //Request listener
    public interface InstagramApiListener {
        public abstract void onSuccess(String response);

        public abstract void onError(String error);
    }
    */
}
