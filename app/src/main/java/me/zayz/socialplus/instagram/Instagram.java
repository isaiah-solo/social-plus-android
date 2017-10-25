package me.zayz.socialplus.instagram;

import android.content.Context;
import android.util.Log;

import me.zayz.socialplus.R;
import me.zayz.socialplus.SocialPlusCallback;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Handles all Instagram-related actions.
 */
public class Instagram {
    private Context mContext;

    private InstagramDialog mDialog;
    private InstagramAuthListener mListener;
    private InstagramSession mSession;

    /**
     * Instantiate new object of this class.
     *
     * @param context Context
     */
    public Instagram(Context context) {
        mContext = context;

        String authUrl = mContext.getString(R.string.auth_code_url);
        final String redirectUri = mContext.getString(R.string.auth_redirect_uri);

        mSession = new InstagramSession(context);

        mDialog = new InstagramDialog(context, authUrl, redirectUri, new InstagramDialog.InstagramDialogListener() {
            @Override
            public void onSuccess(String code) {
                final InstagramApi api = new InstagramApi(mContext);
                api.getAccessToken(code, new SocialPlusCallback() {
                    @Override
                    public void onFinish() {
                        gatherUserStats(api.getUser());
                    }

                    @Override
                    public void onError() {
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
    }

    private void gatherUserStats(InstagramUser user) {
        final InstagramApi api = new InstagramApi(mContext, user);

        // Get self information
        api.getSelf(new SocialPlusCallback() {
            @Override
            public void onFinish() {
                storeUser(api.getUser());
            }

            @Override
            public void onError() {
            }
        });
    }

    private void storeUser(InstagramUser user) {
        mSession.store(user);
        mListener.onSuccess(user);
    }

    /**
     * Authorize user.
     *
     * @param listener Auth listener
     */
    public void authorize(InstagramAuthListener listener) {
        mListener = listener;

        if (sessionIsActive()) {
            Log.d("SOCIAL", "TODO");
            //mListener.onSuccess(mSession.getUser());
        } else {
            mDialog.show();
        }
    }

    /**
     * Reset session.
     */
    /*
    public void resetSession() {
        mSession.reset();

        mDialog.clearCache();
    }
    */

    public boolean sessionIsActive() {
        return mSession.isActive();
    }

    public interface InstagramAuthListener {
        void onSuccess(InstagramUser user);

        void onError(String error);

        void onCancel();
    }
}
