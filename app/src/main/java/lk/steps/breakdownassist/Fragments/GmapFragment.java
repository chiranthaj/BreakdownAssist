package lk.steps.breakdownassist.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
import lk.steps.breakdownassist.BreakdownView;
import lk.steps.breakdownassist.Globals;
import lk.steps.breakdownassist.ManagePermissions;
import lk.steps.breakdownassist.Modules.DirectionFinder;
import lk.steps.breakdownassist.Modules.DirectionFinderListener;
import lk.steps.breakdownassist.Modules.Route;
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
    Map BD_Id_by_Marker_OnMap = new WeakHashMap<Marker,String>(); //Marker is the key

    LatLng lastlocation = new LatLng(7, 80);
    DBHandler dbHandler;

    final public int MAP_STYLE_NIGHT=1;
    final public int MAP_STYLE_NORMAL =2;

    private int iMap_Style= MAP_STYLE_NORMAL;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    Button buttonRestoreMap;

    Boolean bRestoreMap = true;

    boolean isDirectionsJustStarted = false;

    private Context mContext;

    private int iJobs_to_Display=Breakdown.Status_JOB_NOT_ATTENDED;

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


        buttonRestoreMap = (Button) getActivity().findViewById(R.id.buttonRestoreMap);
        buttonRestoreMap.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReStoreMap();
                    }
                }
        );
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String sID=intent.getExtras().getString("_id"); //Breakdown ID, not ID in Customer Table or the SMS inbox ID
            //TODO : If SMS has an ACCT_NUM and GPS data is available with us include it in the Map and SMS log,otherwise put to the SMS log only
            RefreshJobsFromDB();
        }
    };
    public void ReStoreMap(){

        bRestoreMap = true;
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
            iJobs_to_Display=Breakdown.Status_JOB_ANY;
            RefreshJobsFromDB();
            return true;
        }else if (id == R.id.menu_jobs_completed) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display=Breakdown.Status_JOB_COMPLETED;
            RefreshJobsFromDB();
            return true;
        }else if (id == R.id.menu_jobs_unatended) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            iJobs_to_Display=Breakdown.Status_JOB_NOT_ATTENDED;
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
            bRestoreMap = false; //for the button
            buttonRestoreMap.setVisibility(View.VISIBLE);   //Shows a Re-Center Button like in Google Navigation
            // and stop map current location track temporary, after timeout start again and hide the button
        }
        if (isDirectionsJustStarted)
            isDirectionsJustStarted = false;
    }

    public void ApplyNightMode() {
        if (iMap_Style!=MAP_STYLE_NIGHT){
            //TODO Depending upon the time or user preference
            MapStyleOptions NightMode_style = MapStyleOptions.loadRawResourceStyle(
                    getActivity().getApplicationContext(), R.raw.style_json_nightmode);
            mMap.setMapStyle(NightMode_style);
            iMap_Style=MAP_STYLE_NIGHT;
        }
    }
    public void ApplyDayMode() {
        //TODO Depending upon the time or user preference
        if (iMap_Style!= MAP_STYLE_NORMAL){
            MapStyleOptions DayMode_style = MapStyleOptions.loadRawResourceStyle(
                    getActivity().getApplicationContext(), R.raw.style_json_normal);
            mMap.setMapStyle(DayMode_style);
            iMap_Style= MAP_STYLE_NORMAL;
        }
    }
    public void SetTrafficON(){
        mMap.setTrafficEnabled(true);
    }
    public void SetTrafficOFF(){
        mMap.setTrafficEnabled(false);
    }

    public void FocusBreakdown(Breakdown breakdown){
        if (breakdown.get_Status()==Breakdown.Status_JOB_NOT_ATTENDED){
            iJobs_to_Display=Breakdown.Status_JOB_NOT_ATTENDED;
        }else if (breakdown.get_Status()==Breakdown.Status_JOB_COMPLETED){
            iJobs_to_Display=Breakdown.Status_JOB_COMPLETED;
        }else {
            iJobs_to_Display=Breakdown.Status_JOB_ANY;
        }
        Log.d("GMAP","4");
        RefreshJobsFromDB();
        Marker selectedMarker = (Marker) Marker_by_BD_Id_OnMap.get(breakdown.get_id());
        if(selectedMarker!=null){
            Log.d("GMAP","3");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(selectedMarker.getPosition()));
            selectedMarker.showInfoWindow();
        }
    }
    public void ApplyMapDayNightModeAccordingly(){
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
        }
        else if (Globals.getNightMode().equalsIgnoreCase("0")) {//Always
            ApplyNightMode();
        }
        else if (Globals.getNightMode().equalsIgnoreCase("-1")) {//Never
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
    }
//TODO: DO not show the normal dialog for marker click
    public void AddCustomerLocationToMap(String Account_Num) {
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
    }



    public Marker AddBreakDownToMap(Breakdown breakdown){
        //TODO : Check this later @Jagath
        /*int icon = R.drawable.factory;
        if(breakdown.get_TARIFF_COD()=="11"){
            icon = R.drawable.factory;
        }
        return AddBreakDownToMap(breakdown,icon);*/

        BitmapDescriptor MarkerICON;//TODO : Depending on the priority,and current status mark the colour  and the shape
        if (breakdown.get_Status()==Breakdown.Status_JOB_COMPLETED){
            if(breakdown.get_TARIFF_COD() == null)
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.house2);
            else if(breakdown.get_TARIFF_COD().equals("11") | breakdown.get_TARIFF_COD().equals("13")) // Domestic
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.house2);
            else if(breakdown.get_TARIFF_COD().equals("21") | breakdown.get_TARIFF_COD().equals("22")) // Industrial
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory2);
            else if(breakdown.get_TARIFF_COD().equals("31") | breakdown.get_TARIFF_COD().equals("32")) // General
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop2);
            else
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.house2);
        }else{
            if(breakdown.get_TARIFF_COD() == null)
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.house);
            else if(breakdown.get_TARIFF_COD().equals("11") | breakdown.get_TARIFF_COD().equals("13")) // Domestic
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.house);
            else if(breakdown.get_TARIFF_COD().equals("21") | breakdown.get_TARIFF_COD().equals("22")) // Industrial
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.factory);
            else if(breakdown.get_TARIFF_COD().equals("31") | breakdown.get_TARIFF_COD().equals("32")) // General
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.shop);
            else
                MarkerICON = BitmapDescriptorFactory.fromResource(R.drawable.house);
        }
        return AddBreakDownToMap(breakdown,MarkerICON);

    }

    public Marker AddBreakDownToMap(Breakdown breakdown, BitmapDescriptor MarkerICON) /*int icon*/ {
        Marker CreatedMarker=null;//For Return


        Marker bdMarker;
        //To Avoid adding a same BD marker more than once to the map, may be check with the hash map
        if (breakdown.getLocation() != null) {
            if (!Marker_by_BD_Id_OnMap.containsKey(breakdown.get_id())) {/*mMarker.get(bd)==null*/
                bdMarker = mMap.addMarker(new MarkerOptions()
                        .position(breakdown.getLocation())
                        .title(breakdown.get_Job_No() + " - " + breakdown.get_Acct_Num())

                        //.icon(MarkerICON) //TODO : Depending on the priority mark the color
                        //.icon(BitmapDescriptorFactory.fromResource(icon))

                        .icon(MarkerICON)
                        .snippet(breakdown.get_Name() + "\n" + breakdown.get_ADDRESS() + "\n\n" + breakdown.get_Full_Description()));

                //We have two HarshMaps for search either from Marker or breakdown.ID
                Marker_by_BD_Id_OnMap.put(breakdown.get_id(), bdMarker) ; //(key,marker)
                BD_Id_by_Marker_OnMap.put(bdMarker,breakdown.get_id())  ; //(key,data)

                lastlocation = breakdown.getLocation();
                CreatedMarker=bdMarker;
            }
        } else {
            //Add to ICON/Widget for No location
        }
        return CreatedMarker;
    }

    public void AddBreakDownListToMap(List<Breakdown> breakdownlist) {
        for (Breakdown bd : breakdownlist) {
            AddBreakDownToMap(bd);
        }
    }

    public void RefreshJobsFromDB() {
        if (mMap != null) {
            mMap.clear();  //Clear all the overlays
/*            mMarker.clear();//
            mBreakdown.clear();*/

            BD_Id_by_Marker_OnMap.clear();
            Marker_by_BD_Id_OnMap.clear();
        }
        AddBreakDownListToMap(dbHandler.ReadBreakdowns(iJobs_to_Display));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mContext=getActivity().getApplicationContext(); // To use in the startLocationUpdates(), otherwise it crashes when
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
        if (mGoogleApiClient != null &&  mGoogleApiClient.isConnected() ) {
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
     * */
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (mGoogleApiClient !=null && mGoogleApiClient.isConnected())
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
            if (mGoogleApiClient !=null && mGoogleApiClient.isConnected())
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
            if ((Math.abs(lCurTime - lMap_lastUserInteractionTime) > 60000) || bRestoreMap) {

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
        final Marker selectedMarker = marker;  //to access in Override Methods
        //Calling from Harshmap by giving the Marker Ref
        final Breakdown selectedBreakdown = dbHandler.ReadBreakdown_by_ID ((String) BD_Id_by_Marker_OnMap.get(selectedMarker));

        BreakdownView.Dialog(this,selectedBreakdown,selectedMarker,getLastLocation());

        /*final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_dialog);
        //dialog.setTitle("Job Details");

        TextView txtJobno = (TextView) dialog.findViewById(R.id.jobno);
        txtJobno.setText(selectedBreakdown.get_Job_No().trim());
        TextView txtRecTime = (TextView) dialog.findViewById(R.id.received_date_time);
        txtRecTime.setText("Received time : " + selectedBreakdown.get_Received_Time().trim());
        TextView txtAcctNum = (TextView) dialog.findViewById(R.id.acctnum);
        txtAcctNum.setText("Acc. No. : "+selectedBreakdown.get_Acct_Num().trim());
        TextView txtName = (TextView) dialog.findViewById(R.id.name);
        txtName.setText(selectedBreakdown.get_Name().trim() + "\n" + selectedBreakdown.get_ADDRESS().trim());

        TextView txtPhoneNo = (TextView) dialog.findViewById(R.id.phoneno);
        txtPhoneNo.setText(selectedBreakdown.get_Contact_No().trim());

        TextView txtFullDescription = (TextView) dialog.findViewById(R.id.fulldescription);
        //txtFullDescription.setText(selectedBreakdown.get_Full_Description().trim());
        txtFullDescription.setText("");

        ImageButton dialogButton_Complete = (ImageButton) dialog.findViewById(R.id.dialogButtonCompleted);
        // if button is clicked, close the job_dialog dialog
        dialogButton_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BreakdownFeedbackDialog(selectedMarker);
                // UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                //TODO : Use an Undo option
                dialog.dismiss();
            }
        });
        ImageButton dialogButton_visited = (ImageButton) dialog.findViewById(R.id.dialogButtonVisited);
        // if button is clicked, close the job_dialog dialog
        dialogButton_visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_VISITED);
                //TODO : Use an Undo option
                dialog.dismiss();
            }
        });
        ImageButton dialogButton_navigate = (ImageButton) dialog.findViewById(R.id.dialogButtonNavigate);
        // if button is clicked, close the job_dialog dialog
        dialogButton_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMarker.hideInfoWindow();
                Toast.makeText(getActivity().getApplicationContext(),"Press and Hold for Google Navigation !!",
                        Toast.LENGTH_SHORT).show();
                Location currentLocation=getLastLocation();
                if (currentLocation!=null){
                    getDirections(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),selectedMarker.getPosition() );
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Current location is not available, Please try again",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        dialogButton_navigate.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),"Opening Google Navigation...",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + selectedMarker.getPosition().latitude + "," +
                                selectedMarker.getPosition().longitude ));
                startActivity(intent);
                dialog.dismiss();
                return true;
            }

        });


        ImageButton btnCall = (ImageButton) dialog.findViewById(R.id.btnMakeCall);
        // if button is clicked, close the job_dialog dialog
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+selectedBreakdown.get_Contact_No().trim()));
                startActivity(intent);
            }
        });


        dialog.show();*/
        return true;
    }

    public void BreakdownFeedbackDialog(final Marker marker){
        final Marker selectedMarker = marker;  //to access in Override Methods
        //final Breakdown selectedBreakdown=(Breakdown) mBreakdown.get(selectedMarker); //Calling from Harshmap by giving the Marker Ref
        //Calling from Harshmap by giving the Marker Ref
        final Breakdown selectedBreakdown=dbHandler.ReadBreakdown_by_ID ((String) BD_Id_by_Marker_OnMap.get(selectedMarker));

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_feedback_dialog);
        //TODO : Use date time picker
        TextView txtView = (TextView) dialog.findViewById(R.id.jobInfo);
        txtView.setText(selectedBreakdown.get_Name()+"\n"+selectedBreakdown.get_ADDRESS());
        //Spinner
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.failure_type, R.layout.spinner_row );
        final Spinner spinner1 = (Spinner) dialog.findViewById(R.id.spinner1);
        spinner1.setAdapter(adapter1);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.failure_nature, R.layout.spinner_row );
        final Spinner spinner2 = (Spinner) dialog.findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);
        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.failure_cause, R.layout.spinner_row );
        final Spinner spinner3 = (Spinner) dialog.findViewById(R.id.spinner3);
        spinner3.setAdapter(adapter3);

        ImageButton dialogButton_Complete = (ImageButton) dialog.findViewById(R.id.dialogButtonCompleted);
        // if button is clicked, close the job_dialog dialog
        dialogButton_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(spinner1.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure type");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner2.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure nature");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else if(spinner3.getSelectedItemPosition() == 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Please select a Failure cause");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else{
                    UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                    Log.d("Reason ",spinner1.getSelectedItem().toString());
                    dialog.dismiss();
                }*/
                UpdateBreakDown(selectedBreakdown,Breakdown.Status_JOB_COMPLETED);
                Log.d("Reason ",spinner1.getSelectedItem().toString());
                dialog.dismiss();
                //TODO : Use an Undo option
            }
        });
        ImageButton dialogButton_visited = (ImageButton) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the job_dialog dialog
        dialogButton_visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : Use an Undo option
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void UpdateBreakDown(Breakdown selectedBreakdown,int iStatus) {
        dbHandler.UpdateBreakdownStatus(selectedBreakdown,iStatus);
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
            ((TextView) getActivity().findViewById(R.id.tvDistance)).setText("Distance : "+route.distance.text);
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
            isDirectionsJustStarted=true;
            ReStoreMap();
        }
    }

    public void clearDirections(){
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
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    public void getDirections(LatLng origin, LatLng destination) {
        //TODO : Exception when current location is not available
        try {
            String sOrigin=String.valueOf(origin.latitude) + ","+ String.valueOf(origin.longitude);
            String sDestination=String.valueOf(destination.latitude) + ","+ String.valueOf(destination.longitude);
            new DirectionFinder(this,sOrigin , sDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}

