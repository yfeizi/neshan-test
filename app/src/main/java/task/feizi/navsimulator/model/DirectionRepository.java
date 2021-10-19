package task.feizi.navsimulator.model;

import androidx.lifecycle.MutableLiveData;

import org.neshan.common.utils.PolylineEncoding;
import org.neshan.mapsdk.model.Marker;
import org.neshan.mapsdk.model.Polyline;
import org.neshan.servicessdk.direction.NeshanDirection;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.NeshanDirectionResult;
import org.neshan.servicessdk.direction.model.Route;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import task.feizi.navsimulator.Config;

public class DirectionRepository {

    private static DirectionRepository repository;
    private final MutableLiveData<Route> mRoute = new MutableLiveData<>();

    public static DirectionRepository getInstance(){
        if (repository == null){
            repository = new DirectionRepository();
        }
        return repository;
    }

    public MutableLiveData<Route> getListOfMoviesOutputs(Marker begin, Marker end) {

        new NeshanDirection.Builder(Config.NESHAN_API_KEY, begin.getLatLng(), end.getLatLng())
                .build().call(new Callback<NeshanDirectionResult>() {
            @Override
            public void onResponse(Call<NeshanDirectionResult> call, Response<NeshanDirectionResult> response) {

                // two type of routing
                Route route = response.body().getRoutes().get(0);
                mRoute.setValue(route);
            }

            @Override
            public void onFailure(Call<NeshanDirectionResult> call, Throwable t) {
                mRoute.postValue(null);
            }
        });
        return mRoute;
    }
}
