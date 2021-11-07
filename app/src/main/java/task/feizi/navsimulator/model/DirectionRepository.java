package task.feizi.navsimulator.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.model.Marker;
import org.neshan.servicessdk.direction.NeshanDirection;
import org.neshan.servicessdk.direction.model.NeshanDirectionResult;
import org.neshan.servicessdk.direction.model.Route;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import task.feizi.navsimulator.Config;
import task.feizi.navsimulator.RetrofitService;
import task.feizi.navsimulator.api.DirectionApi;
import task.feizi.navsimulator.model.pojo.DirectionApiResponse;
import task.feizi.navsimulator.model.pojo.ReverseApiResponse;

public class DirectionRepository {

    private static final String TAG = "DirectionRepository";
    private static DirectionRepository repository;
    private final DirectionApi directionService;

    public static DirectionRepository getInstance(Context context){
        if (repository == null){
            repository = new DirectionRepository(context);
        }
        return repository;
    }

    public DirectionRepository(Context context) {
        directionService = RetrofitService.getRetrofitInstance(context).create(DirectionApi.class);
    }
    public MutableLiveData<Resource<Route>> getRouteBySdk(Marker begin, Marker end) {

        MutableLiveData<Resource<Route>> route = new MutableLiveData<>();
        new NeshanDirection.Builder(Config.NESHAN_API_KEY, begin.getLatLng(), end.getLatLng())
                .build().call(new Callback<NeshanDirectionResult>() {
            @Override
            public void onResponse(Call<NeshanDirectionResult> call, Response<NeshanDirectionResult> response) {
                if (response.isSuccessful())
                    route.setValue(Resource.success(response.body().getRoutes().get(0)));
                else {
                    route.setValue(Resource.error(response.message(),null));
                }

            }

            @Override
            public void onFailure(Call<NeshanDirectionResult> call, Throwable t) {
                route.setValue(Resource.error(t.getLocalizedMessage(),null));
            }
        });
        return route;
    }

    public MutableLiveData<Resource<Route>> getRouteByRetrofit(@NonNull Marker begin,@NonNull Marker end) {
        String origin = begin.getLatLng().getLatitude()+","+begin.getLatLng().getLongitude();
        String destination = end.getLatLng().getLatitude()+","+end.getLatLng().getLongitude();
        Call<DirectionApiResponse> listOfMovieOut = directionService.getDirection("car",origin, destination);
        MutableLiveData<Resource<Route>> route = new MutableLiveData<>();
        listOfMovieOut.enqueue(new Callback<DirectionApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<DirectionApiResponse> call, @NonNull Response<DirectionApiResponse> response) {

                if (response.isSuccessful())
                    if (response.body() != null) {
                        route.setValue(Resource.success(response.body().getRoutes().get(0)));
                    }
                else {
                    route.setValue(Resource.error(response.message(),null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<DirectionApiResponse> call, @NonNull Throwable t) {
                //TODO handle state for request failure
                route.setValue(Resource.error(t.getLocalizedMessage(),null));
                Log.d(TAG,t.getLocalizedMessage());
            }
        });
        return route;
    }


    public MutableLiveData<Resource<ReverseApiResponse>> getLocationAddress(@NonNull LatLng location) {
        Call<ReverseApiResponse> listOfMovieOut = directionService.getLocationAddress(location.getLatitude(), location.getLongitude());
        MutableLiveData<Resource<ReverseApiResponse>> reverseAddress = new MutableLiveData<>();
        listOfMovieOut.enqueue(new Callback<ReverseApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReverseApiResponse> call, @NonNull Response<ReverseApiResponse> response) {

                if (response.isSuccessful())
                    reverseAddress.setValue(Resource.success(response.body()));
                else {
                    reverseAddress.setValue(Resource.error(response.message(),null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ReverseApiResponse> call, @NonNull Throwable t) {
                //TODO handle state for request failure
                reverseAddress.setValue(Resource.error(t.getLocalizedMessage(),null));
                Log.d(TAG,t.getLocalizedMessage());
            }
        });
        return reverseAddress;
    }

}
