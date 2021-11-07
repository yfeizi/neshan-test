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

import org.neshan.common.model.LatLng;

public class UserLocationReposiotry extends LiveData<Resource<LatLng>> {
    public static final int LOCATION_UPDATE_INTERVAL = 3000;
    public static final int LOCATION_UPDATE_FASTEST_INTERVAL = 3000;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final Context context;
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

    public UserLocationReposiotry(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
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
            this.setValue(Resource.success(new LatLng(location.getLatitude(), location.getLongitude())));
        else
            this.setValue(Resource.error("اطلاعات مکانی موچود نیست", null));
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
