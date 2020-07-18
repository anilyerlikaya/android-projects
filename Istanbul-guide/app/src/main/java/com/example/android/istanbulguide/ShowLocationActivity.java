package com.example.android.istanbulguide;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class ShowLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Location location = (Location) intent.getParcelableExtra("location");

        getSupportActionBar().setTitle(location.getmLocationName());

        ImageView locationImage = (ImageView) findViewById(R.id.location_image);
        locationImage.setImageResource(location.getmImageOfLocation());

        TextView locationInfo = (TextView) findViewById(R.id.location_info);
        locationInfo.setText(location.getmInformation());
    }
}
