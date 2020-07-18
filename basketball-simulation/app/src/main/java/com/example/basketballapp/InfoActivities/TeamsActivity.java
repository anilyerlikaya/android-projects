package com.example.basketballapp.InfoActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.basketballapp.Classes.League;
import com.example.basketballapp.R;

public class TeamsActivity extends AppCompatActivity {

    TextView team1;
    TextView couch1;
    TextView arena1;
    TextView date1;

    TextView team2;
    TextView couch2;
    TextView arena2;
    TextView date2;

    TextView team3;
    TextView couch3;
    TextView arena3;
    TextView date3;

    TextView team4;
    TextView couch4;
    TextView arena4;
    TextView date4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final League league = (League) getIntent().getSerializableExtra("league");

        team1 = findViewById(R.id.team1);
        couch1 = findViewById(R.id.couch1);
        arena1 = findViewById(R.id.arena1);
        date1 = findViewById(R.id.date1);

        team1.setText(league.getTeams()[0].getTeamName());
        couch1.setText(league.getTeams()[0].getCoach());
        arena1.setText(league.getTeams()[0].getArena());
        date1.setText(league.getTeams()[0].getFoundationDate());

        team1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamsActivity.this, PlayerActivity.class);
                intent.putExtra("team", league.getTeams()[0]);
                startActivity(intent);
            }
        });

        team2 = findViewById(R.id.team2);
        couch2 = findViewById(R.id.couch2);
        arena2 = findViewById(R.id.arena2);
        date2 = findViewById(R.id.date2);

        team2.setText(league.getTeams()[1].getTeamName());
        couch2.setText(league.getTeams()[1].getCoach());
        arena2.setText(league.getTeams()[1].getArena());
        date2.setText(league.getTeams()[1].getFoundationDate());

        team2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamsActivity.this, PlayerActivity.class);
                intent.putExtra("team", league.getTeams()[1]);
                startActivity(intent);
            }
        });

        team3 = findViewById(R.id.team3);
        couch3 = findViewById(R.id.couch3);
        arena3 = findViewById(R.id.arena3);
        date3 = findViewById(R.id.date3);

        team3.setText(league.getTeams()[2].getTeamName());
        couch3.setText(league.getTeams()[2].getCoach());
        arena3.setText(league.getTeams()[2].getArena());
        date3.setText(league.getTeams()[2].getFoundationDate());

        team3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamsActivity.this, PlayerActivity.class);
                intent.putExtra("team", league.getTeams()[2]);
                startActivity(intent);
            }
        });

        team4 = findViewById(R.id.team4);
        couch4 = findViewById(R.id.couch4);
        arena4 = findViewById(R.id.arena4);
        date4 = findViewById(R.id.date4);

        team4.setText(league.getTeams()[3].getTeamName());
        couch4.setText(league.getTeams()[3].getCoach());
        arena4.setText(league.getTeams()[3].getArena());
        date4.setText(league.getTeams()[3].getFoundationDate());

        team4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamsActivity.this, PlayerActivity.class);
                intent.putExtra("team", league.getTeams()[3]);
                startActivity(intent);
            }
        });
    }
}
