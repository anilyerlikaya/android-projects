package com.example.basketballapp.InfoActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.basketballapp.Classes.League;
import com.example.basketballapp.Classes.Team;
import com.example.basketballapp.R;

public class LeagueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league);

        final League league = (League) getIntent().getSerializableExtra("league");

        TextView leagueName = findViewById(R.id.league_name);
        leagueName.setText(league.getLeagueName());

        final Team[] teams = league.getTeams();

        //basic sort according to teams win counts
        for(int i=0; i<teams.length-1; i++){
            for(int j=i+1; j<teams.length; j++){
                if(teams[j].getWinCount() > teams[i].getWinCount()){
                    Team tempTeam = teams[j];
                    teams[j] = teams[i];
                    teams[i] = tempTeam;
                }
            }
        }

        TextView teamName1 = findViewById(R.id.team_name1);
        TextView winCount1 = findViewById(R.id.win_count1);
        TextView loseCount1 = findViewById(R.id.lose_count1);
        TextView winStreakCount1 = findViewById(R.id.win_streak1);

        teamName1.setText(teams[0].getTeamName());
        winCount1.setText(String.valueOf(teams[0].getWinCount()));
        loseCount1.setText(String.valueOf(teams[0].getLoseCount()));
        winStreakCount1.setText(String.valueOf(teams[0].getWinStreak()));

        teamName1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeagueActivity.this, PlayerActivity.class);
                intent.putExtra("team", teams[0]);
                startActivity(intent);
            }
        });

        TextView teamName2 = findViewById(R.id.team_name2);
        TextView winCount2 = findViewById(R.id.win_count2);
        TextView loseCount2 = findViewById(R.id.lose_count2);
        TextView winStreakCount2 = findViewById(R.id.win_streak2);

        teamName2.setText(teams[1].getTeamName());
        winCount2.setText(String.valueOf(teams[1].getWinCount()));
        loseCount2.setText(String.valueOf(teams[1].getLoseCount()));
        winStreakCount2.setText(String.valueOf(teams[1].getWinStreak()));

        teamName2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeagueActivity.this, PlayerActivity.class);
                intent.putExtra("team", teams[1]);
                startActivity(intent);
            }
        });

        TextView teamName3 = findViewById(R.id.team_name3);
        TextView winCount3 = findViewById(R.id.win_count3);
        TextView loseCount3 = findViewById(R.id.lose_count3);
        TextView winStreakCount3 = findViewById(R.id.win_streak3);

        teamName3.setText(teams[2].getTeamName());
        winCount3.setText(String.valueOf(teams[2].getWinCount()));
        loseCount3.setText(String.valueOf(teams[2].getLoseCount()));
        winStreakCount3.setText(String.valueOf(teams[2].getWinStreak()));

        teamName3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeagueActivity.this, PlayerActivity.class);
                intent.putExtra("team", teams[2]);
                startActivity(intent);
            }
        });

        TextView teamName4 = findViewById(R.id.team_name4);
        TextView winCount4 = findViewById(R.id.win_count4);
        TextView loseCount4 = findViewById(R.id.lose_count4);
        TextView winStreakCount4 = findViewById(R.id.win_streak4);

        teamName4.setText(teams[3].getTeamName());
        winCount4.setText(String.valueOf(teams[3].getWinCount()));
        loseCount4.setText(String.valueOf(teams[3].getLoseCount()));
        winStreakCount4.setText(String.valueOf(teams[3].getWinStreak()));

        teamName4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeagueActivity.this, PlayerActivity.class);
                intent.putExtra("team", teams[3]);
                startActivity(intent);
            }
        });
    }
}
