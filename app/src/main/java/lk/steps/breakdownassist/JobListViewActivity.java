package lk.steps.breakdownassist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class JobListViewActivity extends AppCompatActivity {

    private SimpleCursorAdapter dataAdapter;
    MyDBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_listview);

        dbHandler = new MyDBHandler(this,null,null,1); //TODO : Close on exit
        registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassist.NewBreakdownBroadcast"));
        displayListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void displayListView() {

        //TODO : Add a listner to show the new SMSs
        Cursor cursor = dbHandler.ReadBreakdownsToCursor(-1);

        // The desired columns to be bound
        String[] columns2 = new String[] {"_Acct_Num","NAME","ADDRESS","Description","Status","_Job_Num"}; /*TODO : "Status" May be in a color of the row or dot*/
        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.acct_num,
                R.id.name,
                R.id.address,
                R.id.description,
                R.id.status,
                R.id.job_no

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
