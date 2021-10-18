package task.feizi.navsimulator.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class UserLocationLiveData extends LiveData<LocationModel> {
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private Context context;
    private final LocationRequest locationRequest = LocationRequest.create();
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location item : locationResult.getLocations()) {
                setLocationData(item);
            }
        }
    };

    public UserLocationLiveData(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        super.onActive();
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                setLocationData(location);
            }
        });
        startLocationUpdate();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void setLocationData(Location location) {
        if (location != null)
            this.setValue(new LocationModel(location.getLongitude(), location.getLatitude()));
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
