package com.joesoft.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
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

    public static ArrayList<Book> getBooksFromJson(String json) {
        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";
        final String DESCRIPTION = "description";
        final String IMAGE_LINKS = "imageLinks";
        final String THUMBNAIL = "thumbnail";


        ArrayList<Book> books = new ArrayList<>();
        try {
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int noOfBooks = arrayBooks.length();
            for (int i = 0; i < noOfBooks; i++) {
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON = bookJSON.getJSONObject(VOLUME_INFO);
                JSONObject imageLinksJSON = volumeInfoJSON.getJSONObject(IMAGE_LINKS);

                int noOfAuthors = volumeInfoJSON.getJSONArray(AUTHORS).length();
                String[] authors = new String[noOfAuthors];
                for (int j = 0; j < noOfAuthors; j++) {
                    authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                }

                Book book = new Book(
                        bookJSON.getString(ID),
                        volumeInfoJSON.getString(TITLE),
                        (volumeInfoJSON.isNull(SUBTITLE) ? "" : volumeInfoJSON.getString(SUBTITLE)),
                        authors,
                        volumeInfoJSON.getString(PUBLISHER),
                        volumeInfoJSON.getString(PUBLISHED_DATE),
                        volumeInfoJSON.getString(DESCRIPTION),
                        imageLinksJSON.getString(THUMBNAIL)
                );

                books.add(book);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

}
