package com.example.basketballapp.InfoActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.basketballapp.Classes.Label;
import com.example.basketballapp.R;
import com.example.basketballapp.ViewAdapters.LabelViewAdapter;
import com.example.basketballapp.ViewAdapters.PlayerViewAdapter;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LabelActivity extends AppCompatActivity {
    private String TAG = "From LabelActivity: ";

    String labelKey;
    List<Label> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        String keyNumber = getIntent().getStringExtra("number");
        labelKey = "label" + keyNumber;

        labels = getSavedListObjectFromPreference(getBaseContext(), "mPreference", labelKey, Label.class);
        Log.d(TAG, "label size is "+ labels.size());

        TextView header = findViewById(R.id.label_header);
        header.setText(("Label " + keyNumber));

        ListView listView = findViewById(R.id.label_list_view);
        LabelViewAdapter labelViewAdapter = new LabelViewAdapter(LabelActivity.this, labels);
        listView.setAdapter(labelViewAdapter);

        Button exportButton = findViewById(R.id.export_button);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = getBaseContext().getExternalCacheDir();
                File file = new File(path, "output.txt");

                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    stream.write("Player Name, Action, Point, PositionX, PositionY, Minute, Second, Quarter\n".getBytes());

                    for(int i=0; i<labels.size(); i++){
                        Label label = labels.get(i);
                        String string = label.getPlayerName() + ", " + label.getAction() + ", " + label.getPoint() + ", " +
                                label.getImageX() + ", " + label.getImageY() + ", " + label.getMinute() + ", " +
                                label.getSecond() + ", " + label.getQuarter()+ "\n";
                        stream.write(string.getBytes());
                    }

                    stream.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "error1 is " + e);
                } catch (IOException e) {
                    Log.d(TAG, "error2 is " + e);
                }

                Uri pathOfFile = Uri.fromFile(file);
                Log.d(TAG, "pathOfFile is " + pathOfFile);

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                //emailIntent.putExtra(Intent.EXTRA_EMAIL, "dummymail2403@gmail.com");
                emailIntent.putExtra(Intent.EXTRA_STREAM, pathOfFile);
                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "From Basketball App: " + labelKey);
                emailIntent.setType("text/plain");
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
            }
        });

    }

    private static List<Label> getSavedListObjectFromPreference(Context context, String preferenceFileName,
                                                                String preferenceKey, Class<Label> classType){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        if(sharedPreferences.contains(preferenceKey)) {
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString(preferenceKey, "");
            return new ArrayList<Label>((Arrays.asList(gson.fromJson(jsonText, Label[].class))));
        }

        return null;
    }
}