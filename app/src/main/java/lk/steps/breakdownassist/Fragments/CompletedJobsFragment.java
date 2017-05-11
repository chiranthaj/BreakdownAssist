package lk.steps.breakdownassist.Fragments;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.MyDBHandler;
import lk.steps.breakdownassist.R;

public class CompletedJobsFragment extends Fragment {

    private View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate( R.layout.job_listview,container,false);
        dbHandler = new MyDBHandler(getActivity(),null,null,1); //TODO : Close on exit
        displayListView();
        return mView;
    }

    private SimpleCursorAdapter dataAdapter;
    MyDBHandler dbHandler;


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassist.NewBreakdownBroadcast"));
    }


    private void displayListView() {
        //TODO : Add a listner to show the new SMSs
        Cursor cursor = dbHandler.ReadBreakdownsToCursor(1);

        // The desired columns to be bound
        String[] columns2 = new String[] {"_Acct_Num","NAME","ADDRESS","Description","Status","_Job_Num","DateTime1","DateTime2"}; /*TODO : "Status" May be in a color of the row or dot*/
        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.acct_num,
                R.id.name,
                R.id.address,
                R.id.description,
                R.id.status,
                R.id.job_no,
                R.id.received_date_time,
                R.id.completed_date_time
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                getActivity(), R.layout.job_listview_row,
                cursor,
                columns2,
                to,
                0);

        ListView listView = (ListView) mView.findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO : Focus to selected breackdown on the map

            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO : If SMS has an ACCT_NUM and GPS data is available with us include it in the Map and SMS log,otherwise put to the SMS log only
            Breakdown mybd =null;
            mybd=dbHandler.ReadBreakdown_by_ID(intent.getExtras().getString("_id")); //Breakdown ID, not ID in Customer Table or the SMS inbox ID
            if (mybd!= null){
                //Add to list view
                displayListView();
            }
            else{
                //Toast.makeText(this,"No records",Toast.LENGTH_SHORT).show();
                //SMS list fragment ....
                //TODO :  Add to Not in GPS Database list,Breakdown list (full SMS list) and increase the number of breakdowns icon on the map
                //TODO : Get other data by the INTENT bundle
            }
        }
    };
}