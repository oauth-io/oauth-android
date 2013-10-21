package io.oauth;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

public class OAuth {

	String _key = "";
	String oauthd_url = "https://oauth.io/auth";
	Context mContext = null;
	
	/*
    **  Constructor
    **
    */
	public OAuth(Context context)
	{
		mContext = context;
	}

	/*
	**	Initialize the oauth key
	**
	*/
	public void initialize (String key) 
	{
		_key = key;
	}
	 
	/*
	**	Display the pop up for the authentication of the provider
	**
	*/
	public void popup (String provider, OAuthCallback callback)
	{
		JSONObject opts = new JSONObject();
		this.popup(provider,  opts, callback);
	}
	 
	/*
	**	Display the pop up for the authentication of the provider
	**
	*/
	public void popup (String provider, JSONObject opts, OAuthCallback callback)
	{
		if (_key == "") {
			OAuthData data = new OAuthData();
			data.status = "error";
			data.error = "Oauth must be initialized with a valid key";
			callback.onFinished(data);
			return;
		}

		if (opts.has("state") == false) {
			try {
				opts.putOpt("state", create_hash());
				opts.putOpt("state_type", "client");
			} catch (JSONException e) {
			}
		}
		
		String url = oauthd_url + "/" + provider + "?k=" + _key;
		try {
			url += "&redirect_uri=" + URLEncoder.encode("http://localhost", "UTF-8");
			if (opts != null)
				url += "&opts=" + URLEncoder.encode(opts.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		OAuthDialog dial = new OAuthDialog(mContext, url);
		dial.setOAuthCallback(callback);
		dial.getData().provider = provider;
	    dial.show();
	 }
 

	/*
	**	Convert byte in String
	**
	*/
	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++)
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		return result;
	}


	/*
	**	Encrypte byte in SHA1
	**
	*/
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


	/*
	**	Create the good hash for the request
	**
	*/
	private String create_hash() 
	{
		String hash = String.valueOf(new Date().getTime());
		hash += ':' + Math.floor(Math.random()*9999999);
		hash = toSHA1(hash.getBytes());
		hash = Base64.encodeToString(hash.getBytes(), Base64.DEFAULT);
		return hash.replaceAll("/+/g", "-").replaceAll("///g", "_").replaceAll("/=+$/", "");
	}
}
