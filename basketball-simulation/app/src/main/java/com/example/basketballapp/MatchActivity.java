package com.example.basketballapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.basketballapp.Classes.Label;
import com.example.basketballapp.Classes.Match;
import com.example.basketballapp.Classes.PlayByPlay;
import com.example.basketballapp.Classes.Player;
import com.example.basketballapp.ViewAdapters.MatchViewAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MatchActivity extends AppCompatActivity {
    private String TAG = "From MatchActivity: ";

    String matchKey;
    String playKey;
    String posKey;
    String minuteKey;
    String secondKey;
    String quarterKey;
    String labelKey;

    TextView homeTeam;
    TextView awayTeam;
    TextView teamsScores;
    TextView matchClock;
    TextView matchArena;
    TextView matchQuarter;
    TextView labelView;

    Button startStopButton;
    Button deleteButton;

    ImageView court;

    Match match;
    PlayByPlay play;
    List<Label> labels;

    int showPlayers = 1;         //    1 -> home team, 2 -> away team
    Player[] homeTeamPlayers;
    Player[] awayTeamPlayers;

    Integer clockMinute;
    Integer clockSecond;
    Integer quarterCount;
    Integer playByPlayPosition;

    int homeScore;
    int awayScore;

    Thread playThread = null;
    boolean start_or_stop = false;

    boolean isFinished = false;

    String labelPlayerName;
    String labelAction;
    String labelPoint;

    boolean isFirstTime = true;
    boolean pressed = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String keyNumber = getIntent().getStringExtra("number");
        matchKey = "match" + keyNumber;
        playKey = "play" + keyNumber;
        posKey = "pos" + keyNumber;
        minuteKey = "min" + keyNumber;
        secondKey = "sec" + keyNumber;
        quarterKey = "qua" + keyNumber;
        labelKey = "label" + keyNumber;

        play = getSavedObjectFromPreference(getBaseContext(), "mPreference", playKey, PlayByPlay.class);
        match = getSavedObjectFromPreference(getBaseContext(), "mPreference", matchKey, Match.class);
        labels = getSavedListObjectFromPreference(getBaseContext(), "mPreference", labelKey, Label.class);

        if(labels != null) {
            Log.d(TAG, "label size is " + labels.size());
        }
        else {
            Log.d(TAG, "no label found");
            labels = new ArrayList<Label>();
        }

        playByPlayPosition = getSavedObjectFromPreference(getBaseContext(), "mPreference",
                posKey, Integer.class);

        if(playByPlayPosition == null){
            Log.d(TAG, "playPosition is null");

            playByPlayPosition = 0;
            clockMinute = 12;
            clockSecond = 0;
            quarterCount = 1;
        } else{
            Log.d(TAG, "playPosition is not null");

            clockMinute = getSavedObjectFromPreference(getBaseContext(), "mPreference", minuteKey, Integer.class);
            clockSecond = getSavedObjectFromPreference(getBaseContext(), "mPreference", secondKey, Integer.class);
            quarterCount = getSavedObjectFromPreference(getBaseContext(), "mPreference", quarterKey, Integer.class);
        }

        Log.d(TAG, "current play position is "+ playByPlayPosition);
        Log.d(TAG, "current minute is "+ clockMinute);
        Log.d(TAG, "current second is "+ clockSecond);
        Log.d(TAG, "current quarter is "+ quarterCount);

        //set match clock
        matchClock = findViewById(R.id.match_clock);
        updateClock();

        //set quarter
        matchQuarter = findViewById(R.id.match_quarter);
        updateQuarter();

        //set arena
        matchArena = findViewById(R.id.match_arena);
        matchArena.setText(match.getArena());

        //get label view
        labelView = findViewById(R.id.label_text);

        homeTeamPlayers = new Player[match.getHomePlayers().length];
        awayTeamPlayers = new Player[match.getAwayPlayers().length];

        updatePlayers();
        setListView(homeTeamPlayers);

        homeTeam = findViewById(R.id.homeTeam);
        awayTeam = findViewById(R.id.awayTeam);
        teamsScores = findViewById(R.id.teams_scores);

        homeTeam.setText(match.getHomeTeam());
        awayTeam.setText(match.getAwayTeam());

        homeScore = match.getHomeTeamScore();
        awayScore = match.getAwayTeamScore();
        updateScoreBoard();

        court = findViewById(R.id.court);

        //left corner of court
        final int[] posXY = new int[2];
        court.getLocationOnScreen(posXY);
        Log.d(TAG, "Image's left corner: " + posXY[0] + "," + posXY[1]);

        court.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event){
                final int imageX = (int) event.getX() - posXY[0];
                final int imageY = (int) event.getY() - posXY[1];
                Log.d(TAG, "imageX: " + imageX + " - imageY: " + imageY);

                if(isFirstTime){
                    isFirstTime = false;

                    //stop timer;

                    pressed = false;
                    if(start_or_stop) {
                        startStopButton.performClick();
                        pressed = true;
                    }

                    AlertDialog.Builder alert = new AlertDialog.Builder(MatchActivity.this);
                    final EditText edittext = new EditText(MatchActivity.this);
                    alert.setMessage("Which player do the action?");
                    alert.setView(edittext);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            labelPlayerName = edittext.getText().toString();
                            Log.d(TAG, "edit text value: " + labelPlayerName);

                            AlertDialog.Builder alert2 = new AlertDialog.Builder(MatchActivity.this);
                            alert2.setMessage("Which action is done by " + labelPlayerName);
                            alert2.setPositiveButton("Point", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    labelAction = "Point";

                                    AlertDialog.Builder alert3 = new AlertDialog.Builder(MatchActivity.this);
                                    alert3.setMessage("How much score done by " + labelPlayerName);
                                    alert3.setPositiveButton("3pt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            labelPoint = "3pt";

                                            isFirstTime = true;
                                            if(pressed)
                                                startStopButton.performClick();

                                            Label tempLabel = new Label(imageX, imageY, clockMinute, clockSecond,
                                                    quarterCount, labelPlayerName, labelAction, labelPoint);
                                            labelView.setText((labelPlayerName + " scored " + labelPoint));
                                            labels.add(tempLabel);
                                            Log.d(TAG, "3 pt: label sequence is " + labelPlayerName + ", " +
                                                    labelAction + ", " + labelPoint);
                                        }
                                    });
                                    alert3.setNeutralButton("2pt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            labelPoint = "2pt";

                                            isFirstTime = true;
                                            if (pressed)
                                                startStopButton.performClick();

                                            Label tempLabel = new Label(imageX, imageY, clockMinute, clockSecond,
                                                    quarterCount, labelPlayerName, labelAction, labelPoint);
                                            labelView.setText((labelPlayerName + " scored " + labelPoint));
                                            labels.add(tempLabel);
                                            Log.d(TAG, "2 pt: label sequence is " + labelPlayerName + ", " + labelAction + ", " + labelPoint);
                                        }
                                    });
                                    alert3.setNegativeButton("1pt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            labelPoint = "1pt";

                                            isFirstTime = true;
                                            if (pressed)
                                                startStopButton.performClick();

                                            Label tempLabel = new Label(imageX, imageY, clockMinute, clockSecond,
                                                    quarterCount, labelPlayerName, labelAction, labelPoint);
                                            labelView.setText((labelPlayerName + " scored " + labelPoint));
                                            labels.add(tempLabel);
                                            Log.d(TAG, "1 pt: label sequence is " + labelPlayerName + ", " + labelAction + ", " + labelPoint);
                                        }
                                    });
                                    alert3.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            isFirstTime = true;
                                            if (pressed)
                                                startStopButton.performClick();
                                        }
                                    });
                                    alert3.show();
                                }
                            });
                            alert2.setNegativeButton("Rebound", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    labelAction = "Rebound";
                                    labelPoint = null;

                                    isFirstTime = true;
                                    if (pressed)
                                        startStopButton.performClick();

                                    Label tempLabel = new Label(imageX, imageY, clockMinute, clockSecond,
                                            quarterCount, labelPlayerName, labelAction, labelPoint);
                                    labelView.setText((labelPlayerName + " " + labelAction));
                                    labels.add(tempLabel);
                                    Log.d(TAG, "at rebound: label sequence is " + labelPlayerName + ", " + labelAction + ", " + labelPoint);
                                }
                            });
                            alert2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    isFirstTime = true;
                                    if (pressed)
                                        startStopButton.performClick();
                                }
                            });
                            alert2.show();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            isFirstTime = true;
                            if (pressed)
                                startStopButton.performClick();
                        }
                    });
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            isFirstTime = true;
                            if (pressed)
                                startStopButton.performClick();
                        }
                    });
                    alert.show();
                }

                return true;
            }
        });

        homeTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayers = 1;
                setListView(homeTeamPlayers);
            }
        });

        awayTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayers = 2;
                setListView(awayTeamPlayers);
            }
        });

        startStopButton = findViewById(R.id.start_stop_match);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "start button clicked + start_or_stop is " + start_or_stop);

                if(!start_or_stop) {
                    start_or_stop = true;

                    startStopButton.setText("Stop");

                    playThread = new Thread() {

                        @Override
                        public void run() {

                            if(play != null)
                                startPlay();
                            else
                                Log.d(TAG, "play is null!!!");
                        }

                    };
                    playThread.start();
                } else {
                    start_or_stop = false;

                    startStopButton.setText("Start");
                }
            }
        });

        deleteButton = findViewById(R.id.delete_match);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_or_stop = false;
                startStopButton.setText("Start");

                final AlertDialog.Builder builder = new AlertDialog.Builder(MatchActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        deleteSavedObjectFromPreference(getBaseContext(), "mPreference", posKey);
                        deleteSavedObjectFromPreference(getBaseContext(), "mPreference", minuteKey);
                        deleteSavedObjectFromPreference(getBaseContext(), "mPreference", secondKey);
                        deleteSavedObjectFromPreference(getBaseContext(), "mPreference", quarterKey);
                        deleteSavedObjectFromPreference(getBaseContext(), "mPreference", matchKey);

                        deleteSavedObjectFromPreference(getBaseContext(), "mPreference", labelKey);
                        isFinished = true;
                        finish();

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void startPlay() {
        int i = playByPlayPosition;

        int minute = clockMinute;
        int second = clockSecond;
        int quarter = quarterCount;

        Calendar calendar = Calendar.getInstance();
        int oldSec = calendar.get(Calendar.SECOND);

        while(start_or_stop){
            calendar = Calendar.getInstance();

            if (oldSec != calendar.get(Calendar.SECOND)) {

                oldSec = calendar.get(Calendar.SECOND);

                if (second == 0) {
                    if (minute == 0) {
                        if(quarter == 4) {
                            Toast.makeText(getBaseContext(), "End of " + quarter + ". period", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "end of " + quarter + ". period");
                            break;
                        }else{
                            quarter++;
                            quarterCount = quarter;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateQuarter();
                                }
                            });

                            minute = 12;
                            second = 0;
                        }
                    }else {
                        minute--;
                        second = 59;
                    }
                } else
                    second--;

                Log.d(TAG, "current minute: " + minute + " - second: " + second + " - quarter: " + quarter + " - i: " + i);

                clockMinute = minute;
                clockSecond = second;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        updateClock();
                    }
                });

                if (i != play.getPlayerName().length) {
                    Log.d(TAG, (i + 1) + ". play minute is " + play.getTimeMinute()[i] + " - second is " +
                            play.getTimeSecond()[i] + " - quarter is " + play.getQuarter()[i]);

                    if (play.getTimeMinute()[i] == minute && play.getTimeSecond()[i] == second && play.getQuarter()[i] == quarter) {
                        Log.d(TAG, "found some event");

                        if (play.getPlayerTeam()[i] == 1) {
                            Log.d(TAG, "home Team");

                            for (int j = 0; j < match.getHomePlayers().length; j++) {

                                if (match.getHomePlayers()[j].getPlayerName().equals(play.getPlayerName()[i])) {
                                    Log.d(TAG, "main player is " + match.getHomePlayers()[j].getPlayerName());

                                    if (play.getAction()[i] == 1) {
                                        Log.d(TAG, "action point");

                                        int scoreValue = play.getPointValue()[i];
                                        match.getHomePlayersPoints()[j] += scoreValue;
                                        homeScore += scoreValue;
                                        match.setHomeTeamScore(homeScore);

                                        if (play.getAssistPlayerName()[i] != null) {

                                            for (int k = 0; k < match.getHomePlayers().length; k++) {

                                                if (match.getHomePlayers()[k].getPlayerName().equals(play.getAssistPlayerName()[i])) {
                                                    Log.d(TAG, "assist player is " + match.getHomePlayers()[k].getPlayerName());

                                                    match.getHomePlayersAssists()[k]++;
                                                }
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "action rebound");

                                        match.getHomePlayersRebounds()[j]++;
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "away Team");

                            for (int j = 0; j < match.getAwayPlayers().length; j++) {

                                if (match.getAwayPlayers()[j].getPlayerName().equals(play.getPlayerName()[i])) {
                                    Log.d(TAG, "main player is " + match.getAwayPlayers()[j].getPlayerName());

                                    if (play.getAction()[i] == 1) {
                                        Log.d(TAG, "action point");

                                        int scoreValue = play.getPointValue()[i];
                                        match.getAwayPlayerPoints()[j] += scoreValue;
                                        awayScore += scoreValue;
                                        match.setAwayTeamScore(awayScore);

                                        if (play.getAssistPlayerName()[i] != null) {

                                            for (int k = 0; k < match.getAwayPlayers().length; k++) {

                                                if (match.getAwayPlayers()[k].getPlayerName().equals(play.getAssistPlayerName()[i])) {
                                                    Log.d(TAG, "assist player is " + match.getAwayPlayers()[k].getPlayerName());

                                                    match.getAwayPlayersAssists()[k]++;
                                                }
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "action rebound");

                                        match.getAwayPlayersRebounds()[j]++;
                                    }
                                }
                            }
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                updateScoreBoard();
                            }
                        });

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Log.d(TAG, "update players");
                                updatePlayers();
                            }
                        });

                        i++;
                    }
                }

                quarterCount = quarter;
                clockMinute = minute;
                clockSecond = second;
                playByPlayPosition = i;
            }
        }

        Log.d(TAG, "after while of startPlay function");
    }

    private void updateClock(){
        String minute = clockMinute.toString();
        if(clockMinute < 10)
            minute = "0" + minute;

        String second = String.valueOf(clockSecond);
        if(clockSecond < 10)
            second = "0" + second;

        matchClock.setText((minute + ":" + second));
    }

    private void updateQuarter(){
        String currentQuarter = quarterCount.toString();
        matchQuarter.setText(("Quarter: " + currentQuarter));
    }

    private void updateScoreBoard(){
        teamsScores.setText((String.valueOf(homeScore) + " - " + String.valueOf(awayScore)));

    }

    private void updatePlayers(){
        for(int i=0; i<match.getHomePlayers().length; i++) {
            homeTeamPlayers[i] = new Player(match.getHomePlayers()[i].getPlayerName(), match.getHomePlayersAssists()[i],
                    match.getHomePlayersPoints()[i], match.getHomePlayersRebounds()[i]);

            awayTeamPlayers[i] = new Player(match.getAwayPlayers()[i].getPlayerName(), match.getAwayPlayersAssists()[i],
                    match.getAwayPlayerPoints()[i], match.getAwayPlayersRebounds()[i]);
        }
    }

    private void setListView(Player[] players){
        ListView listView = findViewById(R.id.match_list_view);
        MatchViewAdapter matchViewAdapter = new MatchViewAdapter(MatchActivity.this, players);
        listView.setAdapter(matchViewAdapter);
    }

    private void savePreferences(){
        saveObjectToSharedPreference(getBaseContext(), "mPreference", posKey, playByPlayPosition);
        saveObjectToSharedPreference(getBaseContext(), "mPreference", minuteKey, clockMinute);
        saveObjectToSharedPreference(getBaseContext(), "mPreference", secondKey, clockSecond);
        saveObjectToSharedPreference(getBaseContext(), "mPreference", quarterKey, quarterCount);
        saveObjectToSharedPreference(getBaseContext(), "mPreference", matchKey, match);

        saveListToSharedPreference(getBaseContext(), "mPreference", labelKey, labels);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        start_or_stop = false;

        if(playThread != null) {
            if(playThread.isAlive()) {
                try {
                    playThread.interrupt();
                } catch (Exception e) {
                    Log.d(TAG, "error: exception: " + e);
                }
            }
        }

        Log.d(TAG, "onBackPressed");

        savePreferences();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");

        if(playThread != null) {
            if(playThread.isAlive()) {
                try {
                    playThread.interrupt();
                } catch (Exception e) {
                    Log.d(TAG, "error: exception: " + e);
                }
            }
        }

        if(!isFinished)
            savePreferences();
    }

    private static void saveListToSharedPreference(Context context, String preferenceFileName, String serializedObjectKey, List<Label> object){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        //Set the values
        Gson gson = new Gson();

        List<Label> textList = new ArrayList<Label>();
        textList.addAll(object);

        String jsonText = gson.toJson(textList);
        sharedPreferencesEditor.putString(serializedObjectKey, jsonText);
        sharedPreferencesEditor.apply();
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

    private static void saveObjectToSharedPreference(Context context, String preferenceFileName, String serializedObjectKey, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
        sharedPreferencesEditor.apply();
    }

    private static <GenericClass> GenericClass getSavedObjectFromPreference(Context context, String preferenceFileName,
                                                                           String preferenceKey, Class<GenericClass> classType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }

        return null;
    }

    private static void deleteSavedObjectFromPreference(Context context, String preferenceFileName, String preferenceKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        sharedPreferences.edit().remove(preferenceKey).apply();
    }
}
