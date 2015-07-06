package io.oauth.http;

import org.json.JSONObject;

/**
 * Callback when an http json request finishes
 */
public abstract class OAuthJSONCallback {

    /**
     * Called when the request succeeded
     *
     * @param data The json data
     */
    public abstract void onFinished(JSONObject data);

    /**
     * Called when the request fails
     *
     * @param message The error message
     */
    public abstract void onError(String message);

    /**
     * Called when the request fails with attached data
     * This fallback on the method onError.
     *
     * @param message The error message
     * @param data The attached data
     */
    public void onErrorData(String message, JSONObject data) {
        onError(message);
    }
}
