package lk.steps.breakdownassist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TestAPI extends AppCompatActivity {
    Button buttonTest,buttonPost,buttonSaveToDB,buttonSync;
    jobstatuschangesRestService restService;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        restService = new jobstatuschangesRestService();
        dbHandler = new DBHandler(this,null,null,1);

        buttonTest = (Button) findViewById(R.id.buttonTestAPI);
        buttonTest.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        restService.getService().getJobStatusRec("DDFDF","F",
                                "2017-06-15T00:00:00", new Callback<JobChangeStatus>() {
                            @Override
                            public void success(JobChangeStatus job, Response response) {
                                Toast.makeText(getApplicationContext(), response.getReason()+" "+ job.comment +" "
                                        + job.change_datetime.toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
        );
        buttonPost= (Button) findViewById(R.id.buttonPost);
        buttonPost.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JobChangeStatus myjobstatusRec=new JobChangeStatus();
                        myjobstatusRec.job_no="chira";
                        myjobstatusRec.change_datetime="2017-06-15T00:00:00";
                        myjobstatusRec.st_code="J";
                        myjobstatusRec.comment="Test comment abcd";


                        restService.getService().addJobStatusRec(myjobstatusRec, new Callback<JobChangeStatus>() {
                                    @Override
                                    public void success(JobChangeStatus job, Response response) {
                                        Toast.makeText(getApplicationContext(), response.getReason()+" "+ job.comment +" "
                                                + job.change_datetime.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_LONG).show();

                                    }
                                });
                    }
                }
        );

        buttonSaveToDB= (Button) findViewById(R.id.buttonSaveToDB);
        buttonSaveToDB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JobChangeStatus myjobstatusRec=new JobChangeStatus();
                        myjobstatusRec.job_no="chira";

                        Date callDayTime = new Date( System.currentTimeMillis());
                        myjobstatusRec.change_datetime=Globals.timeFormat.format(callDayTime);
                        myjobstatusRec.st_code="J";
                        myjobstatusRec.comment="Test comment abcd";

                        dbHandler.addJobStatusChangeObj(myjobstatusRec);

                        Toast.makeText(getApplicationContext(), "OK DB2", Toast.LENGTH_SHORT).show();

                    }
                }
        );

    }

    public void startSyncService(View view) {
        startService(new Intent(getBaseContext(), BackgroundService.class));
    }
    public void stopSyncService(View view) {
        stopService(new Intent(getBaseContext(), BackgroundService.class));
    }
}
