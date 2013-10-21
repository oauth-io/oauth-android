package io.oauth;

/*
**	Class with all the information of the authentication
**
*/

public class OAuthData {

	public String provider;		// name of the provider
	public String state;		// state send
	public String token;		// token received
	public String secret;		// secret received (only in oauth1)
	public String status;		// status of the request (succes, error, ....)
	public String expires_in;	// if the token expires
	public String error;		// error encountered
}
