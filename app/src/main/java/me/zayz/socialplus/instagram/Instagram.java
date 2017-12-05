package me.zayz.socialplus.instagram;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Response;

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
        mCurrentUser = new InstagramUser();

        user = new SocialPlusUser();

        mDialog = new InstagramDialog(context, new InstagramDialog.InstagramDialogListener() {

            @Override
            public void onSuccess(String accessToken) {

                getSelf(accessToken);
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
                public void onFinish(SocialPlusUser socialPlusUser) {

                    user = socialPlusUser;
                    getSelf(user.profile.accessToken);
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
            public void onFinish(SocialPlusUser socialPlusUser) {

                user.stats.calculateStats(mCurrentUser, user.previousUser);
                user.engagement.calculateEngagement(mCurrentUser);

                mSession.updateStats(user, mCurrentUser);

                callback.onFinish();
            }

            @Override
            public void onNotExists() {

                user.stats.calculateStats(mCurrentUser, mCurrentUser);
                user.engagement.calculateEngagement(mCurrentUser);

                mSession.updateStats(user, mCurrentUser);

                callback.onFinish();
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
     * Gets user from Instagram using access token.
     *
     * @param accessToken Access token
     */
    private void getSelf(String accessToken) {
        mApi.getSelf(accessToken, new InstagramApi.Callback() {

            @Override
            public void onFinish(InstagramProfile profile,
                                 InstagramUser instagramUser) {

                mSession.storeLocal(profile.id);

                user.profile = profile;

                mCurrentUser = instagramUser;
                mListener.onSuccess();
            }

            @Override
            public void onError() {

                mListener.onSuccess();
            }
        });
    }

    /**
     * Callback interface to check if authorization was successful.
     */
    public interface InstagramAuthListener {

        void onSuccess();

        void onNeedLogin();

        void onError(String error);

        void onCancel();
    }
}
