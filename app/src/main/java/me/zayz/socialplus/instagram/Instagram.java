package me.zayz.socialplus.instagram;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Response;

import me.zayz.socialplus.R;
import me.zayz.socialplus.config.Config;
import me.zayz.socialplus.models.InstagramEngagement;
import me.zayz.socialplus.models.InstagramProfile;
import me.zayz.socialplus.models.InstagramStats;
import me.zayz.socialplus.models.InstagramUser;
import me.zayz.socialplus.models.SocialPlusUser;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Handles all Instagram-related actions.
 */
public class Instagram {

    public InstagramStats stats;
    public InstagramEngagement engagement;
    public SocialPlusUser user;

    private InstagramDialog mDialog;
    private InstagramAuthListener mListener;
    private InstagramSession mSession;
    private InstagramApi mApi;
    private InstagramUser mCurrentUser;

    /**
     * Instantiates new object.
     *
     * @param context Context
     */
    public Instagram(Context context, InstagramAuthListener listener) {

        mSession = new InstagramSession(context);
        mListener = listener;
        mApi = new InstagramApi(context);

        stats = new InstagramStats();
        engagement = new InstagramEngagement();

        mDialog = new InstagramDialog(context, new InstagramDialog.InstagramDialogListener() {

            @Override
            public void onSuccess(String accessToken) {

                mApi.getSelf(accessToken, new InstagramApi.SocialPlusUserCallback() {

                    @Override
                    public void onFinish(SocialPlusUser socialPlusUser,
                                         InstagramUser instagramUser) {

                        mSession.storeUser(socialPlusUser, instagramUser);
                        mSession.storeLocal(socialPlusUser);

                        user = socialPlusUser;
                        mCurrentUser = instagramUser;
                        mListener.onSuccess(user, mCurrentUser);
                    }

                    @Override
                    public void onError() {

                        user = new SocialPlusUser();
                        mCurrentUser = new InstagramUser();
                        mListener.onSuccess(user, mCurrentUser);
                    }
                });
            }

            @Override
            public void onError(String error) {

                mListener.onError(error);
            }

            @Override
            public void onCancel() {

                mListener.onCancel();
            }
        });

        if (mSession.isActive()) {
            mSession.getUser(new InstagramSession.Callback() {
                @Override
                public void onFinish(InstagramProfile profile, InstagramUser prevUser,
                                     InstagramStats userStats, InstagramEngagement engagement) {
                    mApi.getSelf(profile.accessToken, new InstagramApi.SocialPlusUserCallback() {

                        @Override
                        public void onFinish(SocialPlusUser socialPlusUser,
                                             InstagramUser instagramUser) {

                            user = socialPlusUser;
                            mCurrentUser = instagramUser;
                            mListener.onSuccess(user, mCurrentUser);
                        }

                        @Override
                        public void onError() {

                            user = new SocialPlusUser();
                            mCurrentUser = new InstagramUser();
                            mListener.onSuccess(user, mCurrentUser);
                        }
                    });
                }

                @Override
                public void onNotExists() {

                }
            });
        } else {
            mListener.onNeedLogin();
        }
    }

    /**
     * Get image using API.
     *
     * @param url      URL to getUser image from
     * @param listener Response listener when image finishes downloading
     * @param error    Error response listener
     */
    public void getImage(String url, Response.Listener<Bitmap> listener,
                         Response.ErrorListener error) {
        mApi.getImage(url, listener, error);
    }

    /**
     * Authorizes user.
     */
    public void authorize() {

        mDialog.show();
    }

    /**
     * Gets stats after performing stats calculations.
     */
    public void getStats(final InstagramStats.Callback callback) {

        mSession.getUser(new InstagramSession.Callback() {

            @Override
            public void onFinish(InstagramProfile profile, InstagramUser prevUser, InstagramStats userStats,
                                 InstagramEngagement userEngagement) {

                userStats.calculateStats(mCurrentUser, prevUser);
                userEngagement.calculateEngagement(mCurrentUser);

                stats = userStats;
                engagement = userEngagement;

                mSession.updateStats(profile, userStats, user, mCurrentUser);

                /*
                CountDownLatch latch = new CountDownLatch(stats.all.size());

                InstagramApi.BlockedAsyncTask blockCheck =
                        new InstagramApi.BlockedAsyncTask(mApi, stats, profile, stats.all, latch);
                Log.e("TEST", "Before background task");
                blockCheck.execute();
                try {
                    latch.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Log.e("TEST", "Fail");
                }
                */


                callback.onFinish();
            }

            @Override
            public void onNotExists() {
            }
        });
    }

    /**
     * Gets stats after performing stats calculations.
     */
    public void resetSession() {

        mSession.resetLocal();
    }

    /**
     * Callback interface to check if authorization was successful.
     */
    public interface InstagramAuthListener {

        void onSuccess(SocialPlusUser user, InstagramUser instagramUser);

        void onNeedLogin();

        void onError(String error);

        void onCancel();
    }
}
