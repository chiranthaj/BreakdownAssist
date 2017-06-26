package lk.steps.breakdownassist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TestAPI extends AppCompatActivity {
    Button buttonTest,buttonPost,buttonSaveToDB,buttonSync;
    JobStatusChangesRESTService myJobStatuschangesRestService;
    JobCompletionRESTService myJobCompletionRESTService;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myJobStatuschangesRestService = new JobStatusChangesRESTService();
        myJobCompletionRESTService=new JobCompletionRESTService();
        dbHandler = new DBHandler(this,null,null,1);


        buttonTest = (Button) findViewById(R.id.buttonTestAPI);
        buttonTest.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        myJobStatuschangesRestService.getService().getJobStatusRec("J46/P/2017/06/20/1.1","J",
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

                        EditText editTextJobNo= (EditText) findViewById(R.id.editTextJobNo);
                        EditText editTextJobStatus= (EditText) findViewById(R.id.editTextJobStatus);
                        myjobstatusRec.job_no=editTextJobNo.getText().toString();//"J46/P/2017/06/20/1.1";
                        myjobstatusRec.change_datetime="2017-06-15T00:00:00";
                        myjobstatusRec.st_code=editTextJobStatus.getText().toString();//"B";
                        myjobstatusRec.comment="Test comment abcd";


                        myJobStatuschangesRestService.getService().addJobStatusRec(myjobstatusRec, new Callback<JobChangeStatus>() {
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

                        EditText editTextJobNo= (EditText) findViewById(R.id.editTextJobNo);
                        EditText editTextJobStatus= (EditText) findViewById(R.id.editTextJobStatus);

                        JobChangeStatus myjobstatusRec=new JobChangeStatus();
                        myjobstatusRec.job_no=editTextJobNo.getText().toString();

                        Date callDayTime = new Date( System.currentTimeMillis());
                        myjobstatusRec.change_datetime=Globals.timeFormat.format(callDayTime);
                        myjobstatusRec.st_code=editTextJobStatus.getText().toString();
                        myjobstatusRec.comment="Test comment abcd";

                        dbHandler.addJobStatusChangeRec(myjobstatusRec);

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

    public void SaveCompletedJobtoDB(View view) {
        JobCompletion myjobJobCompletion=new JobCompletion();

        EditText editTextJobNo= (EditText) findViewById(R.id.editTextJobNo);
        EditText editTextJobStatus= (EditText) findViewById(R.id.editTextJobStatus);
        myjobJobCompletion.job_no=editTextJobNo.getText().toString();//"J46/P/2017/06/20/1.1";
        myjobJobCompletion.job_completed_datetime="2017-06-15T00:00:00";
        myjobJobCompletion.st_code=editTextJobStatus.getText().toString();//"B";
        myjobJobCompletion.comment="Test comment abcd";
        myjobJobCompletion.detail_reason_code="de";
        myjobJobCompletion.cause="ca";
        myjobJobCompletion.type_failure="ty";
        myjobJobCompletion.job_completed_by="joComBy";
        myjobJobCompletion.action_code="act";
        myjobJobCompletion.device_timestamp="2017-06-27T12:13:00";
        myjobJobCompletion.synchro_mobile_db=0;

        dbHandler.addJobCompletionRec(myjobJobCompletion);

        Toast.makeText(getApplicationContext(), "SaveCompletedJobtoDB", Toast.LENGTH_SHORT).show();
    }

    public void PostCompletedJob(View view) {
        JobCompletion myjobJobCompletion=new JobCompletion();

        EditText editTextJobNo= (EditText) findViewById(R.id.editTextJobNo);
        EditText editTextJobStatus= (EditText) findViewById(R.id.editTextJobStatus);
        myjobJobCompletion.job_no=editTextJobNo.getText().toString();//"J46/P/2017/06/20/1.1";
        myjobJobCompletion.job_completed_datetime="2017-06-15T00:00:00";
        myjobJobCompletion.st_code=editTextJobStatus.getText().toString();//"B";
        myjobJobCompletion.comment="Test comment abcd";


        myJobCompletionRESTService.getService().addJob_CompletionRec(myjobJobCompletion, new Callback<JobCompletion>() {
            @Override
            public void success(JobCompletion jobCompletion, Response response) {
                Toast.makeText(getApplicationContext(), response.getReason(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
