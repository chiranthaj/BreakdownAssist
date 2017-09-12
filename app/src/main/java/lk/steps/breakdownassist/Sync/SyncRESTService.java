package lk.steps.breakdownassist.Sync;

import android.net.Credentials;

import lk.steps.breakdownassist.MainActivity;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by JagathPrasnaga on 24/08/2017.
 */

public class SyncRESTService {
    private static final String URL = "http://192.168.137.1:31525/";//10.0.2.2//222.165.188.234:8080
    //private static final String URL = "http://111.223.135.20/Team/";

    private SyncService syncService;

    public SyncRESTService()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        syncService = retrofit.create(SyncService.class);
    }

    public SyncService getService()
    {
        return syncService;
    }

}
