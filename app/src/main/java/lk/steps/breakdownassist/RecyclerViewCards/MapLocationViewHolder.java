package lk.steps.breakdownassist.RecyclerViewCards;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.Fragments.JobListFragment;
import lk.steps.breakdownassist.MapMarker;
import lk.steps.breakdownassist.GpsModules.DirectionFinder;
import lk.steps.breakdownassist.GpsModules.DirectionFinderListener;
import lk.steps.breakdownassist.GpsModules.Route;
import lk.steps.breakdownassist.R;

import static lk.steps.breakdownassist.RecyclerViewCards.JobsRecyclerAdapter.onItemTouchListener;

/**
 * Created by JagathPrasanga on 2017-07-02.
 */

public class MapLocationViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback , DirectionFinderListener {

    protected GoogleMap mGoogleMap;
    protected Breakdown mBreakdown;
    public Context mContext;
    public TextView acc_no,job_no,received_date_time,completed_date_time,name,address,description, txtTripInfo;
    private Button button1,button2;
    public CheckBox checkBox1;
    public ImageView imgMap;
    public CardView cardView;
    public MapView mapView;

    public MapLocationViewHolder(Context context, View itemLayoutView) {
        super(itemLayoutView);
        mContext = context;
        cardView = (CardView) itemLayoutView.findViewById(R.id.card_view);
        acc_no = (TextView) itemLayoutView.findViewById(R.id.acct_num);
        job_no = (TextView)itemLayoutView.findViewById(R.id.job_no);
        received_date_time = (TextView) itemLayoutView.findViewById(R.id.received_date_time);
        completed_date_time = (TextView)itemLayoutView.findViewById(R.id.completed_date_time);
        name = (TextView) itemLayoutView.findViewById(R.id.name);
        address = (TextView) itemLayoutView.findViewById(R.id.address);
        imgMap = (ImageView) itemLayoutView.findViewById(R.id.imgMap);
        description = (TextView)itemLayoutView.findViewById(R.id.description);
        mapView = (MapView) itemView.findViewById(R.id.map_view);
        txtTripInfo = (TextView) itemLayoutView.findViewById(R.id.txtTripInfo);
        button1 = (Button) itemView.findViewById(R.id.card_view_button1);
        button2 = (Button) itemView.findViewById(R.id.card_view_button2);
        checkBox1 = (CheckBox) itemView.findViewById(R.id.card_view_checkBox1);

        itemLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemTouchListener.onCardViewTap(v, getLayoutPosition());
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemTouchListener.onButton1Click(v, getLayoutPosition());
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemTouchListener.onButton2Click(v, getLayoutPosition());
            }
        });
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemTouchListener.onCheckBox1Click(v, getLayoutPosition());
            }
        });


        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    public void setBreakdown(Breakdown breakdown) {
        mBreakdown = breakdown;

        // If the map is ready, update its content.
        if (mGoogleMap != null) {
            updateMapContents();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        MapsInitializer.initialize(mContext);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // If we have map data, update the map content.
        if (mBreakdown != null) {
            updateMapContents();
        }
    }

    private void updateMapContents() {
        // Since the mapView is re-used, need to remove pre-existing mapView features.

        mGoogleMap.clear();
        if(mBreakdown.get_Status()== Breakdown.JOB_COMPLETED){
            mapView.setVisibility(View.GONE);
            txtTripInfo.setVisibility(View.GONE);
        }else
            if(mBreakdown.get_LATITUDE()== null | mBreakdown.get_LONGITUDE()== null){
            mapView.setVisibility(View.GONE);
            txtTripInfo.setVisibility(View.GONE);
        }else if(mBreakdown.get_LATITUDE().equals("0") | mBreakdown.get_LONGITUDE().equals("0")){
            mapView.setVisibility(View.GONE);
            txtTripInfo.setVisibility(View.GONE);
        }else{
            BitmapDescriptor icon = MapMarker.GetBitmap(mBreakdown);
            BitmapDescriptor iconBk = BitmapDescriptorFactory.fromResource(R.drawable.breakdown_vehicle);
            setDirections(JobListFragment.currentLocation,mBreakdown.get_location());
            mGoogleMap.addMarker(new MarkerOptions().position(mBreakdown.get_location()).icon(icon));
            mGoogleMap.addMarker(new MarkerOptions().position(JobListFragment.currentLocation).icon(iconBk));
        }
    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        List<Polyline> polylinePaths = new ArrayList<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(JobListFragment.currentLocation);
        builder.include(mBreakdown.get_location());

        PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.BLUE).width(4);
        for (Route route : routes) {
            for (int i = 0; i < route.points.size(); i=i+10){//Remove some points
                polylineOptions.add(route.points.get(i));
                builder.include(route.points.get(i));
            }
        }
        polylinePaths.add(mGoogleMap.addPolyline(polylineOptions));
        txtTripInfo.setText("Distance : "+routes.get(0).distance.text + "\nTime : "+routes.get(0).duration.text);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));

    }

    private void setDirections(LatLng origin, LatLng destination) {
        try {
            String sOrigin=String.valueOf(origin.latitude) + ","+ String.valueOf(origin.longitude);
            String sDestination=String.valueOf(destination.latitude) + ","+ String.valueOf(destination.longitude);
            new DirectionFinder(this,sOrigin , sDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
