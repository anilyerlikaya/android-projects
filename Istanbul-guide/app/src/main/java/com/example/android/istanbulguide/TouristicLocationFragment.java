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

public class TouristicLocationFragment extends Fragment {

    private ArrayList<Location> locations;

    public TouristicLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        locations = new ArrayList<Location>();

        locations.add(new Location(getString(R.string.hagia_sophia), getString(R.string.hagia_sophia_info)
                , R.drawable.hagiasophia));
        locations.add(new Location(getString(R.string.topkapi_palace), getString(R.string.topkapi_palace_info)
                , R.drawable.topkapi_palace));
        locations.add(new Location(getString(R.string.blue_mosque), getString(R.string.blue_mosque_info)
                , R.drawable.blue_mosque));
        locations.add(new Location(getString(R.string.dolmabahce_palace), getString(R.string.dolmabahce_palace_info)
                , R.drawable.dolmabahce_palace));
        locations.add(new Location(getString(R.string.grand_bazaar), getString(R.string.grand_bazaar_info)
                , R.drawable.grand_bazaar));
        locations.add(new Location(getString(R.string.galata_tower), getString(R.string.galata_tower_info)
                , R.drawable.galata_tower));
        locations.add(new Location(getString(R.string.basilica_cistern), getString(R.string.basilica_cistern_info)
                , R.drawable.basilica_cistern));

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
