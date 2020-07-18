package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String URL_LOCATION = BuildConfig.ApiKey;
    private NewsAdapter mAdapter;
    private ScrollView mEmptyStateScrollView;
    private TextView mEmptyStateTextView;
    private TextView mEmptyStateTextViewForConnection;
    private TextView mEmptyStateTextViewForJson;
    private ImageView mEmptyStateImageView;
    private ProgressBar mProgressBar;

    private static final int NEWS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.loading_spinner);
        mProgressBar.setVisibility(View.VISIBLE);

        ListView listView = findViewById(R.id.list);

        mEmptyStateScrollView = findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateScrollView);
        mEmptyStateTextView = findViewById(R.id.empty_text_view);
        mEmptyStateTextViewForConnection = findViewById(R.id.check_connection);
        mEmptyStateTextViewForJson = findViewById(R.id.check_json);
        mEmptyStateImageView = findViewById(R.id.image_view);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentNews = mAdapter.getItem(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNews.getmNewsWebUrl()));
                startActivity(browserIntent);
            }
        });

        boolean isConnected = checkNetworkConnection();
        if(isConnected) {
            //start background thread.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mEmptyStateScrollView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.emptyViewBackground));
            mEmptyStateTextView.setText(R.string.no_internet);
            mEmptyStateImageView.setImageResource(R.drawable.connection_gone);
            mEmptyStateTextViewForConnection.setText(R.string.check_connection);
            mEmptyStateTextViewForJson.setText(R.string.check_json_api);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.v("here", "onCreateLoader");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String pageSize = sharedPreferences.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default)
        );

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String fromDate = sharedPreferences.getString(
                getString(R.string.settings_from_date_key),
                getString(R.string.settings_from_date_default)
        );

        Uri uri = Uri.parse(URL_LOCATION);
        Uri.Builder builder = uri.buildUpon();

        builder.appendQueryParameter("order-by", orderBy);
        builder.appendQueryParameter("from-date", fromDate);
        builder.appendQueryParameter("page-size", pageSize);

        return new NewsLoader(getBaseContext(), builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        Log.v("here", "onLoadFinised");
        boolean isConnected = checkNetworkConnection();

        if(isConnected) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mEmptyStateScrollView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.emptyViewBackground));
            mEmptyStateTextView.setText(R.string.no_news);
            mEmptyStateImageView.setImageResource(R.drawable.connection_gone);
            mEmptyStateTextViewForConnection.setText(R.string.check_connection);
            mEmptyStateTextViewForJson.setText(R.string.check_json_api);

            mAdapter.clear();
            if (news != null && !news.isEmpty()) {
                mAdapter.addAll(news);
            }
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mEmptyStateScrollView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.emptyViewBackground));
            mEmptyStateTextView.setText(R.string.no_internet);
            mEmptyStateImageView.setImageResource(R.drawable.connection_gone);
            mEmptyStateTextViewForConnection.setText(R.string.check_connection);
            mEmptyStateTextViewForJson.setText(R.string.check_json_api);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.v("here", "OnLoaderReset");
        mAdapter.clear();
    }

    private boolean checkNetworkConnection(){
        ConnectivityManager cm =
                (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        if(id == R.id.action_section){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
