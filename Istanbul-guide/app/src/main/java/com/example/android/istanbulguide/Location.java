package com.example.android.istanbulguide;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Location implements Parcelable {

    private String mLocationName;
    private String mInformation;
    private int mImageOfLocation;


    public Location(String locationName, String information, int imageOFLocation){
        mLocationName = locationName;
        mInformation = information;
        mImageOfLocation = imageOFLocation;
    }

    protected Location(Parcel in) {
        mLocationName = in.readString();
        mInformation = in.readString();
        mImageOfLocation = in.readInt();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getmLocationName() {
        return mLocationName;
    }

    public String getmInformation() {
        return mInformation;
    }

    public int getmImageOfLocation() {
        return mImageOfLocation;
    }

    @Override
    public String toString() {
        return "Location{" +
                "mLocationName='" + mLocationName + '\'' +
                ", mInformation='" + mInformation + '\'' +
                ", mImageOfLocation=" + mImageOfLocation +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLocationName);
        dest.writeString(mInformation);
        dest.writeInt(mImageOfLocation);
    }
}
