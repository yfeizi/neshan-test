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
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.carto.graphics.Color;
import com.carto.styles.LineStyle;
import com.carto.styles.LineStyleBuilder;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;

import org.neshan.common.model.LatLng;
import org.neshan.common.utils.PolylineEncoding;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.mapsdk.model.Polyline;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.Route;

import java.util.ArrayList;

import task.feizi.navsimulator.LocationViewModel;
import task.feizi.navsimulator.R;
import task.feizi.navsimulator.databinding.ActivityMainBinding;
import task.feizi.navsimulator.model.Resource;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 100;
    private LocationViewModel locationViewModel;
    private ActivityMainBinding binding;
    private LatLng userOriginLocation;
    private Marker userOriginLocationMarker;
    private Marker destinationLocationMarker;
    private ArrayList<LatLng> routeOverviewPolylinePoints;
    private ArrayList<Object> decodedStepByStepPath;
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
        binding.map.setOnMapLongClickListener(new MapView.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (userCurrnetLocationMarker == null) {
                    userOriginLocation = latLng;
                    setOriginLoactionMarker(latLng);
                } else {
                    addDestinationMarker(latLng); // destination marker will be added in clicked location and previous marker will be removed
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            getRoute(); // get route information between origin and destination marker
                        }
                    });
                }

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

        routeOverviewPolylinePoints = new ArrayList<>(PolylineEncoding.decode(route.getOverviewPolyline().getEncodedPolyline()));
        decodedStepByStepPath = new ArrayList<>();
        // decoding each segment of steps and putting to an array
        for (DirectionStep step : route.getLegs().get(0).getDirectionSteps()) {
            decodedStepByStepPath.addAll(PolylineEncoding.decode(step.getEncodedPolyline()));
        }

        onMapPolyline = new Polyline(routeOverviewPolylinePoints, getLineStyle());
        //draw polyline between route points
        binding.map.addPolyline(onMapPolyline);
        // focusing camera on first point of drawn line
        setMapPosition();
    }

    private void setMapPosition() {
        double centerFirstMarkerX = userOriginLocationMarker.getLatLng().getLatitude();
        double centerFirstMarkerY = userOriginLocationMarker.getLatLng().getLongitude();
        binding.map.moveCamera(new LatLng(centerFirstMarkerX, centerFirstMarkerY), 0.5f);
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
            startLocationUpdate();
        } else if (shouldShowRequestPermission()) {
            Toast.makeText(this, "App requires location permission", Toast.LENGTH_SHORT).show();
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

        binding.map.moveCamera(userOriginLocation, 0);
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
}