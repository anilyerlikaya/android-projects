package com.example.android.istanbulguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class GuideActivity  extends AppCompatActivity{

    private int iconArray[] =
            {R.drawable.ic_place, R.drawable.ic_restaurant, R.drawable.ic_hotel, R.drawable.ic_fun};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        CategoryAdapter pagerAdapter = new CategoryAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        getTabIcons(tabLayout, iconArray.length);
    }

    private void getTabIcons(TabLayout tabLayout, int number){
        for(int size=0; size<number; size++){
            tabLayout.getTabAt(size).setIcon(iconArray[size]);
        }
    }
}
