package com.example.basketballapp.ViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.basketballapp.Classes.Player;
import com.example.basketballapp.R;

public class PlayerViewAdapter extends ArrayAdapter<Player> {

    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    Player[] players;

    public PlayerViewAdapter(@NonNull Context context, Player[] players) {
        super(context, 0, players);

        this.context = context;
        this.players = players;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return players.length;
    }

    @Override
    public Player getItem(int position) {
        return players[position];
    }

    @Override
    public long getItemId(int position) {
        return players[position].hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.player_listview_item, null);

            holder = new ViewHolder();
            holder.playerName = convertView.findViewById(R.id.player_name);
            holder.playerPosition = convertView.findViewById(R.id.player_pos);
            holder.playerAge = convertView.findViewById(R.id.player_age);
            holder.playedGames = convertView.findViewById(R.id.game_played);
            holder.scores = convertView.findViewById(R.id.scores);
            holder.rebounds = convertView.findViewById(R.id.rebounds);
            holder.assists = convertView.findViewById(R.id.assists);

            convertView.setTag(holder);

        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        Player player = players[position];

        if(player != null){
            holder.playerName.setText(player.getPlayerName());
            holder.playerPosition.setText(player.getPlayerPosition());
            holder.playerAge.setText(String.valueOf(player.getPlayerAge()));
            holder.playedGames.setText(String.valueOf(player.getGamesPlayed()));
            holder.scores.setText(String.valueOf(player.getPoints()));
            holder.assists.setText(String.valueOf(player.getAssist()));
            holder.rebounds.setText(String.valueOf(player.getRebound()));
        }

        return convertView;
    }

    //View Holder Pattern for better performance
    private static class ViewHolder {
        TextView playerName;
        TextView playerPosition;
        TextView playerAge;
        TextView playedGames;
        TextView scores;
        TextView rebounds;
        TextView assists;

    }
}
