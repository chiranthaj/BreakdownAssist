package lk.steps.breakdownassistpluss.Sync;

import org.json.JSONStringer;

import java.util.List;

import lk.steps.breakdownassistpluss.Breakdown;
import lk.steps.breakdownassistpluss.GpsTracker.TrackerObject;
import lk.steps.breakdownassistpluss.JobChangeStatus;
import lk.steps.breakdownassistpluss.MaterialList.MaterialObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    Call<String> CreateBreakdown(@Header("Authorization") String auth,
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

    @POST("/Mobile/PostFeedback/")
    Call<String> PostFeedback(@Header("Authorization") String auth,
                                         @Body List<String> data);




    @GET("/Mobile/GetNewBreakdown/{userId}/{breakdownId}")
    Call<List<Breakdown>> GetBreakdowns(@Header("Authorization") String auth,
                                        @Path("userId") String UserId,
                                        @Path("breakdownId") String breakdownId);

    @GET("/Mobile/GetBreakdownGroups/{parentBreakdownId}")
    Call<List<BreakdownGroup>> GetBreakdownGroups(@Header("Authorization") String auth,
                                                  @Path("parentBreakdownId") String ParentBreakdownId);

    @GET("/Mobile/GetBreakdownsStatus/{breakdownId}")
    Call<List<JobChangeStatus>> GetBreakdownsStatus(@Header("Authorization") String auth,
                                              @Path("breakdownId") String BreakdownId);

    /*@GET("/Mobile/GetBreakdownsStatus2/{areaId_ecscId}")
    Call<List<JobChangeStatus>> GetBreakdownsStatus2(@Header("Authorization") String auth,
                                                    @Path("areaId_ecscId") String areaId_ecscId);*/



}
