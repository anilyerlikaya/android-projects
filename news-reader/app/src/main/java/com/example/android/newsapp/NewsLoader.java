package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;
    private Context mContext;

    public NewsLoader(Context context, String url) {
        super(context);

        mContext = context;
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if(mUrl == null){
            return null;
        }

        NewsUtils newsUtils = new NewsUtils(mContext);
        List<News> news = newsUtils.getNewsList(mUrl);
        return news;
    }
}
