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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.MyDBHandler;
import lk.steps.breakdownassist.R;

public class UnattainedJobsFragment extends Fragment {

    private View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate( R.layout.job_listview,container,false);
        dbHandler = new MyDBHandler(getActivity(),null,null,1); //TODO : Close on exit
        displayListView();
        return mView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.jobs_to_display_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_jobs_all) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            return true;
        }else if (id == R.id.menu_jobs_completed) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            return true;
        }else if (id == R.id.menu_jobs_unatended) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        //TODO : Make Sure we are showing all the Breakdown.Status_JOB_NOT_ATTENDED,Status_JOB_VISITED etc
        Cursor cursor = dbHandler.ReadBreakdownsToCursor(Breakdown.Status_JOB_NOT_ATTENDED);

        // The desired columns to be bound
        String[] columns2 = new String[] {"_Acct_Num","NAME","ADDRESS",
                "Description","Status","_Job_Num","DateTime1","DateTime2"}; /*TODO : "Status" May be in a color of the row or dot*/
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
                getActivity(), R.layout.breakdowninfo,
                cursor,
                columns2,
                to,
                1);

        ListView listView = (ListView) mView.findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO : Focus to selected breakdown on the map

            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Add to list view
            displayListView();

        }
    };
}