package com.joesoft.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class BooksListActivity extends AppCompatActivity {

    private static final String TAG = BooksListActivity.class.getSimpleName();
    private ProgressBar mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);

        mLoadingProgress = findViewById(R.id.pb_loading);

        URL booksURL = ApiUtil.buildUrl("cooking");
        new BooksQueryTask().execute(booksURL);
    }

    public class BooksQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;

            try {
                result = ApiUtil.getJSON(searchURL);
            } catch (IOException e) {
                Log.e(TAG,  e.getMessage());
            }
            return result;
        }

        // called when the doInBackground has finished executing
        @Override
        protected void onPostExecute(String result) {
            TextView tvResult = findViewById(R.id.tv_response);
            TextView tvError = findViewById(R.id.tv_error);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if(result == null) {
                tvResult.setVisibility(View.INVISIBLE) ;
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvResult.setVisibility(View.VISIBLE) ;
                tvError.setVisibility(View.INVISIBLE);
            }

            tvResult.setText(result);
        }

        // called before
        @Override
        protected void onPreExecute() {
            mLoadingProgress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
    }
}
