package task.feizi.navsimulator;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import task.feizi.navsimulator.model.UserLocationLiveData;

public class LocationViewModel extends AndroidViewModel {

    private UserLocationLiveData locationLiveData;
    public LocationViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new UserLocationLiveData(application);
    }

    public UserLocationLiveData getLocationLiveData() {
        return locationLiveData;
    }
}
