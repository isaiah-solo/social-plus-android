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

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Dialog for Instagram login.
 */
@SuppressLint({"NewApi", "SetJavaScriptEnabled"})
class InstagramDialog extends Dialog {
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;

    private String mAuthUrl;
    private String mRedirectUri;

    private InstagramDialogListener mListener;

    InstagramDialog(Context context, String authUrl, String redirectUri, InstagramDialogListener listener) {
        super(context);

        mAuthUrl = authUrl;
        mListener = listener;
        mRedirectUri = redirectUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);

        setUpWebView();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point outSize = new Point();

        display.getSize(outSize);

        int width = outSize.x;
        int height = outSize.y;

        double[] dimensions = new double[2];
        dimensions[0] = width < height ? 0.87 * width : 0.75 * width;
        dimensions[1] = width < height ? 0.82 * height : 0.75 * height;

        addContentView(mContent, new FrameLayout.LayoutParams((int) dimensions[0], (int) dimensions[1]));
    }

    private void setUpWebView() {
        mWebView = new WebView(getContext());

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new InstagramWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mAuthUrl);
        mWebView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mContent.addView(mWebView);
    }

    void clearCache() {
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.clearFormData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mListener.onCancel();

    }

    private class InstagramWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();

            if (url.startsWith(mRedirectUri)) {
                if (url.contains("code")) {
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
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
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

    interface InstagramDialogListener {
        void onSuccess(String code);

        void onCancel();

        void onError(String error);
    }
}
