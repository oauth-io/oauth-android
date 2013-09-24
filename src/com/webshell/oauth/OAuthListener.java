package com.webshell.oauth;

import android.app.AlertDialog;

/*
**	Listener to communicate between OAuth class and OAuthWebview
**
*/

public interface OAuthListener {

	void authentificationFinished(AlertDialog dial, OAuthData data);
}
