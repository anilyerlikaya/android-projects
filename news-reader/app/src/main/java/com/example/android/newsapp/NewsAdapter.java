package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        News news = getItem(position);

        ImageView photoOfNews = convertView.findViewById(R.id.news_photo);
        if(news.getmImageUrl() == ""){
            int locationOfImage = getImage(news.getmCategory());
            photoOfNews.setImageResource(locationOfImage);
        }else{
            Picasso.get().load(news.getmImageUrl()).into(photoOfNews);
        }

        TextView authorOfNews = convertView.findViewById(R.id.news_author);
        authorOfNews.setText(news.getmAuthor());

        TextView titleOfNews = convertView.findViewById(R.id.news_title);
        titleOfNews.setText(news.getmTitle());

        //split date and time from oldDate
        String oldDate = news.getmDate();
        String[] parts = oldDate.split("T");
        String newDate = parts[0];
        String[] again = parts[1].split("Z");
        String newTime = again[0];

        TextView sectionOfNews = convertView.findViewById(R.id.news_section);
        sectionOfNews.setText(news.getmSection());

        TextView dateOfNews = convertView.findViewById(R.id.news_date);
        dateOfNews.setText(newDate);

        TextView timeOfNews = convertView.findViewById(R.id.news_time);
        timeOfNews.setText(newTime);

        return convertView;
    }


    private int getImage(String category){
        int locationOfImage;

        switch (category){
            case "Arts":
                locationOfImage = R.drawable.art;
                break;
            case "Sport":
                locationOfImage = R.drawable.sports;
                break;
            case "Lifestyle":
                locationOfImage = R.drawable.lifesytle;
                break;
            case "Opinion":
                locationOfImage = R.drawable.opinion;
                break;
            case "News":
            default:
                locationOfImage = R.drawable.news;
                break;
        }

        return locationOfImage;
    }
}
