package lk.steps.breakdownassistpluss.Fragments;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.MapMarker;
import lk.steps.breakdownassistpluss.Models.Statistics;
import lk.steps.breakdownassistpluss.R;


public class DashboardFragment extends Fragment implements OnMapReadyCallback {

    //Timer timer;
    //MyTimerTask myTimerTask;
    private View mView;
    private GoogleMap googleMap;
    private MapView mapView;
    android.support.v7.app.ActionBar mActionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        refreshCounts();
        //DrawChart1();
        //DrawChart2();
        //DrawChart3();
        Globals.AverageTime = Globals.dbHandler.getAttendedTime();
        final TextView txtAvgTime = (TextView) mView.findViewById(R.id.txtAvgTime);
        txtAvgTime.setText(Globals.AverageTime + " min");
        //MapTask();
        //timer = new Timer();
        //myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
        //timer.schedule(myTimerTask, 1000, 20000);

        mapView = (MapView) mView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        //mapView.setPadding(20, 20, 20, 20);
        mapView.getMapAsync(this);

        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        UpdateOnlineStatus();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter("lk.steps.breakdownassistpluss.DashboardBroadcastReceiver"));

        return mView;
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String online_status_changed = intent.getStringExtra("online_status_changed");
            String refresh_counts = intent.getStringExtra("refresh_counts");

            if (online_status_changed != null) {
                UpdateOnlineStatus();
            } else if (refresh_counts != null) {
                refreshCounts();
            }

            // now you can call all your fragments method here
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        List<Breakdown> BreakdownList = new ArrayList<>(Globals.dbHandler.ReadBreakdowns(Breakdown.JOB_NOT_ATTENDED, true, false));

        try {
            map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(mView.getContext(), R.raw.style_json_dark));
        } catch (Exception e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
      //  if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          try{
              googleMap.setMyLocationEnabled(true);
          }catch(SecurityException e){ }

      //  }
        if (BreakdownList.size() > 0) {
            LatLngBounds.Builder builder = LatLngBounds.builder();

            for (Breakdown bd : BreakdownList) {
                LatLng loc = bd.getLocation();
                if (loc != null) {
                    map.addMarker(new MarkerOptions()
                            .position(loc)
                            .title(bd.get_Job_No())
                            .icon(MapMarker.GetBitmap(bd)));
                    builder.include(loc);
                }
            }
            LatLngBounds bounds = builder.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.8204307, 80.2189718), 8));
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                FragmentTransaction t = getActivity().getFragmentManager().beginTransaction();
                Fragment mFrag = new GmapFragment();
                t.replace(R.id.content_frame, mFrag);
                t.commit();
            }
        });

        UiSettings settings = map.getUiSettings();
        //settings.setZoomControlsEnabled(false);
        // settings.setAllGesturesEnabled(false);
        // settings.setMyLocationButtonEnabled(false);
        //  settings.setZoomControlsEnabled(false);
        settings.setMapToolbarEnabled(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshCounts();
        if (googleMap != null) onMapReady(googleMap);
    }

    private void refreshCounts() {
        try {
            Statistics stat = Globals.dbHandler.getBreakdownCounts();

            TextView tv = (TextView) mView.findViewById(R.id.txtUnattainedCount);
            tv.setText(String.valueOf(stat.ACKNOWLEDGED+stat.ATTENDING+stat.DELIVERED));
            tv = (TextView) mView.findViewById(R.id.c0);
            tv.setText(String.valueOf(stat.COMPLETED));
            tv = (TextView) mView.findViewById(R.id.c1);
            tv.setText(String.valueOf(stat.TEMPORARY_COMPLETED));
            tv = (TextView) mView.findViewById(R.id.c2);
            tv.setText(String.valueOf(stat.WITHDRAWN));
            tv = (TextView) mView.findViewById(R.id.c3);
            tv.setText(String.valueOf(stat.REJECT));
            tv = (TextView) mView.findViewById(R.id.c4);
            tv.setText(String.valueOf(stat.RETURNED));

        } catch (Exception e) {

        }

    }

    private void DrawChart1() {
        //DBHandler dbHandler = new DBHandler(getActivity().getApplicationContext(), null, null, 1);
        //String counts[][] = dbHandler.getBreakdownStatistics();

        BarChart mBarChart = (BarChart) mView.findViewById(R.id.chart1);
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        /*for (int i =0; i<counts[0].length; i++) {
            //Log.d("DATE,COUNT",counts[0][i] +"-" +counts[1][i]);
            series.addPoint(new ValueLinePoint(counts[0][i], Float.valueOf(counts[1][i])));
        }*/

        mBarChart.addBar(new BarModel(2.3f, 0xFF123456));
        mBarChart.addBar(new BarModel(2.f, 0xFF343456));
        mBarChart.addBar(new BarModel(3.3f, 0xFF563456));
        mBarChart.addBar(new BarModel(1.1f, 0xFF873F56));
        mBarChart.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart.addBar(new BarModel(2.f, 0xFF343456));
        mBarChart.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart.addBar(new BarModel(4.f, 0xFF1BA4E6));
        mBarChart.startAnimation();
    }

    private void DrawChart2() {
        String counts[][] = Globals.dbHandler.getBreakdownStatistics();

        ValueLineChart mCubicValueLineChart = (ValueLineChart) mView.findViewById(R.id.chart2);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        for (int i = 0; i < counts[0].length; i++) {
            //Log.d("DATE,COUNT",counts[0][i] +"-" +counts[1][i]);
            series.addPoint(new ValueLinePoint(counts[0][i], Float.valueOf(counts[1][i])));
        }
        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();
    }

    private void DrawChart3() {
        FitChart fitChart = (FitChart) mView.findViewById(R.id.chart3);
        fitChart.setMinValue(0f);
        fitChart.setMaxValue(100f);

        List<FitChartValue> values = new ArrayList<>();
        values.add(new FitChartValue(30f, Color.parseColor("#f4f142")));
        values.add(new FitChartValue(20f, Color.parseColor("#47f441")));
        values.add(new FitChartValue(15f, Color.parseColor("#41f4df")));
        values.add(new FitChartValue(10f, Color.parseColor("#b841f4")));

        fitChart.setValues(values);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // timer.cancel();
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }



    /*private void MapTask(){
        MapView mapView = (MapView) mView.findViewById(R.id.map_view);
        mapView.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //UpdateOnlineStatus();
                    // refreshCounts();
                }
            });
        }
    }


    public void UpdateOnlineStatus() {
        Log.d("Online status", "2" + Globals.ServerConnected);
        if (Globals.ServerConnected & mActionBar != null) mActionBar.setSubtitle("Online");
        else if (!Globals.ServerConnected & mActionBar != null)
            mActionBar.setSubtitle("Offline");
    }
}