package com.example.basketballapp.InfoActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.basketballapp.Classes.Player;
import com.example.basketballapp.Classes.Team;
import com.example.basketballapp.R;
import com.example.basketballapp.ViewAdapters.PlayerViewAdapter;

public class PlayerActivity extends AppCompatActivity {
    private Player[] players;

    TextView teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Team team = (Team) getIntent().getSerializableExtra("team");

        teamName = findViewById(R.id.team_name);
        teamName.setText(team.getTeamName());

        players = team.getPlayers();
        ListView listView = findViewById(R.id.player_list_view);
        PlayerViewAdapter playerViewAdapter = new PlayerViewAdapter(PlayerActivity.this, players);
        listView.setAdapter(playerViewAdapter);
    }
}
