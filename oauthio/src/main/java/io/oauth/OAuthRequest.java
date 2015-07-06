package io.oauth;

import io.oauth.OAuthData;

/**
 * The OAuthRequest class contains abstract method you can implement to set your
 * http request's url and add headers to inject your oauth tokens.
 */
public abstract class OAuthRequest {
	private OAuthData _oauth_data;
	protected Object data;

	public OAuthRequest() {
	}
	public OAuthRequest(Object _data) {
		data = _data;
	}

	/**
	 * This method is called once the final url is returned.
	 * 
	 * @param url The url to set to your http request
	 */
	public abstract void onSetURL(String url);

	/**
	 * This method is called for each header to add to the request.
	 * 
	 * @param header The header's name to add
	 * @param value The header's value to set
	 */
	public abstract void onSetHeader(String header, String value);
	
	/**
	 * This method is called if every other callback are successfully called
	 */
	public void onReady() {}
	
	/**
	 * This method is called if an error occured
	 * 
	 * @param message The error's description
	 */
	public void onError(String message) {}
	
	public void setOAuthData(OAuthData oauth_data) {
		_oauth_data = oauth_data;
	}
	
	public OAuthData getOAuthData() {
		return _oauth_data;
	}
}
