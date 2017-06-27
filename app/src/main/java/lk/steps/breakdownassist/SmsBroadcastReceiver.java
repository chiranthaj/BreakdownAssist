package lk.steps.breakdownassist;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

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
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            for (int i = 0; i < sms.length; ++i)
            {
                SmsMessage smsMessage;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = intentExtras.getString("format");
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                } else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                }

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                Date callDayTime = new Date(smsMessage.getTimestampMillis());
                //SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
                String time = Globals.timeFormat.format(callDayTime);
                String sNextID =ReadSMS.getNextID(context);
                String sAcct_num=ReadSMS.extractAccountNo(smsBody);
                String sJob_No=ReadSMS.extractJobNo(smsBody);
                String sPhone_No=ReadSMS.extractPhoneNo(smsBody);

                DBHandler dbHandler = new DBHandler(context,null,null,1);
                dbHandler.addBreakdown(sNextID,time,sAcct_num,smsBody,sJob_No,sPhone_No,address);
                String sIssuedBreadownID=dbHandler.getLastBreakdownID();
                dbHandler.close();

                //Informing the Map view about the new bd, then it can add it
                Intent myintent=new Intent();
                myintent.setAction("lk.steps.breakdownassist.NewBreakdownBroadcast");

                myintent.putExtra("_id",sIssuedBreadownID);
                context.sendBroadcast(myintent);

/*                MediaPlayer mPlayer2;
                mPlayer2= MediaPlayer.create(context, R.raw.crash);
                mPlayer2.start();*/
            }

        }
    }

}