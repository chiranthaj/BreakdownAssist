package lk.steps.breakdownassist;

/**
 * Created by Chirantha on 25/06/2017.
 */

public class JobCompletionRESTService {
    private static final String URL = "http://222.165.188.234:8080/jobstatusapi/";//10.0.2.2//222.165.188.234:8080
    private retrofit.RestAdapter restAdapter;
    private JobCompletionService apiService;

    public JobCompletionRESTService()
    {
        restAdapter = new retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .build();

        apiService = restAdapter.create(JobCompletionService.class);
    }

    public JobCompletionService getService()
    {
        return apiService;
    }
}
