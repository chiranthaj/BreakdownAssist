package lk.steps.breakdownassist.RecyclerViewCards;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.R;

import static lk.steps.breakdownassist.RecyclerViewCards.JobsRecyclerAdapter.onItemTouchListener;

/**
 * Created by JagathPrasanga on 2017-07-02.
 */

public class MapLocationViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

    protected GoogleMap mGoogleMap;
    protected Breakdown mBreakdown;
    public Context mContext;
    public TextView acc_no,job_no,received_date_time,completed_date_time,name,address,description;
    public Button button1,button2;
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
        //googleMap.getUiSettings().setAllGesturesEnabled(false);

        // If we have map data, update the map content.
        if (mBreakdown != null) {
            updateMapContents();
        }
    }

    protected void updateMapContents() {
        // Since the mapView is re-used, need to remove pre-existing mapView features.
        mGoogleMap.clear();

        if(mBreakdown.get_LATITUDE()== null){
            mapView.setVisibility(View.GONE);
        }else if(mBreakdown.get_LATITUDE().equals("0")){
            mapView.setVisibility(View.GONE);
        }else{
            // Update the mapView feature data and camera position.
            mGoogleMap.addMarker(new MarkerOptions().position(mBreakdown.get_location()));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mBreakdown.get_location(), 15f);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }
}
