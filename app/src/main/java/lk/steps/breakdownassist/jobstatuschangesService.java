package lk.steps.breakdownassist;

/**
 * Created by Chirantha on 13/06/2017.
 */

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface jobstatuschangesService {
    //Retrofit turns the WEB API into a Java interface.

    @GET("/api/jobstatuschanges")
    public void getJobStatusRecs(Callback<List<JobChangeStatus>> callback);

    @GET("/api/jobstatuschanges")
    public void getJobStatusRec(@Query("job_no") String job_no, @Query("st_code") String st_code,
                           @Query("change_datetime") String change_datetime,
                           Callback<JobChangeStatus> callback);

    @DELETE("/api/jobstatuschanges/{id}")
    public void deleteJobStatusRec(@Path("id") Integer id,Callback<JobChangeStatus> callback);

    @PUT("/api/jobstatuschanges/{id}")
    public void updateJobStatusRec(@Path("id") Integer id,@Body JobChangeStatus JobStatusRec,
                                   Callback<JobChangeStatus> callback);

    @POST("/api/jobstatuschanges")
    public void addJobStatusRec(@Body JobChangeStatus JobStatusRec,Callback<JobChangeStatus> callback);

}
