package lk.steps.breakdownassistpluss;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.steps.breakdownassistpluss.Sync.SignalRObject;
import lk.steps.breakdownassistpluss.Sync.SignalRService;
import lk.steps.breakdownassistpluss.Sync.SyncService;

import static android.content.Context.ACTIVITY_SERVICE;

public class SmsBroadcastReceiver extends BroadcastReceiver
{
    //TODO : Use a method to unregisterReceiver the BroadcastReceiver when app is not in use, on Destroy,
// hint : add this BroadcastReceiver in run time and destroy from it in the Main activity,
// TODO :or make it a service to add the sms in background to the Database, then no need to Sync

    public static final String SMS_BUNDLE = "pdus";
    MediaPlayer mMediaPlayer;
    public void onReceive(Context context, Intent intent)
    {
        try{
            Bundle intentExtras = intent.getExtras();
            Globals.LoadAreaCodesForSMSBroadCastReceiver(context);  //For the smsbroadcast receiver background run situation

            if (intentExtras != null)
            {
                Object[] pdus = (Object[]) intentExtras.get(SMS_BUNDLE);
                SmsMessage[] smsMessages = new SmsMessage[pdus.length];

                String sFullMessage="";
                String sAddress="";
                Date smsDayTime=new Date();

                for (int i = 0; i < pdus.length; ++i) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = intentExtras.getString("format");
                        smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        sAddress = smsMessages[i].getOriginatingAddress();
                        sFullMessage += smsMessages[i].getMessageBody();
                        smsDayTime = new Date(smsMessages[i].getTimestampMillis());
                    } else {
                        smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        sAddress = smsMessages[i].getOriginatingAddress();
                        sFullMessage += smsMessages[i].getMessageBody();
                        smsDayTime = new Date(smsMessages[i].getTimestampMillis());
                    }
                }

                String time = Globals.timeFormat.format(smsDayTime);
                String sID =ReadSMS.getNextID(context);
                String sAcct_num=ReadSMS.extractAccountNo(sFullMessage);
                String sJob_No=ReadSMS.extractJobNo(sFullMessage);
                String sPhone_No=ReadSMS.extractPhoneNo(sFullMessage);
                int iPriority=ReadSMS.extractPriority(sFullMessage);

                Log.d("SmsReceiver","sFullMessage ->"+sFullMessage);
                if (ReadSMS.IsValidJobNo(sJob_No)) {// Added on 2017/06/30 to prevent irrelevant sms to add as a breakdown
                    Breakdown breakdown = new Breakdown();
                    if(!sAcct_num.equals("")){
                        String filtered = sFullMessage
                                .replace(sAcct_num,"")
                                .replace(sPhone_No+",","")
                                .replace(sJob_No+",","");
                        breakdown.set_Full_Description(filtered);
                    }else{
                        breakdown.set_Full_Description(sFullMessage);
                        // breakdown = Globals.dbHandler.GetCustomerData(sAcct_num); // Local consumer database not using
                    }
                    /*String description =sFullMessage.replace(sAcct_num,"")
                            .replace(sJob_No,"")
                            .replace(time,"")
                            .replace(sPhone_No,"")
                            .replace(breakdown.get_ADDRESS(),"")
                            .replace(breakdown.get_Name(),"");*/

                    breakdown.set_Received_Time(time);
                    breakdown.set_inbox_ref(sID + " " +time);
                    breakdown.set_Acct_Num(sAcct_num);
                    //breakdown.set_Full_Description(description);
                    breakdown.set_JOB_SOURCE("IT");//sAddress
                    breakdown.set_Job_No(sJob_No);
                    breakdown.MobilePhNo=(sPhone_No);
                    breakdown.set_Priority(iPriority);
                    breakdown.set_ParentBreakdownId("0000000000");
                    breakdown.set_BA_SERVER_SYNCED("0");
                    /*Log.d("sID","="+sID);// empty box, no SMS
                    Log.d("sAddress","="+sAddress);
                    Log.d("sFullMessage","="+sFullMessage);
                    Log.d("time","="+time);
                    Log.d("sJob_No","="+sJob_No);
                    Log.d("sAcct_num","="+sAcct_num);
                    Log.d("sPhone_No","="+sPhone_No);
                    Log.d("iPriority","="+iPriority);*/
                    //dbHandler.addBreakdown(sID, time, sAcct_num, sFullMessage, sJob_No, sPhone_No, sAddress,iPriority);
                    StartMainActivityIfRequired(context);

                    Globals.dbHandler.InsertOrUpdateBreakdown(breakdown);
                    //Informing the Map view about the new bd, then it can add it
                    Intent myintent=new Intent();
                    myintent.setAction("lk.steps.breakdownassistpluss.MainActivityBroadcastReceiver");
                    myintent.putExtra("sms_received", "sms_received");
                    context.sendBroadcast(myintent);
                    SyncService.PostBreakdowns(context);
                    PlayTone(context, R.raw.ding_ling, false);
                }else{
                    Log.d("SmsReceiver","NOt a breakdown sms ->"+sJob_No);
                }
                /*  MediaPlayer mPlayer2;
                    mPlayer2= MediaPlayer.create(context, R.raw.crash);
                    mPlayer2.start();*/
               // }
            }
        }catch(Exception e){
            Log.e("SmsBroadcastReceiver",e.getMessage());
        }
    }

    private void StartMainActivityIfRequired(final Context context) {
        if(Globals.dbHandler==null){
            StartUpTasks.InitVariables(context);
        }
        if (!isForeground(context)) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Intent myIntent = new Intent(context, MainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(myIntent);
               }
            });
        }
    }

    public boolean isForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals("lk.steps.breakdownassistpluss");
    }

    private void PlayTone(Context context, int resourceId, boolean looping) {
        mMediaPlayer = MediaPlayer.create(context, resourceId);
        mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setLooping(looping);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
                Log.e("MediaPlayer", "onCompletion");
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                Log.e("MediaPlayer", "onPrepared");
            }
        });
        //mMediaPlayer.prepareAsync();
        //mMediaPlayer.start();
    }
    private void StopMediaPlayer(){
        try{
            if(mMediaPlayer != null){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }catch(Exception e){

        }
    }


}