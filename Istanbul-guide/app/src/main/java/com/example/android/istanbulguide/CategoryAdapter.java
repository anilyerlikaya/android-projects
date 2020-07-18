package com.example.android.istanbulguide;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CategoryAdapter extends FragmentStatePagerAdapter {

    private Activity mActivity;

    public CategoryAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        mActivity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TouristicLocationFragment();
            case 1:
                return new RestaurantFragment();
            case 2:
                return new HotelsFragment();
            case 3:
                return new FunPlacesFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position) {
            case 0:
                return mActivity.getString(R.string.touristic_places);
            case 1:
                return mActivity.getString(R.string.restaurants);
            case 2:
                return mActivity.getString(R.string.hotels);
            case 3:
                return mActivity.getString(R.string.fun_places);
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return 4;
    }
}


