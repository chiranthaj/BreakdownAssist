package lk.steps.breakdownassistpluss.RecyclerViewCards;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Fragments.JobListFragment;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.MapMarker;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinder;
import lk.steps.breakdownassistpluss.GpsModules.DirectionFinderListener;
import lk.steps.breakdownassistpluss.GpsModules.Route;
import lk.steps.breakdownassistpluss.R;

import static lk.steps.breakdownassistpluss.RecyclerViewCards.JobsRecyclerAdapter.onItemTouchListener;

/**
 * Created by JagathPrasanga on 2017-07-02.
 */

public class MapLocationViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback , DirectionFinderListener {

    protected GoogleMap mGoogleMap;
    protected Breakdown mBreakdown;
    public Context mContext;
    public TextView acc_no,JOB_NO,received_date_time,completed_date_time,name,address,description, txtTripInfo, note;
    private Button button1,button2;
    public CheckBox checkBox1;
    //public ImageView imgMap;
    public ImageView imgSource;
    public ImageView imgPriority;
    public CardView cardView;
    public MapView mapView;
    public ChildListView childrenList;


    public MapLocationViewHolder(Context context, View itemLayoutView) {
        super(itemLayoutView);
        mContext = context;
        cardView = (CardView) itemLayoutView.findViewById(R.id.card_view);
        acc_no = (TextView) itemLayoutView.findViewById(R.id.acct_num);
        JOB_NO = (TextView)itemLayoutView.findViewById(R.id.JOB_NO);
        received_date_time = (TextView) itemLayoutView.findViewById(R.id.received_date_time);
        completed_date_time = (TextView)itemLayoutView.findViewById(R.id.completed_date_time);
        name = (TextView) itemLayoutView.findViewById(R.id.name);
        address = (TextView) itemLayoutView.findViewById(R.id.address);
        //imgMap = (ImageView) itemLayoutView.findViewById(R.id.imgMap);
        imgSource= (ImageView) itemLayoutView.findViewById(R.id.imgSource);
        imgPriority = (ImageView) itemLayoutView.findViewById(R.id.imgPriority);
        note = (TextView)itemLayoutView.findViewById(R.id.txtNote);
        description = (TextView)itemLayoutView.findViewById(R.id.description);
        mapView = (MapView) itemView.findViewById(R.id.map_view);
        txtTripInfo = (TextView) itemLayoutView.findViewById(R.id.txtTripInfo);
        button1 = (Button) itemView.findViewById(R.id.card_view_button1);
        button2 = (Button) itemView.findViewById(R.id.card_view_button2);
        checkBox1 = (CheckBox) itemView.findViewById(R.id.card_view_checkBox1);
        childrenList= (ChildListView) itemView.findViewById(R.id.childrenList);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemTouchListener.onCardViewTap(v, getLayoutPosition());
            }
        });
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               // Log.e("YYY","8888 long");
                onItemTouchListener.onCardViewLongTap(v, getLayoutPosition());
                return true;
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
        //mGoogleMap.setMapStyle(
        //        MapStyleOptions.loadRawResourceStyle(mContext, R.raw.style_json_silver));
        // If we have map data, update the map content.
        if (mBreakdown != null) {
            updateMapContents();
        }
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                cardView.performClick();
            }
        });

    }

    private void updateMapContents() {
        // Since the mapView is re-used, need to remove pre-existing mapView features.

        mGoogleMap.clear();
        if(mBreakdown.get_Status()== Breakdown.JOB_COMPLETED){
            mapView.setVisibility(View.GONE);
            txtTripInfo.setVisibility(View.GONE);
       // }else if(mBreakdown.get_LATITUDE()== null | mBreakdown.get_LONGITUDE()== null){
        }else if(mBreakdown.getLocation() == null){
            mapView.setVisibility(View.GONE);
            txtTripInfo.setVisibility(View.GONE);
        }else if(mBreakdown.getLatitude().equals("0") | mBreakdown.getLongitude().equals("0")){
            mapView.setVisibility(View.GONE);
            txtTripInfo.setVisibility(View.GONE);
        }else if(JobListFragment.currentLocation != null & mBreakdown.getLocation() != null){
            BitmapDescriptor icon = MapMarker.GetBitmap(mBreakdown);
            BitmapDescriptor iconBk = BitmapDescriptorFactory.fromResource(R.drawable.breakdown_vehicle);
            setDirections(Globals.LastLocation,mBreakdown.getLocation());
            mGoogleMap.addMarker(new MarkerOptions().position(mBreakdown.getLocation()).icon(icon));
            mGoogleMap.addMarker(new MarkerOptions().position(Globals.LastLocation).icon(iconBk));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(Globals.LastLocation);
            builder.include(mBreakdown.getLocation());

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
        }
    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        if(routes.size()<1) return;
        LatLng bdLoc = mBreakdown.getLocation();
        if(bdLoc==null)return;
       // List<Polyline> polylinePaths = new ArrayList<>();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(JobListFragment.currentLocation);
        builder.include(bdLoc);

        PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.BLUE).width(4);
        for (Route route : routes) {
            for (int i = 0; i < route.points.size(); i=i+10){//Remove some points
                polylineOptions.add(route.points.get(i));
                builder.include(route.points.get(i));
            }
        }
        mGoogleMap.addPolyline(polylineOptions);
        //polylinePaths.add(mGoogleMap.addPolyline(polylineOptions));
        //txtTripInfo.setText("Distance : "+routes.get(0).distance.text + "\nTime : "+routes.get(0).duration.text);
       // txtTripInfo.setText(routes.get(0).duration.text + " ( "+routes.get(0).distance.text+" )");
        String text = "<font color='blue'>"+routes.get(0).duration.text + "</font><font color='gray'> ( "+routes.get(0).distance.text+" ) </font>";
        txtTripInfo.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));

    }

    private void setDirections(LatLng origin, LatLng destination) {
        try {
            if(origin == null ) return;
            String sOrigin=String.valueOf(origin.latitude) + ","+ String.valueOf(origin.longitude);
            String sDestination=String.valueOf(destination.latitude) + ","+ String.valueOf(destination.longitude);
            new DirectionFinder(this,sOrigin , sDestination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
