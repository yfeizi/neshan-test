package task.feizi.navsimulator.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.carto.graphics.Color;
import com.carto.styles.LineStyle;
import com.carto.styles.LineStyleBuilder;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;
import com.carto.utils.Log;

import org.neshan.common.model.LatLng;
import org.neshan.common.utils.PolylineEncoding;
import org.neshan.mapsdk.model.Marker;
import org.neshan.mapsdk.model.Polyline;
import org.neshan.servicessdk.direction.model.Route;

import java.util.ArrayList;

import task.feizi.navsimulator.LocationViewModel;
import task.feizi.navsimulator.R;
import task.feizi.navsimulator.databinding.ActivityMainBinding;
import task.feizi.navsimulator.model.Resource;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LOCATION_REQUEST = 100;
    public static final int CAMERA_DURATION_SECONDS = 1;
    private LocationViewModel locationViewModel;
    private ActivityMainBinding binding;
    private LatLng userOriginLocation;
    private Marker userOriginLocationMarker;
    private Marker destinationLocationMarker;
    private Polyline onMapPolyline;
    private LatLng userCurrnetLocation;
    private Marker userCurrnetLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        initMap();
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
    }

    private void initMap() {
        binding.map.setOnMapLongClickListener(latLng -> {
            if (userCurrnetLocationMarker == null) {
                userOriginLocation = latLng;
                setOriginLoactionMarker(latLng);
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    addDestinationMarker(latLng); // destination marker will be added in clicked location and previous marker will be removed
                    getRoute(); // get route information between origin and destination marker
                });
            }

        });
    }

    private void getRoute() {
        locationViewModel.getRoute(userOriginLocationMarker, destinationLocationMarker).observe(this, route -> {

            switch (route.status) {
                case LOADING:
                    displayLoading();
                    break;
                case ERROR:
                    dismissLoading();
                    Toast.makeText(MainActivity.this, route.message, Toast.LENGTH_LONG).show();
                    break;
                case SUCCESS:
                    dismissLoading();
                    drawRoute(route.data);
                    break;

            }
        });
    }

    private void dismissLoading() {
        //TODO hide loading

    }

    private void displayLoading() {
        //TODO display loading state to user
    }

    private void drawRoute(Route route) {
        // remove previous polyline if exist
        if (onMapPolyline != null) {
            binding.map.removePolyline(onMapPolyline);
        }

        ArrayList<LatLng> routeOverviewPolylinePoints = new ArrayList<>(PolylineEncoding.decode(route.getOverviewPolyline().getEncodedPolyline()));
        onMapPolyline = new Polyline(routeOverviewPolylinePoints, getLineStyle());
        //draw polyline between route points
        binding.map.addPolyline(onMapPolyline);
        // focusing camera on first point of drawn line
        setMapPosition();
    }

    private void setMapPosition() {
        double centerFirstMarkerX = userOriginLocationMarker.getLatLng().getLatitude();
        double centerFirstMarkerY = userOriginLocationMarker.getLatLng().getLongitude();
        binding.map.moveCamera(new LatLng(centerFirstMarkerX, centerFirstMarkerY), CAMERA_DURATION_SECONDS);
        binding.map.setZoom(18, 0.5f);

    }

    private LineStyle getLineStyle() {
        LineStyleBuilder lineStCr = new LineStyleBuilder();
        lineStCr.setColor(new Color((short) 2, (short) 119, (short) 189, (short) 190));
        return lineStCr.buildStyle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if required permissions granted and start update user location
        invokeUserLocation();
    }


    private void invokeUserLocation() {
        if (isPermissionGranted()) {
            if (isGpsEnabled())
                startLocationUpdate();
            else {
                Toast.makeText(this, getString(R.string.msg_gps_is_disabled), Toast.LENGTH_SHORT).show();

            }
        } else if (shouldShowRequestPermission()) {
            Toast.makeText(this,getString(R.string.msg_erquire_location_permission), Toast.LENGTH_SHORT).show();
        } else {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST);
        }
    }


    private void startLocationUpdate() {
        locationViewModel.getLocationLiveData().observe(this, locationModel -> {
            if (locationModel.status == Resource.Status.SUCCESS) {
                // add user origin location for first time
                if (userOriginLocation == null) {
                    userOriginLocation = locationModel.data;
                    setUserLocationOnMap();

                }
                // add and update user current location marker
                userCurrnetLocation = locationModel.data;
                setUserCurrentLocationOnMap();
            }

        });
    }

    private void setUserLocationOnMap() {

        binding.map.moveCamera(userOriginLocation, CAMERA_DURATION_SECONDS);
        binding.map.setZoom(17, 2);
        setOriginLoactionMarker(userOriginLocation);
    }

    private void setUserCurrentLocationOnMap() {
        setCurrentLoactionMarker(userCurrnetLocation);


    }

    private void setOriginLoactionMarker(LatLng loc) {
        if (userOriginLocationMarker != null) {
            binding.map.removeMarker(userOriginLocationMarker);
        }
        userOriginLocationMarker = addMarker(loc, R.drawable.ic_marker_a);
        binding.txtOrigin.setText(userOriginLocationMarker.getDescription());
        locationViewModel.getLocationAddress(loc).observe(this, model -> {
            switch (model.status){
                case SUCCESS:
                    binding.txtOrigin.setText(model.data != null ? model.data.getAddress() : "");
                    break;
                case ERROR:
                    Toast.makeText(MainActivity.this, model.message, Toast.LENGTH_LONG).show();
                    break;
            }


        });
    }

    private void setCurrentLoactionMarker(LatLng loc) {
        if (userCurrnetLocationMarker != null) {
            binding.map.removeMarker(userCurrnetLocationMarker);
        }
        userCurrnetLocationMarker = addMarker(loc, R.drawable.ic_marker);


    }

    private void addDestinationMarker(LatLng location) {
        if (destinationLocationMarker != null) {
            binding.map.removeMarker(destinationLocationMarker);
        }
        destinationLocationMarker = addMarker(location, R.drawable.ic_marker_b);
        locationViewModel.getLocationAddress(location).observe(this, model -> {
            switch (model.status){
                case SUCCESS:
                    binding.txtDestination.setText(model.data != null ? model.data.getAddress() : "");
                    break;
                case ERROR:
                    Toast.makeText(MainActivity.this, model.message, Toast.LENGTH_LONG).show();
                    break;
            }


        });
    }

    private Marker addMarker(LatLng location, int resId) {
        // Creating marker
        MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();
        markerStyleBuilder.setSize(20f);
        markerStyleBuilder.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), resId)));
        MarkerStyle markStyle = markerStyleBuilder.buildStyle();
        Marker marker = new Marker(location, markStyle);

        // Adding marker to map!
        binding.map.addMarker(marker);
        return marker;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST) {
            invokeUserLocation();
        }
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private boolean shouldShowRequestPermission() {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        );
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnMyLocation) {
            if (userCurrnetLocation != null)
                binding.map.moveCamera(userCurrnetLocation, CAMERA_DURATION_SECONDS);
            else if (userOriginLocation != null) {
                binding.map.moveCamera(userCurrnetLocation, CAMERA_DURATION_SECONDS);
            }
        }
    }

    private boolean isGpsEnabled(){
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {
            Log.debug(ex.getLocalizedMessage());
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            Log.debug(ex.getLocalizedMessage());
        }

        return gps_enabled && network_enabled;
    }
}