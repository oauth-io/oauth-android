package io.oauth;

import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONObject;

import android.util.Log;

/**
 *	Class with all the information of the authentication
 */
public class OAuthData {
	
	private OAuth _oauth;

	public String provider;		/** name of the provider */
	public String state;		/** state send */
	public String token;		/** token received */
	public String secret;		/** secret received (only in oauth1) */
	public String status;		/** status of the request (succes, error, ....) */
	public String expires_in;	/** if the token expires */
	public String error;		/** error encountered */
	public JSONObject request;	/** API request description */
	
	public OAuthData(OAuth o)
	{
		_oauth = o;
	}
	
	/**
	 * Inject the authorization informations to an http request by providing an
	 * OAuthRequest implementation. If the authorization method of the provider is OAuth 1,
	 * the request will be proxified by oauth.io / oauthd. If it is OAuth 2, the request
	 * is direct, using the API request description received beside the token.
	 * 
	 * @param url The url can be absolute or relative to the provider's API base url.
	 * @param setters OAuthRequest implementation to your http request.
	 */
	public void http(String url, OAuthRequest setters) {
		if ("".equals(url)) return;
		setters.setOAuthData(this);
		try {
			if (token != null && secret != null) {
				if (request == null || ! request.has("url")) {
					setters.onError("The provider does have an API request description");
					return;
				}
				if (url.charAt(0) != '/')
					url = "/" + url;
				url = _oauth.getOAuthdURL() + "/request/" + provider + url;
				String oauthio_header = "k=" + _oauth.getPublicKey() + "&oauthv=1"
						+ "&oauth_token=" + URLEncoder.encode(token, "UTF-8")
						+ "&oauth_token_secret=" + URLEncoder.encode(secret, "UTF-8");
				Log.d("OAuthData", "oauthio header " + oauthio_header);
				setters.onSetURL(url);
				setters.onSetHeader("oauthio", oauthio_header);
				setters.onReady();
			}
			else if (token != null) {
				if ( ! url.matches("[a-z]{2,16}://.*"))
				{
					if (request == null || ! request.has("url")) {
						setters.onError("The provider does have an API request description");
						return;
					}
					if (url.charAt(0) != '/')
						url = "/" + url;
					url = request.getString("url") + url;
				}
				
				String qs = "";
				if (request.has("query")) {
					Log.d("OAuthData", "has query");
					JSONObject query = request.getJSONObject("query");
					Iterator<?> keys = query.keys();
					while (keys.hasNext()) {
						String key = (String)keys.next();
						String val = (String)query.get(key);
						Log.d("OAuthData", "key " + key + ", val " + val);
						if (val.equals("{{token}}"))
							val = token;
						qs += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
					}
				}
				
				Log.d("OAuthData", "final qs: " + qs);
				
				if (qs.length() > 0 && url.indexOf("?") == -1)
					qs = "?" + qs.substring(1);
				url += qs;
				
				setters.onSetURL(url);
				
				if (request.has("headers")) {
					JSONObject headers = request.getJSONObject("headers");
					Iterator<?> keys = headers.keys();
					while (keys.hasNext()) {
						String key = (String)keys.next();
						String val = (String)headers.get(key);
						if (val.equals("{{token}}"))
							val = token;
						setters.onSetHeader(key, val);
					}
				}
				setters.onReady();
			}
			else
				setters.onReady();
		}
		catch (Exception e) { setters.onError(e.getMessage()); }
	}
}
