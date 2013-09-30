package com.webshell.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import 	android.view.MotionEvent;

/*
**  Reimplementation of a Webview class to manage correclty input and display
**
*/

class OAuthWebview extends WebView
{
    Context mContext;
    OAuthData mdata = new OAuthData();
    AlertDialog mAlert = null;
    OAuthListener mListerner = null;
    
    
    /*
    **  Constructor
    **
    */

    public OAuthWebview(Context context, String URL, AlertDialog alert)
    {
        super(context);
        mContext = context;
        setWebViewClient(URL);
        mAlert = alert;
    }

    @Override
    public boolean onCheckIsTextEditor()
    {
        return true;
    }

    /*
    **  Set the listener
    **
    */

    public void addOAuthListener(OAuthListener l)
    {
    	mListerner = l;
    }
    
    /*
    **  Manage the input and the display 
    **
    */


    @SuppressLint("SetJavaScriptEnabled")
    boolean setWebViewClient(String URL)
    {
        setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus(View.FOCUS_DOWN);

        WebSettings webSettings = getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(false);

        setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus())
                        {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        this.setWebViewClient(new WebViewClient()
        {
            ProgressDialog dialog = new ProgressDialog(mContext);


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
            	if (urldecode.contains("#oauthio="))
            	{
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
            					mdata.expires_in = data.getString("expires_in");;
            			}
            			
					} catch (JSONException e) {
						mdata.status = "error";
						mdata.error = e.getMessage();
					}
            		mListerner.authentificationFinished(mAlert, mdata);
            	}
            	else
            		loadUrl(url);

                return true;
            }

            /*
            **  Catch the error if an error occurs
            ** 
            */

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                //Toast.makeText(mContext, " " + description, Toast.LENGTH_SHORT).show(); //Display the error
                mdata.status = "error";
                mdata.error = description;
                mListerner.authentificationFinished(mAlert, mdata);
            }

            /*
            **  Display a dialog when the page start
            **
            */

            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if (dialog != null)
                {
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }

            /*
            **  Remove the dialog when the page finish loading
            **
            */

            public void onPageFinished(WebView view, String url)
            {
                if (dialog != null)
                {
                    dialog.cancel();
                }
            }
        });

        this.setWebChromeClient(new WebChromeClient()
        {
            public void onProgressChanged(WebView view, int progress)
            {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result)
            {
                result.confirm();
                return true;
            }
        });

        loadUrl(URL);

        return true;
    }
}