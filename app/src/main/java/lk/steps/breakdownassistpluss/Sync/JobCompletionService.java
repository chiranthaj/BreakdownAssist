package lk.steps.breakdownassistpluss.Sync;

import java.util.List;

import lk.steps.breakdownassistpluss.JobCompletion;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
