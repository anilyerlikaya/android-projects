package com.example.basketballapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.basketballapp.Classes.Label;
import com.example.basketballapp.Classes.League;
import com.example.basketballapp.Classes.Match;
import com.example.basketballapp.Classes.PlayByPlay;
import com.example.basketballapp.Classes.Team;
import com.example.basketballapp.InfoActivities.LabelActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamesActivity extends AppCompatActivity {
    private String TAG = "From GamesActivity: ";

    League league;
    Match[] match;
    PlayByPlay[] plays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        league = (League) getIntent().getSerializableExtra("league");

        match = new Match[2];
        deleteSavedObjectFromPreference(getBaseContext(), "mPreference", "playOne");
        deleteSavedObjectFromPreference(getBaseContext(), "mPreference", "playTwo");

        setMatch1();
        setMatch2();

        updateVisuality();

        //set plays
        plays = new PlayByPlay[2];
        setPlay1();
        setPlay2();

        LinearLayout match1 = findViewById(R.id.match1);
        match1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("From GamesActivity:", "click on match1");

                Intent intent = new Intent(GamesActivity.this, MatchActivity.class);
                intent.putExtra("number", "One");
                startActivity(intent);
            }
        });

        LinearLayout match2 = findViewById(R.id.match2);
        match2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("From GamesActivity:", "click on match2");

                Intent intent = new Intent(GamesActivity.this, MatchActivity.class);
                intent.putExtra("number", "Two");
                startActivity(intent);
            }
        });

        Button showLabel1 = findViewById(R.id.show_label_one_button);
        showLabel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click on show label one button");

                List<Label> label = getSavedListObjectFromPreference(getBaseContext(), "mPreference",
                        "labelOne", Label.class);

                boolean isLabelEmpty = true;
                if(label != null){
                    if(label.size() > 0)
                        isLabelEmpty = false;
                }

                if(!isLabelEmpty){
                    Intent intent = new Intent(GamesActivity.this, LabelActivity.class);
                    intent.putExtra("number", "One");
                    startActivity(intent);
                }else
                    Toast.makeText(getBaseContext(), "Label 1 is empty!", Toast.LENGTH_SHORT).show();
            }
        });

        Button showLabel2 = findViewById(R.id.show_label_two_button);
        showLabel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click on show label two button");

                List<Label> label = getSavedListObjectFromPreference(getBaseContext(), "mPreference",
                        "labelTwo", Label.class);

                boolean isLabelEmpty = true;
                if(label != null){
                    if(label.size() > 0)
                        isLabelEmpty = false;
                }

                if(!isLabelEmpty){
                    Intent intent = new Intent(GamesActivity.this, LabelActivity.class);
                    intent.putExtra("number", "Two");
                    startActivity(intent);
                }else
                    Toast.makeText(getBaseContext(), "Label 2 is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        //set again for deletion
        setMatch1();
        setMatch2();
        setPlay1();
        setPlay2();

        updateVisuality();
    }

    private void updateVisuality(){
        //match1
        TextView team1 = findViewById(R.id.games_team1);
        team1.setText(match[0].getHomeTeam());

        TextView scoreMatch1 = findViewById(R.id.scores_match1);
        scoreMatch1.setText((String.valueOf(match[0].getHomeTeamScore()) + " - " + String.valueOf(match[0].getAwayTeamScore())));

        TextView team2 = findViewById(R.id.games_team2);
        team2.setText(match[0].getAwayTeam());

        //match2
        TextView team3 = findViewById(R.id.games_team3);
        team3.setText(match[1].getHomeTeam());

        TextView scoreMatch2 = findViewById(R.id.scores_match2);
        scoreMatch2.setText((String.valueOf(match[1].getHomeTeamScore()) + " - " + String.valueOf(match[1].getAwayTeamScore())));

        TextView team4 = findViewById(R.id.games_team4);
        team4.setText(match[1].getAwayTeam());

    }

    private void setMatch1(){
        match[0] = getSavedObjectFromPreference(getBaseContext(), "mPreference", "matchOne",
                Match.class);
        Log.d("From GamesActivity: ", "match 1 is " + match[0]);

        if(match[0] == null) {
            Team homeTeam = league.getTeams()[0];
            Team awayTeam = league.getTeams()[1];

            int[] array = new int[15];

            for (int i = 0; i < 15; i++) {
                array[i] = 0;
            }

            match[0] = new Match(homeTeam.getTeamName(), awayTeam.getTeamName(), homeTeam.getArena(), homeTeam.getPlayers(),
                    awayTeam.getPlayers(), array, array, array, array, array, array, 0, 0,
                    12, 0, 1);

            //Save match
            saveObjectToSharedPreference(getBaseContext(), "mPreference", "matchOne", match[0]);
        }
    }

    private void setPlay1(){
        plays[0] = getSavedObjectFromPreference(getBaseContext(), "mPreference", "playOne",
                PlayByPlay.class);
        Log.d("From GamesActivity: ", "play 1 is " + plays[0]);

        if(plays[0] == null){

            //only first quarter - array lengths are 48
            String[] playerNames = {"Avery Bradley", "Avery Bradley", "Fred VanVleet", "JaVale McGee", "Marc Gasol",
                "Anthony Davis", "Marc Gasol", "Avery Bradley", "Fred VanVleet", "Avery Bradley", "LeBron James",
                    "JaVale McGee", "LeBron James", "Norman Powell", "Anthony Davis", "Danny Green", "LeBron James",
                "Norman Powell", "Norman Powell", "JaVale McGee", "JaVale McGee", "Norman Powell", "Avery Bradley",
                "Pascal Siakam", "Anthony Davis", "Fred VanVleet", "LeBron James", "LeBron James", "Anthony Davis",
                "Anthony Davis", "LeBron James", "Kyle Kuzma", "Terence Davis", "Chris Boucher", "Terence Davis",
                "Kentavious Caldwell-Pope", "Chris Boucher", "Pascal Siakam", "Pascal Siakam", "Pascal Siakam",
                "Pascal Siakam", "Alex Caruso", "Quinn Cook", "Kyle Kuzma", "Anthony Davis", "Kyle Kuzma",
                "Shamorie Ponds", "Shamorie Ponds"};
            String[] assistPlayerNames = {null, "LeBron James", null, null, null, null, null, "Anthony Davis", null,
                    "LeBron James", null, null, null, "Fred VanVleet", "LeBron James", null, "Anthony Davis",
                null, null, null, null, "Marc Gasol", "LeBron James", null, "LeBron James", null, null, null,
                null, null, null, "Quinn Cook", "Pascal Siakam", null, null, null, null, null, null, null, null,
                null, "Alex Caruso", null, "Quinn Cook", null, "Chris Boucher", "Fred VanVleet"};

            int[] playerTeam = {1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2,
                1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2};
            int[] action = {2, 1, 2, 2, 1, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2,
                2, 1, 2, 1, 1, 1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 1};
            int[] pointValue = {0, 3, 0, 0, 2, 0, 0, 2, 1, 2, 1, 2, 0, 2, 2, 0, 2, 1, 1, 0, 2, 3, 2, 2, 2, 3, 2,
                0, 0, 2, 0, 2, 3, 2, 0, 0, 0, 2, 1, 0, 1, 0, 2, 0, 2, 0, 3, 2};

            int[] timeMinute = {11, 11, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 5, 5, 5, 5, 5, 5, 4, 4, 4,
                4, 4, 3, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0};
            int[] timeSecond = {30, 24, 38, 25, 54, 28, 11, 2, 52, 43, 25, 51, 31, 8, 45, 28, 13, 57, 56, 40, 39,
                26, 15, 55, 47, 35, 23, 4, 52, 51, 39, 33, 18, 39, 24, 14, 2, 55, 54, 42, 33, 31, 4, 47, 36, 24, 4, 1};
            int[] quarter = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

            plays[0] = new PlayByPlay(playerNames, assistPlayerNames, playerTeam, action, pointValue, timeMinute,
                    timeSecond, quarter);

            //Save play
            saveObjectToSharedPreference(getBaseContext(), "mPreference", "playOne",
                    plays[0]);
        }
    }

    private void setMatch2(){
        match[1] = getSavedObjectFromPreference(getBaseContext(), "mPreference", "matchTwo",
                Match.class);
        Log.d("From GamesActivity: ", "match 2 is " + match[1]);

        if(match[1] == null) {
            Team homeTeam = league.getTeams()[2];
            Team awayTeam = league.getTeams()[3];

            int[] array = new int[15];

            for (int i = 0; i < 15; i++) {
                array[i] = 0;
            }

            match[1] = new Match(homeTeam.getTeamName(), awayTeam.getTeamName(), homeTeam.getArena(), homeTeam.getPlayers(),
                    awayTeam.getPlayers(), array, array, array, array, array, array, 0, 0,
                    12, 0, 1);

            //Save match
            saveObjectToSharedPreference(getBaseContext(), "mPreference", "matchTwo", match[1]);
        }

    }

    private void setPlay2(){
        plays[1] = getSavedObjectFromPreference(getBaseContext(), "mPreference", "playTwo",
                PlayByPlay.class);

        Log.d("From GamesActivity: ", "play 2 is " + plays[1]);

        if(plays[1] == null){

            //first and second quarters - array lengths are 107
            String[] playerNames = {"P.J. Tucker", "Danuel House Jr.", "Danuel House Jr.", "Russell Westbrook",
                    "Giannis Antetokounmpo", "Russell Westbrook", "P.J. Tucker", "Wesley Matthews", "James Harden",
                    "James Harden", "James Harden", "Russell Westbrook", "Eric Bledsoe", "Clint Capela", "Russell Westbrook",
                    "Eric Bledsoe", "James Harden", "James Harden", "James Harden", "Danuel House Jr.", "Clint Capela",
                    "Clint Capela", "Wesley Matthews", "Russell Westbrook", "George Hill", "Ersan Ilyasova",
                    "Clint Capela", "P.J. Tucker", "Wesley Matthews", "Khris Middleton", "Clint Capela",
                    "Austin Rivers", "Tyson Chandler", "Tyson Chandler", "George Hill", "Thabo Sefolosha",
                    "Thabo Sefolosha", "James Harden", "James Harden", "James Harden", "Tyson Chandler",
                    "Tyson Chandler", "Brook Lopez", "Brook Lopez", "Russell Westbrook", "Sterling Brown",
                    "Sterling Brown", "Ersan Ilyasova", "Tyson Chandler", "Russell Westbrook", "Kyle Korver",
                    "Tyson Chandler", "Tyson Chandler", "Giannis Antetokounmpo", "Giannis Antetokounmpo",
                    "Sterling Brown", "Kyle Korver", "Eric Gordon", "Eric Bledsoe", "Tyson Chandler", "Giannis Antetokounmpo",
                    "Brook Lopez", "Giannis Antetokounmpo", "Eric Gordon", "Giannis Antetokounmpo", "Russell Westbrook",
                    "Eric Gordon", "Brook Lopez", "Giannis Antetokounmpo", "P.J. Tucker", "Kyle Korver", "Giannis Antetokounmpo",
                    "Giannis Antetokounmpo", "Pat Connaughton", "Eric Gordon", "Ben McLemore", "Pat Connaughton",
                    "Pat Connaughton", "James Harden", "James Harden", "Clint Capela", "P.J. Tucker", "James Harden",
                    "Pat Connaughton", "Danuel House Jr.", "Pat Connaughton", "Danuel House Jr.", "Khris Middleton",
                    "Pat Connaughton", "Giannis Antetokounmpo", "Clint Capela", "Pat Connaughton", "Khris Middleton",
                    "Ben McLemore", "Kyle Korver", "Kyle Korver", "Kyle Korver", "Khris Middleton", "James Harden",
                    "James Harden", "Russell Westbrook", "James Harden", "Clint Capela", "P.J. Tucker", "Clint Capela",
                    "Clint Capela", "Ersan Ilyasova"};
            String[] assistPlayerNames = {"James Harden", null, null, null, null, null, null, null, null,
                null, null, null, "Giannis Antetokounmpo", "James Harden", null, "Giannis Antetokounmpo", null,
                null, null, null, "James Harden", null, null, null, "Brook Lopez", null, null, "James Harden",
                null, null, null, null, null, null, "Khris Middleton", null, null, null, null, null, null,
                    "James Harden", null, "George Hill", null, null, null, null, null, null, null, null,
                    "Russell Westbrook", null, "George Hill", null, "Giannis Antetokounmpo", "Russell Westbrook",
                "Brook Lopez", "Russell Westbrook", null, null, null, "Russell Westbrook", null, null, "Russell Westbrook",
                null, null, null, null, null, null, null, null, "James Harden", "Giannis Antetokounmpo", null,
                null, null, null, "Ben McLemore", null, "George Hill", "James Harden", "Wesley Matthews", "James Harden",
                null, "Khris Middleton", null, null, null, "Giannis Antetokounmpo", null, null, null, null, "Giannis Antetokounmpo",
                null, null, null, null, null, "Russell Westbrook", null, null, null};

            int[] playerTeam = {1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2,
                1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 1, 1, 2, 1, 1, 2, 2, 2, 2,
                1, 2, 1, 2, 2, 2, 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 2,
                2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2};
            int[] action = {1, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 1, 2,
                2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2, 2, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2,
                1, 2, 2, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2};
            int[] pointValue = {3, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 2, 2, 3, 3, 1, 1, 1, 0, 2, 0, 0, 0, 3, 0,
                0, 2, 2, 0, 0, 0, 0, 2, 3, 0, 0, 1, 1, 1, 0, 2, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0, 2, 1, 2, 0, 3,
                3, 3, 2, 2, 0, 2, 2, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 0, 3, 3, 0, 1, 1, 0, 3, 0, 2, 3, 2, 3, 0,
                3, 0, 0, 0, 2, 3, 0, 2, 0, 3, 2, 3, 0, 0, 0, 3, 0, 1, 0};

            int[] timeMinute = {11, 11, 11, 11, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 8, 8, 8,
                7, 7, 7, 6, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1,
                1, 1, 0, 0, 0, 0, 11, 11, 11, 11, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 8, 8, 7, 7,
                7, 7, 7, 6, 6, 6, 5, 5, 5, 5, 5, 4, 4, 3, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0, 0};
            int[] timeSecond = {35, 20, 19, 1, 49, 41, 31, 25, 8, 4, 3, 50, 38, 28, 9, 41, 33, 32, 31, 19, 13,
                52, 39, 24, 55, 30, 8, 48, 33, 19, 12, 51, 39, 38, 27, 56, 38, 33, 32, 31, 8, 1, 0, 49, 48, 31,
                26, 54, 44, 40, 20, 10, 1, 55, 35, 21, 3, 34, 23, 14, 5, 48, 39, 33, 32, 23, 16, 46, 26, 18, 8,
                51, 40, 26, 18, 14, 54, 37, 16, 15, 0, 45, 21, 7, 55, 38, 29, 7, 2, 16, 7, 57, 39, 21, 49, 48,
                30, 16, 6, 40, 18, 7, 54, 49, 34, 28, 27};
            int[] quarter = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

            plays[1] = new PlayByPlay(playerNames, assistPlayerNames, playerTeam, action, pointValue, timeMinute,
                    timeSecond, quarter);

            //Save play
            saveObjectToSharedPreference(getBaseContext(), "mPreference", "playTwo",
                    plays[1]);

        }
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

    public static void saveObjectToSharedPreference(Context context, String preferenceFileName, String serializedObjectKey, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
        sharedPreferencesEditor.apply();
    }

    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context context, String preferenceFileName,
                                                                           String preferenceKey, Class<GenericClass> classType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }
        return null;
    }

    public static void deleteSavedObjectFromPreference(Context context, String preferenceFileName, String preferenceKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        sharedPreferences.edit().remove(preferenceKey).apply();
    }
}
