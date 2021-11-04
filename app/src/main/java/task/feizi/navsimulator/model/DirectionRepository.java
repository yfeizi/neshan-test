package task.feizi.navsimulator.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import org.neshan.mapsdk.model.Marker;
import org.neshan.servicessdk.direction.NeshanDirection;
import org.neshan.servicessdk.direction.model.NeshanDirectionResult;
import org.neshan.servicessdk.direction.model.Route;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import task.feizi.navsimulator.Config;
import task.feizi.navsimulator.RetrofitService;
import task.feizi.navsimulator.api.DirectionApi;
import task.feizi.navsimulator.model.pojo.DirectionApiResponse;

public class DirectionRepository {

    private static final String TAG = "DirectionRepository";
    private static DirectionRepository repository;
    private final MutableLiveData<Resource<Route>> mRoute = new MutableLiveData<>();
    private final DirectionApi directionService;

    public static DirectionRepository getInstance(){
        if (repository == null){
            repository = new DirectionRepository();
        }
        return repository;
    }

    public DirectionRepository() {
        directionService = RetrofitService.getRetrofitInstance().create(DirectionApi.class);
    }
    public MutableLiveData<Resource<Route>> getRouteBySdk(Marker begin, Marker end) {

        new NeshanDirection.Builder(Config.NESHAN_API_KEY, begin.getLatLng(), end.getLatLng())
                .build().call(new Callback<NeshanDirectionResult>() {
            @Override
            public void onResponse(Call<NeshanDirectionResult> call, Response<NeshanDirectionResult> response) {
                if (response.isSuccessful())
                    mRoute.setValue(Resource.success(response.body().getRoutes().get(0)));
                else {
                    mRoute.setValue(Resource.error(response.message(),null));
                }

            }

            @Override
            public void onFailure(Call<NeshanDirectionResult> call, Throwable t) {
                mRoute.setValue(Resource.error(t.getLocalizedMessage(),null));
            }
        });
        return mRoute;
    }

    public MutableLiveData<Resource<Route>> getRouteByRetrofit(@NonNull Marker begin,@NonNull Marker end) {
        String origin = begin.getLatLng().getLatitude()+","+begin.getLatLng().getLongitude();
        String destination = end.getLatLng().getLatitude()+","+end.getLatLng().getLongitude();
        Call<DirectionApiResponse> listOfMovieOut = directionService.getDirection("car",origin, destination);
        listOfMovieOut.enqueue(new Callback<DirectionApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<DirectionApiResponse> call, @NonNull Response<DirectionApiResponse> response) {

                if (response.isSuccessful())
                    mRoute.setValue(Resource.success(response.body().getRoutes().get(0)));
                else {
                    mRoute.setValue(Resource.error(response.message(),null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<DirectionApiResponse> call, @NonNull Throwable t) {
                //TODO handle state for request failure
                mRoute.setValue(Resource.error(t.getLocalizedMessage(),null));
                Log.d(TAG,t.getLocalizedMessage());
            }
        });
        return mRoute;
    }

}
