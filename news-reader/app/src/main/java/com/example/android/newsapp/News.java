package com.example.android.newsapp;

public class News {

    //Web Url of news
    private String mNewsWebUrl;

    //Section of news
    private String mSection;

    //Category of news
    private String mCategory;

    //Title of news
    private String mTitle;

    //Image of news
    private String mImageUrl;

    //Author of news
    private String mAuthor;

    //Publishing date of news
    private String mDate;

    public News(String newsWebUrl, String imageUrl, String section, String category, String title, String author, String date){
        mNewsWebUrl = newsWebUrl;
        mImageUrl = imageUrl;
        mCategory = category;
        mSection = section;
        mTitle = title;
        mAuthor = author;
        mDate = date;
    }

    public String getmNewsWebUrl() {
        return mNewsWebUrl;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmSection() {
        return mSection;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    @Override
    public String toString() {
        return "News{" +
                "mNewsWebUrl='" + mNewsWebUrl + '\'' +
                ", mSection='" + mSection + '\'' +
                ", mCategory='" + mCategory + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mDate='" + mDate + '\'' +
                '}';
    }
}
