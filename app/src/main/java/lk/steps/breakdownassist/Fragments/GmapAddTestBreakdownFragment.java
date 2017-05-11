package lk.steps.breakdownassist.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.ManagePermissions;
import lk.steps.breakdownassist.MyDBHandler;
import lk.steps.breakdownassist.R;

public class GmapAddTestBreakdownFragment extends Fragment implements OnMapReadyCallback ,
        GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;

    Map mMarker =new WeakHashMap<Breakdown,Marker>();
    Map mBreakdown =new WeakHashMap<Marker,Breakdown>();
    HashMap listBreakdownsOnMap=new HashMap<String,Breakdown>();
    ArrayList<Marker> listMarkersOnMap = new ArrayList<Marker>();

    LatLng lastlocation= new LatLng(7, 80);
    MyDBHandler dbHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ManagePermissions.CheckAndRequestAllRuntimePermissions(getActivity().getApplicationContext(),getActivity());
        return inflater.inflate(R.layout.fragment_gmaps, container,false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler=new MyDBHandler(getActivity().getApplicationContext(),null,null,1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    //TODO : Draw the markers once the screen in rotated, using a markers array, or harsh tag, marker list
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        RefreshMarkersFromDB();
        AddMarkers();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastlocation));
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getActivity().getLayoutInflater()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHandler.close();
    }
    // TODO : Should be added like in  new SMS BD add method, create a new ID
    public void AddBreakDownToMap(Breakdown breakdown) {
        add_breakdown(breakdown);
    }

    public void AddBreakDownListToMap(List<Breakdown> breakdownlist) {
        for(Breakdown bd : breakdownlist){
            AddBreakDownToMap(bd);
        }
    }

    public void RefreshMarkersFromDB(){
        AddBreakDownListToMap(dbHandler.ReadAllCustomers());
    }

    public void AddMarkers(){
        if (mMap!=null){
            for(Iterator<Breakdown> i =listBreakdownsOnMap.values().iterator(); i.hasNext();){
                Breakdown bd = i.next();
                Marker bdMarker;
                //To Avoid adding a same BD marker more than once to the map, may be check with the hash map and marker ID
                if (mMarker.containsKey(bd)==false){/*mMarker.get(bd)==null*/
                    bdMarker=mMap.addMarker(new MarkerOptions()
                            .position(bd.getLocation())
                            .title(bd.get_Acct_Num())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)) //TODO : Depending on the priority mark the color
                            .snippet(bd.get_Name() + "\n" + bd.get_ADDRESS() + "\n\n" + bd.get_Full_Description()  ));
                    //bdMarker.setTag(bd.get_id());

                    mMarker.put(bd,bdMarker); //Adding to Harshmap
                    mBreakdown.put(bdMarker,bd);//We have two HarshMaps for search either from Marker or Breakdown
                    lastlocation=bd.getLocation();
                }
            }
        }
    }

    public void add_breakdown(Breakdown bd){
        Marker bdMarker;
        listBreakdownsOnMap.put(bd.get_id(),bd);
        AddMarkers(); //this happenes only if (mMap!=null)
    }

    public void remove_breakdown(Breakdown breakdown){
        Marker selectedMarker =(Marker)mMarker.get(breakdown);
        listBreakdownsOnMap.remove(breakdown);
        //mMarker.remove(breakdown);
        //mBreakdown.remove(selectedMarker);
        selectedMarker.remove();
        //selectedMarker.setVisible(false);
    }

    /*TODO Check in Jelly Bean*/ /*TODO Remove it to a seperate class like Util.showMessageOKCancel*/
    private void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //TODO : Create a Marker object and assign a Tag using setTag, then when removing getTag and remove it from db
        //send to main activity to update the status after the user confirmation
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String message = "Add as a Breakdown ?" ;
        marker.hideInfoWindow();
        final Marker selectedMarker =marker;  //to access in Override Methods
        final Breakdown selectedBreakdown=(Breakdown) mBreakdown.get(selectedMarker); //Calling from Harshmap by giving the Marker Ref
        showMessageOKCancel(getActivity(),message,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {//TODO : Use Enum class to to have Breakdownstatus.Done like thing
                        dbHandler.AddTestBreakdownObj(selectedBreakdown,getActivity().getApplicationContext());
                        //TODO : Use an Undo option
                        Toast.makeText(getActivity().getApplicationContext()
                                , "Added Breakdown"  + selectedBreakdown.get_Name() + ", Undo ? "
                                , Toast.LENGTH_SHORT).show();

                    }
                });

    }



}

