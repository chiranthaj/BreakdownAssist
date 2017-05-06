package lk.steps.breakdownassist;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Chirantha on 15/10/2016.
 */
public class ReadSMS
{

    /* TODO : Handle the empty inbox case when a new SMS receive error*/

    public static String getNextID(Context context)
    {
        String[] reqCols = new String[] { "_id"};
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), reqCols,  null, null, null);

        String returnval="1";
        if (cursor.moveToFirst())
        { // must check the result to prevent exception
            returnval=String.valueOf((Long.parseLong( cursor.getString(0))+1));
        }
        else
        {
            // empty box, no SMS
            returnval="1";
        }

        cursor.close();
        return returnval;
    }

    public static void SyncSMSInbox(Context context)
    {
        ProgressDialog progress;
        progress = ProgressDialog.show(context, "dialog title",
                "dialog message", true);

        // List required columns

        String[] reqCols = new String[] { "_id", "address","date_sent", "body"};
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), reqCols, null, null, null);

        if (cursor.moveToFirst())
        { // must check the result to prevent exception
            do
            {
                String sID = cursor.getString(0);
                String sAddress = cursor.getString(1);
                String sFullMessage=cursor.getString(3);
                Date callDayTime = new Date(Long.parseLong( cursor.getString(2)));
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy:MM:d h:m:s a");
                String time = timeFormat.format(callDayTime);
//                //String sNextID =getNextID(context);
                String sAcct_num=extractAccountNo(sFullMessage);
                String sJob_No=extractJobNo(sFullMessage);
                String sPhone_No=extractPhoneNo(sFullMessage);

                MyDBHandler dbHandler= new MyDBHandler (context,null,null,1);
                dbHandler.addBreakdown(sID,time,sAcct_num,sFullMessage,sJob_No,sPhone_No,sAddress);

                dbHandler.close();
            }
            while (cursor.moveToNext());
        }
        else
        {
            // empty box, no SMS
        }
        progress.dismiss();


        cursor.close();
    }

    public static String extractAccountNo(String sInputText)//To detect area codes part (41, 42 ) in the account no
    {
        String sAccountNo="";
        String ProbableAreaCodesMask ="";

        if (Globals.NoOfAreaCodes==1 && Globals.AreaCode1.length()==2) //detect only area code 41
        {
            ProbableAreaCodesMask ="(" + Globals.AreaCode1 + "\\d\\d\\d\\d\\d\\d\\d\\d" + ")" ;
        }
        else if (Globals.NoOfAreaCodes==2 && Globals.AreaCode1.length()==2 && Globals.AreaCode2.length()==2)//detect both area codes 41,42
        {
            ProbableAreaCodesMask ="(" + Globals.AreaCode1 + "\\d\\d\\d\\d\\d\\d\\d\\d" +  "|"
                    + Globals.AreaCode2 + "\\d\\d\\d\\d\\d\\d\\d\\d" + ")" ;
        }
        else if (Globals.NoOfAreaCodes==3 && Globals.AreaCode1.length()==2
                && Globals.AreaCode2.length()==2 && Globals.AreaCode3.length()==2)//detect all the area codes 41,42,71
        {
            ProbableAreaCodesMask ="(" + Globals.AreaCode1 + "\\d\\d\\d\\d\\d\\d\\d\\d" + "|"
                    + Globals.AreaCode2 + "\\d\\d\\d\\d\\d\\d\\d\\d" + "|" + Globals.AreaCode3 + "\\d\\d\\d\\d\\d\\d\\d\\d" + ")" ;
        }

        String patternString=  ProbableAreaCodesMask;
        Matcher m = Pattern.compile(patternString).matcher(sInputText);

        if(m.find())
        {
            sAccountNo = m.group(1);
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
        String sJobNo="";
        String ProbableAreaCodesMask ="";

        if (Globals.NoOfAreaCodes==1 && Globals.AreaCode1.length()==2) //detect only area code 41
        {
            ProbableAreaCodesMask ="(J" + "(" + Globals.AreaCode1 + ")" + "/[A-Z]/2\\d\\d\\d/\\d\\d/\\d\\d/\\d*\\.\\d*)";
        }
        else if (Globals.NoOfAreaCodes==2 && Globals.AreaCode1.length()==2 && Globals.AreaCode2.length()==2)//detect both area codes 41,42
        {
            ProbableAreaCodesMask ="(J(" + Globals.AreaCode1 + "|" + Globals.AreaCode2 + ")" + "/[A-Z]/2\\d\\d\\d/\\d\\d/\\d\\d/\\d*\\.\\d*)" ;
        }
        else if (Globals.NoOfAreaCodes==3 && Globals.AreaCode1.length()==2
                && Globals.AreaCode2.length()==2 && Globals.AreaCode3.length()==2)//detect all the area codes 41,42,71
        {
            ProbableAreaCodesMask ="(J(" + Globals.AreaCode1 + "|" + Globals.AreaCode2 + "|" + Globals.AreaCode3 + ")"
                    + "/[A-Z]/2\\d\\d\\d/\\d\\d/\\d\\d/\\d*\\.\\d*)" ;
        }

        String patternString=  ProbableAreaCodesMask;
        Matcher m = Pattern.compile(patternString).matcher(sInputText);

        if(m.find())
        {
            sJobNo = m.group(1);
        }
        return sJobNo;
    }

    public static String extractPhoneNo(String sInputText)//To detect Phone no
    {
        String sPhoneNo="";
        String ProbableAreaCodesMask ="";

        ProbableAreaCodesMask ="(\\b0\\d\\d\\d\\d\\d\\d\\d\\d\\d\\b|\\b\\d\\d\\d\\d\\d\\d\\d\\d\\d\\b)" ;

        String patternString=  ProbableAreaCodesMask;
        Matcher m = Pattern.compile(patternString).matcher(sInputText);

        if(m.find())
        {
            sPhoneNo = m.group(1);
        }
        return sPhoneNo;
    }

}
