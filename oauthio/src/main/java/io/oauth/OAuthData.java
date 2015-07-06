package io.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;

import org.json.JSONObject;

import io.oauth.http.OAuthJSONCallback;
import io.oauth.http.OAuthJSONRequest;

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
					JSONObject query = request.getJSONObject("query");
					Iterator<?> keys = query.keys();
					while (keys.hasNext()) {
						String key = (String)keys.next();
						String val = (String)query.get(key);
						if (val.equals("{{token}}"))
							val = token;
						qs += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
					}
				}

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

    public void http(final OAuthJSONRequest jsonRequest, final OAuthJSONCallback jsonCallback) {
        http(jsonRequest.httpReq.url, new OAuthRequest() {
            @Override
            public void onSetURL(String url) {
                jsonRequest.httpReq.url = url;
            }

            @Override
            public void onSetHeader(String header, String value) {
                if (jsonRequest.httpReq.headers == null)
                    jsonRequest.httpReq.headers = new Hashtable<String, String>();
                jsonRequest.httpReq.headers.put(header, value);
            }

            @Override
            public void onReady() {
                jsonRequest.execute(jsonCallback);
            }

            @Override
            public void onError(String message) {
                jsonCallback.onError(message);
            }
        });
    }

    public void me(OAuthJSONCallback callback) {
        String url = _oauth.getOAuthdURL() + "/auth/" + provider + "/me";

        String oauthio_header = "k=" + _oauth.getPublicKey();
        try {
            //url = "http://httpbin.org/get?" + URLEncoder.encode(url, "UTF-8");
            if (token != null && secret != null)
                oauthio_header += "&oauthv=1"
                                + "&oauth_token=" + URLEncoder.encode(token, "UTF-8")
                                + "&oauth_token_secret=" + URLEncoder.encode(secret, "UTF-8");
            else
                oauthio_header += "&access_token=" + URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Hashtable<String, String> headers = new Hashtable<>();
        headers.put("oauthio", oauthio_header);

        new OAuthJSONRequest().http(OAuthJSONRequest.HTTP_GET, url, null, headers, callback);
    }
}
