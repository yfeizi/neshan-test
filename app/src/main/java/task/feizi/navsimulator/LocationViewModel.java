package task.feizi.navsimulator;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.neshan.mapsdk.model.Marker;
import org.neshan.servicessdk.direction.model.Route;

import task.feizi.navsimulator.model.DirectionRepository;
import task.feizi.navsimulator.model.Resource;
import task.feizi.navsimulator.model.UserLocationReposiotry;

public class LocationViewModel extends AndroidViewModel {

    private UserLocationReposiotry locationLiveData;
    public LocationViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new UserLocationReposiotry(application);
    }

    public MutableLiveData<Resource<Route>> getRoute(@NonNull Marker start,@NonNull Marker end){
        //return DirectionRepository.getInstance().getRouteBySdk(start,end);
        return DirectionRepository.getInstance().getRouteByRetrofit(start,end);
    }

    public UserLocationReposiotry getLocationLiveData() {
        return locationLiveData;
    }
}
