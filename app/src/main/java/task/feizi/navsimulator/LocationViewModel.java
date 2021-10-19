package task.feizi.navsimulator;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.neshan.mapsdk.model.Marker;
import org.neshan.servicessdk.direction.model.Route;

import task.feizi.navsimulator.model.DirectionRepository;
import task.feizi.navsimulator.model.UserLocationLiveData;

public class LocationViewModel extends AndroidViewModel {

    private UserLocationLiveData locationLiveData;
    public LocationViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new UserLocationLiveData(application);
    }

    public MutableLiveData<Route> getRoute(Marker start, Marker end){
        return DirectionRepository.getInstance().getListOfMoviesOutputs(start,end);
    }

    public UserLocationLiveData getLocationLiveData() {
        return locationLiveData;
    }
}
