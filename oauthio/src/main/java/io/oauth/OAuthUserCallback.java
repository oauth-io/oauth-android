package io.oauth;

/**
 * The onFinished is called when a user method is finished.
 */
public interface OAuthUserCallback {

    /**
     * Called when a user method got a result.
     */
    public abstract void onFinished();

    /**
     * Called when the method fails
     *
     * @param message The error message
     */
    public abstract void onError(String message);
}
