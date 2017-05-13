package lk.steps.breakdownassist.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.MyDBHandler;
import lk.steps.breakdownassist.R;

public class NewDashboardFragment extends Fragment {


    private View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate( R.layout.fragment_dashboard_new,container,false);
        refreshCounts();
        DrawChart();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCounts();
    }

    private void refreshCounts(){
        MyDBHandler  dbHandler = new MyDBHandler(getActivity().getApplicationContext(), null, null, 1);
        int counts[] = dbHandler.getBreakdownCounts();

        TextView txtUnattainedCount = (TextView) mView.findViewById(R.id.txtUnattainedCount);
        TextView txtCompletedCount = (TextView) mView.findViewById(R.id.txtCompletedCount);
        txtCompletedCount.setText(String.valueOf(counts[0]));
        txtUnattainedCount.setText(String.valueOf(counts[1]));
    }

    private void DrawChart(){
        MyDBHandler  dbHandler = new MyDBHandler(getActivity().getApplicationContext(), null, null, 1);
        int counts[][] = dbHandler.getBreakdownStatistics();
        /*BarChart mBarChart = (BarChart) mView.findViewById(R.id.chart);
        mBarChart.addBar(new BarModel(2.3f, 0xFF123456));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(3.3f, 0xFF563456));
        mBarChart.addBar(new BarModel(1.1f, 0xFF873F56));
        mBarChart.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart.addBar(new BarModel(4.f,  0xFF1BA4E6));
        mBarChart.startAnimation();*/

        ValueLineChart mCubicValueLineChart = (ValueLineChart) mView.findViewById(R.id.chart);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        Log.d("x",counts.length+"=");
        Log.d("y",counts[0].length+"=");
        for (int i =0; i<1; i++) {
            Log.d("DATE,COUNT",counts[0][i] +"," +counts[1][i]);
            series.addPoint(new ValueLinePoint(String.valueOf(counts[0][i]), counts[1][i]));
        }

        /*series.addPoint(new ValueLinePoint("Jan", 2.4f));
        series.addPoint(new ValueLinePoint("Feb", 3.4f));
        series.addPoint(new ValueLinePoint("Mar", .4f));
        series.addPoint(new ValueLinePoint("Apr", 1.2f));
        series.addPoint(new ValueLinePoint("Mai", 2.6f));
        series.addPoint(new ValueLinePoint("Jun", 1.0f));
        series.addPoint(new ValueLinePoint("Jul", 3.5f));
        series.addPoint(new ValueLinePoint("Aug", 2.4f));
        series.addPoint(new ValueLinePoint("Sep", 2.4f));
        series.addPoint(new ValueLinePoint("Oct", 3.4f));
        series.addPoint(new ValueLinePoint("Nov", .4f));
        series.addPoint(new ValueLinePoint("Dec", 1.3f));*/

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();
    }
}