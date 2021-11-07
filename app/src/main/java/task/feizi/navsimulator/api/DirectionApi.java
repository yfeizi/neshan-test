package task.feizi.navsimulator.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import task.feizi.navsimulator.Config;
import task.feizi.navsimulator.model.pojo.DirectionApiResponse;
import task.feizi.navsimulator.model.pojo.ReverseApiResponse;

public interface DirectionApi {
    @Headers("Api-Key:" + Config.NESHAN_API_KEY)
    @GET("/v3/direction")
    Call<DirectionApiResponse> getDirection(@Query("type") String type, @Query("origin") String origin, @Query("destination") String destination);

    @Headers("Api-Key:" + Config.NESHAN_API_KEY)
    @GET("/v1/reverse")
    Call<ReverseApiResponse> getLocationAddress(@Query("lat") double lat, @Query("lng") double lng);
}
