package com.example.android.newsapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NewsUtils {
    public static final String LOG_TAG = NewsUtils.class.getName();

    private static Context mContext;

    private static final int connectionTimeout = 1500;
    private static final int readTimeout = 1000;

    public NewsUtils(Context context){
        mContext = context;
    }

    public static List<News> getNewsList(String stringUrl){
        String jsonResponse = null;

        URL url = createUrl(stringUrl);

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, mContext.getString(R.string.get_news_list_error), e);
        }

        List<News> news = extractNews(jsonResponse);
        return news;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, mContext.getString(R.string.create_url_error), e);
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        if(url == null){
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.setConnectTimeout(connectionTimeout);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, mContext.getString(R.string.error_code) + urlConnection.getResponseCode());
            }
        }catch (IOException exception){
            Log.e(LOG_TAG, mContext.getString(R.string.retrieving_json_error), exception);
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (inputStream != null){
            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(streamReader);

            String line = reader.readLine();
            while(line != null){
                stringBuilder.append(line);
                line = reader.readLine();
            }
        }

        return stringBuilder.toString();
    }

    private static List<News> extractNews(String jsonNews){

        if(TextUtils.isEmpty(jsonNews)){
            return null;
        }

        List<News> news = new ArrayList<News>();
        News currentNews;

        try {
            JSONObject root = new JSONObject(jsonNews);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i=0; i<results.length(); i++){
                JSONObject object = results.getJSONObject(i);


                String category = "";
                if(object.has("pillarName")) {
                    category = object.getString("pillarName");
                }
                String section = object.getString("sectionName");
                String webUrl = object.getString("webUrl");
                String title = object.getString("webTitle");
                String date = object.getString("webPublicationDate");

                String author = "";
                JSONArray tags = object.getJSONArray("tags");
                //control for tags array has author
                if(tags.length() > 0){
                    JSONObject tagObject = tags.getJSONObject(0);
                    author = tagObject.getString("webTitle");
                }

                String imageUrl = "";
                if(object.has("fields")) {
                    JSONObject fields = object.getJSONObject("fields");
                    imageUrl = fields.getString("thumbnail");
                }

                currentNews = new News(webUrl,imageUrl, section, category, title, author, date);
                news.add(currentNews);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, mContext.getString(R.string.parsiong_json_error), e);
        }

        return news;
    }

}
