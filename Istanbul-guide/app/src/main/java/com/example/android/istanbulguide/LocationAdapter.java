package com.example.android.istanbulguide;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationAdapter extends ArrayAdapter<Location> {

    public LocationAdapter(Activity context, ArrayList<Location> locations){
        super(context, 0 , locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.location_item, parent, false);
        }

        Location currentLocation = getItem(position);

        ImageView locationImage = (ImageView) convertView.findViewById(R.id.location_image);
        locationImage.setImageResource(currentLocation.getmImageOfLocation());

        TextView locationInfo = (TextView) convertView.findViewById(R.id.location_name);
        locationInfo.setText(currentLocation.getmLocationName());

        return convertView;
    }

}
