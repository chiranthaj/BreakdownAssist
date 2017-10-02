package lk.steps.breakdownassistpluss;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Chirantha on 15/10/2016.
 */
public class ReadSMS {
    /* TODO : Handle the empty inbox case when a new SMS receive error*/

    public static String getNextID(Context context) {
        String[] reqCols = new String[]{"_id"};
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), reqCols, null, null, null);

        String returnval = "1";
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            returnval = String.valueOf((Long.parseLong(cursor.getString(0)) + 1));
        } else {
            // empty box, no SMS
            returnval = "1";
        }

        cursor.close();
        return returnval;
    }

    public static void SyncSMSInbox(Context context) {
        ProgressDialog progress;
        progress = ProgressDialog.show(context, "dialog title",
                "dialog message", true);

        // List required columns

        String[] reqCols = new String[]{"_id", "address", "date_sent", "body"};
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), reqCols, null, null, null);
        if(cursor != null){
            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    String sID = cursor.getString(0);
                    String sAddress = cursor.getString(1);
                    String sFullMessage = cursor.getString(3);
                    Date callDayTime = new Date(Long.parseLong(cursor.getString(2)));
                    //SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
                    String time = Globals.timeFormat.format(callDayTime);
                    //String sNextID =getNextID(context);
                    String sJob_No = extractJobNo(sFullMessage);
                    String sAcct_num = extractAccountNo(sFullMessage);
                    String sPhone_No = extractPhoneNo(sFullMessage);
                    int iPriority=extractPriority(sFullMessage);
                    Log.d("SmsReceiver","="+sJob_No);// empty box, no SMS
                    if (IsValidJobNo(sJob_No)) {// Added on 2017/05/22 to prevent irrelevant sms to add as a breakdown
                        DBHandler dbHandler = new DBHandler(context, null, null, 1);
                        dbHandler.addBreakdown(sID, time, sAcct_num, sFullMessage, sJob_No, sPhone_No, sAddress,iPriority);
                        dbHandler.close();
                        Log.d("SmsReceiver",sJob_No);// empty box, no SMS
                    }
                }
                while (cursor.moveToNext());
            } else {
                Log.d("Breakdown added","Not a breakdown sms");// empty box, no SMS
            }
            progress.dismiss();
            cursor.close();
        }
    }

    public static boolean IsValidJobNo(String jobNo) {
        if (jobNo == null) return false;
        return jobNo.trim().length() >= 20;
    }

    static String extractAccountNo(String sInputText)//To detect area codes part (41, 42 ) in the account no
    {
        String sAccountNo = "";
        String ProbableAreaCodesMask = "";

        try {
            if (Globals.NoOfAreaCodes == 1 && Globals.AreaCode1.length() == 2) //detect only area code 41
            {
                ProbableAreaCodesMask = "(" + Globals.AreaCode1 + "\\d\\d\\d\\d\\d\\d\\d\\d" + ")";
            } else if (Globals.NoOfAreaCodes == 2 && Globals.AreaCode1.length() == 2 && Globals.AreaCode2.length() == 2)//detect both area codes 41,42
            {
                ProbableAreaCodesMask = "(" + Globals.AreaCode1 + "\\d\\d\\d\\d\\d\\d\\d\\d" + "|"
                        + Globals.AreaCode2 + "\\d\\d\\d\\d\\d\\d\\d\\d" + ")";
            } else if (Globals.NoOfAreaCodes == 3 && Globals.AreaCode1.length() == 2
                    && Globals.AreaCode2.length() == 2 && Globals.AreaCode3.length() == 2)//detect all the area codes 41,42,71
            {
                ProbableAreaCodesMask = "(" + Globals.AreaCode1 + "\\d\\d\\d\\d\\d\\d\\d\\d" + "|"
                        + Globals.AreaCode2 + "\\d\\d\\d\\d\\d\\d\\d\\d" + "|" + Globals.AreaCode3 + "\\d\\d\\d\\d\\d\\d\\d\\d" + ")";
            }

            String patternString = ProbableAreaCodesMask;
            Matcher m = Pattern.compile(patternString).matcher(sInputText);

            if (m.find()) {
                sAccountNo = m.group(1);
            }
        } catch (Exception e) {
            Log.e("extractAccountNo", e.getMessage());
            sAccountNo = "0000000000";
        }
        return sAccountNo;
    }

/*
* J41/H/2016/09/08/4.1, , N, 0112098250, D.R. GABADAGE, NO: 126/A,,STATION RD, ,HOMAGAMA., Supply fails only in house, 4108481003
* J41/H/2016/07/31/9.1
* C42/M/2016/12/02/10   J42/M/2016/12/02/10.1
*/

    //TODO : Extract ACCT_NUM, JOB NO< Tel No in One shot
    //Pattern J(41|71|77)/[A-Z]/2\d\d\d/\d\d/\d\d/\d*\.\d*
    public static String extractJobNo(String sInputText)//To detect Job no
    {
        String sJobNo = "";
        String ProbableAreaCodesMask = "";
        try {
            if (Globals.NoOfAreaCodes == 1 && Globals.AreaCode1.length() == 2) //detect only area code 41
            {
                ProbableAreaCodesMask = "(J" + "(" + Globals.AreaCode1 + ")" + "/[A-Z]/2\\d\\d\\d/\\d\\d/\\d\\d/\\d*\\.\\d*)";
            } else if (Globals.NoOfAreaCodes == 2 && Globals.AreaCode1.length() == 2 && Globals.AreaCode2.length() == 2)//detect both area codes 41,42
            {
                ProbableAreaCodesMask = "(J(" + Globals.AreaCode1 + "|" + Globals.AreaCode2 + ")" + "/[A-Z]/2\\d\\d\\d/\\d\\d/\\d\\d/\\d*\\.\\d*)";
            } else if (Globals.NoOfAreaCodes == 3 && Globals.AreaCode1.length() == 2
                    && Globals.AreaCode2.length() == 2 && Globals.AreaCode3.length() == 2)//detect all the area codes 41,42,71
            {
                ProbableAreaCodesMask = "(J(" + Globals.AreaCode1 + "|" + Globals.AreaCode2 + "|" + Globals.AreaCode3 + ")"
                        + "/[A-Z]/2\\d\\d\\d/\\d\\d/\\d\\d/\\d*\\.\\d*)";
            }

            String patternString = ProbableAreaCodesMask;
            Matcher m = Pattern.compile(patternString).matcher(sInputText);

            if (m.find()) {
                sJobNo = m.group(1);
            }
        } catch (Exception e) {
            Log.e("extractJobNo", e.getMessage());
            sJobNo = "0000000000";
        }
        return sJobNo;
    }

    public static String extractPhoneNo(String sInputText)//To detect Phone no
    {
        String sPhoneNo = "";
        String ProbableAreaCodesMask = "";

        ProbableAreaCodesMask = "(\\b0\\d\\d\\d\\d\\d\\d\\d\\d\\d\\b|\\b\\d\\d\\d\\d\\d\\d\\d\\d\\d\\b)";

        String patternString = ProbableAreaCodesMask;
        Matcher m = Pattern.compile(patternString).matcher(sInputText);

        if (m.find()) {
            sPhoneNo = m.group(1);
        }
        return sPhoneNo;
    }

    public static int extractPriority(String sInputText)//To detect Priority N,U,V,H,L
    {
        String sPriority = "";
        int iPriority = Breakdown.Priority_Normal;
        String ProbableAreaCodesMask = "";

        ProbableAreaCodesMask = ", ([NUVHL]),";

        try{
            Matcher m = Pattern.compile(ProbableAreaCodesMask).matcher(sInputText);

            if (m.find()) {
                sPriority = m.group(1);
            }

            if (sPriority.equals("N")){
                iPriority=Breakdown.Priority_Normal;
            }else if (sPriority.equals("U")){
                iPriority=Breakdown.Priority_Urgent;
            }else if (sPriority.equals("V")){
                iPriority=Breakdown.Priority_High;
            }else if (sPriority.equals("H")){
                iPriority=Breakdown.Priority_High;
            }else if (sPriority.equals("L")){
                iPriority=Breakdown.Priority_Normal;
            }else {
                iPriority=Breakdown.Priority_Normal;
            }

        }catch(Exception e){

        }
        return iPriority;
    }

}
