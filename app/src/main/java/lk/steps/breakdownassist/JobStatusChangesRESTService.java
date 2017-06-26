package lk.steps.breakdownassist;

/**
 * Created by Chirantha on 13/06/2017.
 */

public class JobStatusChangesRESTService {
    private static final String URL = "http://222.165.188.234:8080/jobstatusapi/";//10.0.2.2/
    private retrofit.RestAdapter restAdapter;
    private JobStatusChangesService apiService;

    public JobStatusChangesRESTService()
    {
        restAdapter = new retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .build();

        apiService = restAdapter.create(JobStatusChangesService.class);
    }

    public JobStatusChangesService getService()
    {
        return apiService;
    }
}
