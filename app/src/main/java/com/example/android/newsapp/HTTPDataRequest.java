package com.example.android.newsapp;

/**
 * Created by aspun_000 on 8/25/2016.
 */

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by aspun_000 on 8/25/2016.
 */
public class HTTPDataRequest {

    static String stream = null;

    /* Empty constructor since no data needs to be held in the class */
    public HTTPDataRequest() {
    }

    public String getHTTPData(String urlString) {
        try {
            URL url = new URL(urlString);
            Log.i("getHTTPData", urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Check the connection status
            if (urlConnection.getResponseCode() == 200) {
                // if response code = 200 ok
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Read the BufferedInputStream
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }
                stream = sb.toString();
                // End reading...............

                // Disconnect the HttpURLConnection
                urlConnection.disconnect();
            } else {
                Log.e("HttPDataRequest", String.valueOf(urlConnection.getResponseCode()));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        // Return the data from specified url
        return stream;
    }
}
