package io.oauth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import io.oauth.http.OAuthJSONCallback;
import io.oauth.http.OAuthJSONRequest;

/**
 * Methods related to the user authentication
 */
public class OAuthUsers {

    private OAuth _oauth;

    public OAuthUser authUser;

    public OAuthUsers(OAuth o)
    {
        _oauth = o;
    }

    public void signin(OAuthData oauth_data, OAuthUserCallback callback) {
        JSONObject postdata = new JSONObject();
        try {
            if (oauth_data.token != null && oauth_data.secret != null) {
                postdata.put("oauth_token", oauth_data.token);
                postdata.put("oauth_token_secret", oauth_data.secret);
            }
            else if (oauth_data.token != null) {
                postdata.put("access_token", oauth_data.token);
            }
            postdata.put("provider", oauth_data.provider);
            _signin(postdata, callback);

        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    public void signin(String email, String password, OAuthUserCallback callback) {
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("email", email);
            postdata.put("password", password);
            _signin(postdata, callback);

        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    private void _signin(JSONObject postdata, final OAuthUserCallback callback) {
        String url = _oauth.getOAuthdURL() + "/api/usermanagement/signin?k=" + _oauth.getPublicKey();
        new OAuthJSONRequest().post(url, postdata, new OAuthJSONCallback() {
            @Override
            public void onFinished(JSONObject data) {
                OAuthUser parsedUser = new OAuthUser(_oauth);
                try {
                    parsedUser.token = data.getString("token");
                    if(data.has("user")) {
                        JSONObject userObj = data.getJSONObject("user");
                        Iterator<String> it = userObj.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            parsedUser.data.put(key, userObj.get(key).toString());
                        }
                    }
                    if(data.has("providers")) {
                        JSONArray providersArray = data.getJSONArray("providers");
                        for (int i = 0; i < providersArray.length(); i++) {
                            parsedUser.providers.add(providersArray.getString(i));
                        }
                    }
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                    return;
                }
                authUser = parsedUser;
                callback.onFinished();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }

            @Override
            public void onErrorData(String message, JSONObject data) {
                if (data.has("email"))
                    onError("Missing email");
                else
                    onError(message);
            }
        });
    }

    public void signup(OAuthData oauth_data, OAuthUserCallback callback) {
        signup(oauth_data, null, callback);
    }

    public void signup(OAuthData oauth_data, Map<String, String> data, OAuthUserCallback callback) {
        if (data != null)
            data = new Hashtable<>(data);
        else
            data = new Hashtable<String, String>();

        if (oauth_data.token != null && oauth_data.secret != null) {
            data.put("oauth_token", oauth_data.token);
            data.put("oauth_token_secret", oauth_data.secret);
        }
        else if (oauth_data.token != null) {
            data.put("access_token", oauth_data.token);
        }
        data.put("provider", oauth_data.provider);
        signup(data, callback);
    }

    public void signup(Map<String, String> data, final OAuthUserCallback callback) {
        String url = _oauth.getOAuthdURL() + "/api/usermanagement/signup?k=" + _oauth.getPublicKey();

        if ( ! data.containsKey("provider") && (! data.containsKey("email")
                || ! data.containsKey("password")
                || ! data.containsKey("firstname")
                || ! data.containsKey("lastname")
        )) {
            callback.onError("Missing email/password/firstname/lastname");
            return;
        }

        JSONObject postdata = new JSONObject(data);

        new OAuthJSONRequest().post(url, postdata, new OAuthJSONCallback() {
            @Override
            public void onFinished(JSONObject data) {
                OAuthUser parsedUser = new OAuthUser(_oauth);
                try {
                    parsedUser.token = data.getString("token");
                    if (data.has("user")) {
                        JSONObject userObj = data.getJSONObject("user");
                        Iterator<String> it = userObj.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            parsedUser.data.put(key, userObj.get(key).toString());
                        }
                    }
                    if (data.has("providers")) {
                        JSONArray providersArray = data.getJSONArray("providers");
                        for (int i = 0; i < providersArray.length(); i++) {
                            parsedUser.providers.add(providersArray.getString(i));
                        }
                    }
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                    return;
                }
                authUser = parsedUser;
                callback.onFinished();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void resetPassword(String email, final OAuthUserCallback callback) {
        String url = _oauth.getOAuthdURL() + "/api/usermanagement/password/reset?k=" + _oauth.getPublicKey();
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("email", email);
            new OAuthJSONRequest().post(url, postdata, new OAuthJSONCallback() {
                @Override
                public void onFinished(JSONObject data) {
                    callback.onFinished();
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }

            });
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    public OAuthUser getIdentity() {
        return authUser;
    }

    public boolean isLogged() {
        return authUser != null && authUser.isLogged();
    }
}
