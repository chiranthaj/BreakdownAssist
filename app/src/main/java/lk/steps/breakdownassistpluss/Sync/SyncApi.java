package lk.steps.breakdownassistpluss.Sync;

import java.util.List;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.GpsTracker.TrackerObject;
import lk.steps.breakdownassistpluss.Models.JobChangeStatus;
import lk.steps.breakdownassistpluss.Models.Team;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by JagathPrasnaga on 24/08/2017.
 */

public interface SyncApi {
    //Retrofit turns the WEB API into a Java interface.

    @FormUrlEncoded
    @POST("/token")
    Call<Token> GetJwt(@Field("username") String username,
                       @Field("password") String password);


    @POST("/Mobile/UpdateBreakdownStatus/")
    Call<SyncObject> UpdateBreakdownStatus(@Header("Authorization") String auth,
                                           @Body SyncObject syncObject);

    @POST("/Mobile/CreateBreakdown/")
    Call<List<Breakdown>> CreateBreakdown(@Header("Authorization") String auth,
                                           @Body Breakdown breakdown);

    @POST("/Mobile/UpdateTrackingData/")
    Call<TrackerObject> PushTrackingData(@Header("Authorization") String auth,
                                         @Body List<TrackerObject> list);

    @POST("/Mobile/InsertMaterials/")
    Call<SyncMaterialObject> PushMaterials(@Header("Authorization") String auth,
                                           @Body List<SyncMaterialObject> list);

    @POST("/Mobile/PostGroups/")
    Call<BreakdownGroup> PostGroups(@Header("Authorization") String auth,
                                    @Body List<BreakdownGroup> list);

    @POST("/Mobile/PostFeedbackNew/")
    Call<FeedbackObject> PostFeedback(@Header("Authorization") String auth,
                                         @Body List<FeedbackObject> data);


    @GET("/Mobile/GetNewBreakdown/{userId}/{breakdownId}")
    Call<List<Breakdown>> GetBreakdowns(@Header("Authorization") String auth,
                                        @Path("userId") String UserId,
                                        @Path("breakdownId") String breakdownId);

    @GET("/Mobile/GetNotCompletedBreakdowns/{areaId}/{teamId}")
    Call<List<Breakdown>> GetNotCompletedBreakdowns(@Header("Authorization") String auth,
                                        @Path("areaId") String AreaId,
                                        @Path("teamId") String TeamId);

    @GET("/Mobile/GetBreakdownGroups/{parentBreakdownId}")
    Call<List<BreakdownGroup>> GetBreakdownGroups(@Header("Authorization") String auth,
                                                  @Path("parentBreakdownId") String ParentBreakdownId);
    @GET("/Mobile/GetTeams/{AreaId}")
    Call<List<Team>> GetTeams(@Header("Authorization") String auth,
                              @Path("AreaId") String AreaId);

    @GET("/Mobile/GetBreakdownsStatus/{breakdownId}")
    Call<List<JobChangeStatus>> GetBreakdownsStatus(@Header("Authorization") String auth,
                                              @Path("breakdownId") String BreakdownId);

    /*@GET("/Mobile/GetBreakdownsStatus2/{areaId_ecscId}")
    Call<List<JobChangeStatus>> GetBreakdownsStatus2(@Header("Authorization") String auth,
                                                    @Path("areaId_ecscId") String areaId_ecscId);*/


    @Streaming
    @GET("/Mobile/GetApk/{id}")
    Call<ResponseBody> GetApk(@Header("Authorization") String auth,
                                        @Path("id") int id);

}
