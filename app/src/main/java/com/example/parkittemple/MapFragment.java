package com.example.parkittemple;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parkittemple.database.Street;
import com.example.parkittemple.database.TempleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    private static final double NE_LAT = 39.975498;
    private static final double NE_LNG = -75.166811;
    private static final double SW_LAT = 39.988104;
    private static final double SW_LNG = -75.146070;
    private static final double TEMPLE_LAT = 39.981415;
    private static final double TEMPLE_LNG = -75.155308;
    private static final float MIN_ZOOM = 14.8f;
    private static final LatLngBounds TEMPLE_LATLNGBOUND = new LatLngBounds(new LatLng(NE_LAT, NE_LNG), new LatLng(SW_LAT, SW_LNG));
    private static final String TAG = "MainActivity";
    public static final String STREET_NAME = "street_name";
    public static final String DESCRIPTION = "description";
    public static final String FREE = "free";
    private GoogleMap mMap;
    private TempleMap tm;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        FirebaseApp.initializeApp(root.getContext());
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    Log.d(TAG, "onCreate: TempleMap size = " + tm.getStreets().size());
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(googleMap -> {
                        mMap = googleMap;
                        mMap.setOnInfoWindowClickListener(marker -> {
                            Bundle bundle = new Bundle();
                            Street street =(Street) marker.getTag();
                            //Launch details activity on top of map fragment
                            Intent streetDetails = new Intent(getContext(), StreetDetailsActivity.class);
                            bundle.putString(STREET_NAME, street.getStreetName());
                            bundle.putString(DESCRIPTION, street.getRegulation().getDescription());
                            bundle.putBoolean(FREE, street.getRegulation().isFree());
                            streetDetails.putExtras(bundle);
                            startActivity(streetDetails);
                        });
                        mMap.setOnInfoWindowCloseListener(marker -> {
                            //Remove marker from map
                            marker.remove();
                        });


                        //Set map to Temple
                        LatLng temple = new LatLng(TEMPLE_LAT, TEMPLE_LNG);
                        mMap.setLatLngBoundsForCameraTarget(TEMPLE_LATLNGBOUND);
                        mMap.setMinZoomPreference(MIN_ZOOM);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temple, mMap.getMinZoomLevel()));

                        //Add onCameraMoveListener to adjust bounds if the user zooms in
                        mMap.setOnCameraMoveListener(() -> {
                            if (mMap.getCameraPosition().zoom > MIN_ZOOM) {
                                mMap.setLatLngBoundsForCameraTarget(TEMPLE_LATLNGBOUND);
                            }
                        });

                        //TODO
                        /******************Add polylines here
                         * We have to run a loop to add each street as a separate polyline
                         * Set tag as the Street Object (polyline tags can accept arbitrary objects)
                         */
                        //Load streets into a list: List<Street> streets = new ArrayList<>();
                        //Run loop: for each street in list: generate polyline
                        for (int i = 0; i < tm.getStreets().size(); i++) {
                            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                    .clickable(true)
                                    .width(20));
                            polyline.setPoints(geoToLatLng(tm.getStreets().get(i).getGeoPoints()));
                            polyline.setTag(tm.getStreets().get(i));
                            polyline.setColor(setPolylineColor(tm.getStreets().get(i)));
                        }


                        //TODO Change "Object street" to an actual Street object
                        mMap.setOnPolylineClickListener(polylineX -> {
                            Street street = (Street) polylineX.getTag();
                            //Add marker with info window
                            Marker testMarker = mMap.addMarker(new MarkerOptions()
                                    .position(midPoint(polylineX.getPoints()))
                                    .title(street.getStreetName()));
                            testMarker.setTag(street);
                            testMarker.showInfoWindow();
                        });
                    });
                }
                return false;
            }
        });

        Thread t = new Thread() {
            @Override
            public void run() {
                tm = new TempleMap();

                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        t.start();


        return root;
    }


    //Converts a list of firebase geopoints to a list of googlemaps latlng
    private List<LatLng> geoToLatLng(List<GeoPoint> geoPoints) {
        ArrayList<LatLng> list = new ArrayList<>();
        for (int i = 0; i < geoPoints.size(); i++ ){
            list.add(new LatLng(geoPoints.get(i).getLatitude(), geoPoints.get(i).getLongitude()));
        }
        return list;
    }

    //TODO Update parameter to be a Street object
    private int setPolylineColor(Object polyTag) {
        /**
         * polytag = Street object;
         *
         * double prob = polyTag.getProb();
         *
         * switch (prob)
         *      75 - 100: Green
         *      50 -74: Yellow
         *      25 - 49: Orange
         *      0 - 24: Red
         *
         * return color
         */

        return Color.BLACK;
    }

    LatLng midPoint(List<LatLng> geopoints){

        if (geopoints.size() < 3) {

            double lat1 = geopoints.get(0).latitude;
            double lat2 = geopoints.get(1).latitude;
            double lng1 = geopoints.get(0).longitude;
            double lng2 = geopoints.get(1).longitude;


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

        return geopoints.get((int) Math.ceil(geopoints.size()/2));
    }

}
