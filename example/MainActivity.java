package io.oauthio.oauth_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import io.oauth.*;


public class MainActivity extends Activity implements OAuthCallback { // implement the OAuthCallback interface to get the right information

	Button facebookButton;
	Button twitterButton;
	TextView facebookText;
	TextView twitterText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            
        final OAuth o = new OAuth(this);
        o.initialize("hRMMOd7z3NAuUxDSM6-_TUdWeJI"); // Initialize the oauth key

        facebookButton = (Button) findViewById(R.id.facebook);
        twitterButton = (Button) findViewById(R.id.twitter);
        facebookText = (TextView) findViewById(R.id.facebookText); 
        twitterText = (TextView) findViewById(R.id.twitterText);

        facebookButton.setOnClickListener(new View.OnClickListener() { // Listen the on click event
            @Override
            public void onClick(View v) 
            {
				o.popup("facebook", MainActivity.this); // Launch the pop up with the right provider & callback
            }
        });
        
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
				o.popup("twitter", MainActivity.this); // Launch the pop up with the right provider & callback
            }
        });
        
        
    }

    /*
    **	Get the information
    **
    */
	public void onFinished(OAuthData data) {
		final TextView textview = data.provider.equals("twitter") ? twitterText : facebookText;
		if ( ! data.status.equals("success")) {
			textview.setTextColor(Color.parseColor("#FF0000"));
			textview.setText("error, " + data.error);
		}
		
		// You can access the tokens through data.token and data.secret
		
		textview.setText("loading...");
		textview.setTextColor(Color.parseColor("#00FF00"));
		
		// Let's skip the NetworkOnMainThreadException for the purpose of this sample.
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

		// To make an authenticated request, you can implement OAuthRequest with your prefered way.
		// Here, we use an URLConnection (HttpURLConnection) but you can use any library.
		data.http(data.provider.equals("facebook") ? "/me" : "/1.1/account/verify_credentials.json", new OAuthRequest() {
			private URL url;
			private URLConnection con;
			
			@Override
			public void onSetURL(String _url) {
				try {
					url = new URL(_url);
					con = url.openConnection();
				} catch (Exception e) { e.printStackTrace(); }
			}
			
			@Override
			public void onSetHeader(String header, String value) {
				con.addRequestProperty(header, value);
			}
			
			@Override
			public void onReady() {
				try {
					BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
					StringBuilder total = new StringBuilder();
					String line;
					while ((line = r.readLine()) != null) {
					    total.append(line);
					}
					JSONObject result = new JSONObject(total.toString());
					textview.setText("hello, " + result.getString("name"));
				} catch (Exception e) { e.printStackTrace(); }
			}
			
			@Override
			public void onError(String message) {
				textview.setText("error: " + message);
			}
		});
    }
}
