package lk.steps.breakdownassist;

/**
 * Created by Chirantha on 13/06/2017.
 */

public class jobstatuschangesRestService {
    private static final String URL = "http://10.0.2.2/jobstatusapi/";//
    private retrofit.RestAdapter restAdapter;
    private jobstatuschangesService apiService;

    public jobstatuschangesRestService()
    {
        restAdapter = new retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .build();

        apiService = restAdapter.create(jobstatuschangesService.class);
    }

    public jobstatuschangesService getService()
    {
        return apiService;
    }
}
