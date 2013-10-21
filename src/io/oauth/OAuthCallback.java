package io.oauth;

/*
**	The authenticationFinished is called when the authorize dialog closes.
**
*/

public interface OAuthCallback {
	public abstract void onFinished(OAuthData data);
}
