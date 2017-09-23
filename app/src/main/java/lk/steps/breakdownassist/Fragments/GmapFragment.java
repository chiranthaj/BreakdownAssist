package lk.steps.breakdownassist.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.JobView;
import lk.steps.breakdownassist.Globals;
import lk.steps.breakdownassist.ManagePermissions;
import lk.steps.breakdownassist.MapMarker;
import lk.steps.breakdownassist.GpsModules.DirectionFinder;
import lk.steps.breakdownassist.GpsModules.DirectionFinderListener;
import lk.steps.breakdownassist.GpsModules.Route;
import lk.steps.breakdownassist.DBHandler;
import lk.steps.breakdownassist.R;

public class GmapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener,
        LocationListener, GoogleMap.OnCameraMoveStartedListener, DirectionFinderListener {

    public GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    long lMap_lastUserInteractionTime = 0;

    Map Marker_by_BD_Id_OnMap = new WeakHashMap<String, Marker>(); //BD_Id is the key
    Map BD_Id_by_Marker_OnMap = new WeakHashMap<Marker, String>(); //Marker is the key

    LatLng lastlocation = new LatLng(7, 80);
    DBHandler dbHandler;

    final public int MAP_STYLE_NIGHT = 1;
    final public int MAP_STYLE_NORMAL = 2;

    private int iMap_Style = MAP_STYLE_NORMAL;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    Button buttonRestoreMap;

    Boolean bReCenterMap = true;
    Boolean bDontReCenterMap = false;

    boolean isDirectionsJustStarted = false;

    private Context mContext;

    private int iJobs_to_Display = Breakdown.JOB_NOT_ATTENDED;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ManagePermissions.CheckAndRequestAllRuntimePermissions(getActivity().getApplicationContext(), getActivity());
        return inflater.inflate(R.layout.fragment_gmaps, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbHandler = new DBHandler(getActivity().getApplicationContext(), null, null, 1);

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
        }

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            //listBreakdownsOnMap.clear();
            // listBreakdownsOnMap=(HashMap<String,Breakdown>)savedInstanceState.getSerializable("Data");

        } else {
            // Probably initialize members with default values for a new instance
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Adding the pointList arraylist to Bundle
        // savedInstanceState.putSerializable("Data",listBreakdownsOnMap);
        //outState.putParcelableArrayList("points", pointList);
        super.onSaveInstanceState(savedInstanceState);

        //TODO : Save the location of the current position of map and recall it from the saved location
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        buttonRestoreMap = (Button) getActivity().findViewById(R.id.buttonReCenterMap);
        buttonRestoreMap.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReCenterMap();
                    }
                }
        );
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String sID = intent.getExtras().getString("_id"); //Breakdown ID, not ID in Customer Table or the SMS inbox ID
            //TODO : If SMS has an ACCT_NUM and GPS data is available with us include it in the Map and SMS log,otherwise put to the SMS log only
            RefreshJobsFromDB();
        }
    };

    public void MapManuallyMoved() {
        bReCenterMap = false; //for the button
        bDontReCenterMap = true;
        buttonRestoreMap.setVisibility(View.VISIBLE);   //Shows a Re-Center Button like in Google Navigation
        // and stop map current location track temporary, after timeout start again and hide the button
    }

    public void ReBoundMap(){
        Location currentLocation = getLastLocation();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Object item : BD_Id_by_Marker_OnMap.keySet()) {
            if (item != null) { // <- Is this test necessary?
                Marker marker = (Marker) item;
                builder.include(marker.getPosition());
            }
        }
        if (currentLocation != null) {
            builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
    }

    public void ReCenterMap() {

        bReCenterMap = true;
        bDontReCenterMap = false;
        buttonRestoreMap.setVisibility(View.INVISIBLE);

        Location currentLocation = getLastLocation();

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            //Change the Map orientation like in Google Navigation according to the movement direction
            float zoom = mMap.getCameraPosition().zoom;
            float tilt = 20.0F;
            float bearing = currentLocation.getBearing();

            //TODO : Try to keep the zoomed values if possible within a given range
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, tilt, bearing)));

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.jobs_to_display_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_jobs_all) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_STATUS_ANY;
            RefreshJobsFromDB();
            return true;
        } else if (id == R.id.menu_jobs_completed) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_COMPLETED;
            RefreshJobsFromDB();
            return true;
        } else if (id == R.id.menu_jobs_unatended) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display = Breakdown.JOB_NOT_ATTENDED;
            RefreshJobsFromDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Location getLastLocation() {
        Location location = null;
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        return location;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHandler.close();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


    @Override
    public void onCameraMoveStarted(int reason) {

        if ((reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE ||
                reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_API_ANIMATION) && !isDirectionsJustStarted) {
            lMap_lastUserInteractionTime = System.currentTimeMillis();//Record the time now
            MapManuallyMoved();
        }
        if (isDirectionsJustStarted)
            isDirectionsJustStarted = false;
    }

    public void ApplyNightMode() {
        if (iMap_Style != MAP_STYLE_NIGHT) {
            //TODO Depending upon the time or user preference
            MapStyleOptions NightMode_style = MapStyleOptions.loadRawResourceStyle(
                    getActivity().getApplicationContext(), R.raw.style_json_nightmode);
            mMap.setMapStyle(NightMode_style);
            iMap_Style = MAP_STYLE_NIGHT;
        }
    }

    public void ApplyDayMode() {
        //TODO Depending upon the time or user preference
        if (iMap_Style != MAP_STYLE_NORMAL) {
            MapStyleOptions DayMode_style = MapStyleOptions.loadRawResourceStyle(
                    getActivity().getApplicationContext(), R.raw.style_json_normal);
            mMap.setMapStyle(DayMode_style);
            iMap_Style = MAP_STYLE_NORMAL;
        }
    }

    public void SetTrafficON() {
        mMap.setTrafficEnabled(true);
    }

    public void SetTrafficOFF() {
        mMap.setTrafficEnabled(false);
    }

    public void FocusBreakdown(Breakdown breakdown) {
        if (breakdown.get_Status() == Breakdown.JOB_NOT_ATTENDED) {
            iJobs_to_Display = Breakdown.JOB_NOT_ATTENDED;
        } else if (breakdown.get_Status() == Breakdown.JOB_COMPLETED) {
            iJobs_to_Display = Breakdown.JOB_COMPLETED;
        } else {
            iJobs_to_Display = Breakdown.JOB_STATUS_ANY;
        }
        //Log.d("GMAP","4");
        RefreshJobsFromDB();
        Marker selectedMarker = (Marker) Marker_by_BD_Id_OnMap.get(breakdown.get_id());
        if (selectedMarker != null) {
            //Log.d("GMAP","3");
            mMap.moveCamera(CameraUpdateFactory.newLatLng(selectedMarker.getPosition()));
            MapManuallyMoved();
            selectedMarker.showInfoWindow();
        }
    }

    public void ApplyMapDayNightModeAccordingly() {
        //Change only if the AutoMode in ON and apply once if not already applied the Night/Day mode
        if (Globals.getNightMode().equalsIgnoreCase("1")) {//Automatic
            Calendar calendar = Calendar.getInstance();
            final SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("HH:mm");
            final String strCurrentTime = simpleDateFormat.format(calendar.getTime());

            try {
                Date MorningTime = simpleDateFormat.parse("05:47");
                Date NightTime = simpleDateFormat.parse("18:25");

                Date userDate = simpleDateFormat.parse(strCurrentTime);
                if (userDate.after(NightTime) || userDate.before(MorningTime)) {
                    ApplyNightMode();
                } else if (userDate.after(MorningTime) || userDate.before(NightTime)) {
                    ApplyDayMode();
                }

            } catch (Exception e) {/* Invalid date was entered*/}
        } else if (Globals.getNightMode().equalsIgnoreCase("0")) {//Always
            ApplyNightMode();
        } else if (Globals.getNightMode().equalsIgnoreCase("-1")) {//Never
            ApplyDayMode();
        }
    }

    //TODO : Draw the markers once the screen in rotated, using a markers array, or harsh tag, marker list
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setTrafficEnabled(true);//TODO : call from the preferences

        ApplyMapDayNightModeAccordingly();

        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnMarkerClickListener(this);
        // mMap.setOnInfoWindowClickListener(this);
        RefreshJobsFromDB();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastlocation));
        // mMap.setInfoWindowAdapter(new InfoWindowAdapter(getActivity().getLayoutInflater()));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                final Breakdown selectedBreakdown = dbHandler.ReadBreakdown_by_ID((String) BD_Id_by_Marker_OnMap.get(marker));
                JobView.DialogInfo(GmapFragment.this, selectedBreakdown, marker, getLastLocation(), 0);
                marker.hideInfoWindow();
            }
        });
    }
//TODO: DO not show the normal dialog for marker click
    /*public void AddCustomerLocationToMap(String Account_Num) {
        Breakdown newBreakdown = dbHandler.ReadCustomer_by_ACCT_NUM(Account_Num);
        if (newBreakdown!=null){
            Marker CreatedMarker = AddBreakDownToMap(newBreakdown,
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            if (CreatedMarker!=null){
                mMap.animateCamera(CameraUpdateFactory.newLatLng(CreatedMarker.getPosition()));
                CreatedMarker.showInfoWindow();
            }
        }
        else
            Toast.makeText(getActivity().getApplicationContext(),"Search Failed, Try again",Toast.LENGTH_SHORT);
    }*/


    public Marker AddBreakDownToMap(Breakdown breakdown) /*int icon*/ {
        Marker CreatedMarker = null;//For Return
        BitmapDescriptor MarkerICON = MapMarker.GetBitmap(breakdown);

        Marker bdMarker;
        //To Avoid adding a same BD marker more than once to the map, may be check with the hash map
        if (breakdown.getLocation() != null) {
            if (!Marker_by_BD_Id_OnMap.containsKey(breakdown.get_id())) {/*mMarker.get(bd)==null*/
                bdMarker = mMap.addMarker(new MarkerOptions()
                        .position(breakdown.getLocation())
                        .title(breakdown.get_Job_No())
                        //.icon(MarkerICON) //TODO : Depending on the priority mark the color
                        //.icon(BitmapDescriptorFactory.fromResource(icon))
                        .icon(MarkerICON)
                        //.snippet(breakdown.get_Name() + "\n" + breakdown.get_ADDRESS() + "\n\n" + breakdown.get_Full_Description()));
                        .snippet(breakdown.get_ADDRESS().trim()));

                //We have two HarshMaps for search either from Marker or breakdown.ID
                Marker_by_BD_Id_OnMap.put(breakdown.get_id(), bdMarker); //(key,marker)
                BD_Id_by_Marker_OnMap.put(bdMarker, breakdown.get_id()); //(key,data)

                lastlocation = breakdown.getLocation();
                CreatedMarker = bdMarker;
            }
        } else {
            //Add to ICON/Widget for No location
        }
        return CreatedMarker;
    }

    public void AddBreakDownListToMap(List<Breakdown> breakdownList) {
        for (Breakdown bd : breakdownList) {
            AddBreakDownToMap(bd);
        }
        //SetMapBound(breakdownList);
    }

    private void SetMapBound(List<Breakdown> breakdownList) {
        //the include method will calculate the min and max bound.
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Breakdown bd : breakdownList) {
            double latCentre = Double.parseDouble(bd.get_LATITUDE());
            double lonCentre = Double.parseDouble(bd.get_LONGITUDE());
            builder.include(new LatLng(latCentre, lonCentre));
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    public void RefreshJobsFromDB() {
        if (mMap != null) {
            mMap.clear();  //Clear all the overlays
/*            mMarker.clear();//
            mBreakdown.clear();*/

            BD_Id_by_Marker_OnMap.clear();
            Marker_by_BD_Id_OnMap.clear();
        }
        AddBreakDownListToMap(dbHandler.ReadBreakdowns(iJobs_to_Display, true));
        ReBoundMap();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mContext = getActivity().getApplicationContext(); // To use in the startLocationUpdates(), otherwise it crashes when
        //getActivity().getApplicationContext() is used
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); //TODO : Test the requirement of High Accuracy

        startLocationUpdates();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext.registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassist.NewBreakdownBroadcast"));
        // Resuming the periodic location updates
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(broadcastReceiver);
        stopLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        if (mMap != null) {

            //TODO : Only zoom to nearest breakdown location, if no breakdown nearby (within 5km) zoom to level 16
            //TODO : Show a nearest breakdown location by a direction Arrow icon, floating action button

            long lCurTime = System.currentTimeMillis();

            // if (last_update_time/ user last interacted time with map - current_time)>1min Zoom and move back to current location
            // TODO :else update only the Vehicle Icon (if exists)
            if (((Math.abs(lCurTime - lMap_lastUserInteractionTime) > 60000) || bReCenterMap) && !bDontReCenterMap) {

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                //Change the Map orientation like in Google Navigation according to the movement direction
                float zoom = mMap.getCameraPosition().zoom;
                float tilt = 20.0F;
                float bearing = location.getBearing();

                //TODO : Try to keep the zoomed values if possible within a given range
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, tilt, bearing)));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                if (zoom > 17 || zoom < 15.5) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }
            }

        }

    }

    /*@Override
    public boolean onMarkerClick(Marker marker) {
        //TODO : Create a Marker object and assign a Tag using setTag, then when removing getTag and remove it from db
        //send to main activity to update the status after the user confirmation

        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
        return true;
    }*/

    @Override
    public boolean onMarkerClick(final Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        //final Marker selectedMarker = marker;  //to access in Override Methods
        //Calling from Harshmap by giving the Marker Ref
        final Breakdown selectedBreakdown = dbHandler.ReadBreakdown_by_ID((String) BD_Id_by_Marker_OnMap.get(marker));

        JobView.DialogInfo(this, selectedBreakdown, marker, getLastLocation(), 0);
        marker.hideInfoWindow();

        return true;
    }


    public void UpdateBreakDown(Breakdown selectedBreakdown, int iStatus) {
        dbHandler.UpdateBreakdownStatus(selectedBreakdown, iStatus);
        RefreshJobsFromDB();
        //TODO : Add methods to handle other status like Visited etc may be with custom time, then change the marker
    }

    @Override
    public void onDirectionFinderStart() {
        clearDirections();
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "Finding direction..!", true);

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            ((LinearLayout) getActivity().findViewById(R.id.directions)).setVisibility(View.VISIBLE);

            ((TextView) getActivity().findViewById(R.id.tvDuration)).setText("Duration : " + route.duration.text);
            ((TextView) getActivity().findViewById(R.id.tvDistance)).setText("Distance : " + route.distance.text);
            Button buttonClearDirection = (Button) getActivity().findViewById(R.id.buttonCLR);
            buttonClearDirection.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clearDirections();
                        }
                    }
            );

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
            isDirectionsJustStarted = true;
            ReCenterMap();
        }
    }

    public void clearDirections() {
        ((LinearLayout) getActivity().findViewById(R.id.directions)).setVisibility(View.INVISIBLE);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    public void getDirections(LatLng origin, LatLng destination) {
        //TODO : Exception when current location is not available
        try {
            String sOrigin = String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude);
            String sDestination = String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude);
            new DirectionFinder(this, sOrigin, sDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}

