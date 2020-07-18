package com.example.android.locationApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
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
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlacesUtils{
    public static final String LOG_TAG = PlacesUtils.class.getName();
    private static Context mContext;

    private static final int connectionTimeout = 1500;
    private static final int readTimeout = 1000;

    public PlacesUtils(Context context){
        mContext = context;
    }

    public static List<Places> getPlacesList(String stringUrl){
        String jsonResponse = null;

        URL url = createUrl(stringUrl);

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, mContext.getString(R.string.get_places_list_error), e);
        }

        List<Places> news = extractPlaces(jsonResponse);
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

    private static List<Places> extractPlaces(String jsonNews){

        if(TextUtils.isEmpty(jsonNews)){
            return null;
        }

        List<Places> places = new ArrayList<Places>();
        Places currentPlaces;

        try {
            JSONObject root = new JSONObject(jsonNews);
            JSONObject response = root.getJSONObject("response");
            JSONArray venues = response.getJSONArray("venues");
            for (int i=0; i<venues.length(); i++){
                JSONObject object = venues.getJSONObject(i);

                String id = object.getString("id");

                // get venue's photo
                /*SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String currentDate = sdf.format(new Date());
                String urlString  = " https://api.foursquare.com/v2/venues/" + id + "/photos/?client_id=0V2FHHHQAVYUUUTR4BRTKUJ1FRUUFSUV2UBELCDVUDF4UAH1&client_secret=CGYBBJ1XVIOAVQHPXHMOJ3SSE0NVD2PA4AO2OGQSKPZIN3IO&v=" + currentDate;
                Log.v("here", "url: " + urlString);
                URL urlPhoto = createUrl(urlString);
                Log.v("here", "urlPhoto: " + urlPhoto);
                String photoString;

                try {
                    photoString = makeHttpRequest(urlPhoto);
                    Log.v("here", "json:" + photoString);
                } catch (IOException e) {
                    Log.e(LOG_TAG, mContext.getString(R.string.get_places_list_error), e);
                }*/

                String name = object.getString("name");

                JSONObject location = object.getJSONObject("location");
                Double lat = location.getDouble("lat");
                Double lng = location.getDouble("lng");

                String prefix = "";
                String suffix = "";
                Bitmap bitmap = null;
                try {
                    JSONArray categories = object.getJSONArray("categories");
                    JSONObject object1 = categories.getJSONObject(0);
                    JSONObject object11 = object1.getJSONObject("icon");
                    prefix = object11.getString("prefix");
                    suffix = object11.getString("suffix");
                }catch (Exception e){
                    Log.v("here", "Exception handling: ", e);
                }


                URL url = null ;
                try {
                    url = new URL(prefix + "bg_88" + suffix);
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                currentPlaces = new Places(lat, lng, name, bitmap, id);
                places.add(currentPlaces);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, mContext.getString(R.string.parsiong_json_error), e);
        }

        return places;
    }
}
