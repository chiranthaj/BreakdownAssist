package lk.steps.breakdownassist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/*import com.arlib.floatingsearchview.FloatingSearchView;*/
import com.facebook.stetho.Stetho;

import java.util.Timer;
import java.util.TimerTask;

import lk.steps.breakdownassist.Fragments.NewDashboardFragment;
import lk.steps.breakdownassist.Fragments.GmapAddTestBreakdownFragment;
import lk.steps.breakdownassist.Fragments.GmapFragment;
import lk.steps.breakdownassist.Fragments.JobListFragment;
import lk.steps.breakdownassist.Fragments.SearchViewFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MyDBHandler dbHandler;
    FragmentManager fm;

    protected PowerManager.WakeLock mWakeLock;
    public static final String STATE_SCORE = "playerScore";
    public static final String MAP_FRAGMENT_TAG = "TagMapFragment";
    public static final String MAP_ADDBREAKDOWN_FRAGMENT_TAG = "TagMapAddBreakdownFragment";

    private static Context context;

    boolean doubleBackToExitPressedOnce = false;

    Timer timer;
    MyTimerTask myTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbHandler = new MyDBHandler(this,null,null,1);

        ManagePermissions.CheckAndRequestAllRuntimePermissions(getApplicationContext(),this);
        fm = getFragmentManager();

        fm.beginTransaction().replace(R.id.content_frame, new NewDashboardFragment()).commit();
       // fm.beginTransaction().replace(R.id.content_frame, new GmapFragment(),MAP_FRAGMENT_TAG).commit();

        final PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
        timer.schedule(myTimerTask, 1000, 5000);

        Globals.initAreaCodes(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        if (this.mWakeLock.isHeld())
            this.mWakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassist.NewBreakdownBroadcast"));
        if (!this.mWakeLock.isHeld())
            this.mWakeLock.acquire();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Stetho.initializeWithDefaults(this); //TODO : Remove this later, which is a tool for database inspection
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_SCORE, 123);//Testing

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sID=intent.getExtras().getString("_id"); //Breakdown ID, not ID in Customer Table or the SMS inbox ID
            //TODO : If SMS has an ACCT_NUM and GPS data is available with us include it in the Map and SMS log,otherwise put to the SMS log only
        }
    };


    @Override
    protected void onDestroy() {
        dbHandler.close();
        if (this.mWakeLock.isHeld()) this.mWakeLock.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            //TODO : Check if this works on previous versions
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //TODO : Move these to designated fragments and localize them
                @Override
                public boolean onQueryTextSubmit(String s) {
                    //Log.d("TEST", "onQueryTextSubmit ");
                  /*  cursor=studentRepo.getStudentListByKeyword(s);
                    if (cursor==null){
                        Toast.makeText(MainActivity.this,"No records found!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, cursor.getCount() + " records found!",Toast.LENGTH_LONG).show();
                    }
                    customAdapter.swapCursor(cursor);*/
                    Fragment currentFragment = fm.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);
                    if (currentFragment instanceof GmapFragment) {
                        GmapFragment GmapFrag= (GmapFragment) currentFragment;
                        GmapFrag.AddCustomerLocationToMap(s);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    //Log.d("TEST", "onQueryTextChange ");
                    /*cursor=studentRepo.getStudentListByKeyword(s);
                    if (cursor!=null){
                        customAdapter.swapCursor(cursor);
                    }*/
                    return false;
                }

            });

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_map_view) {
            //TODO : If current fragment is NOT Map fragment only replace the fragment
            fm.beginTransaction().replace(R.id.content_frame, new GmapFragment(),MAP_FRAGMENT_TAG).commit();
        }else if (id == R.id.nav_dashboard) {
            fm.beginTransaction().replace(R.id.content_frame, new NewDashboardFragment()).commit();
        }
        else if (id == R.id.nav_search) {
            fm.beginTransaction().replace(R.id.content_frame, new SearchViewFragment()).commit();
        } else if (id == R.id.nav_unattained_jobs) {
            Bundle arguments = new Bundle();
            arguments.putInt("JOB_STATUS", Breakdown.Status_JOB_NOT_ATTENDED);
            JobListFragment fragment = new JobListFragment();
            fragment.setArguments(arguments);
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
            //fm.beginTransaction().replace(R.id.content_frame, new UnattainedJobsFragment()).commit();
        }  else if (id == R.id.nav_completed_jobs) {
            Bundle arguments = new Bundle();
            arguments.putInt("JOB_STATUS", Breakdown.Status_JOB_COMPLETED);
            JobListFragment fragment = new JobListFragment();
            fragment.setArguments(arguments);
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
            //fm.beginTransaction().replace(R.id.content_frame, new CompletedJobsFragment()).commit();
        }else if (id == R.id.nav_Test_BD_ADD) {
            fm.beginTransaction().replace(R.id.content_frame, new GmapAddTestBreakdownFragment(),MAP_ADDBREAKDOWN_FRAGMENT_TAG).addToBackStack("GmapAddTestBreakdownFragment").commit();
        } else if (id == R.id.nav_sync_sms_inbox) {
            Toast.makeText(this, "Please wait.. This will take some time to complete" , Toast.LENGTH_LONG).show();
            ReadSMS.SyncSMSInbox(this);
            Toast.makeText(this, "Synced !!" , Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static Context getAppContext() {
        return MainActivity.context;
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Fragment currentFragment = fm.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);
                    if (currentFragment instanceof GmapFragment) {
                        GmapFragment GmapFrag= (GmapFragment) currentFragment;
                        GmapFrag.ApplyMapDayNightModeAccordingly();
                    }
                }});
        }
    }

}
