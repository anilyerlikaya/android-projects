package com.example.android.istanbulguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FunPlacesFragment extends Fragment {

    private ArrayList<Location> locations;

    public FunPlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        locations = new ArrayList<Location>();

        locations.add(new Location(getString(R.string.galata_bridge), getString(R.string.galata_bridge_info)
                , R.drawable.galata_bridge));
        locations.add(new Location( getString(R.string.miniatürk), getString(R.string.miniatürk_info)
                , R.drawable.miniaturk));
        locations.add(new Location( getString(R.string.istanbul_toy_museum), getString(R.string.istanbul_toy_museum_info)
                , R.drawable.toy_museum));
        locations.add(new Location( getString(R.string.rahmi_koc_museum), getString(R.string.rahmi_koc_museum_info)
                , R.drawable.koc_museum));
        locations.add(new Location( getString(R.string.ortaköy_square), getString(R.string.ortaköy_square_info)
                , R.drawable.ortakoy_square));

        final LocationAdapter locationAdapter = new LocationAdapter(getActivity(), locations);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(locationAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location mLocation = locations.get(position);

                Intent mIntent = new Intent(getActivity(), ShowLocationActivity.class);
                mIntent.putExtra("location",mLocation);
                startActivity(mIntent);
            }
        });

        return rootView;
    }

}
