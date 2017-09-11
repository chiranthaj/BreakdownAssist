package lk.steps.breakdownassist.Sync;

/**
 * Created by JagathPrasnaga on 24/08/2017.
 */

public class SyncRESTService {
    private static final String URL = "http://192.168.137.1:31525/Team/";//10.0.2.2//222.165.188.234:8080
    //private static final String URL = "http://111.223.135.20/Team/";
    private retrofit.RestAdapter restAdapter;
    private SyncService apiService;

    public SyncRESTService()
    {
        restAdapter = new  retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .build();

        apiService = restAdapter.create(SyncService.class);
    }

    public SyncService getService()
    {
        return apiService;
    }
}
