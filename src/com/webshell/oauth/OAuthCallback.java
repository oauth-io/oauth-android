package com.webshell.oauth;

/*
**	The authentificationFinished is called when the authentication is finished
**
*/

public interface OAuthCallback {

	void authentificationFinished(OAuthData data);
}
