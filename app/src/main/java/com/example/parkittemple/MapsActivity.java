package com.example.parkittemple;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final double NE_LAT = 39.975498;
    private static final double NE_LNG = -75.166811;
    private static final double SW_LAT = 39.988104;
    private static final double SW_LNG = -75.146070;
    private static final double TEMPLE_LAT = 39.981415;
    private static final double TEMPLE_LNG = -75.155308;
    private static final float MIN_ZOOM = 14.8f;
    private static final LatLngBounds TEMPLE_LATLNGBOUND = new LatLngBounds(new LatLng(NE_LAT, NE_LNG), new LatLng(SW_LAT, SW_LNG));
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker to Temple and move the camera
        LatLng temple = new LatLng(TEMPLE_LAT, TEMPLE_LNG);
        mMap.addMarker(new MarkerOptions().position(temple).title("Temple University"));
        mMap.setLatLngBoundsForCameraTarget(TEMPLE_LATLNGBOUND);
        mMap.setMinZoomPreference(MIN_ZOOM);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temple, mMap.getMinZoomLevel()));

        //Add onCameraMoveListener to adjust bounds if the user zooms in
        mMap.setOnCameraMoveListener(() -> {
            if (mMap.getCameraPosition().zoom > MIN_ZOOM){
                mMap.setLatLngBoundsForCameraTarget(TEMPLE_LATLNGBOUND);
            }
        });

        //Add polylines
        List<LatLng> testPolyPoints = new ArrayList<>();
        testPolyPoints.add(new LatLng(39.980231, -75.157521));
        testPolyPoints.add(new LatLng(39.983189, -75.156869));
        testPolyPoints.add(new LatLng(39.982933, -75.154721));
        testPolyPoints.add(new LatLng(39.979940, -75.155371));
        testPolyPoints.add(new LatLng(39.980231, -75.157521));

        Polyline polylines = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(Color.RED)
                .width(20));

        polylines.setPoints(testPolyPoints);
    }


}
