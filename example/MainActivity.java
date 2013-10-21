package io.oauthio.oauth_test;

import android.os.Bundle;
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
		String text = data.status + ", ";
		if (data.error != null)
			text += data.error;
		if (data.token != null)
			text += "token = " + data.token;
		if (data.secret != null)
			text += " & secret = " + data.secret;
		
		TextView textview = data.provider.contains("twitter") ? twitterText : facebookText;
		textview.setText(text);
		textview.setTextColor(Color.parseColor(data.status.contains("success") ? "#00FF00" : "#FF0000"));
	}
}
