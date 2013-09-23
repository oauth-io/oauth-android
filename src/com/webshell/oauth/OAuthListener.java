package com.webshell.oauth;

import android.app.AlertDialog;

public interface OAuthListener {

	void authentificationFinished(AlertDialog dial, OAuthData data);
}
