package lk.steps.breakdownassist.Sync;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.facebook.stetho.json.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.DBHandler;
import lk.steps.breakdownassist.Globals;
import lk.steps.breakdownassist.MainActivity;
import lk.steps.breakdownassist.R;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignalRService extends Service {
    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    DBHandler dbHandler;
    private Handler mHandler; // to display Toast message
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients
    Timer timer;
    MyTimerTask myTimerTask;
    //private boolean ServerConnected=false;
    private boolean RetryTimerStarted=false;
    private boolean mBound = false;
    SyncRESTService syncRESTService;
    //String serverUrl = "http://192.168.0.196:8099";
    //String serverUrl = "http://192.168.137.1:31525";
    //String serverUrl = "http://111.223.135.20";

    public SignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());

        Log.e("SignalRService","002");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        dbHandler = new DBHandler(this,null,null,1);
        syncRESTService = new SyncRESTService();
        Toast.makeText(this, "SignalR Service Started", Toast.LENGTH_SHORT).show();

        StartRetryTimer();
        return result;
    }

    private void StartRetryTimer(){
        if(RetryTimerStarted)return;
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 10000);
        RetryTimerStarted=true;
    }
    private void StopRetryTimer(){
        if(!RetryTimerStarted)return;
        timer.cancel();
        RetryTimerStarted=false;
    }
    @Override
    public void onDestroy() {
        mHubConnection.stop();
        //Log.e("SignalRService","501");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
       // if(!ServerConnected)
            startSignalR();
        return mBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

    /**
     * method for clients (activities)
     */
    public void sendMessage(String message) {
        String SERVER_METHOD_SEND = "send";
        mHubProxy.invoke(SERVER_METHOD_SEND, message);
    }

    /**
     * method for clients (activities)
     */
    public void sendMessage_To(String receiverName, String message) {
        String SERVER_METHOD_SEND_TO = "send";
        mHubProxy.invoke(SERVER_METHOD_SEND_TO, receiverName, message);

    }
    private String ReadStringPreferences(String key, String defaultValue){
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
    }

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        mHubConnection = new HubConnection(Globals.serverUrl);
        String SERVER_HUB = "BreakdownAssistAndroidHub";
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB);
        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        //final String group_token = ReadStringPreferences("group_token","");
        final String area_id = ReadStringPreferences("area_id","");
        final String team_id = ReadStringPreferences("team_id","");
        Log.e("SignalR ", "*groupid*"+area_id+"_"+team_id);
        mHubConnection.setGroupsToken(area_id + "_" + team_id);
        try {
            signalRFuture.get();
            //ServerConnected=true;
            Log.e("SignalR ", "*Connected*");
            Log.e("SignalR ", "ID="+mHubConnection.getConnectionId());
            StopRetryTimer();
            //Log.e("SimpleSignalR", "ServerConnected=true");
        } catch (InterruptedException | ExecutionException e) {
            //Log.e("SimpleSignalR", "ServerConnected=false");
            Log.e("SignalR", e.toString());
            //ServerConnected=false;
            StartRetryTimer();
            return;
        }
       // sendMessage("Hello from BNK!");


        String CLIENT_METHOD_BROADAST_MESSAGE = "broadcastMessage";
        mHubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE,
                new SubscriptionHandler2<String, String>() {
                    @Override
                    public void run(final String name, final String msg) {
                        //Log.e("SimpleSignalR", "4569"+msg+name);
                        final String finalMsg =  msg.toString();
                        // display Toast message
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), finalMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                , String.class,String.class);


        // Subscribe to the received event
        mHubConnection.received(new MessageReceivedHandler() {

            @Override
            public void onMessageReceived(final JsonElement json) {
                //Log.e("onMessageReceived ", json.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d("API-","its running-01");
                     //   JsonObject jobject = json.getAsJsonObject();
                     //   String result = jobject.get("A").toString().replace("\"","").replace("[","").replace("]","");
                        GetNewBreakdownsFromServer(area_id, team_id);
                      //  Log.e("API-","-"+json);
                      //  Log.e("API-","-02"+result);
                        Toast.makeText(getApplicationContext(),"SignalR request received", Toast.LENGTH_SHORT).show();
                       // Toast.makeText(getApplicationContext(),json.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mHubConnection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                //ServerConnected=false;
                error.printStackTrace();
                StartRetryTimer();
            }
        });
        mHubConnection.connected(new Runnable() {
            @Override
            public void run() {
                //ServerConnected=true;
                Log.e("SignalR", "CONNECTED");
            }
        });

        mHubConnection.reconnected(new Runnable() {
            @Override
            public void run() {
                //ServerConnected=true;
                Log.e("SignalR", "RECONNECTED");
            }
        });
        // Subscribe to the closed event
        mHubConnection.closed(new Runnable() {
            @Override
            public void run() {
                //ServerConnected=false;
                Log.e("SignalR", "DISCONNECTED");
                StartRetryTimer();
            }
        });
    }
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
          //  mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    public void GetNewBreakdownsFromServer(String area_Id, String team_id){
        //Log.e("GetNewBreakdowns","1");
        try{
            SyncRESTService syncRESTService = new SyncRESTService();
            Call<List<Breakdown>> call = syncRESTService.getService()
                    .getNewBreakdowns( "Bearer "+ MainActivity.mToken.access_token,
                            MainActivity.mToken.user_id,area_Id,team_id);

            call.enqueue(new Callback<List<Breakdown>>(){
                @Override
                public void onResponse(Call<List<Breakdown>> call, Response<List<Breakdown>> response) {
                    //Log.e("GetNewBreakdowns","2");
                    if (response.isSuccessful()) {
                        //Log.e("GetNewBreakdowns","Successful");
                        List<Breakdown> breakdowns = response.body();
                        Log.e("GetNewBreakdowns","Number-"+breakdowns.size());
                        DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
                       // int playTone = 0;
                        for (Breakdown breakdown :breakdowns) {
                            dbHandler.addBreakdown2(
                                    breakdown.get_Received_Time(),
                                    breakdown.get_Acct_Num(),
                                    breakdown.get_Full_Description(),
                                    breakdown.get_Job_No(),
                                    breakdown.get_Contact_No(),
                                    breakdown.get_ADDRESS(),
                                    breakdown.get_Priority());
                            Log.e("get_Full_Description()",breakdown.get_Full_Description());

                            if(breakdown.get_Priority()==4)MainActivity.NewBreakdownsReceived=2;
                            else MainActivity.NewBreakdownsReceived = 1;
                        }
                        MainActivity.NewBreakdowns = breakdowns;

                        String sIssuedBreakdownID=dbHandler.getLastBreakdownID();
                        dbHandler.close();

                        //Informing the Map view about the new bd, then it can add it
                        Intent myintent=new Intent();
                        myintent.setAction("lk.steps.breakdownassist.NewBreakdownBroadcast");
                        myintent.putExtra("_id",sIssuedBreakdownID);
                        getApplicationContext().sendBroadcast(myintent);
                        /*if(MainActivity.NewBreakdownsReceived==1){
                            MediaPlayer mPlayer2;
                            mPlayer2= MediaPlayer.create(getApplicationContext(), R.raw.fb_sound);
                            mPlayer2.start();
                        }*/
                    } else if (response.errorBody() != null) {
                        if(response.code() == 401) { //Authentication fail
                            Toast.makeText(getApplicationContext(), "Authentication fail..", Toast.LENGTH_SHORT).show();
                            MainActivity.ReLoginRequired=true;
                        }else{
                            Toast.makeText(getApplicationContext(), "GetNewBreakdowns\nResponse code ="+response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetNewBreakdowns","onResponse" + response.errorBody()+"*code*"+response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Breakdown>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error in network..\n"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetNewBreakdowns","5-" + t.getMessage());
                }
            });
        }catch(Exception e){
            Log.e("GetNewBreakdowns","" + e.getMessage());
        }

    }



    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            //if(!ServerConnected){
                Log.e("SignalR ", "*NOT*Connected*");
                startSignalR();
           // }else{
           //     Log.e("SignalR ", "*Connected*");
           // }
        }
    }




}
