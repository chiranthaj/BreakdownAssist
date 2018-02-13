package lk.steps.breakdownassistpluss.Sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

import lk.steps.breakdownassistpluss.Common;
import lk.steps.breakdownassistpluss.Globals;
import lk.steps.breakdownassistpluss.Models.Interruption;
import lk.steps.breakdownassistpluss.Models.Team;
import lk.steps.breakdownassistpluss.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JagathPrasanga on 1/7/2018.
 */

public class Network {
    public static void GetTeams(final Context context) {
        try {
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<Team>> call = syncRESTService.getService()
                    .GetTeams("Bearer " + Globals.mToken.access_token, Globals.mToken.area_id);

            call.enqueue(new Callback<List<Team>>() {
                @Override
                public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                    if (response.isSuccessful()) {
                        //Log.e("GetNewBreakdowns","Successful");
                        List<Team> teams = response.body();
                       // Log.e("GetTeams", "Successful" + new Gson().toJson(response));
                        //Log.e("GetTeams", "Number-" + teams.size());
                       // Log.e("GetTeams", "Number-" + response);

                        String teams_in_area = new Gson().toJson(teams);
                        Common.WriteStringPreferences(context,"teams_in_area",teams_in_area);

                    } else if (response.errorBody() != null) {
                        if (response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            Common.RemoteLoginWithLastCredentials(context,1);
                        } else {
                            Toast.makeText(context, "GetTeams\nResponse code =" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetTeams", "onResponse" + response.errorBody() + "*code*" + response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<Team>> call, Throwable t) {
                    Toast.makeText(context, "Error in network..\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetTeams", "5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });
        } catch (Exception e) {
            Log.e("GetTeams", "" + e.getMessage());
        }
    }



    public static void GetInterruptions(final Context context) {
        try {
            String device_id = Common.ReadStringPreferences(context,"device_id", "");
            final SyncRESTService syncRESTService = new SyncRESTService(10);
            Call<List<Interruption>> call = syncRESTService.getService()
                    .GetAllInterruptions("Bearer " + Globals.mToken.access_token, device_id);

            call.enqueue(new Callback<List<Interruption>>() {
                @Override
                public void onResponse(Call<List<Interruption>> call, Response<List<Interruption>> response) {
                    if (response.isSuccessful()) {
                        //Log.e("GetNewBreakdowns","Successful");
                        List<Interruption> interruptions = response.body();
                       //  Log.e("GetInterruptions", "Successful" + new Gson().toJson(response));
                        Log.e("GetInterruptions", "Number-" + interruptions.size());
                        // Log.e("GetTeams", "Number-" + response);

                        Intent intent = new Intent();
                        intent.setAction("lk.steps.breakdownassistpluss.CalenderActivityBroadcastReceiver");
                        intent.putExtra("interruptions", new Gson().toJson(interruptions));
                        context.sendBroadcast(intent);
                       // Common.WriteStringPreferences(context,"teams_in_area",teams_in_area);

                    } else if (response.errorBody() != null) {
                        if (response.code() == 401) { //Authentication fail
                            Toast.makeText(context, "Authentication fail..", Toast.LENGTH_SHORT).show();
                            Common.RemoteLoginWithLastCredentials(context,1);
                        } else {
                            Toast.makeText(context, "GetInterruptions\nResponse code =" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("GetInterruptions", "onResponse" + response.errorBody() + "*code*" + response.code());
                    }
                    syncRESTService.CloseAllConnections();
                }

                @Override
                public void onFailure(Call<List<Interruption>> call, Throwable t) {
                    Toast.makeText(context, "Error in network..\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GetInterruptions", "5-" + t.getMessage());
                    syncRESTService.CloseAllConnections();
                }
            });
        } catch (Exception e) {
            Log.e("GetInterruptions", "" + e.getMessage());
        }
    }
}
