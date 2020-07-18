package com.example.android.istanbulguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class RestaurantFragment extends Fragment {

    private ArrayList<Location> locations;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        locations = new ArrayList<Location>();

        locations.add(new Location(getString(R.string.ulus_29), getString(R.string.ulus_29_info)
                , R.drawable.ulus_29));
        locations.add(new Location(getString(R.string.rana), getString(R.string.rana_info)
                , R.drawable.rana));
        locations.add(new Location(getString(R.string.baynan), getString(R.string.baynan_info)
                , R.drawable.banyan));
        locations.add(new Location(getString(R.string.maiden_tower), getString(R.string.maiden_tower_info)
                , R.drawable.maiden_tower));
        locations.add(new Location(getString(R.string.feriye_palace), getString(R.string.feriye_palace_info)
                , R.drawable.feriye_palace));

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
