package lk.steps.breakdownassist.Sync;

import java.util.List;

import lk.steps.breakdownassist.Breakdown;
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

    @GET("/Mobile/GetNewBreakdowns/{UserId}/{area}/{ecsc}/{team}")
    public Call<List<Breakdown>> getNewBreakdowns(@Header("Authorization") String auth,
                                                  @Path("UserId") String UserId,
                                                     @Path("area") String area,
                                                     @Path("ecsc") String ecsc,
                                                     @Path("team") String team);


    @POST("/Mobile/UpdateBreakdownStatus/")
    public Call<SyncObject> UpdateBreakdownStatus(@Header("Authorization") String auth,@Body SyncObject syncObject);

    @FormUrlEncoded
    @POST("/token")
    public Call<Token> GetJwt(@Field("username") String username, @Field("password") String password);



}
