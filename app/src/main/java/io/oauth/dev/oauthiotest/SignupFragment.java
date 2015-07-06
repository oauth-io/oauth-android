package io.oauth.dev.oauthiotest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Hashtable;

import io.oauth.OAuth;
import io.oauth.OAuthCallback;
import io.oauth.OAuthData;
import io.oauth.OAuthUserCallback;
import io.oauth.OAuthUsers;

public class SignupFragment extends Fragment implements OAuthCallback {
    private View rootView;
    private MainActivity activity;
    private OAuth oauth;
    private OAuthUsers users;

    public SignupFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = ((MainActivity) getActivity());
        oauth = activity.oauth;
        users = activity.users;
        activity.SetLogged(false);

        rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        Button bt_create = (Button) rootView.findViewById(R.id.createbtn);

        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onCreateClick();
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

        return rootView;
    }

    private void onCreateClick() {
        Hashtable<String, String> infos = new Hashtable<>();
        infos.put("firstname", ((EditText) rootView.findViewById(R.id.editFirstname)).getText().toString());
        infos.put("lastname", ((EditText) rootView.findViewById(R.id.editLastname)).getText().toString());
        infos.put("email", ((EditText) rootView.findViewById(R.id.editEmail)).getText().toString());
        infos.put("password", ((EditText) rootView.findViewById(R.id.editPassword)).getText().toString());

        users.signup(infos, new UserCallback(null));
    }

    private void onProviderClick(String provider) {
        oauth.popup(provider, SignupFragment.this);
    }

    @Override
    public void onFinished(OAuthData data) {
        if (data.status.equals("error"))
            activity.displayError(data.error);
        else {
            Hashtable<String, String> infos = new Hashtable<>();
            infos.put("firstname", ((EditText) rootView.findViewById(R.id.editFirstname)).getText().toString());
            infos.put("lastname", ((EditText) rootView.findViewById(R.id.editLastname)).getText().toString());
            infos.put("email", ((EditText) rootView.findViewById(R.id.editEmail)).getText().toString());
            users.signup(data, infos, new UserCallback(data));
        }
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
            if (data != null && message.equals("Missing email")) {
                final InputDialog dlg = new InputDialog(activity);
                dlg.setTitle("Please setup your email");
                dlg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Hashtable<String,String> infos = new Hashtable<String, String>();
                        infos.put("email", dlg.value);
                        infos.put("firstname", ((EditText) rootView.findViewById(R.id.editFirstname)).getText().toString());
                        infos.put("lastname", ((EditText) rootView.findViewById(R.id.editLastname)).getText().toString());
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
