package lk.steps.breakdownassistpluss.Sync;

/**
 * Created by Chirantha on 13/06/2017.
 */

import java.util.List;

import lk.steps.breakdownassistpluss.JobChangeStatus;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface JobStatusChangesService {
    //Retrofit turns the WEB API into a Java interface.

    @GET("/api/jobstatuschanges")
    public void getJobStatusRecs(Callback<List<JobChangeStatus>> callback);

    @GET("/api/jobstatuschanges")
    public void getJobStatusRec(@Query("job_no") String job_no, @Query("st_code") String st_code,
                           @Query("change_datetime") String change_datetime,
                           Callback<JobChangeStatus> callback);

    @DELETE("/api/jobstatuschanges/{id}")
    public void deleteJobStatusRec(@Path("id") Integer id, Callback<JobChangeStatus> callback);

    @PUT("/api/jobstatuschanges/{id}")
    public void updateJobStatusRec(@Path("id") Integer id,@Body JobChangeStatus JobStatusRec,
                                   Callback<JobChangeStatus> callback);

    @POST("/api/jobstatuschanges")
    public void addJobStatusRec(@Body JobChangeStatus JobStatusRec, Callback<JobChangeStatus> callback);

}
