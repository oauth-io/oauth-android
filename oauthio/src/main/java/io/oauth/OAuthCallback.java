package io.oauth;

/**
 *	The onFinished is called when the authorize dialog closes.
 */
public interface OAuthCallback {

	/**
	 * Called when the authorize dialog closes.
	 * 
	 * @param data The authorize result: tokens, expiration, error message...
	 */
	public abstract void onFinished(OAuthData data);
}
