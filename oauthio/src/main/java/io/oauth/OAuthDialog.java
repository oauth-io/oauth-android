/**
 * 
 */
package io.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * A full screen OAuth dialog which contains a webview. This takes an authorize url
 * and returns a filled OAuthData in the OAuthCallback.onFinished method.
 */
public class OAuthDialog extends Dialog {

	private ProgressDialog mProgress;
	private LinearLayout mLayout;
	private WebView mWebView;
	private OAuthData mdata;
	private OAuthCallback mListener;
	private String mUrl;
	private static final FrameLayout.LayoutParams MATCH = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

	/**
	 * @param context
	 * @param o The OAuth object which calls this dialog
	 * @param url The authorize url
	 */
	public OAuthDialog(Context context, OAuth o, String url) {
		super(context, android.R.style.Theme);
		mdata = new OAuthData(o);
		mUrl = url;
	}
	
	/**
	 * 
	 * @return The used OAuthData
	 */
	public OAuthData getData() {
		return mdata;
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	/**
	 * When the dialog is created, we add the webview and load the authorize url.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mProgress = new ProgressDialog(getContext());
		mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgress.setMessage("Loading...");
		
		mLayout = new LinearLayout(getContext());
		mLayout.setOrientation(LinearLayout.VERTICAL);
		
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.setLayoutParams(MATCH);

		mWebView.setWebViewClient(new OAuthWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        
        mWebView.loadUrl(mUrl);
        mLayout.addView(mWebView);
        
        Display display = getWindow().getWindowManager().getDefaultDisplay();
		addContentView(mLayout, new FrameLayout.LayoutParams(display.getWidth() - 20, display.getHeight() - 20));
		CookieSyncManager.createInstance(getContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		
	}
	
	/**
	 * Set the callback when the authorization ends.
	 * 
	 * @param callback
	 */
	public void setOAuthCallback(OAuthCallback callback) {
		mListener = callback;
	}


	private class OAuthWebViewClient extends WebViewClient {

		/*
        **  Manage if the url should be load or not, and get the result of the request
        **
        */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
           	String urldecode = null;
        	try {
				urldecode = URLDecoder.decode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				mdata.status = "error";
                mdata.error = e.getMessage();
			}
        	if ( ! urldecode.contains("#oauthio="))
        		return false;
        	
	     	int index = urldecode.indexOf("=");
	     	String json = urldecode.substring(0, index + 1);
	     	json = urldecode.replace(json, "");
	     	JSONObject jsonObj = null;
	     	try {
				jsonObj = new JSONObject(json);
			} catch (JSONException e) {
				mdata.status = "error";
	            mdata.error = e.getMessage();
			}
	     	try {
	     		mdata.status = jsonObj.getString("status");
	     		if (mdata.status.contains("success"))
	     		{
	     			mdata.provider = jsonObj.getString("provider");
	     			mdata.state = jsonObj.getString("state");
	     			JSONObject data = jsonObj.getJSONObject("data");
	     			if (data.has("access_token"))
	     				mdata.token = data.getString("access_token");
	     			else if (data.has("oauth_token"))
	     				mdata.token = data.getString("oauth_token");
	                    if (data.has("oauth_token_secret"))
	     				mdata.secret = data.getString("oauth_token_secret");
	     			if (data.has("expires_in"))
	     				mdata.expires_in = data.getString("expires_in");
	     			if (data.has("request"))
	     				mdata.request = data.getJSONObject("request");
	     		}
	     		if (mdata.status.contains("error"))
	     		{
	     			mdata.error = jsonObj.getString("message");
	     		}
			} catch (JSONException e) {
				mdata.status = "error";
				mdata.error = e.getMessage();
			}
	     	mListener.onFinished(mdata);
	     	OAuthDialog.this.dismiss();
	     	return true;
        }


        /*
        **  Catch the error if an error occurs
        ** 
        */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
        	super.onReceivedError(view, errorCode, description, failingUrl);
            mdata.status = "error";
            mdata.error = description;
            mListener.onFinished(mdata);
            OAuthDialog.this.dismiss();
        }


        /*
        **  Display a dialog when the page start
        **
        */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
        	super.onPageStarted(view, url, favicon);
           	mProgress.show();
        }


		/*
		**  Remove the dialog when the page finish loading
		**
		*/
		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			mProgress.dismiss();
		}
	}
}
