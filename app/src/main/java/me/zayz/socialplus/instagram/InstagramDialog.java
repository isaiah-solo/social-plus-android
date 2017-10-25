package me.zayz.socialplus.instagram;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import me.zayz.socialplus.config.Config;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Dialog for Instagram login.
 */
@SuppressLint({"NewApi", "SetJavaScriptEnabled"})
class InstagramDialog extends Dialog {

    private ProgressDialog mSpinner;
    private LinearLayout mContent;

    private String mAuthUrl;
    private String mRedirectUri;

    private InstagramDialogListener mListener;

    InstagramDialog(Context context, InstagramDialogListener listener) {

        super(context);

        mAuthUrl = "https://api.instagram.com/oauth/authorize/?"
                + "client_id=" + Config.INSTAGRAM_API_CLIENT_ID.value + "&amp;"
                + "redirect_uri=" + Config.INSTAGRAM_API_REDIRECT_URI.value + "&amp;"
                + "response_type=token&amp;scope=basic+public_content+follower_list";
        mRedirectUri = Config.INSTAGRAM_API_REDIRECT_URI.value;

        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);

        setupWebView();

        Window window = getWindow();

        if (window == null) {
            return;
        }

        Display display = window.getWindowManager().getDefaultDisplay();
        Point outSize = new Point();

        display.getSize(outSize);

        int width = outSize.x;
        int height = outSize.y;

        double[] dimensions = new double[2];
        dimensions[0] = width < height ? 0.87 * width : 0.75 * width;
        dimensions[1] = width < height ? 0.82 * height : 0.75 * height;

        addContentView(mContent, new FrameLayout.LayoutParams((int) dimensions[0],
                (int) dimensions[1]));
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        mListener.onCancel();
    }

    /**
     * Sets up web view parameters.
     */
    private void setupWebView() {

        WebView webView = new WebView(getContext());

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new InstagramWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(mAuthUrl);
        webView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mContent.addView(webView);
    }

    /**
     * Web view client, grabs code before redirecting to redirect uri.
     */
    private class InstagramWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            String url = request.getUrl().toString();

            if (url.startsWith(mRedirectUri)) {
                if (url.contains("access_token")) {
                    String temp[] = url.split("=");

                    mListener.onSuccess(temp[1]);
                } else if (url.contains("error")) {
                    String temp[] = url.split("=");

                    mListener.onError(temp[temp.length - 1]);
                }

                InstagramDialog.this.dismiss();

                return true;
            }

            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                                    WebResourceError error) {

            super.onReceivedError(view, request, error);

            mListener.onError(error.getDescription().toString());

            InstagramDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);

            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);

            mSpinner.dismiss();
        }
    }

    /**
     * Callback interface to check if login was successful.
     */
    interface InstagramDialogListener {

        void onSuccess(String code);

        void onCancel();

        void onError(String error);
    }
}
