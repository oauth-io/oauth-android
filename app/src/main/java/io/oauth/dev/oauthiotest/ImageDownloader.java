package io.oauth.dev.oauthiotest;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloader {
    private ImageView iv;
    private String _url;

    public ImageDownloader(ImageView iview, String url) {
        iv = iview;
        _url = url;

        new ImageDownload().execute();
    }

    public class ImageDownload extends AsyncTask<Void, Void, Bitmap> {

        protected Bitmap doInBackground(Void... params) {
            try {
                InputStream inputStream = null;
                URL url = new URL(_url);
                URLConnection conn = url.openConnection();

                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpConn.getInputStream();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inSampleSize = 1;
                    Bitmap bm = BitmapFactory.decodeStream(inputStream, null, bmOptions);
                    inputStream.close();
                    return bm;
                }
            } catch (Exception e1) {
                System.err.println("exception while loading avatar: " + e1.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Bitmap bm) {
            if (bm != null)
                iv.setImageBitmap(bm);
        }

    }
}
