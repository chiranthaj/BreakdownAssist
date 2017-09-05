package lk.steps.breakdownassist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import lk.steps.breakdownassist.HtmlTextView.HtmlTextView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AboutActivity extends Activity {

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
/*
        PackageInfo info;
        String vName ="";
        int vCode =0;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            vName = info.versionName;
            vCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

        String version = "0";
        int versionCode = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textview = (TextView) findViewById(R.id.vName);
        textview.setText("Version : "+ version);
      //  textview.setText("Version : "+ Global.VersionName.substring(0, Math.min(Global.VersionName.length(), 5)));

        textview = (TextView) findViewById(R.id.vCode);
        textview.setText("Build number : " + Integer.toString(versionCode));

        HtmlTextView text = (HtmlTextView) findViewById(R.id.html_text);
        text.setHtmlFromString(readEula(this), true);
       // Typeface font = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
      //  text.setTypeface(font);

    }

    private static String readEula(Activity activity) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(activity.getAssets().open("eula.txt")));
            String line;
            String eula="";
            while ((line = in.readLine()) != null) eula=eula+line;
            Log.d("eula ", eula);
            return eula;
        } catch (IOException e) {
            return "";
        }
    }


    public void test(View view){
      /*  NewBreakdownRESTService newBreakdownRESTService = new NewBreakdownRESTService();

        JobChangeStatus _jobchangestatus_obj= new JobChangeStatus();
        _jobchangestatus_obj.job_no=("456358");
        _jobchangestatus_obj.st_code=("st_code");
        _jobchangestatus_obj.change_datetime="";
        _jobchangestatus_obj.comment=("comment");
        _jobchangestatus_obj.device_timestamp=("");
        _jobchangestatus_obj.synchro_mobile_db=1;

        newBreakdownRESTService.getService().UpdateBreakdownStatus(_jobchangestatus_obj, new Callback<JobChangeStatus>() {
            @Override
            public void success(JobChangeStatus job, Response response) {
                Log.d("API-","OK");
               // dbHandler.UpdateSyncState_JobStatusChangeObj(obj,1);//Successfully done
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("API-","error");
                //Toast.makeText(getApplicationContext()," RetrofitError " + error.getMessage().toString(), Toast.LENGTH_LONG).show();
                if (!error.isNetworkError()) {
                    if (error.getResponse().getStatus()==409){
                       // dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 1); //Already record is there, may be due to timeout
                    }else {
                       // dbHandler.UpdateSyncState_JobStatusChangeObj(obj, -5); //To avoid retry again and again
                    }
                }else if(error.isNetworkError()){
                    //dbHandler.UpdateSyncState_JobStatusChangeObj(obj, 0);//Not Uploaded due to no network
                }
            }
        });*/










        /*newBreakdownRESTService.getService().getNewBreakdowns("111","77","S","2", new Callback<List<Breakdown>>() {
            @Override
            public void success(List<Breakdown> breakdowns, Response response) {
                Log.d("API-","OK");
                Log.d("breakdowns-",""+breakdowns.size());
                DBHandler dbHandler = new DBHandler(getApplicationContext(), null, null, 1);
                for (Breakdown breakdown :breakdowns) {
                    dbHandler.addBreakdown2(
                            breakdown.get_Received_Time(),
                            breakdown.get_Acct_Num(),
                            breakdown.get_Full_Description(),
                            breakdown.get_Job_No(),
                            breakdown.get_Contact_No(),
                            breakdown.get_ADDRESS(),
                            1);
                }
                String sIssuedBreakdownID=dbHandler.getLastBreakdownID();
                dbHandler.close();

                //Informing the Map view about the new bd, then it can add it
                Intent myintent=new Intent();
                myintent.setAction("lk.steps.breakdownassist.NewBreakdownBroadcast");
                myintent.putExtra("_id",sIssuedBreakdownID);
                getApplicationContext().sendBroadcast(myintent);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("API-","ERROR-"+error.getMessage());
            }

        });*/
    }
}
