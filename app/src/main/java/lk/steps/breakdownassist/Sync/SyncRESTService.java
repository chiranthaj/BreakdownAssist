package lk.steps.breakdownassist.Sync;

import java.util.concurrent.TimeUnit;

import lk.steps.breakdownassist.Globals;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by JagathPrasnaga on 24/08/2017.
 */

public class SyncRESTService {
   // private static final String serverUrl = "http://192.168.137.1:31525/";//10.0.2.2//222.165.188.234:8080
    //private static final String URL = "http://111.223.135.20/Team/";

    private SyncService syncService;

    public SyncRESTService()
    {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.serverUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        syncService = retrofit.create(SyncService.class);
    }

    public SyncService getService()
    {
        return syncService;
    }

}
