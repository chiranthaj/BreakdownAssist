package lk.steps.breakdownassist.Sync;

import java.util.List;

import lk.steps.breakdownassist.Breakdown;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by JagathPrasnaga on 24/08/2017.
 */

public interface SyncService {
    //Retrofit turns the WEB API into a Java interface.

    @GET("/GetNewBreakdowns/{UserId}/{area}/{ecsc}/{team}")
    public void getNewBreakdowns(@Path("UserId") String UserId,
                                 @Path("area") String area,
                                 @Path("ecsc") String ecsc,
                                 @Path("team") String team,
                                 Callback<List<Breakdown>> callback);


    @POST("/UpdateBreakdownStatus/")
    public void UpdateBreakdownStatus(@Body SyncObject syncObject,
                                      Callback<SyncObject> callback);

    @FormUrlEncoded
    @POST("/token")
    public void GetJwt(@Field("username") String username, @Field("password") String password, Callback<Token> callback);


}
