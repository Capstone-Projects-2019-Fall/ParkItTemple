package com.example.parkittemple;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowCloseListener {

    private static final double NE_LAT = 39.975498;
    private static final double NE_LNG = -75.166811;
    private static final double SW_LAT = 39.988104;
    private static final double SW_LNG = -75.146070;
    private static final double TEMPLE_LAT = 39.981415;
    private static final double TEMPLE_LNG = -75.155308;
    private static final float MIN_ZOOM = 14.8f;
    private static final LatLngBounds TEMPLE_LATLNGBOUND = new LatLngBounds(new LatLng(NE_LAT, NE_LNG), new LatLng(SW_LAT, SW_LNG));
    private static final String TEST_POLY_TAG = "test";
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
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);

        //Set map to Temple
        LatLng temple = new LatLng(TEMPLE_LAT, TEMPLE_LNG);
        mMap.setLatLngBoundsForCameraTarget(TEMPLE_LATLNGBOUND);
        mMap.setMinZoomPreference(MIN_ZOOM);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temple, mMap.getMinZoomLevel()));

        //Add onCameraMoveListener to adjust bounds if the user zooms in
        mMap.setOnCameraMoveListener(() -> {
            if (mMap.getCameraPosition().zoom > MIN_ZOOM){
                mMap.setLatLngBoundsForCameraTarget(TEMPLE_LATLNGBOUND);
            }
        });

        /******************Add polylines here
         * We have to run a loop to add each street as a separate polyline
         * Set tag as the Street Object (polyline tags can accept arbitrary objects)
         * Snippet = basic info about street
         */
        //Load streets into a list: List<Street> streets = new ArrayList<>();
        //Run loop: for each street in list:  run code below
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(Color.RED)
                .width(20)
                .add(new LatLng(39.980231, -75.157521),
                        new LatLng(39.983189, -75.156869)));
        polyline.setTag(TEST_POLY_TAG);

        mMap.setOnPolylineClickListener(polylineX -> {
                //Add marker with info window
                Marker testMarker = mMap.addMarker(new MarkerOptions()
                    .position(midPoint(polylineX.getPoints().get(0).latitude,
                            polylineX.getPoints().get(0).longitude,
                            polylineX.getPoints().get(1).latitude,
                            polylineX.getPoints().get(1).longitude))
                    .title(TEST_POLY_TAG)
                    .snippet(TEST_POLY_TAG));
                testMarker.setTag(TEST_POLY_TAG);
                testMarker.showInfoWindow();
        });


    }

    LatLng midPoint(double lat1, double lng1 ,double lat2, double lng2){

        double dLng = Math.toRadians(lng2 - lng1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lng1 = Math.toRadians(lng1);

        double Bx = Math.cos(lat2) * Math.cos(dLng);
        double By = Math.cos(lat2) * Math.sin(dLng);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lng3 = lng1 + Math.atan2(By, Math.cos(lat1) + Bx);

        //return out in degrees
        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lng3));
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.getTag().equals(TEST_POLY_TAG)){
            //Launch details activity on top of map fragment
            Intent streetDetails = new Intent(MapsActivity.this, StreetDetailsActivity.class);
            streetDetails.putExtra(TEST_POLY_TAG, marker.getTag().toString());
            startActivity(streetDetails);
        } else {

        }
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        if (marker.getTag().equals(TEST_POLY_TAG)){
            //Remove marker from map
            marker.remove();
        } else {

        }
    }
}
