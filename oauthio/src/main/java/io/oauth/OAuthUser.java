package io.oauth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.oauth.http.OAuthJSONCallback;
import io.oauth.http.OAuthJSONRequest;

/**
 * An authenticated user
 */
public class OAuthUser {

    private OAuth _oauth;

    public String token;              /** The user' token */
    public List<String> providers;    /** List of providers linked to this account */
    public Map<String, String> data;  /** The raw user infos */

    public OAuthUser(OAuth o)
    {
        _oauth = o;
        providers = new ArrayList<>();
        data = new Hashtable<>();
    }

    public boolean isLogged() {
        return ! token.isEmpty();
    }

    public void refreshIdentity(final OAuthUserCallback callback) {
        if (token.isEmpty()) {
            callback.onFinished();
            return;
        }

        String url = _oauth.getOAuthdURL() + "/api/usermanagement/user?k=" + _oauth.getPublicKey() + "&token=" + token;

        new OAuthJSONRequest().get(url, new OAuthJSONCallback() {
            @Override
            public void onFinished(JSONObject _data) {
                try {
                    token = _data.getString("token");
                    if(_data.has("user")) {
                        JSONObject userObj = _data.getJSONObject("user");
                        Iterator<String> it = userObj.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            data.put(key, userObj.get(key).toString());
                        }
                    }
                    if(_data.has("providers")) {
                        JSONArray providersArray = _data.getJSONArray("providers");
                        for (int i = 0; i < providersArray.length(); i++) {
                            providers.add(providersArray.getString(i));
                        }
                    }
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                    return;
                }
                callback.onFinished();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void saveIdentity(final OAuthUserCallback callback) {
        String url =  _oauth.getOAuthdURL() + "/api/usermanagement/user?k=" + _oauth.getPublicKey() + "&token=" + token;

        JSONObject postdata = new JSONObject();
        try {
            Iterator it = data.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                postdata.put(key, data.get(key));
            }
        } catch (JSONException e) {
            callback.onError(e.getMessage());
            return;
        }

        new OAuthJSONRequest().get(url, new OAuthJSONCallback() {
            @Override
            public void onFinished(JSONObject data) {
                callback.onFinished();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public boolean hasProvider(String provider) {
        return providers.indexOf(provider) != -1;
    }

    public void getProviders(final OAuthUserCallback callback) {
        String url = _oauth.getOAuthdURL() + "/api/usermanagement/user/providers?k=" + _oauth.getPublicKey() + "&token=" + token;

        new OAuthJSONRequest().get(url, new OAuthJSONCallback() {
            @Override
            public void onFinished(JSONObject data) {
                providers.clear();
                JSONArray items = data.optJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    providers.add(items.optString(i));
                }
                callback.onFinished();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void addProvider(final OAuthData oauth_data, final OAuthUserCallback callback) {
        String url = _oauth.getOAuthdURL() + "/api/usermanagement/user/providers?k=" + _oauth.getPublicKey() + "&token=" + token;

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
        } catch (JSONException e) {
            callback.onError(e.getMessage());
            return;
        }
        new OAuthJSONRequest().post(url, postdata, new OAuthJSONCallback() {
            @Override
            public void onFinished(JSONObject _data) {
                providers.add(oauth_data.provider);
                callback.onFinished();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void removeProvider(final String provider, final OAuthUserCallback callback) {
        String url = _oauth.getOAuthdURL() + "/api/usermanagement/user/providers/" + provider + "?k=" + _oauth.getPublicKey() + "&token=" + token;

        new OAuthJSONRequest().del(url, new OAuthJSONCallback() {
            @Override
            public void onFinished(JSONObject _data) {
                providers.remove(provider); // check if it removes by ref or equality
                callback.onFinished();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void logout(final OAuthUserCallback callback) {
        if (token.isEmpty()) {
            callback.onFinished();
            return;
        }

        String url = _oauth.getOAuthdURL() + "/api/usermanagement/user/logout?k=" + _oauth.getPublicKey() + "&token=" + token;

        new OAuthJSONRequest().post(url, new JSONObject(), new OAuthJSONCallback() {
            @Override
            public void onFinished(JSONObject _data) {
                token = "";
                providers.clear();
                data.clear();
                callback.onFinished();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}
