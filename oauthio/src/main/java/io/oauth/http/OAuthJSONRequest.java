package io.oauth.http;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

/**
 * Call an http service that provides JSON
 */
public class OAuthJSONRequest {

    public final static int HTTP_GET = 1;
    public final static int HTTP_POST = 2;
    public final static int HTTP_PUT = 3;
    public final static int HTTP_DELETE = 4;

    public HttpRequestData httpReq = null;

    public OAuthJSONRequest() {}

    public OAuthJSONRequest(int method, String url) {
        http(method, url, null, null, false, null);
    }

    public OAuthJSONRequest(int method, String url, String body) {
        http(method, url, body, null, false, null);
    }

    public OAuthJSONRequest(int method, String url, String body, Map<String, String> headers) {
        http(method, url, body, headers, false, null);
    }

    public void get(String url, OAuthJSONCallback callback) {
        http(HTTP_GET, url, callback);
    }

    public void del(String url, OAuthJSONCallback callback) {
        http(HTTP_DELETE, url, callback);
    }

    public void post(String url, JSONObject body, OAuthJSONCallback callback) {
        http(HTTP_POST, url, body.toString(), callback);
    }

    public void post(String url, String body, OAuthJSONCallback callback) {
        http(HTTP_POST, url, body, callback);
    }

    public void put(String url, JSONObject body, OAuthJSONCallback callback) {
        http(HTTP_PUT, url, body.toString(), callback);
    }

    public void put(String url, String body, OAuthJSONCallback callback) {
        http(HTTP_PUT, url, body, callback);
    }

    public void http(int method, String url, OAuthJSONCallback callback) {
        http(method, url, null, null, true, callback);
    }

    public void http(int method, String url, String body, OAuthJSONCallback callback) {
        http(method, url, body, null, true, callback);
    }

    public void http(int method, String url, String body, Map<String, String> headers,
                     OAuthJSONCallback callback) {
        http(method, url, body, headers, true, callback);
    }

    public void http(int method, String url, String body, Map<String, String> headers,
                     Boolean parsePayload, OAuthJSONCallback callback) {
        HttpRequestData req = new HttpRequestData();
        req.url = url;
        req.method = method;
        req.body = body;
        req.headers = headers;
        req.parsePayload = parsePayload;
        req.callback = callback;
        if (callback != null)
            new JSONRequest().execute(req);
        else
            httpReq = req;
    }

    public void execute(OAuthJSONCallback callback) {
        if (httpReq != null) {
            httpReq.callback = callback;
            new JSONRequest().execute(httpReq);
        }
    }

    public class HttpRequestData {
        public String url;
        public int method;
        public String body;
        public Map<String, String> headers;
        public Boolean parsePayload = true;
        public OAuthJSONCallback callback;
    }
    private class JSONRequest extends AsyncTask<HttpRequestData, Void, Boolean> {

        private String errorMessage;
        private JSONObject resultObj;
        private HttpRequestData param;

        @Override
        protected Boolean doInBackground(HttpRequestData... params) {
            param = params[0];
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            InputStream inputStream = null;
            String result = null;

            try {
                HttpUriRequest req = null;
                //param.url = "http://httpbin.org/post";
                if (param.method == HTTP_GET) {
                    req = new HttpGet(param.url);
                }
                else if (param.method == HTTP_DELETE) {
                    req = new HttpDelete(param.url);
                }
                else if (param.method == HTTP_POST) {
                    HttpPost httppost = new HttpPost(param.url);
                    httppost.setHeader("Content-Type", "application/json");
                    httppost.setEntity(new StringEntity(param.body));
                    req = httppost;
                }
                else if (param.method == HTTP_PUT) {
                    HttpPut httpput = new HttpPut(param.url);
                    httpput.setHeader("Content-Type", "application/json");
                    httpput.setEntity(new StringEntity(param.body));
                    req = httpput;
                }
                else
                    throw new Exception("Unknown http method");
                req.setHeader("Accept", "application/json");
                req.setHeader("Origin", "http://localhost");
                req.setHeader("User-Agent", "OAuthio-android/1.0");

                if (param.headers != null)
                    for (Map.Entry<String, String> entry : param.headers.entrySet())
                        req.setHeader(entry.getKey(), entry.getValue());

                HttpResponse response = httpclient.execute(req);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();

                JSONObject jsondata = new JSONObject(result);

                if (param.parsePayload) {
                    resultObj = jsondata.optJSONObject("data");

                    if (resultObj == null) {
                        resultObj = new JSONObject();
                        JSONArray arr = jsondata.optJSONArray("data");
                        if (arr != null)
                            resultObj.put("items", arr);
                    }

                    if (jsondata.has("status") && jsondata.getString("status").equals("error")) {
                        if (jsondata.has("message"))
                            errorMessage = jsondata.optString("message");
                        else if (jsondata.has("data")) {
                            JSONObject dataObj = jsondata.getJSONObject("data");
                            Iterator<?> keys = dataObj.keys();
                            String key = null;
                            errorMessage = "";
                            while (keys.hasNext()) {
                                if (key != null)
                                    errorMessage += ", ";
                                key = (String) keys.next();
                                errorMessage += dataObj.optString(key) + " " + key;
                            }
                            if (errorMessage.length() > 0)
                                errorMessage = Character.toString(errorMessage.charAt(0)).toUpperCase() + errorMessage.substring(1);
                        }
                        return false;
                    }
                }
                else
                    resultObj = jsondata;
                return true;
            } catch (Exception e) {
                errorMessage = e.getMessage();
                return false;
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                }
            }
        }

        protected void onPostExecute(Boolean succeeded) {
            if (succeeded)
                param.callback.onFinished(resultObj);
            else
                param.callback.onErrorData(errorMessage, resultObj);
        }
    }
}
