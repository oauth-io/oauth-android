
# OAuth.io Android SDK

This is the official android sdk for [OAuth.io](https://oauth.io) !

The OAuth.io android sdk allows you to use OAuth for your android application, and connect any OAuth provider [available on OAuth.io](https://oauth.io/providers).



## OAuth.io Requirements and Set-Up

To use this sdk you will need to make sure you've registered your OAuth.io app and have a public key (https://oauth.io/docs).


	
### Installation

First you need to create an android project in eclipse for example
To install the sdk android just download OAuth.jar and put it in your libs directory
Then refresh the libs directory in eclipse, right click on the OAuth.jar and add it in your build Path

You MUST add the line : 
  <uses-permission android:name="android.permission.INTERNET" />
in your AndroidManifest.xml in the manifest tag


### Usage

The usage is basically the same than the web [javascript API](https://oauth.io/docs/api), the only difference being there is only the popup mode, as mobiles don't distinct redirection/popup.

In your Activity, you can now instantiate a OAuth class:

	final OAuth oauth = new OAuth();
	oauth.initialize('Public key');


To connect your user to a provider (e.g. facebook):

 ```
oauth.popup('facebook', callback, options, context);
 ```

The callback interface can be any class that implement the OAuthCallback method.

The OAuthCallback interface implement just one method :

    void authentificationFinished(OAuthData data);
  
The OAuthData class contain all the OAuth information :

    public class OAuthData {

    public String provider = "";	// name of the provider
    public String state = "";		// state send
    public String token = "";		// token received
    public String secret = "";		// secret received (only in oauth1)
    public String status = "";		// status of the request (succes, error, ....)
    public String expires_in = "";	// if the token expires
    public String error = "";		// error encountered
    }


And the context is just the context where the pop up will be displayed.

### Run the included samples

1. Create a new project as described in the [Android documentation](http://developer.android.com/training/basics/firstapp/index.html). By example in eclipse:

		File -> New -> Other -> Android Project

2. Install OAuth.io Android sdk into the project

3. Replace the generated example _res->view->view.xml_ , _MainActivity.java_ with the files included in the example folder. Don't forget to add uses-permission android:name="android.permission.INTERNET"  to your AndroidManifest.xml. A valid key is provided, but you can do your own app on [OAuth.io](https://oauth.io/).

4. Plug your phone & run it ! (or use an emulator)
