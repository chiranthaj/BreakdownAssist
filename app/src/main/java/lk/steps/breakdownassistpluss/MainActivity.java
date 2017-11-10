package lk.steps.breakdownassistpluss;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


import lk.steps.breakdownassistpluss.Fragments.DashboardFragment;

import lk.steps.breakdownassistpluss.Fragments.JobListFragment;


import lk.steps.breakdownassistpluss.Fragments.GmapFragment;

import lk.steps.breakdownassistpluss.Fragments.SearchViewFragment;
import lk.steps.breakdownassistpluss.GpsTracker.GpsTrackerAlarmReceiver;
import lk.steps.breakdownassistpluss.Sync.BreakdownGroup;
import lk.steps.breakdownassistpluss.Sync.SyncRESTService;
import lk.steps.breakdownassistpluss.Sync.SyncService;
import lk.steps.breakdownassistpluss.Sync.SignalRService;
import lk.steps.breakdownassistpluss.Sync.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fm;

    protected PowerManager.WakeLock mWakeLock;
    //public static final String STATE_SCORE = "playerScore";
    public static final String MAP_FRAGMENT_TAG = "TagMapFragment";
   // public static final String MAP_ADDTestBREAKDOWN_FRAGMENT_TAG = "TagMapAddTestBreakdownFragment";
    private NavigationView navigationView;
    private static Context context;
    public static Token mToken;
    public static boolean ReLoginRequired = false;
    boolean doubleBackToExitPressedOnce = false;
    private String UserName;
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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);

        Globals.dbHandler = new DBHandler(this, null, null, 1);

        ManagePermissions.CheckAndRequestAllRuntimePermissions(getApplicationContext(), this);


        final PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
        timer.schedule(myTimerTask, 1000, 10000);

        startService(new Intent(getBaseContext(), SyncService.class));
        startService(new Intent(getBaseContext(), SignalRService.class));

        Globals.initAreaCodes(getApplicationContext());

        fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new DashboardFragment()).commit();
        if (savedInstanceState != null) {
            String previousFragment = savedInstanceState.getString("CURRENT_FRAGMENT");
            if (previousFragment != null) {
                try {
                    Class mClass = Class.forName(previousFragment);
                    fm.beginTransaction().replace(R.id.content_frame, (Fragment) mClass.newInstance()).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
       // CalculateAttainedTime();
        mToken = ReadToken();

        View v = navigationView.getHeaderView(0);
        UserName=ReadStringPreferences("last_username", "[username]");
        TextView username = (TextView ) v.findViewById(R.id.txtUsername);
        username.setText(UserName);//

        username = (TextView ) v.findViewById(R.id.txtArea);
        username.setText(ReadStringPreferences("area_name", "[Area name]"));//

        trackLocation();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String M = extras.getString("SignalR-msg.M");
            String A = extras.getString("SignalR-msg.A");
            if(M != null & A != null){
                SignalRService.HandleMsg(getApplicationContext(),M,A);

            }
        }

        //android.support.v7.app.ActionBar ab = getSupportActionBar();
        //ab.setSubtitle("Offline");

        // Show the "What's New" screen once for each new release of the application
        new WhatsNewScreen(this).show();
    }

    private String ReadStringPreferences(String key, String defaultValue){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
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
        registerReceiver(broadcastReceiver, new IntentFilter("lk.steps.breakdownassistpluss.NewBreakdownBroadcast"));
        if (!this.mWakeLock.isHeld())
            this.mWakeLock.acquire();
        //Log.d("onResume","555");

        List<Breakdown> notAckedBreakdowns = Globals.dbHandler.ReadNotAckedBreakdowns();
        Log.d("onResume","notAckedBreakdowns="+notAckedBreakdowns.size());
        if(notAckedBreakdowns.size()>0){
            NewBreakdownsDialog(notAckedBreakdowns);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Stetho.initializeWithDefaults(this); //TODO : Remove this later, which is a tool for database inspection
    }

    /*@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_SCORE, 123);//Testing

        //Send current fragment to use in on create
        Fragment currentFragment = this.getFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment != null) {
            savedInstanceState.putString("CURRENT_FRAGMENT", currentFragment.getClass().getName());
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }*/

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String sID = intent.getExtras().getString("_id"); //Breakdown ID, not ID in Customer Table or the SMS inbox ID
            //TODO : If SMS has an ACCT_NUM and GPS data is available with us include it in the Map and SMS log,otherwise put to the SMS log only
            String json = intent.getStringExtra("new_breakdowns");
            //String ring = intent.getStringExtra("ring");
            String refreshReq = intent.getStringExtra("job_status_changed");
            Log.d("TEST","BroadcastReceiver0");
            //Log.e("TEST","555");
            if(json != null){Log.d("TEST","BroadcastReceiver1");
                Type type = new TypeToken<List<Breakdown>>(){}.getType();
                Gson gson = new Gson();
                List<Breakdown> breakdowns = gson.fromJson(json, type);
                NewBreakdownsDialog(breakdowns);
            }else if(refreshReq!=null){Log.d("TEST","BroadcastReceiver2");
                onNavigationItemSelected(navigationView.getMenu().getItem(2)); //Focus to job list fragment
                NewStatusDialog();
                if(JobListFragment.mAdapter!=null)JobListFragment.mAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onDestroy() {
        stopService(new Intent(getBaseContext(), SyncService.class));
        //stopService(new Intent(getBaseContext(), SignalRService.class));
        Globals.dbHandler.close();
        if (this.mWakeLock.isHeld()) this.mWakeLock.release();
        StopTrackLocation();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.e("TEST","006");
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Log.e("TEST","007");
            if (doubleBackToExitPressedOnce) {
                Log.e("TEST","008");
                super.onBackPressed();
            } else if (navigationView.getMenu().findItem(R.id.nav_dashboard).isChecked()) {
                Log.e("TEST","009");
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                //TODO : Check if this works on previous versions
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                Log.e("TEST","010");
                MenuItem target = navigationView.getMenu().findItem(R.id.nav_dashboard);
                target.setChecked(true);
                onNavigationItemSelected(target);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);


        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //TODO : Move these to designated fragments and localize them
            @Override
            public boolean onQueryTextSubmit(String keyWord) {
                List<Breakdown> BreakdownsList = Globals.dbHandler.SearchInDatabase(keyWord);
                    /*if (BreakdownsList.size() == 0) {
                        BreakdownsList = dbHandler.SearchInCustomers(keyWord);
                    }*/
                if (BreakdownsList.size() == 0) {
                    Toast.makeText(MainActivity.getAppContext(), "No match found..!", Toast.LENGTH_LONG).show();
                } else if (BreakdownsList.size() == 1) {
                    Fragment currentFragment = fm.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);
                    if (currentFragment instanceof GmapFragment) {
                        GmapFragment GmapFrag = (GmapFragment) currentFragment;
                        Marker CreatedMarker = GmapFrag.AddBreakDownToMap(BreakdownsList.get(0));
                        if (CreatedMarker != null) {
                            GmapFrag.mMap.animateCamera(CameraUpdateFactory.newLatLng(CreatedMarker.getPosition()));
                            CreatedMarker.showInfoWindow();
                        }
                    } else {
                        Bundle arguments = new Bundle();
                        arguments.putString("KEY_WORD", keyWord);
                        //arguments.putParcelableArrayList("LIST",BreakdownsList);
                        SearchViewFragment fragment = new SearchViewFragment();
                        fragment.setArguments(arguments);
                        fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                } else {
                    Bundle arguments = new Bundle();
                    arguments.putString("KEY_WORD", keyWord);
                    SearchViewFragment fragment = new SearchViewFragment();
                    fragment.setArguments(arguments);
                    fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
                return true;
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

        //}
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
        /*else if(id == R.id.action_Test_API){
            Intent intent = new Intent(this, TestAPI.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.action_Add_Test_Breakdowns){
            fm.beginTransaction().replace(R.id.content_frame, new GmapAddTestBreakdownFragment(),
                    MAP_ADDTestBREAKDOWN_FRAGMENT_TAG).addToBackStack(MAP_FRAGMENT_TAG).commit();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(true);
        if (id == R.id.nav_dashboard) {
            fm.beginTransaction().replace(R.id.content_frame, new DashboardFragment()).commit();
        } else if (id == R.id.nav_map_view) {
            //TODO : If current fragment is NOT Map fragment only replace the fragment
            fm.beginTransaction().replace(R.id.content_frame, new GmapFragment(), MAP_FRAGMENT_TAG).commit();
        } /*else if (id == R.id.nav_search) {
            fm.beginTransaction().replace(R.id.content_frame, new SearchViewFragment()).commit();
        }*/ else if (id == R.id.nav_unattained_jobs) {
            Bundle arguments = new Bundle();
            arguments.putInt("JOB_STATUS", Breakdown.JOB_NOT_ATTENDED);
            JobListFragment fragment = new JobListFragment();
            fragment.setArguments(arguments);
            fm.beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss(); // just commit(); crash when calling this at new bd dialog
            //fm.beginTransaction().replace(R.id.content_frame, new UnattainedJobsFragment()).commit();
        } else if (id == R.id.nav_completed_jobs) {
            Bundle arguments = new Bundle();
            arguments.putInt("JOB_STATUS", Breakdown.JOB_COMPLETED);
            JobListFragment fragment = new JobListFragment();
            fragment.setArguments(arguments);
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_sync_sms_inbox) {
            Toast.makeText(this, "Please wait.. This will take some time to complete", Toast.LENGTH_LONG).show();
            ReadSMS.SyncSMSInbox(this);
            Toast.makeText(this, "Synced !!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            WriteLongPreferences("keep_sign_in",false);
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            MainActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void openUnattainedJobFragment(View view) {
        MenuItem target = navigationView.getMenu().findItem(R.id.nav_unattained_jobs);
        target.setChecked(true);
        onNavigationItemSelected(target);
    }

    public void openCompletedJobFragment(View view) {
        MenuItem target = navigationView.getMenu().findItem(R.id.nav_completed_jobs);
        target.setChecked(true);
        onNavigationItemSelected(target);
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment currentFragment = fm.findFragmentByTag(MainActivity.MAP_FRAGMENT_TAG);
                    if (currentFragment instanceof GmapFragment) {
                        GmapFragment GmapFrag = (GmapFragment) currentFragment;
                        GmapFrag.ApplyMapDayNightModeAccordingly();
                    }
                    if(ReLoginRequired){
                        ReLoginRequired=false;
                        WriteLongPreferences("restart_due_to_authentication_fail",true);
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        MainActivity.this.finish();
                    }
                    /*if(NewBreakdownsReceived == 1){
                        NewBreakdownsReceived = 0;
                        NewBreakdownsDialog(false);
                    }else if(NewBreakdownsReceived == 2){
                        NewBreakdownsReceived = 0;
                        NewBreakdownsDialog(true);
                    }*/
                }
            });
        }
    }
    private void WriteLongPreferences(String key, boolean value){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prfs.edit();
        editor.putBoolean(key,value).apply();
    }
    private Token ReadToken(){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        Token token = new Token(){};
        token.user_id=prfs.getString("user_id", "");
        token.area_id=prfs.getString("area_id", "");
        token.area_name=prfs.getString("area_name", "");
        token.access_token=prfs.getString("access_token", "");
        token.expires_in= prfs.getLong("expires_in", 0);
        return token;
    }
    /*private void CalculateAttainedTime() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    Globals.AverageTime = Globals.dbHandler.getAttendedTime();
                    //handler.postDelayed(this, 1000*60*10);//Continue updating 10min
                } catch (Exception e) {
                    Log.e("CalAttainedTime",e.getMessage());
                }
            }
        };
        handler.postDelayed(r, 5000); //2Sec
    }*/


    private Dialog newBreakdownDialog;
    private void NewBreakdownsDialog(final List<Breakdown> breakdowns){

        final List<Breakdown> AllNotAckedBreakdowns = Globals.dbHandler.ReadNotAckedBreakdowns();

        if(newBreakdownDialog!=null)newBreakdownDialog.dismiss();;
        newBreakdownDialog = new Dialog(this);
        newBreakdownDialog.setContentView(R.layout.alert_dialog);
        TextView msg = (TextView) newBreakdownDialog.findViewById(R.id.textDialog);
        if(AllNotAckedBreakdowns.size() == 1){
            msg.setText("New breakdown received.");
        }else{
            msg.setText("New "+AllNotAckedBreakdowns.size()+" breakdowns received.");
        }
        //dialog.setTitle("BreakdownAssist...");
        newBreakdownDialog.setCancelable(false);
        Button dialogButton = (Button) newBreakdownDialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    UpdateJobStatusAcknowledged(AllNotAckedBreakdowns);
                    Intent myintent=new Intent();
                    myintent.setAction("lk.steps.breakdownassistpluss.stopmediaplayer");
                    getApplicationContext().sendBroadcast(myintent);
                newBreakdownDialog.dismiss();
                newBreakdownDialog = null;
            }
        });
        newBreakdownDialog.show();
        onNavigationItemSelected(navigationView.getMenu().getItem(2)); //Focus to job list fragment
    }


    private void NewStatusDialog(){

        if(newBreakdownDialog!=null)return;
        newBreakdownDialog = new Dialog(this);
        newBreakdownDialog.setContentView(R.layout.alert_dialog);
        TextView msg = (TextView) newBreakdownDialog.findViewById(R.id.textDialog);
        msg.setText("Job status has updated.");
        //dialog.setTitle("BreakdownAssist...");
        newBreakdownDialog.setCancelable(false);
        Button dialogButton = (Button) newBreakdownDialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBreakdownDialog.dismiss();
                newBreakdownDialog = null;
            }
        });
        newBreakdownDialog.show();
        onNavigationItemSelected(navigationView.getMenu().getItem(2)); //Focus to job list fragment
    }

    private static void UpdateJobStatusAcknowledged(List<Breakdown> breakdowns) {
        try{
            String time = "" + Globals.timeFormat.format(new Date());
            for (Breakdown breakdown :breakdowns) {
                //String jobNo = dbHandler.GetNewJobNumber(breakdown.get_Job_No());
                JobChangeStatus status = new JobChangeStatus();
                status.job_no=Globals.dbHandler.GetNewJobNumber(breakdown.get_Job_No());
                status.change_datetime=time;
                status.device_timestamp=time;
                status.synchro_mobile_db=0;
                status.st_code=String.valueOf(Breakdown.JOB_ACKNOWLEDGED);

                Globals.dbHandler.addJobStatusChangeRec(status);
                Globals.dbHandler.UpdateBreakdownStatusByJobNo(breakdown.get_Job_No(), "", String.valueOf(Breakdown.JOB_ACKNOWLEDGED));
            }
            SyncService.PostBreakdownStatusChange(getAppContext());
        }catch(Exception e){
            Log.e("Error","UpdateJobStatusAcknowledged "+e.getMessage());
        }

    }

    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;
    private int intervalInMinutes = 1;


    protected void trackLocation() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("GPSTRACKER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("intervalInMinutes", 1);
        editor.putString("userName", UserName);
       // editor.putString("defaultUploadWebsite", "123");

            startAlarmManager();

            editor.putBoolean("currentlyTracking", true);
            editor.putFloat("totalDistanceInMeters", 0f);
            editor.putBoolean("firstTimeGettingPosition", true);
            editor.putString("sessionID",  UUID.randomUUID().toString());
        editor.apply();
    }

    protected void StopTrackLocation() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("GPSTRACKER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("intervalInMinutes", 1);
        editor.putString("userName", "123");
      //  editor.putString("defaultUploadWebsite", "123");
        cancelAlarmManager();
        editor.putBoolean("currentlyTracking", false);
        editor.putString("sessionID", "");

        editor.apply();
    }

    private void startAlarmManager() {
        Log.d("GPS TRACKER", "startAlarmManager");

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        SharedPreferences sharedPreferences = this.getSharedPreferences("GPSTRACKER", Context.MODE_PRIVATE);
        intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 60000, // 60000 = 1 minute
                pendingIntent);
    }

    private void cancelAlarmManager() {
        Log.d("GPSTRACKER", "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
