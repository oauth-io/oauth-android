# OAuth.io Android SDK

This is the official android sdk for [OAuth.io](https://oauth.io) !

The OAuth.io android sdk allows you to use OAuth for your android application, and connect any OAuth provider [available on OAuth.io](https://oauth.io/providers).



## OAuth.io Requirements and Set-Up

To use this sdk you will need to make sure you've registered your OAuth.io app and have a public key (https://oauth.io/docs).


	
### Installation

First you need to create an android project in eclipse for example
To install the sdk android just download oauth.jar and put it in your libs directory
Then refresh the libs directory in eclipse, right click on the oauth.jar and add it in your build path

You MUST add the line : 
  <uses-permission android:name="android.permission.INTERNET" />
in your AndroidManifest.xml in the manifest tag


### Usage

The usage is basically the same than the web [javascript API](https://oauth.io/docs/api), but there are some differences to match the android coding style.
Only the popup mode is present, as mobiles don't distinct redirection/popup.

In your Activity, you can instantiate a OAuth class:

	final OAuth oauth = new OAuth(context);
	oauth.initialize('Public key');


To connect your user to a provider (e.g. facebook):

 ```
oauth.popup('facebook', callback);

// or (with options a org.json.JSONObject):

oauth.popup('facebook', options, callback);
 ```

The callback is a class that implement the OAuthCallback method.

The OAuthCallback interface implement just one method :

    void onFinished(OAuthData data);
  
The OAuthData class contain all the OAuth information :

    public class OAuthData {
        public String provider;		// name of the provider
        public String state;		// state send
        public String token;		// token received
        public String secret;		// secret received (only in oauth1)
        public String status;		// status of the request (succes, error, ....)
        public String expires_in;	// if the token expires
        public String error;		// error encountered
    }


### Run the included sample

1. Create a new project as described in the [Android documentation](http://developer.android.com/training/basics/firstapp/index.html). By example in eclipse:

		File -> New -> Other -> Android Project

2. Install OAuth.io Android sdk into the project

3. Replace the generated example *res/layout/activity_main.xml* , _MainActivity.java_ with the files included in the example folder. Don't forget to add uses-permission android:name="android.permission.INTERNET"  to your AndroidManifest.xml. A valid key is provided, but you can do your own app on [OAuth.io](https://oauth.io/).

4. Plug your phone & run it ! (or use a virtual device)
