package lk.steps.breakdownassist.Sync;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.IOException;

/**
 * Created by JagathPrasnaga on 24/08/2017.
 */


import lk.steps.breakdownassist.MainActivity;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class SyncAuthService {
    private static final String URL = "http://192.168.137.1:31525/";//10.0.2.2//222.165.188.234:8080
    //private static final String URL = "http://111.223.135.20/Team/";
    private retrofit.RestAdapter restAdapter;
    private SyncService apiService;
   // private String Jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6ImExM2RjZDQ0LTZhODgtNGU1ZS05Y2YzLWJhYTZkOGVhYTA0YyIsImp0aSI6ImNjNGNiNWExLTFjMjEtNDJmZC1hNDRhLTQwM2ViMWQ0ODgyZSIsImlhdCI6MTUwNTA2MjQyNCwiaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5L2NsYWltcy9yb2xlIjpbIk1hbmFnZXIiLCJNZW1iZXIiLCJBcmVhIGVuZ2luZWVyIiwiQmFjay1vZmZpY2UgYWdlbnQiLCJMaW5lc21hbiIsIkNhbGwgYWdlbnQiLCJBZG1pbmlzdHJhdG9yIl0sIm5iZiI6MTUwNTA2MjQyNCwiZXhwIjoxNTA1Mjc4NDI0LCJpc3MiOiJBbHRhaXJDQSIsImF1ZCI6IkFsdGFpckNBQXVkaWVuY2UifQ.a9RXq8yZ-ixkL3prESCsuyyQ0UbgSvoGxBHrT1JXf30";



    public SyncAuthService()
    {
        restAdapter = new  retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .build();

        apiService = restAdapter.create(SyncService.class);
    }

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {

            request.addHeader("Authorization", "Bearer "+ MainActivity.mToken.access_token);
        }
    };

    public SyncService getService()
    {
        return apiService;
    }


}