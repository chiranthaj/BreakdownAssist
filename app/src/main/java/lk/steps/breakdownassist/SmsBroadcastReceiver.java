package lk.steps.breakdownassist;


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
            String msgBody = "";
            String msg_from = "";
            Date rxTime  = null;
            try {
                Object[] pdus = (Object[]) intentExtras.get(SMS_BUNDLE);
                SmsMessage[] msgs = new SmsMessage[pdus.length];

                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    msg_from = msgs[i].getOriginatingAddress();
                    msgBody += msgs[i].getMessageBody();
                    rxTime = new Date(msgs[i].getTimestampMillis());
                }

                String time = Globals.timeFormat.format(rxTime);
                String sID =ReadSMS.getNextID(context);
                String sAcct_num=ReadSMS.extractAccountNo(msgBody);
                String sJob_No=ReadSMS.extractJobNo(msgBody);
                String sPhone_No=ReadSMS.extractPhoneNo(msgBody);
                int iPriority=ReadSMS.extractPriority(msgBody);

                /*DBHandler dbHandler = new DBHandler(context,null,null,1);
                dbHandler.addBreakdown(sNextID,time,sAcct_num,smsBody,sJob_No,sPhone_No,address,iPriority);
                String sIssuedBreadownID=dbHandler.getLastBreakdownID();
                dbHandler.close();*/

                if (ReadSMS.IsValidJobNo(sJob_No)) {// Added on 2017/06/30 to prevent irrelevant sms to add as a breakdown
                    DBHandler dbHandler = new DBHandler(context, null, null, 1);
                    dbHandler.addBreakdown(sID, time, sAcct_num, msgBody, sJob_No, sPhone_No, msg_from,iPriority);
                    String sIssuedBreakdownID=dbHandler.getLastBreakdownID();
                    dbHandler.close();

                    //Informing the Map view about the new bd, then it can add it
                    Intent myintent=new Intent();
                    myintent.setAction("lk.steps.breakdownassist.NewBreakdownBroadcast");

                    myintent.putExtra("_id",sIssuedBreakdownID);
                    context.sendBroadcast(myintent);
                }else{
                    Log.d("SmsReceiver","NOt a breakdown sms");
                }

            } catch (Exception e) {
                //                            Log.d("Exception caught",e.getMessage());
            }





/*                MediaPlayer mPlayer2;
                mPlayer2= MediaPlayer.create(context, R.raw.crash);
                mPlayer2.start();*/
           // }

        }
    }

}