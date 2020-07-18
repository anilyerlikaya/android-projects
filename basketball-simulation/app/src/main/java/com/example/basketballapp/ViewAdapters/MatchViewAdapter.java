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

public class MatchViewAdapter extends ArrayAdapter<Player> {

    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    Player[] players;

    public MatchViewAdapter(@NonNull Context context, Player[] players) {
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

            convertView = inflater.inflate(R.layout.match_listview_item, null);

            holder = new ViewHolder();
            holder.playerName = convertView.findViewById(R.id.match_player_name);
            holder.scores = convertView.findViewById(R.id.match_scores);
            holder.rebounds = convertView.findViewById(R.id.match_rebounds);
            holder.assists = convertView.findViewById(R.id.match_assists);

            convertView.setTag(holder);

        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        Player player = players[position];

        if(player != null){
            holder.playerName.setText(player.getPlayerName());
            holder.scores.setText(String.valueOf(player.getMatchPoints()));
            holder.assists.setText(String.valueOf(player.getMatchAssists()));
            holder.rebounds.setText(String.valueOf(player.getMatchRebounds()));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView playerName;
        TextView scores;
        TextView rebounds;
        TextView assists;

    }
}
