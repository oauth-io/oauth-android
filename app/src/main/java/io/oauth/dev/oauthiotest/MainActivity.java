package io.oauth.dev.oauthiotest;

import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.oauth.OAuth;
import io.oauth.OAuthUsers;


public class MainActivity extends FragmentActivity {

    private Boolean isLogged = false;
    public OAuth oauth;
    public OAuthUsers users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oauth = new OAuth(this);
        oauth.initialize("-HAiwR_DX9C0xT72AGcRaIcCFBo");
        users = new OAuthUsers(oauth);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
             SetFragment(new SigninFragment());
        }
    }

    public void SetFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSigninClick(View v) {
        SetFragment(new SigninFragment());
    }

    public void onSignupClick(View v) {
        if (isLogged)
            SetFragment(new SigninFragment());
        else
            SetFragment(new SignupFragment());
    }

    public void displayError(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setTitle("Error");
        alert.show();
    }

    public void SetLogged(Boolean _isLogged) {
        isLogged = _isLogged;
        if (isLogged) {
            Button b = (Button) findViewById(R.id.mainbtn1);
            b.setVisibility(View.INVISIBLE);
            b = (Button) findViewById(R.id.mainbtn2);
            b.setText(getString(R.string.logout));
        } else {
            Button b = (Button) findViewById(R.id.mainbtn1);
            b.setVisibility(View.VISIBLE);
            b = (Button) findViewById(R.id.mainbtn2);
            b.setText(R.string.signup);
        }
    }
}
