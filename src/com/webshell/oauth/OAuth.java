package com.webshell.oauth;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OAuth implements OAuthListener {

	String _key = "";
	String oauthd_url = "https://oauth.io/auth";
	OAuthData mData = new OAuthData();
	OAuthCallback mCallback = null;
	
	 public void initialize (String key) 
	 {
		 _key = key;
	 }
	 
	 
	 public void popup(String provider,  OAuthCallback callback , JSONObject  opts, Context mContext) throws OAuthException
	 {
		popup(provider, callback, opts, mContext, "http://localhost");
	 }
	 
	 public void popup (String provider,  OAuthCallback callback , JSONObject  opts, Context mContext, String domain) throws OAuthException
	 {
		if (_key == "")
			throw new OAuthException("Oauth must be initialized with a valid key");
		if (callback == null)
			throw new OAuthException("Oauth must have a valid callback");
		mCallback = callback;
		
		if (opts != null)
		{	
			if (opts.has("state") == false) {
				try {
					opts.putOpt("state", create_hash());
					opts.putOpt("state_type", "client");
				} catch (JSONException e) {
					throw new OAuthException(e.getMessage());
				}

			}
		}
		
		String url = oauthd_url + "/" + provider + "?k=" + _key;
		try {
			url += "&redirect_uri=" + URLEncoder.encode(domain, "UTF-8");
			if (opts != null)
				url += "&opts=" + URLEncoder.encode(opts.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new OAuthException(e.getMessage());
		}

		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setTitle("Authentication");
		alert.setCancelable(true);

	    final AlertDialog dial = alert.create();
	    OAuthWebview wv = new OAuthWebview(mContext, url, dial);
	    wv.addOAuthListener(this);
	    dial.setView(wv);
	    
	    dial.show();
	 }
	
	 public void authentificationFinished(AlertDialog dial, OAuthData data)
	  {
		 mData = data;
		 dial.dismiss();
		 mCallback.authentificationFinished(data);
	  }
	 
	 public static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
		}
	 
	 public static String toSHA1(byte[] convertme) {
		    MessageDigest md = null;
		    try {
		        md = MessageDigest.getInstance("SHA-1");
		    }
		    catch(NoSuchAlgorithmException e) {
		        e.printStackTrace();
		    } 
		    return byteArrayToHexString(md.digest(convertme));
		}
	 
	 private String create_hash() 
	 {
		 	String hash = String.valueOf(new Date().getTime());
		 	hash += ':' + Math.floor(Math.random()*9999999);
			hash = toSHA1(hash.getBytes());
			hash = Base64.encodeToString(hash.getBytes(), Base64.DEFAULT);
			return hash.replaceAll("/+/g", "-").replaceAll("///g", "_").replaceAll("/=+$/", "");
		}
}
