package com.example.android.photouploader;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Images implements Serializable {

    private String mImageFilePath;
    private String mImageName;

    public Images(String imageFilePath, String imageName){
        mImageFilePath = imageFilePath;
        mImageName = imageName;
    }

    public String getImageFilePath(){
        return mImageFilePath;
    }

    public String getImageName(){
        return mImageName;
    }

}
