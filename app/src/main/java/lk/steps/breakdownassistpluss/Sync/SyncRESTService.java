package lk.steps.breakdownassistpluss.Sync;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import lk.steps.breakdownassistpluss.Globals;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by JagathPrasnaga on 24/08/2017.
 */

public class SyncRESTService {
   // private static final String serverUrl = "http://192.168.137.1:31525/";//10.0.2.2//222.165.188.234:8080
    //private static final String URL = "http://111.223.135.20/Team/";

    private SyncApi syncService;
    private OkHttpClient okHttpClient;

    public SyncRESTService(int timeout)
    {
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.serverUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        syncService = retrofit.create(SyncApi.class);
    }

    public SyncApi getService()
    {
        return syncService;
    }

    /** Close and remove all idle connections in the pool. */
    public void CloseAllConnections(){
        try {
            Log.e("okHttpClient","1 connectionCount="+okHttpClient.connectionPool().connectionCount());
            okHttpClient.connectionPool().evictAll();
            Log.e("okHttpClient","2 connectionCount="+okHttpClient.connectionPool().connectionCount());
        } catch (Exception e) {
            Log.e("CloseAllConnections",""+e.getMessage());
        }

    }

}
