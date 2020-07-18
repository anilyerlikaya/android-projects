package com.example.android.locationApp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static final String URL = BuildConfig.LocationApiKey;

    private static final int REQUEST_LOCATION = 1;
    private static final int USER_OPEN_GPS = 10;
    private static final int USER_NOT_OPEN_GPS = 11;

    private static final String CATEGORY_ID_HISTORICAL_PLACES = "4deefb944765f83613cdba6e";
    private static final String CATEGORY_ID_RESTAURANTS = "4d4b7105d754a06374d81259";
    private static final String CATEGORY_ID_MUSEUM = "4bf58dd8d48988d181941735";
    private static final String CATEGORY_ID_TRANSPORTATION = "4bf58dd8d48988d1fe931735,52f2ab2ebcbc57f1066b8b4f,4bf58dd8d48988d1fd931735,4e74f6cabd41c4836eac4c31,53fca564498e1a175f32528b,4bf58dd8d48988d129951735,52f2ab2ebcbc57f1066b8b51,4bf58dd8d48988d130951735,56aa371be4b08b9a8d57353e";

    private ImageButton trackerButton;
    private ImageButton menuButton;
    private RelativeLayout relativeLayout;
    private LocationManager locationManager;
    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    //network connection
    static boolean isConnected = false;
    static boolean userPermitGps = false;

    private boolean isFollow = true;
    private boolean zoomLevelPermission = true;

    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean getLocations = true;
    private int userPermission = 0;

    //changed option in navigation drawer
    private boolean optionsChanged = false;
    //if option changed, go for thread
    private boolean searchForNewLocations = false;

    //circle around user marker
    private boolean userCircle = false;
    Circle circle50 = null;
    Circle circle100 = null;

    //double click
    Date firstDate = null;

    //hide image buttons
    Date buttonsNotUsed = new Date();
    boolean isImageButtonsOn = true;

    //start thread
    private boolean getContactsEmpty = true;

    private boolean searchHistoricalPalaces = true;
    private boolean searchRestaurants = false;
    private boolean searchMuseums = false;
    private boolean searchTransportations = false;

    private int zoomLevel = 17;

    // TODO: user can change radius size.
    private long radius = 4000;

    private Marker userMarker = null;
    private Marker previousMarker = null;


    // List of venues
    List<Places> venues;

    private GoogleMap mMap;
    private double firstLatitude, firstLongitude;
    private double latitude, longitude;

    private NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // check network connection
        isConnected = checkNetworkConnection();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
                buttonsNotUsed = new Date();

            }
        });

        trackerButton = findViewById(R.id.trackerButton);
        trackerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFollow) {
                    isFollow = false;
                    trackerButton.setImageResource(R.drawable.ic_navigation);
                }
                else {
                    isFollow = true;
                    trackerButton.setImageResource(R.drawable.ic_near_me);
                    if(userMarker != null) {
                        LatLng userLocation = new LatLng(latitude, longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
                    }
                }

                buttonsNotUsed = new Date();
            }
        });

        progressBar = findViewById(R.id.progressBar);
        relativeLayout = findViewById(R.id.relativeLayout);

        //float action button listener
        FloatingActionButton button = findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMarker != null) {
                    if(!isFollow){
                        isFollow = true;
                        trackerButton.setImageResource(R.drawable.ic_near_me);
                    }

                    LatLng markerLoc = new LatLng(latitude, longitude);
                    final CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(markerLoc)      // Sets the center of the map to Mountain View
                            .zoom(zoomLevel)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    if(!isConnected){
                        Snackbar.make(relativeLayout, "Please connect Network", Snackbar.LENGTH_LONG)
                                .setAction("Go Setting", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.setComponent(new ComponentName("com.android.settings",
                                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }).show();
                    }
                } else {
                    if(!userPermitGps){
                        gpsPermission();
                    }
                    else if (isConnected)
                        controlGps();
                    else {
                        Snackbar.make(relativeLayout, "Please connect Network", Snackbar.LENGTH_LONG)
                                .setAction("Go Setting", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.setComponent(new ComponentName("com.android.settings",
                                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }).show();
                    }
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) { }

            @Override
            public void onDrawerOpened(@NonNull View view) { }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                if(optionsChanged) {
                    searchForNewLocations = true;
                    optionsChanged = false;

                    controlGps();
                }
            }

            @Override
            public void onDrawerStateChanged(int i) { }
        });

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SwitchCompat historicalPlaceSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_historical_place).getActionView();
        historicalPlaceSwitch.setOnCheckedChangeListener(null);
        historicalPlaceSwitch.setChecked(true);
        historicalPlaceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(!searchHistoricalPalaces) {
                        searchHistoricalPalaces = true;
                        optionsChanged = true; }
                } else {
                    if(searchHistoricalPalaces) {
                        searchHistoricalPalaces = false;
                        optionsChanged = true;
                    }
                }
            }
        });

        SwitchCompat restaurantSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_restaurants).getActionView();
        restaurantSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(!searchRestaurants) {
                        searchRestaurants = true;
                        optionsChanged = true; }
                } else {
                    if(searchRestaurants) {
                        searchRestaurants = false;
                        optionsChanged = true;
                    }
                }
            }
        });

        SwitchCompat museumSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_museums).getActionView();
        museumSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(!searchMuseums) {
                        searchMuseums = true;
                        optionsChanged = true; }
                } else {
                    if(searchMuseums) {
                        searchMuseums = false;
                        optionsChanged = true;
                    }
                }
            }
        });

        SwitchCompat transportationSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_transportation).getActionView();
        transportationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(!searchTransportations) {
                        searchTransportations = true;
                        optionsChanged = true; }
                } else {
                    if(searchTransportations) {
                        searchTransportations = false;
                        optionsChanged = true;
                    }
                }
            }
        });

        SwitchCompat zoomLevelSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_zoom_level_settings).getActionView();
        zoomLevelSwitch.setOnCheckedChangeListener(null);
        zoomLevelSwitch.setChecked(true);
        zoomLevelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    zoomLevelPermission = true;
                } else {
                    zoomLevelPermission = false;
                }
            }
        });

        // broadcastReceiver start which check network connection.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

    }

    //show or not utility image buttons
    private void switchImageButtons(boolean value){
        if(value){
            trackerButton.setVisibility(View.VISIBLE);
            menuButton.setVisibility(View.VISIBLE);
        }else {
            trackerButton.setVisibility(View.GONE);
            menuButton.setVisibility(View.GONE);
        }
    }

    private void gpsPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            //Toast.makeText(getBaseContext(), "The app was not allowed to access your location", Toast.LENGTH_LONG).show();
        }else if(userPermitGps){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,0,this);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        locationManager.removeUpdates(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // click markers excepts user on map
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.equals(userMarker)) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                    if(isFollow) {
                        isFollow = false;
                        trackerButton.setImageResource(R.drawable.ic_navigation);
                    }

                    String locName = marker.getTitle();
                    showPopUpInfo(locName);
                    //Toast.makeText(getBaseContext(), "Selected: " + locName, Toast.LENGTH_SHORT).show();

                    /*if (previousMarker != null) {
                        previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    previousMarker = marker;*/

                    if(firstDate != null)
                        firstDate = null;

                    return true;
                } else if(marker.equals(userMarker)){
                    /*if(previousMarker != null)
                        previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));*/

                    Date time = new Date();

                    if(firstDate == null)
                        firstDate = time;
                    else if(Math.abs(time.getSeconds() - firstDate.getSeconds()) < 2) {
                        if (!userCircle) {
                            circle100 = mMap.addCircle(new CircleOptions()
                                    .center(userMarker.getPosition())
                                    .radius(100)
                                    .strokeWidth(0)
                                    .strokeColor(Color.parseColor("#22DD2C00"))
                                    .fillColor(Color.parseColor("#22DD2C00")));

                            circle50 = mMap.addCircle(new CircleOptions()
                                    .center(userMarker.getPosition())
                                    .radius(50)
                                    .strokeWidth(0)
                                    .strokeColor(Color.parseColor("#224CAF50"))
                                    .fillColor(Color.parseColor("#224CAF50")));

                            userCircle = true;
                        } else {
                            circle50.remove();
                            circle100.remove();

                            userCircle = false;
                        }

                        firstDate = null;
                    }else
                        firstDate = null;
                }

                return false;
            }
        });

        // click anywhere on map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(!isImageButtonsOn) {
                    switchImageButtons(true);
                    buttonsNotUsed = new Date();
                    isImageButtonsOn = true;
                }

                if (isFollow) {
                    isFollow = false;
                    trackerButton.setImageResource(R.drawable.ic_navigation);
                }

                /*if(previousMarker != null)
                    previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));*/

                if (firstDate != null)
                    firstDate = null;
            }
        });

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_map));

            if (!success) {
                Log.e("here", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("here", "Can't find style. Error: ", e);
        }

        if(isConnected)
            controlGps();
    }

    private void showPopUpInfo(String locationName){
        View alertLayout = getLayoutInflater().inflate(R.layout.pop_up_info, null);
        ImageView imageView = (ImageView) alertLayout.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.istanbul_view);
        TextView textView = (TextView) alertLayout.findViewById(R.id.text);
        textView.setText("You are in " + locationName);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle(locationName);
        builder.setView(alertLayout);

        //builder.setCancelable(false);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    userPermitGps = true;
                    controlGps();
                } else {
                    userPermitGps = false;
                    Toast.makeText(getBaseContext(), "The app was not allowed to access your location", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void controlGps(){
        // getting GPS status
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGpsEnabled)
            userPermission = USER_OPEN_GPS;

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        assert locationManager != null;
        if(!isGpsEnabled && userPermission != USER_NOT_OPEN_GPS){
            buildAlertMessageNoGps();
        }else if(!isGpsEnabled && userPermission == USER_NOT_OPEN_GPS){
            showGPSOFFSnackbar();
        }
        else{
            getLocation();
        }
    }

    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            Toast.makeText(getBaseContext(), "The app was not allowed to access your location", Toast.LENGTH_SHORT).show();
        }else{
            Location gpsLocation = null, networkLocation = null, finalLocation = null;

            // get location with getLastKnownLocation
            if(isGpsEnabled)
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(isNetworkEnabled)
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation != null && networkLocation != null) {
                //smaller the number more accurate result will
                if (gpsLocation.getAccuracy() > networkLocation.getAccuracy())
                    finalLocation = networkLocation;
                else
                    finalLocation = gpsLocation;
            } else {
                if (gpsLocation != null) {
                    finalLocation = gpsLocation;
                } else if (networkLocation != null) {
                    finalLocation = networkLocation;
                }
            }

            getValues(finalLocation);
        }
    }

    private void getValues(Location location){
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if(((Math.abs(firstLatitude - latitude) + Math.abs(firstLongitude - longitude)) > 0.03))
                getLocations = true;

            LatLng userLocation = new LatLng(latitude, longitude);

            BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.user_location);
            Bitmap b = bitmapDraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);

            if(isConnected && (getLocations || searchForNewLocations)){
                mMap.clear();
                previousMarker = null;
            }

            if (userMarker != null)
                userMarker.remove();

            userMarker = mMap.addMarker(new MarkerOptions().position(userLocation).snippet("User")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

            if(isFollow) {
                if(zoomLevelPermission)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
                else
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
            }

            if(isConnected && getContactsEmpty && (getLocations || searchForNewLocations)) {
                firstLatitude = latitude;
                firstLongitude = longitude;

                new GetContacts().execute();
                getLocations = false;
                searchForNewLocations = false; }
        }else
            Toast.makeText(this, "Unable to Trace your Location!", Toast.LENGTH_SHORT).show();
    }

    private void goWithoutInternet(Location location){
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng userLocation = new LatLng(latitude, longitude);

        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.user_location);
        Bitmap b = bitmapDraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);

        if (userMarker != null)
            userMarker.remove();

        userMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("User")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).snippet("User"));


        if(isFollow) {
            if(zoomLevelPermission)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
            else
             mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
        }
    }

    protected void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showGPSOFFSnackbar();
                        userPermission = USER_NOT_OPEN_GPS;
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showGPSOFFSnackbar(){
        Snackbar.make(relativeLayout, "GPS is OFF!", Snackbar.LENGTH_LONG)
                .setAction("TURN ON", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location.getAccuracy() < 20.01) {
            if (isConnected)
                getValues(location);
            else
                goWithoutInternet(location);
        }

        if(isImageButtonsOn){
            Date time = new Date();

            if(Math.abs(time.getSeconds() - buttonsNotUsed.getSeconds()) > 10) {
                switchImageButtons(false);
                isImageButtonsOn = false;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v("here", "onStatusChanged:" + provider + "- status:" + status + " - extras:" + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v("here", "onProviderEnabled:" + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v("here", "onProviderDisabled:" + provider);
    }

    //check network connection
    private boolean checkNetworkConnection(){
        ConnectivityManager cm =
                (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_historical_place:  return false;
            case R.id.nav_restaurants:  return false;
            case R.id.nav_museums:  return false;
            case R.id.nav_transportation:  return false;
            case R.id.nav_zoom_level_settings: return false;
            case R.id.nav_zoom_settings: selectZoomLevel();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectZoomLevel(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Zoom Level");
        String[] types = {"Low", "Medium", "High"};
        b.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                switch(which){
                    case 0:
                        zoomLevel = 15;
                        break;
                    case 1:
                        zoomLevel = 17;
                        break;
                    case 2:
                        zoomLevel = 19;
                        break;
                }
            }

        });

        b.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetContacts extends AsyncTask<Void, Void, List<Places>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getContactsEmpty = false;
            progressBar.setVisibility(View.VISIBLE);
            Snackbar.make(relativeLayout, "Location Data Loading", Snackbar.LENGTH_SHORT)
                    .show();
        }

        @Override
        protected List<Places> doInBackground(Void... arg0) {
            String urlCopy = URL;

            String locationStr = String.valueOf(latitude) + "," +
                    String.valueOf(longitude);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currentDate = sdf.format(new Date());

            urlCopy += currentDate + "&ll=" + locationStr + "&radius=" + radius + "&categoryId=";


            if(searchHistoricalPalaces)
                urlCopy += CATEGORY_ID_HISTORICAL_PLACES + ",";

            if(searchRestaurants)
                urlCopy += CATEGORY_ID_RESTAURANTS + ",";

            if(searchMuseums)
                urlCopy += CATEGORY_ID_MUSEUM + ",";

            if(searchTransportations)
                urlCopy += CATEGORY_ID_TRANSPORTATION + ",";

            Log.v("here", "url: " + urlCopy);

            return PlacesUtils.getPlacesList(urlCopy);
        }

        @Override
        protected void onPostExecute(List<Places> result) {
            super.onPostExecute(result);
            venues = new ArrayList<Places>(result);

            for(int i=0; i<venues.size(); i++){
                Places place = venues.get(i);
                Log.v("here", "name:" + place.getLocationName() + " - id:" + place.getLocationId() + " - latitude:" + place.getLatitude() + " - longitude:" + place.getLongitude() + " - bitmap:" + place.getLocationIcon());
                Marker newMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .title(place.getLocationName()));

                if(place.getLocationIcon() != null)
                    newMarker.setIcon(BitmapDescriptorFactory.fromBitmap(place.getLocationIcon()));
            }

            progressBar.setVisibility(View.GONE);
            getContactsEmpty=true;
        }
    }


    //Follow changes of network connection and get location data from server when connection switch of to on.
    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // bool not use
            boolean bool = isNetworkAvailable(context);
        }

        @SuppressLint("LongLogTag")
        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE); //Sistem ağını dinliyor internet var mı yok mu

            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                            // TODO: when both wifi and lte are open, whenever one of the closed,
                            // TODO: map cleared and locations loading again.
                            if (!isConnected) {
                                isConnected = true;

                                controlGps();
                            }
                            return true;
                        }
                }
            }
            isConnected = false;
            Snackbar.make(relativeLayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction("Go Setting", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$DataUsageSummaryActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                    }).show();

            return false;
        }
    }
}
