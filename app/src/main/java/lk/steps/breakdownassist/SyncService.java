package lk.steps.breakdownassist;

import android.telecom.Call;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

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

}
