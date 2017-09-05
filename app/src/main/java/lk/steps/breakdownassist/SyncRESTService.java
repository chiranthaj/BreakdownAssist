package lk.steps.breakdownassist;

/**
 * Created by JagathPrasnaga on 24/08/2017.
 */

public class SyncRESTService {
    private static final String URL = "http://192.168.137.1/Team/";//10.0.2.2//222.165.188.234:8080
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
