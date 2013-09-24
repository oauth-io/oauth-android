package com.webshell.oauth;


import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.TextView;
import com.webshell.oauth.*;


public class MainActivity extends Activity implements OAuthCallback { // implement the OAutCallback interface to get the right information

	Context context = this;
	OAuthCallback call = this;
    /** Called when the activity is first created. */
	Button twitter;
	Button facebook;
	TextView facebookText;
	TextView twitterText;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
            
        final OAuth o = new OAuth();
        o.initialize("l1M0mtaSpzGLwUSTPNrLVfXaorA"); // Initialize the oauth key
        
        facebookText = (TextView) findViewById(R.id.facebookText); 
        twitterText = (TextView) findViewById(R.id.twitterText);
        
        facebook = (Button) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() { // Listen the on click event
            @Override
            public void onClick(View v) 
            {

                JSONObject opts = new JSONObject();

                try {
					o.popup("facebook", call, opts, context); // Launch the pop up with the right provider, callback, options, and context
				} catch (OAuthException e) {
					Log.e("ERROR OAUTH ", e.getMessage()); // Get the error
				}
            }
        });
        
        
        twitter = (Button) findViewById(R.id.twitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {

                JSONObject opts = new JSONObject();
                try {
					o.popup("twitter", call,  opts, context); // Launch the pop up with the right provider, callback, options, and context
				} catch (OAuthException e) {
					Log.e("ERROR OAUTH ", e.getMessage()); // Listen the on click event
				}
            }
        });
        
        
    }
    
    /*
    **	Get the information
    **
    */

	@Override
	public void authentificationFinished(OAuthData data) {
		if (data.status.contains("success"))
		{
			if (data.provider.contains("twitter"))
			{
				twitter.setEnabled(false);
				twitterText.setText("You are now authenticate on " + data.provider);
				twitterText.setTextColor(Color.parseColor("#00FF00"));
				
			}
			else
			{
				facebook.setEnabled(false);
				facebookText.setText("You are now authenticate on " + data.provider );
				facebookText.setTextColor(Color.parseColor("#00FF00"));
			}
		}
		else
			if (data.provider.contains("twitter"))
			{
				twitter.setEnabled(true);
				twitterText.setText("Error of authentification on " + data.provider);
				twitterText.setTextColor(Color.parseColor("#FF0000"));
			}
			else
			{
				facebook.setEnabled(false);
				facebookText.setText("Error of authentification on " + data.provider);
				facebookText.setTextColor(Color.parseColor("#FF0000"));
			}
	}
}
