package io.oauth.dev.oauthiotest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import java.util.Hashtable;

import io.oauth.OAuth;
import io.oauth.OAuthCallback;
import io.oauth.OAuthData;
import io.oauth.OAuthUserCallback;
import io.oauth.OAuthUsers;
import io.oauth.http.OAuthJSONCallback;
import io.oauth.http.OAuthJSONRequest;

public class SigninFragment extends Fragment implements OAuthCallback {

    private View rootView;
    private MainActivity activity;
    private OAuth oauth;
    private OAuthUsers users;

    public SigninFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = ((MainActivity) getActivity());
        oauth = activity.oauth;
        users = activity.users;

        rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        Button bt_login = (Button) rootView.findViewById(R.id.loginbtn);

        bt_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginClick();
            }
        });

        View.OnClickListener providerOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProviderClick(v.getTag().toString());
            }
        };

        ((ImageView) rootView.findViewById(R.id.imageLogoFb)).setOnClickListener(providerOnClick);
        ((ImageView) rootView.findViewById(R.id.imageLogoTw)).setOnClickListener(providerOnClick);
        ((ImageView) rootView.findViewById(R.id.imageLogoGoogle)).setOnClickListener(providerOnClick);
        ((ImageView) rootView.findViewById(R.id.imageLogoLin)).setOnClickListener(providerOnClick);
        ((ImageView) rootView.findViewById(R.id.imageLogoGh)).setOnClickListener(providerOnClick);

        activity.SetLogged(false);

        return rootView;
    }

    private void onLoginClick() {
        String email = ((EditText) rootView.findViewById(R.id.editEmail)).getText().toString();
        String password = ((EditText) rootView.findViewById(R.id.editPassword)).getText().toString();
        users.signin(email, password, new UserCallback(null));
    }

    private void onProviderClick(String provider) {
        oauth.popup(provider, SigninFragment.this);
    }

    @Override
    public void onFinished(OAuthData data) {
        if (data.status.equals("error"))
            activity.displayError(data.error);
        else
            users.signin(data, new UserCallback(data));
    }

    private class UserCallback implements OAuthUserCallback {

        public OAuthData data;
        public UserCallback(OAuthData _data) { data = _data; }

        @Override
        public void onFinished() {
            activity.SetFragment(new InfosFragment());
        }

        @Override
        public void onError(String message) {
            if (message.equals("Missing email")) {
                final InputDialog dlg = new InputDialog(activity);
                dlg.setTitle("Please setup your email to create your account.");
                dlg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Hashtable<String,String> infos = new Hashtable<String, String>();
                        infos.put("email", dlg.value);
                        users.signup(data, infos, new UserCallback(data));
                    }
                });
                dlg.show();
            }
            else
                activity.displayError(message);
        }
    }
}
