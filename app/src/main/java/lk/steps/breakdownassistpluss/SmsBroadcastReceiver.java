package lk.steps.breakdownassistpluss;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsBroadcastReceiver extends BroadcastReceiver
{//TODO : Use a method to unregisterReceiver the BroadcastReceiver when app is not in use, on Destroy,
// hint : add this BroadcastReceiver in run time and destroy from it in the Main activity,
// TODO :or make it a service to add the sms in background to the Database, then no need to Sync

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent)
    {
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


            if (ReadSMS.IsValidJobNo(sJob_No)) {// Added on 2017/06/30 to prevent irrelevant sms to add as a breakdown
                DBHandler dbHandler = new DBHandler(context, null, null, 1);
                dbHandler.addBreakdown(sID, time, sAcct_num, sFullMessage, sJob_No, sPhone_No, sAddress,iPriority);
                String sIssuedBreakdownID=dbHandler.getLastBreakdownID();
                dbHandler.close();

                //Informing the Map view about the new bd, then it can add it
                Intent myintent=new Intent();
                myintent.setAction("lk.steps.breakdownassistpluss.NewBreakdownBroadcast");

                myintent.putExtra("_id",sIssuedBreakdownID);
                context.sendBroadcast(myintent);
            }else{
                Log.d("SmsReceiver","NOt a breakdown sms");
            }

/*                MediaPlayer mPlayer2;
                mPlayer2= MediaPlayer.create(context, R.raw.crash);
                mPlayer2.start();*/
           // }

        }
    }

}