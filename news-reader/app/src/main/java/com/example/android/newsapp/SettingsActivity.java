package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {
    public static final String LOG_TAG = SettingsActivity.class.getName();

    private static String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            if(date.equals("")){
                date = getString(R.string.settings_from_date_default);
                Log.v(LOG_TAG, "date: " + date);
            }

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference newsCount = findPreference(getString(R.string.settings_page_size_key));
            bindPreferenceSummaryToValue(newsCount);

            Preference fromDate = findPreference(getString(R.string.settings_from_date_key));
            bindPreferenceSummaryToValue(fromDate);
            fromDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    String key = preference.getKey();
                    if(key.equalsIgnoreCase(getString(R.string.settings_from_date_key))){
                        DatePickerFragment dateFragment = new DatePickerFragment();
                        dateFragment.show(((FragmentActivity)getActivity()).getSupportFragmentManager(), "datePicker");
                    }

                    // I couldn't found another way.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //if fragment still active but activity is not.
                            try {
                                bindPreferenceSummaryToValue(preference);
                            }catch (IllegalStateException e){
                                Log.v(LOG_TAG, "asd");

                            }

                        }
                    }, 8000);

                    return true;
                }
            });
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String stringValue = o.toString();

            if(preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if(prefIndex >= 0){
                    CharSequence[] label = listPreference.getEntries();
                    preference.setSummary(label[prefIndex]);
                }
            }else{
                preference.setSummary(stringValue);
            }

            return true;
        }

        public void bindPreferenceSummaryToValue(Preference preference){
            preference.setOnPreferenceChangeListener(this);

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            if(preference.getKey().equalsIgnoreCase(getString(R.string.settings_from_date_key))) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(preference.getKey(), date);
                editor.commit();
            }

            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String stringDay = String.valueOf(day);
            String stringMonth = String.valueOf(month);

            if(day < 10){
                stringDay = "0" + stringDay;
            }
            if(month < 10){
                stringMonth = "0" + stringMonth;
            }

            date = String.valueOf(year) + "-" + stringMonth + "-" + stringDay;

            //warning for user to wait for program to get Date value.
            Toast.makeText(getActivity(), getString(R.string.please_wait), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SettingsActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
