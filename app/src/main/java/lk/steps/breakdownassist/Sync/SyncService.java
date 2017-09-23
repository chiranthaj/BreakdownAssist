package lk.steps.breakdownassist.Sync;

import org.json.JSONStringer;

import java.util.List;

import lk.steps.breakdownassist.Breakdown;
import lk.steps.breakdownassist.GpsTracker.TrackerObject;
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

public interface SyncService {
    //Retrofit turns the WEB API into a Java interface.

    @FormUrlEncoded
    @POST("/token")
    Call<Token> GetJwt(@Field("username") String username, @Field("password") String password);

    @GET("/Mobile/GetNewBreakdowns/{userId}/{areaId}/{teamId}")
    Call<List<Breakdown>> getNewBreakdowns(@Header("Authorization") String auth,
                                                  @Path("userId") String UserId,
                                                  @Path("areaId") String area,
                                                  @Path("teamId") String team);

    @POST("/Mobile/UpdateBreakdownStatus/")
    Call<SyncObject> UpdateBreakdownStatus(@Header("Authorization") String auth, @Body SyncObject syncObject);



    @POST("/Mobile/UpdateTrackingData/")
    Call<TrackerObject> PushTrackingData(@Header("Authorization") String auth, @Body List<TrackerObject> list);

}
