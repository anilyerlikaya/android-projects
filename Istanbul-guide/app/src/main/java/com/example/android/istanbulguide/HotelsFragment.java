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

public class HotelsFragment extends Fragment {

    private ArrayList<Location> locations;

    public HotelsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        locations = new ArrayList<Location>();

        locations.add(new Location(getString(R.string.four_season_hotel) , getString(R.string.four_season_hotel_info)
                , R.drawable.four_season));
        locations.add(new Location(getString(R.string.tomtom_suites) , getString(R.string.tomtom_suites_info)
                , R.drawable.tomtom));
        locations.add(new Location(getString(R.string.sirkeci_mansion) , getString(R.string.sirkeci_mansion_info)
                , R.drawable.sirkeci_mansion));
        locations.add(new Location(getString(R.string.kempinski_hotel) , getString(R.string.kempinski_hotel_info)
                , R.drawable.kempinski));
        locations.add(new Location(getString(R.string.hotel_sultania) , getString(R.string.hotel_sultania_info)
                , R.drawable.hotel_sultania));
        locations.add(new Location(getString(R.string.levni_hotel) , getString(R.string.levni_hotel_info)
                , R.drawable.levli_hotel));

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
