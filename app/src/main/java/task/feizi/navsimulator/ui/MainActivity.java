package task.feizi.navsimulator.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.model.Marker;

import task.feizi.navsimulator.LocationViewModel;
import task.feizi.navsimulator.R;
import task.feizi.navsimulator.databinding.ActivityMainBinding;
import task.feizi.navsimulator.model.LocationModel;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 100;
    private LocationViewModel locationViewModel;
    private ActivityMainBinding binding;
    private LatLng userCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        invokeUserLocation();
    }



    private void invokeUserLocation(){
        if (isPermissionGranted()) {
            startLocationUpdate();
        }else if (shouldShowRequestPermission()){
            Toast.makeText(this, "App requires location permission", Toast.LENGTH_SHORT).show();
        } else {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this,permissions,LOCATION_REQUEST);
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST){
            invokeUserLocation();
        }
    }

    private void startLocationUpdate() {
        locationViewModel.getLocationLiveData().observe(this, locationModel -> {
            if(userCurrentLocation == null){
                userCurrentLocation = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
                setUserLocationOnMap();
            }
        });
    }
    private void setUserLocationOnMap(){
        binding.map.moveCamera(userCurrentLocation,0 );
        binding.map.setZoom(17,2);
        setMarker(userCurrentLocation);
    }

    private void setMarker(LatLng  loc) {
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_a)));
        MarkerStyle markSt = markStCr.buildStyle();

        // Creating user marker
        Marker marker = new Marker(loc, markSt);

        // Adding user marker to map!
        binding.map.addMarker(marker);
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private boolean shouldShowRequestPermission(){
        return ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        );
    }
}