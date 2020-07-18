package com.example.basketballapp.ViewAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.basketballapp.Classes.Label;
import com.example.basketballapp.Classes.Player;
import com.example.basketballapp.R;

import java.util.List;

public class LabelViewAdapter extends ArrayAdapter<Label> {
    String TAG = "FROM LabelViewAdampter";

    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;

    List<Label> labels;

    public LabelViewAdapter(@NonNull Context context, List<Label> labels) {
        super(context, 0, labels);

        this.context = context;
        this.labels = labels;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return labels.size();
    }

    @Override
    public Label getItem(int position) {
        return labels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return labels.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.label_listview_item, null);

            holder = new ViewHolder();
            holder.labelPlayerName = convertView.findViewById(R.id.label_player_name);
            holder.labelAction = convertView.findViewById(R.id.label_action);
            holder.labelPoint = convertView.findViewById(R.id.label_points);
            holder.labelMinute = convertView.findViewById(R.id.label_minute);
            holder.labelSecond = convertView.findViewById(R.id.label_second);
            holder.labelQuarter = convertView.findViewById(R.id.label_quarter);

            convertView.setTag(holder);

        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        Label label = labels.get(position);

        if(label != null){
            holder.labelPlayerName.setText(label.getPlayerName());
            holder.labelAction.setText(label.getAction());
            holder.labelPoint.setText(label.getPoint());
            holder.labelMinute.setText(String.valueOf(label.getMinute()));
            holder.labelSecond.setText(String.valueOf(label.getSecond()));
            holder.labelQuarter.setText(String.valueOf(label.getQuarter()));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView labelPlayerName;
        TextView labelAction;
        TextView labelPoint;
        TextView labelMinute;
        TextView labelSecond;
        TextView labelQuarter;
    }
}
