package lk.steps.breakdownassist;

/**
 * Created by Chirantha on 13/06/2017.
 */
import java.util.Date;
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
    //Retrofit turns our WEB API into a Java interface.

    //i.e. http://localhost/api/institute/Students
    @GET("/api/jobstatuschanges")
    public void getStudent(Callback<List<JobChangeStatus>> callback);

    //i.e. http://localhost/api/institute/Students/1
    //Get student record base on ID
    @GET("/api/jobstatuschanges/{id}")
    public void getStudentById(@Path("id") Integer id,Callback<JobChangeStatus> callback);

    //i.e. http://localhost/api/institute/Students/1
    //Get student record base on ID
//    @GET("/api/Students/{id}")
//    public void getJobById( @Path("id") Integer id, Callback<Student> callback);
    @GET("/api/jobstatuschanges")
    public void getJobById(@Query("job_no") String job_no, @Query("st_code") String st_code,
                           Callback<JobChangeStatus> callback);
    //i.e. http://localhost/api/institute/Students/1
    //Delete student record base on ID
    @DELETE("/api/jobstatuschanges/{id}")
    public void deleteStudentById(@Path("id") Integer id,Callback<JobChangeStatus> callback);

    //i.e. http://localhost/api/institute/Students/1
    //PUT student record and post content in HTTP request BODY
    @PUT("/api/jobstatuschanges/{id}")
    public void updateStudentById(@Path("id") Integer id,@Body JobChangeStatus student,Callback<JobChangeStatus> callback);

    //i.e. http://localhost/api/institute/Students
    //Add student record and post content in HTTP request BODY
    @POST("/api/jobstatuschanges")
    public void addStudent(@Body JobChangeStatus student,Callback<JobChangeStatus> callback);

}
