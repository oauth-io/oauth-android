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

	private String _key = "";
	private String oauthd_url = "https://oauth.io";
	private Context mContext = null;
	
	/**
     * Constructor
     */
	public OAuth(Context context)
	{
		mContext = context;
	}

	/**
	 * Initialize the oauthd/oauth.io key
	 *
	 * @param key The public key to use
	 */
	public void initialize(String key) 
	{
		_key = key;
	}
	
	/**
	 * @return The public key used
	 */
	public String getPublicKey()
	{
		return _key;
	}
	
	/**
	 * Set a oauthd URL. By default, this is set to https://oauth.io
	 * 
	 * @param url The oauth daemon url to set. You can download oauthd at
	 * <a href="https://github.com/oauth-io/oauthd">https://github.com/oauth-io/oauthd</a>
	 */
	public void setOAuthdURL(String url)
	{
		oauthd_url = url;
	}
	
	/**
	 * @return The current oauth daemon URL
	 */
	public String getOAuthdURL()
	{
		return oauthd_url;
	}
	
	/**
	 * Display a full screen authorization webview
	 * 
	 * @param provider The provider's name. e.g. facebook, google, twitter...
	 * @param callback An OAuthCallback implementing onFinished
	 */
	public void popup (String provider, OAuthCallback callback)
	{
		JSONObject opts = new JSONObject();
		this.popup(provider,  opts, callback);
	}
	 
	/**
	 * Display a full screen authorization webview
	 * 
	 * @param provider The provider's name. e.g. facebook, google, twitter...
	 * @param opts A JSONObject containing additional options.
	 * It can contain an "authorize" JSONObject with additional query parameters
	 * to pass to the authorize url.
	 * @param callback An OAuthCallback implementing onFinished
	 */
	public void popup (String provider, JSONObject opts, OAuthCallback callback)
	{
		if (_key == "") {
			OAuthData data = new OAuthData(this);
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
		
		String url = oauthd_url + "/auth/" + provider + "?mobile=true&k=" + _key;
		try {
			url += "&redirect_uri=" + URLEncoder.encode("http://localhost", "UTF-8");
			if (opts != null)
				url += "&opts=" + URLEncoder.encode(opts.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		OAuthDialog dial = new OAuthDialog(mContext, this, url);
		dial.setOAuthCallback(callback);
		dial.getData().provider = provider;
	    dial.show();
	 }
 

	/*
	**	Convert byte in String
	**
	*/
	private String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++)
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		return result;
	}


	/*
	**	Encrypte byte in SHA1
	**
	*/
	private String toSHA1(byte[] convertme) {
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
