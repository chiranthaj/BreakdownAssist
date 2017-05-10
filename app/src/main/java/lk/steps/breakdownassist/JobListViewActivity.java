package lk.steps.breakdownassist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
//TODO :  move JobListViewActivity and CompletedJobListViewActivity to fragments, so that it will be able to show with the side bar
public class JobListViewActivity extends AppCompatActivity {

    private SimpleCursorAdapter dataAdapter;
    MyDBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_listview);

        dbHandler = new MyDBHandler(this,null,null,1); //TODO : Close on exit

        displayListView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassist.NewBreakdownBroadcast"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //TODO : Select the Home in Drawer
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void displayListView() {

        //TODO : Add a listner to show the new SMSs
        Cursor cursor = dbHandler.ReadBreakdownsToCursor(0);

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
                this, R.layout.breakdowninfo,
                cursor,
                columns2,
                to,
                0);



        ListView listView = (ListView) findViewById(R.id.listView1);
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
                //Add to list view
                displayListView();

        }
    };
}
