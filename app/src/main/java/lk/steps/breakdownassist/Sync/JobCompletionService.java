package lk.steps.breakdownassist.Sync;

import java.util.List;

import lk.steps.breakdownassist.JobCompletion;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Chirantha on 25/06/2017.
 */

public interface JobCompletionService {
    //Retrofit turns the WEB API into a Java interface.

    @GET("/api/job_completion")
    public void getJob_CompletionRecs(Callback<List<JobCompletion>> callback);

    @GET("/api/job_completion")
    public void getJob_CompletionRec(@Query("job_no") String job_no, Callback<JobCompletion> callback);

    @DELETE("/api/job_completion/{id}")
    public void deleteJob_CompletionRec(@Path("id") Integer id, Callback<JobCompletion> callback);

    @PUT("/api/job_completion/{id}")
    public void updateJob_CompletionRec(@Path("id") Integer id,@Body JobCompletion JobCompletionRec,
                                   Callback<JobCompletion> callback);

    @POST("/api/job_completion")
    public void addJob_CompletionRec(@Body JobCompletion JobCompletionRec,Callback<JobCompletion> callback);

}
