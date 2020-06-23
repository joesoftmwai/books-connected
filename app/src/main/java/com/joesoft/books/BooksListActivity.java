package com.joesoft.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BooksListActivity extends AppCompatActivity {

    private static final String TAG = BooksListActivity.class.getSimpleName();
    private RecyclerView mRecyclerBooks;
    private LinearLayoutManager mBooksLayoutManager = new LinearLayoutManager(this);
    private ProgressBar mLoadingProgress;
    private URL mBooksURL;
    private boolean mIsSearching;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);

        mRecyclerBooks = findViewById(R.id.recycler_books);
        mLoadingProgress = findViewById(R.id.pb_loading);

        handleSearchIntent(getIntent());

        if (!mIsSearching)
            mBooksURL = ApiUtil.buildUrl("android");
            new BooksQueryTask().execute(mBooksURL);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSearchIntent(intent);
    }

    private void handleSearchIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mIsSearching = true;
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            try {
                mBooksURL = ApiUtil.buildUrl(query);
                new BooksListActivity.BooksQueryTask().execute(mBooksURL);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            Log.d(TAG, "handleSearchIntent: " + query);
        }
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
            TextView textError = findViewById(R.id.tv_error);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if(result == null) {
                mRecyclerBooks.setVisibility(View.INVISIBLE) ;
                textError.setVisibility(View.VISIBLE);
            } else {
                mRecyclerBooks.setVisibility(View.VISIBLE) ;
                textError.setVisibility(View.INVISIBLE);
            }

            final ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
            mRecyclerBooks.setLayoutManager(mBooksLayoutManager);

            BooksRecyclerAdapter booksRecyclerAdapter = new BooksRecyclerAdapter(books);
            mRecyclerBooks.setAdapter(booksRecyclerAdapter);

        }

        // called before
        @Override
        protected void onPreExecute() {
            mLoadingProgress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;

    }




}
