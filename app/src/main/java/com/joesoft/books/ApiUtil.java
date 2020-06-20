package com.joesoft.books;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class ApiUtil {
    private ApiUtil() { }

    public static final String BASE_API_URL = "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER_KEY = "q";
    public static final String KEY = "key";
    public static final String API_KEY = "AIzaSyA-qNDnZh2MLjfOy20pkd5dI4OIlN-3eUE";



    public static URL buildUrl(String title) {
//        String fullUrl = BASE_API_URL + "?q=" + title;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY, API_KEY)
                .build();

        URL url = null;


        try {
//            url = new URL(fullUrl);
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getJSON(URL url) throws IOException {
        // establish the connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // read the data
             InputStream stream = connection.getInputStream();
            // convert stream to string
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A"); /* -> \\A is regExp that reads all */
            // check if we have data
            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return null;
        } finally {
            connection.disconnect();
        }
    }

}
