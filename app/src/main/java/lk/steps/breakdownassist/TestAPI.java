package lk.steps.breakdownassist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TestAPI extends AppCompatActivity {
    Button buttonTest;
    jobstatuschangesRestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        restService = new jobstatuschangesRestService();


        buttonTest = (Button) findViewById(R.id.buttonTestAPI);
        buttonTest.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        restService.getService().getJobById("G","F", new Callback<JobChangeStatus>() {
                            @Override
                            public void success(JobChangeStatus job, Response response) {

                                Toast.makeText(getApplicationContext(), response.getReason() , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
        );
    }

}
