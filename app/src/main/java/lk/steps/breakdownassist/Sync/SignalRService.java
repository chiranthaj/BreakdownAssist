package lk.steps.breakdownassist.Sync;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.DBHandler;
import lk.steps.breakdownassist.JobChangeStatus;
import lk.steps.breakdownassist.JobCompletion;
import lk.steps.breakdownassist.MainActivity;
import lk.steps.breakdownassist.R;
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
    private boolean ServerConnected=false;
    private boolean mBound = false;
    SyncRESTService syncRESTService;

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
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 10000);

        return result;
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
        if(!ServerConnected)startSignalR();
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
        Log.e("SIGNALR","012");
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

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        //String serverUrl = "http://192.168.0.196:8099";
        String serverUrl = "http://192.168.137.1:31525";
        //String serverUrl = "http://111.223.135.20";

        mHubConnection = new HubConnection(serverUrl);
        String SERVER_HUB_CHAT = "BreakdownAssistAndroidHub";
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB_CHAT);

        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        try {
            signalRFuture.get();
            ServerConnected=true;
            Log.e("SimpleSignalR", "ServerConnected=true");
        } catch (InterruptedException | ExecutionException e) {
            Log.e("SimpleSignalR", "ServerConnected=false");
            Log.e("SimpleSignalR", e.toString());
            ServerConnected=false;
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
                        Log.d("API-","its running-01");
                        GetNewBreakdownsFromServer();
                        Log.d("API-","its running-02");
                        Toast.makeText(getApplicationContext(),"SignalR request received.XXX", Toast.LENGTH_SHORT).show();
                       // Toast.makeText(getApplicationContext(),json.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
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


    public void GetNewBreakdownsFromServer(){
        //Log.e("GetNewBreakdowns","1");
        SyncRESTService syncRESTService = new SyncRESTService();
        Call<List<Breakdown>> call = syncRESTService.getService()
                .getNewBreakdowns( "Bearer "+ MainActivity.mToken.access_token,
                        MainActivity.mToken.user_id,"77","S","3");

        call.enqueue(new Callback<List<Breakdown>>(){
            @Override
            public void onResponse(Call<List<Breakdown>> call, Response<List<Breakdown>> response) {
                //Log.e("GetNewBreakdowns","2");
                if (response.isSuccessful()) {
                    Log.e("GetNewBreakdowns","Successful");
                    List<Breakdown> breakdowns = response.body();
                    Log.e("GetNewBreakdowns","Number-"+breakdowns.size());
                    DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
                    boolean playTone = false;
                    for (Breakdown breakdown :breakdowns) {
                        dbHandler.addBreakdown2(
                                breakdown.get_Received_Time(),
                                breakdown.get_Acct_Num(),
                                breakdown.get_Full_Description(),
                                breakdown.get_Job_No(),
                                breakdown.get_Contact_No(),
                                breakdown.get_ADDRESS(),
                                1);
                        playTone = true;
                    }
                    String sIssuedBreakdownID=dbHandler.getLastBreakdownID();
                    dbHandler.close();

                    //Informing the Map view about the new bd, then it can add it
                    Intent myintent=new Intent();
                    myintent.setAction("lk.steps.breakdownassist.NewBreakdownBroadcast");
                    myintent.putExtra("_id",sIssuedBreakdownID);
                    getApplicationContext().sendBroadcast(myintent);
                    if(playTone){
                        MediaPlayer mPlayer2;
                        mPlayer2= MediaPlayer.create(getApplicationContext(), R.raw.fb_sound);
                        mPlayer2.start();
                    }
                } else if (response.errorBody() != null) {
                    Log.e("GetNewBreakdowns","4-" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Breakdown>> call, Throwable t) {
                Log.e("GetNewBreakdowns","5-" + t.getMessage());
            }

        });
    }



    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
           // Log.e("SimpleSignalR ", "timer tick");
            if(!ServerConnected)startSignalR();
        }
    }


}
