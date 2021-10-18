package task.feizi.navsimulator.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class GpsUtils {

    private static final String TAG = "GpsUtils";
    private final SettingsClient settingsClient;
    private final LocationSettingsRequest locationSettingsRequest;
    private final LocationManager locationManager;
    private final LocationRequest locationRequest = LocationRequest.create();
    private final Activity context;
    private final int gpsRequest;

    public GpsUtils(Activity context, int gpsRequest) {
        this.gpsRequest = gpsRequest;
        this.context = context;
        settingsClient = LocationServices.getSettingsClient(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    public void turnGpsOn(OnGpsListener gpsListener) {
        if (gpsListener == null) {
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsListener.gpsStatus(true);
        } else {
            settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            gpsListener.gpsStatus(true);
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e instanceof ApiException){
                        if(((ApiException) e).getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(GpsUtils.this.context, gpsRequest);
                            } catch (IntentSender.SendIntentException exception) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                        } else if (((ApiException) e).getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ){
                            String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                            Log.e(TAG, errorMessage);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    public interface OnGpsListener {
        public void gpsStatus(Boolean isGPSEnable);
    }
}
