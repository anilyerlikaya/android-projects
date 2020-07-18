package com.example.basketballapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.basketballapp.Classes.League;
import com.example.basketballapp.Classes.Player;
import com.example.basketballapp.Classes.Team;
import com.example.basketballapp.InfoActivities.LeagueActivity;
import com.example.basketballapp.InfoActivities.TeamsActivity;

public class MainActivity extends AppCompatActivity {

    TextView games;
    TextView league;
    TextView teams;

    League newLeague;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        newLeague = setLeague();

        games = findViewById(R.id.games);
        league = findViewById(R.id.league);
        teams = findViewById(R.id.teams);

        games.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GamesActivity.class);
                intent.putExtra("league", newLeague);
                startActivity(intent);
            }
        });

        league.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeagueActivity.class);
                intent.putExtra("league", newLeague);
                startActivity(intent);
            }
        });

        teams.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TeamsActivity.class);
                intent.putExtra("league", newLeague);
                startActivity(intent);
            }
        });


        //TODO: add video play with label
    }

    //static league build but with api or database, it can be change to dynamic.
    public League setLeague() {
        Team[] teams = new Team[4];

        String[] teamNames = {"Los Angeles Lakers", "Toronto Raptors", "Houston Rockets", "Milwaukee Bucks"};
        String[] teamCoach = {"Frank Vogel", "Nick Nurse", "Mike D'Antoni", "Mike Budenholzer"};
        String[] teamCity = {"Los Angeles", "Toronto", "Houston", "Milwaukee"};
        String[] foundationDate = {"1948", "1995", "1967", "1968"};
        String[] arena = {"Staples Center", "Scotiabank Arena", "Toyota Center", "Fiserv Forum"};

        int[] winCount = {16, 13, 12, 15};
        int[] loseCount = {2, 4, 6, 3};
        int[] winStreak = {9, 5, 1, 9};

        String[][] playerName = {{"LeBron James", "Dwight Howard" , "Rajon Rondo", "Jared Dudley", "JaVale McGee",
                "Danny Green", "Avery Bradley", "DeMarcus Cousins", "Anthony Davis", "Kentavious Caldwell-Pope",
                "Troy Daniels", "Quinn Cook", "Kyle Kuzma", "Alex Caruso", "Kostas Antetokounmpo"},
                {"Kyle Lowry", "Marc Gasol" , "Serge Ibaka", "Rondae Hollis-Jefferson", "Stanley Johnson",
                        "Norman Powell", "Patrick McCaw", "Fred VanVleet", "Pascal Siakam", "OG Anunoby",
                        "Malcolm Miller", "Chris Boucher", "Terence Davis", "Shamorie Ponds", "Oshae Brissett"},
                {"Tyson Chandler", "Nene Hilario" , "Thabo Sefolosha", "Gerald Green", "Russell Westbrook",
                        "Eric Gordon", "James Harden", "P.J. Tucker", "Austin Rivers", "Ben McLemore",
                        "Clint Capela", "Danuel House Jr.", "Gary Clark", "Isaiah Hartenstein", "Chris Clemons"},
                {"Kyle Korver", "George Hill" , "Ersan Ilyasova", "Brook Lopez", "Robin Lopez", "Wesley Matthews",
                        "Eric Bledsoe", "Khris Middleton", "Giannis Antetokounmpo", "Pat Connaughton",
                        "Cameron Reynolds", "D.J. Wilson", "Frank Mason", "Sterling Brown", "Donte DiVincenzo"}};

        String[][] playerPos = {{"F", "C-F" , "G", "F", "C-F", "G", "G", "C", "F-C", "G", "G", "F", "G", "F", "G"},
                {"G", "C" , "F", "F", "F-G", "G", "G", "G", "F", "F", "G-F", "F-C", "G", "G", "F-G"},
                {"C", "C-F" , "F-G", "G-F", "G", "G", "G", "F", "G", "G", "C", "F-G", "F", "C-F", "G"},
                {"G-F", "G" , "F", "C", "C", "G", "G", "F", "F", "G", "F", "F", "G", "G-F", "G"}};

        int[][] playerAge = {{34, 33, 33, 34, 31, 32, 29, 29, 26, 26, 28, 26, 24, 25, 22},
                {33, 34, 30, 24, 23, 26, 24, 25, 25, 22, 26, 26, 22, 21, 21},
                {37, 37, 35, 33, 31, 30, 30, 34, 27, 26, 25, 26, 25, 21, 22},
                {38, 33, 32, 31, 31, 33, 29, 28, 24, 26, 22, 23, 25, 24, 22}};

        int[][] gamesPlayed = {{18, 18, 8, 10, 18, 18, 10, 0, 17, 18, 13, 15, 14, 16, 2},
                {8, 17, 8, 10, 5, 17, 2, 17, 17, 16, 6, 15, 17, 1, 3},
                {16, 0, 15, 3, 16, 9, 18, 18, 18, 18, 15, 14, 5, 6, 12},
                {15, 16, 16, 18, 18, 18, 18, 11, 18, 16, 3, 7, 4, 15, 15}};

        double[][] points = {{25.8, 6.8, 7.9, 1.2, 6.8, 8.4, 9.6, 0, 26.1, 8.2, 5.3, 5.0, 12.3, 4.9, 0.0},
                {21.8, 5.8, 14, 10, 1.4, 11.5, 4.0, 18.3, 26.0, 11.9, 3.0, 6.2, 6.8, 4.0, 1.0},
                {1.7, 0, 1.9, 9.2, 22.5, 10.9, 37.7, 9.7, 7.6, 7.3, 14.6, 12.4, 3.6, 4.0, 4.4},
                {5.9, 9.2, 6.4, 10.8, 4.3, 8.2, 16.8, 18.3, 31.1, 5.8, 5.0, 2.7, 5.1, 5.8, 8.3}};

        double[][] rebounds = {{7.3, 7.2, 3.8, 0.8, 5.9, 3.4, 3.2, 0, 9.0, 2.2, 1.3, 1.3, 3.7, 1.6, 0.5},
                {4.3, 6.6, 6.5, 6.3, 1.4, 4.0, 3.0, 3.8, 8.4, 5.6, 0.8, 5.1, 2.9, 1.0, 0.3},
                {2.9, 0, 2.4, 2.5, 7.6, 1.9, 6.1, 6.1, 2.4, 1.9, 14.7, 4.6, 1.8, 3.3, 0.8},
                {1.4, 3.2, 5.0, 4.8, 2.5, 2.2, 5.2, 5.5, 13.7, 3.4, 1.6, 1.6, 1.1, 5.0, 4.1}};

        double[][] assist = {{11.0, 0.8, 5.5, 0.6, 0.7, 1.2, 1.8, 0, 3.6, 1.3, 0.5, 1.8, 0.8, 2.0, 0.0},
                {6.5, 3.5, 0.8, 1.6, 0.0, 1.6, 1.5, 7.5, 3.9, 1.5, 0.3, 0.6, 2.1, 2.0, 0.3},
                {0.4, 0, 0.9, 0.5, 6.8, 0.8, 7.8, 1.3, 1.2, 0.8, 1.0, 1.1, 0.4, 0.3, 0.5},
                {0.2, 3.1, 0.6, 1.5, 0.4, 1.3, 5.5, 2.9, 6.2, 1.5, 0.7, 0.7, 2.2, 1.5, 1.6}};

        Player[][] players = new Player[4][15];
        for (int i = 0; i < 4; i++) {
            for(int j=0; j<15; j++){
                players[i][j] = new Player(playerName[i][j], playerPos[i][j], playerAge[i][j], gamesPlayed[i][j],
                        assist[i][j], points[i][j], rebounds[i][j]);
            }
        }

        for(int i = 0; i<4; i++){
            teams[i] = new Team(teamNames[i], teamCoach[i], teamCity[i], foundationDate[i], arena[i],
                    winStreak[i], winCount[i], loseCount[i], players[i]);
        }

        return new League("NBA - Final Four", teams);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
