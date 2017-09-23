package lk.steps.breakdownassist.Fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lk.steps.breakdownassist.DBHandler;
import lk.steps.breakdownassist.Globals;
import lk.steps.breakdownassist.MainActivity;
import lk.steps.breakdownassist.R;

import static android.R.attr.animation;

public class DashboardFragment extends Fragment {

    Timer timer;
    MyTimerTask myTimerTask;
    private View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate( R.layout.fragment_dashboard_new,container,false);
        refreshCounts();
        DrawChart1();
        DrawChart2();
        DrawChart3();
        final TextView txtAvgTime = (TextView) mView.findViewById(R.id.txtAvgTime);
        txtAvgTime.setText(Globals.AverageTime+" min");

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
        timer.schedule(myTimerTask, 1000, 20000);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCounts();
    }

    private void refreshCounts(){
        try {
            DBHandler dbHandler = new DBHandler(getActivity().getApplicationContext(), null, null, 1);
            int counts[] = dbHandler.getBreakdownCounts();

            TextView txtUnattainedCount = (TextView) mView.findViewById(R.id.txtUnattainedCount);
            TextView txtCompletedCount = (TextView) mView.findViewById(R.id.txtCompletedCount);
            txtCompletedCount.setText(String.valueOf(counts[0]));
            txtUnattainedCount.setText(String.valueOf(counts[1]));
        }catch(Exception e){

        }

    }

    private void DrawChart1(){
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
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(3.3f, 0xFF563456));
        mBarChart.addBar(new BarModel(1.1f, 0xFF873F56));
        mBarChart.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart.addBar(new BarModel(4.f,  0xFF1BA4E6));
        mBarChart.startAnimation();
    }

    private void DrawChart2(){
        DBHandler dbHandler = new DBHandler(getActivity().getApplicationContext(), null, null, 1);
        String counts[][] = dbHandler.getBreakdownStatistics();
        dbHandler.close();

        ValueLineChart mCubicValueLineChart = (ValueLineChart) mView.findViewById(R.id.chart2);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        for (int i =0; i<counts[0].length; i++) {
            //Log.d("DATE,COUNT",counts[0][i] +"-" +counts[1][i]);
            series.addPoint(new ValueLinePoint(counts[0][i], Float.valueOf(counts[1][i])));
        }
        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();
    }
    private void DrawChart3() {
        FitChart fitChart = (FitChart)mView.findViewById(R.id.chart3);
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
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshCounts();
                }
            });
        }
    }
}