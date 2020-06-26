package com.joesoft.books;

import androidx.annotation.NonNull;
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
import java.util.Objects;

public class BooksListActivity extends AppCompatActivity {

    public static final String ADVANCED_QUERY = "com.joesoft.books.ADVANCED_QUERY";
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
            initialContentExtendsAdvanceSearching();



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

    private void initialContentExtendsAdvanceSearching() {
        final Bundle extras = getIntent().getExtras();
        final String query = (extras==null ? null : extras.getString(ADVANCED_QUERY));

        try {
            if (query==null || query.isEmpty()){
                mBooksURL = ApiUtil.buildUrl("programming");
            } else {
                mBooksURL = new URL(query);
            }

            new BooksQueryTask().execute(mBooksURL);
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
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

                final ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
                mRecyclerBooks.setLayoutManager(mBooksLayoutManager);

                BooksRecyclerAdapter booksRecyclerAdapter = new BooksRecyclerAdapter(books);
                mRecyclerBooks.setAdapter(booksRecyclerAdapter);

            }


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


        // Retrieving recent advanced query searches
        final ArrayList<String> recentList = SPUtil.getQueryList(getApplicationContext());
        int noOfItems = recentList.size();
        MenuItem recentMenu;
        for (int i=0; i<noOfItems; i++) {
            recentMenu = menu.add(Menu.NONE, i, Menu.NONE, recentList.get(i));
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_advanced_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                int position = item.getItemId() + 1;
                String preferenceName = SPUtil.QUERY + String.valueOf(position);
                String query = SPUtil.getPreferencesString(getApplicationContext(), preferenceName);
                String[] prefParams = query.split("\\,");
                String[] queryParams = new String[4];
                for (int i=0; i<prefParams.length; i++) {
                    queryParams[i] = prefParams[i];
                }
                URL bookUrl = ApiUtil.buildUrl(
                        queryParams[0]==null ? "" : queryParams[0],
                        queryParams[1]==null ? "" : queryParams[1],
                        queryParams[2]==null ? "" : queryParams[2],
                        queryParams[3]==null ? "" : queryParams[3]

                );

                new BooksQueryTask().execute(bookUrl);
        }

        return super.onOptionsItemSelected(item);
    }
}
