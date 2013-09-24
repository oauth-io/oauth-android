package com.webshell.oauth;

/*
**	Exception class to send the correct error
**
*/

public class OAuthException extends Exception{  
 
	public OAuthException() {}  

	public OAuthException(String message) {  
		super(message); 
	}  

	public OAuthException(Throwable cause) {  
		super(cause); 
	}  

	public OAuthException(String message, Throwable cause) {  
		super(message, cause); 
	} 
}