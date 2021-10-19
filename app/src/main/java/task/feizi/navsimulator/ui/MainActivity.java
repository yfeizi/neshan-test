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

import java.util.ArrayList;

import task.feizi.navsimulator.LocationViewModel;
import task.feizi.navsimulator.R;
import task.feizi.navsimulator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 100;
    private LocationViewModel locationViewModel;
    private ActivityMainBinding binding;
    private LatLng userCurrentLocation;
    private Marker userCurrentLocationMarker;
    private Marker destinationLocationMarker;
    private ArrayList<LatLng> routeOverviewPolylinePoints;
    private ArrayList<Object> decodedStepByStepPath;
    private Polyline onMapPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // when long clicked on map, a marker is added in clicked location
        binding.map.setOnMapLongClickListener(new MapView.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                addDestinationMarker(latLng);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        drawRoute();
                    }
                });

            }
        });
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
    }

    private void drawRoute() {
        locationViewModel.getRoute(userCurrentLocationMarker,destinationLocationMarker).observe(this, route -> {
            if (onMapPolyline!=null) {
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
            mapSetPosition(false);
        });
    }
    private void mapSetPosition(boolean overview) {
        double centerFirstMarkerX = userCurrentLocationMarker.getLatLng().getLatitude();
        double centerFirstMarkerY = userCurrentLocationMarker.getLatLng().getLongitude();
        if (overview) {
            double centerFocalPositionX = (centerFirstMarkerX + destinationLocationMarker.getLatLng().getLatitude()) / 2;
            double centerFocalPositionY = (centerFirstMarkerY + destinationLocationMarker.getLatLng().getLongitude()) / 2;
            binding.map.moveCamera(new LatLng(centerFocalPositionX, centerFocalPositionY), 0.5f);
            binding.map.setZoom(14, 0.5f);
        } else {
            binding.map.moveCamera(new LatLng(centerFirstMarkerX, centerFirstMarkerY), 0.5f);
            binding.map.setZoom(18, 0.5f);
        }

    }
    private LineStyle getLineStyle() {
        LineStyleBuilder lineStCr = new LineStyleBuilder();
        lineStCr.setColor(new Color((short) 2, (short) 119, (short) 189, (short) 190));
        return lineStCr.buildStyle();
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
        setCurrentLoactionMarker(userCurrentLocation);
    }

    private void setCurrentLoactionMarker(LatLng  loc) {
        if (userCurrentLocationMarker !=null){
            binding.map.removeMarker(userCurrentLocationMarker);
        }
        userCurrentLocationMarker = addMarker(loc,  R.drawable.ic_marker_a);
    }
    private void addDestinationMarker(LatLng loc) {
        if (destinationLocationMarker !=null){
            binding.map.removeMarker(destinationLocationMarker);
        }
        destinationLocationMarker = addMarker(loc,  R.drawable.ic_marker_b);
    }

    private Marker addMarker(LatLng loc, int resId) {
        // Creating marker
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(20f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), resId)));
        MarkerStyle markSt = markStCr.buildStyle();
        Marker marker = new Marker(loc, markSt);

        // Adding marker to map!
        binding.map.addMarker(marker);
        return marker;
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