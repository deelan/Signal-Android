package org.thoughtcrime.securesms;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BillingAddFragment extends Fragment {

    private WebView webView;
    private AuthCodeReceivedListener authCodeReceivedListener;

    public void setAuthCodeReceivedListener(AuthCodeReceivedListener authCodeReceivedListener) {
        this.authCodeReceivedListener = authCodeReceivedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        View v = inflater.inflate(R.layout.billing_webview, viewGroup, false);

        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading...", true);

        webView = (WebView) v.findViewById(R.id.billing_webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String code = null;

                if (url.indexOf("localhost/stripe") == -1) {
                    return;
                }

                String placeholder = "<html><body style='background-color: black; color: white; text-align: center; margin-top: 100px;'>Please wait...</body></html>";
                view.loadData(placeholder, "text/html", null);

                int index = url.indexOf("?");
                String[] parameters = url.substring(index + 1).split("&");

                for (int i = 0; i < parameters.length; ++i) {
                    String pair = parameters[i];

                    if (pair.indexOf("code") > -1) {
                        code = pair.split("=")[1];
                        break;
                    }
                }

                if (code != null) {
                    authCodeReceivedListener.onCodeReceived(code);
                } else {
                    String error = null;
                    String description = null;

                    for (int i = 0; i < parameters.length; ++i) {
                        String pair = parameters[i];

                        if (pair.indexOf("error_description") > -1) {
                            description = pair.split("=")[1];
                        } else if (pair.indexOf("error") > -1) {
                            error = pair.split("=")[1];
                        }
                    }

                    authCodeReceivedListener.onError(error, description);
                }
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        webView.loadUrl(BuildConfig.STRIPE_AUTH_URI + BuildConfig.STRIPE_CLIENT_ID);

        return webView;
    }

    public interface AuthCodeReceivedListener {
        public void onCodeReceived(String data);
        public void onError(String error, String description);
    }
}
