package lk.steps.breakdownassist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chirantha on 18/06/2017.
 */

public class BackgroundService extends Service {
    DBHandler dbHandler;
    jobstatuschangesRestService restService;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        dbHandler = new DBHandler(this,null,null,1);
        restService = new jobstatuschangesRestService();
        Toast.makeText(this, "Synch Service Started", Toast.LENGTH_SHORT).show();
        Sync_JobStatusChangeObj_to_Server();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    private void Sync_JobStatusChangeObj_to_Server()
    {
        List<JobChangeStatus> JobChangeStatusList = dbHandler.getJobStatusChangeObjNotSync_List();

        for (final JobChangeStatus obj: JobChangeStatusList)
        {
            restService.getService().addJobStatusRec(obj, new Callback<JobChangeStatus>() {
                @Override
                public void success(JobChangeStatus job, Response response) {
                    dbHandler.UpdateSyncState_JobStatusChangeObj(obj,1);
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    //dbHandler.UpdateSyncState_JobStatusChangeObj(obj,0);
                }
            });
        }

    }
}
