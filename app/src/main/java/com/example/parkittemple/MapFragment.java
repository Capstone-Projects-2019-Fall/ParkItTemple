package com.example.parkittemple;


import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.parkittemple.database.Street;
import com.example.parkittemple.database.TempleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    static final int MY_PERMISSIONS_LOCATION = 1;
    private onMapInteraction parent;
    private static final double NE_LAT = 39.975498;
    private static final double NE_LNG = -75.166811;
    private static final double SW_LAT = 39.988104;
    private static final double SW_LNG = -75.146070;
    private static final double TEMPLE_LAT = 39.981415;
    private static final double TEMPLE_LNG = -75.155308;
    private static final double TEMPLE_15_OXFORD_LAT = 39.977496;
    private static final double TEMPLE_15_OXFORD_LNG = -75.159847;
    private static final double TEMPLE_11_DIAMOND_LAT = 39.983987;
    private static final double TEMPLE_11_DIAMOND_LNG = -75.151277;
    private LatLng templeMapCenter = new LatLng(39.980548, -75.155258);
    private GroundOverlayOptions templeMapOverlay;
    private boolean cleared, showPolylines;
    private float currZoom;
    private LatLng currLatLng;
    private static final float MIN_ZOOM = 15.0f;
    private static final LatLngBounds TEMPLE_LATLNGBOUND = new LatLngBounds(new LatLng(NE_LAT, NE_LNG), new LatLng(SW_LAT, SW_LNG));
    private static final String TAG = "MainActivity";
    static final String STREET_NAME = "street_name";
    static final String DESCRIPTION = "description";
    static final String FREE = "free";
    private static final String TEMPLE_MAP = "temple_map";
    private GoogleMap mMap;
    private TempleMap tm;
    private Handler handler;
    private boolean outOfBoundsDisplayed;
    private PopupWindow popupWindow;


    public MapFragment() {
        // Required empty public constructor
    }

    /*public static MapFragment newInstance(TempleMap templeMap) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable(TEMPLE_MAP, templeMap);
        fragment.setArguments(args);
        return fragment;
    }

     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread t = new Thread() {
            @Override
            public void run() {
                tm = new TempleMap();
                handler.sendEmptyMessage(1);
            }
        };
        t.start();

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATION);
        }



    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;


            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Get user location
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setOnMyLocationButtonClickListener(() -> {
                    Toast.makeText(getContext(), "Roger that! Coming to you Captain!", Toast.LENGTH_SHORT).show();
                    return false;
                });

                /*mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                    @Override
                    public void onMyLocationClick(@NonNull Location location) {
                        Toast.makeText(getContext(), "Current location:\n" + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();

                    }
                });

                 */
            }


            //Set map to Temple
            LatLng temple = new LatLng(TEMPLE_LAT, TEMPLE_LNG);

            if (currZoom > 0f) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, currZoom));
            }
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temple, MIN_ZOOM));

            //Create ground overlay
            templeMapOverlay = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.temple_logo))
                    .bearing(10f)
                    .position(templeMapCenter, 905f);

            //Add onCameraMoveListener to adjust bounds if the user zooms in
            mMap.setOnCameraMoveListener(() -> {
                currZoom = mMap.getCameraPosition().zoom;
                currLatLng = mMap.getCameraPosition().target;
                //user is zoomed in
                if (mMap.getCameraPosition().zoom > MIN_ZOOM) {
                    if (!showPolylines){
                        try {
                            mMap.clear();
                        } catch (IllegalArgumentException e){
                            Log.d(TAG, "onCreateView: " + e);
                            mMap.clear();
                        }
                        handler.sendEmptyMessage(1);
                        showPolylines = true;
                        cleared = false;
                    }
                } else if (mMap.getCameraPosition().zoom < MIN_ZOOM){
                    if (!cleared) {
                        try {
                            mMap.clear();
                        } catch (IllegalArgumentException e){
                            Log.d(TAG, "onCreateView: " + e);
                            mMap.clear();
                        }
                        mMap.addGroundOverlay(templeMapOverlay);
                        cleared = true;
                        showPolylines = false;
                    }
                }

                /*
                //user is not at Temple
                Log.d(TAG, "onCreateView: inBounds = " + inBounds(mMap.getCameraPosition().target));
                Log.d(TAG, "onCreateView: outOfBoundsDisplayed = " + outOfBoundsDisplayed);
                outOfBoundsDisplayed = popupWindow != null && popupWindow.isShowing();
                if (!inBounds(mMap.getCameraPosition().target)){
                    if (!outOfBoundsDisplayed) {
                        showOutOfBoundsPopUp(root);
                    }
                }
                if (inBounds(mMap.getCameraPosition().target)){
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                }

                 */

            });
            mMap.setOnInfoWindowClickListener(marker -> {
                if (marker.getTag() instanceof Street) {
                    parent.onStreetClick((Street) marker.getTag());
                }
                else
                    Toast.makeText(getContext(),"Lot clicked", Toast.LENGTH_SHORT).show();
            });
            //Remove marker from map
            mMap.setOnInfoWindowCloseListener(Marker::remove);
            mMap.setOnPolylineClickListener(polylineX -> {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(midPoint(polylineX.getPoints())));
                Street street = (Street) polylineX.getTag();
                //Add marker with info window
                assert street != null;
                Marker testMarker = mMap.addMarker(new MarkerOptions()
                        .position(midPoint(polylineX.getPoints()))
                        .title(street.getStreetName()));
                testMarker.setTag(street);
                testMarker.showInfoWindow();

            });
            mMap.setOnPolygonClickListener(polygonX -> {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(centerPoint(polygonX.getPoints())));
                //TODO ParkingLot lot = (ParkingLot) polygonX.getTag();
                //Add marker with info window
                /*

                assert lot != null;
                Marker testMarker = mMap.addMarker(new MarkerOptions()
                        .position(centerPoint(polygonX.getPoints()))
                        .title(lot.getLotName()));
                testMarker.setTag(lot);
                testMarker.showInfoWindow();

                 */
                Marker testMarker = mMap.addMarker(new MarkerOptions()
                        .position(centerPoint(polygonX.getPoints()))
                        .title("Lot 7"));
                testMarker.setTag("Lot 7");
                testMarker.showInfoWindow();

            });

            handler = new Handler(msg -> {
                if (msg.what== 1) {
                    Log.d(TAG, "onCreate: TempleMap size = " + tm.getStreets().size());
                    //TODO add parking lot schema to db
                    /* ***************Add polylines and polygons here
                    We have to run a loop to add each street as a separate polyline
                    Set tag as the Street Object (polyline tags can accept arbitrary objects)
                    Load streets into a list: List<Street> streets = new ArrayList<>();
                    Run loop: for each street in list: generate polyline*/
                    for (int i = 0; i < tm.getStreets().size(); i++) {
                        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                                .width(20));
                        polyline.setPoints(geoToLatLng(tm.getStreets().get(i).getGeoPoints()));
                        polyline.setTag(tm.getStreets().get(i));
                        polyline.setColor(setPolylineColor(tm.getStreets().get(i)));
                    }


                    //TODO this is a test polygon
                    // Instantiates a new Polygon object and adds points to define a rectangle
                    // MAKE SURE TO ADD LATLNG IN THIS ORDER: top right -> top left -> bottom left -> bottom right
                    PolygonOptions SERCParkingLot = new PolygonOptions()
                            .add(new LatLng(39.982490, -75.151671),     //top right
                                    new LatLng(39.982564, -75.152224),  //top left
                                    new LatLng(39.981800, -75.152412),  //bottom left
                                    new LatLng(39.981714, -75.151841))  //bottom right
                            .fillColor(getResources().getColor(R.color.blue_semi_trans))
                            .clickable(true)
                            .strokeWidth(1f);

                    // Get back the mutable Polygon
                    Polygon polygon = mMap.addPolygon(SERCParkingLot);

                    return true;
                }
                return false;
            });

        });


        return root;
    }

    private boolean inBounds(LatLng target) {
        boolean eastbound = target.longitude < MapFragment.TEMPLE_LATLNGBOUND.northeast.longitude;
        boolean westbound = target.longitude > MapFragment.TEMPLE_LATLNGBOUND.southwest.longitude;
        boolean inLng;

        if (MapFragment.TEMPLE_LATLNGBOUND.northeast.longitude < MapFragment.TEMPLE_LATLNGBOUND.southwest.longitude){
            inLng = eastbound || westbound;
        } else {
            inLng = eastbound && westbound;
        }

        boolean inLat = target.latitude > MapFragment.TEMPLE_LATLNGBOUND.southwest.latitude && target.latitude < MapFragment.TEMPLE_LATLNGBOUND.northeast.latitude;

        return inLat && inLng;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onMapInteraction) {
            parent = (onMapInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        mMap.clear();
        super.onDestroy();
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

    private void showOutOfBoundsPopUp(View anchorView) {


        View view = getLayoutInflater().inflate(R.layout.popup_out_of_bounds, null);

        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set an elevation value for popup window
        // Call requires API level 21
        popupWindow.setElevation(6.0f);


        view.setOnClickListener(v -> {
            Toast.makeText(view.getContext(), "Got it! Going back to campus!", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(false);
        popupWindow.setOnDismissListener(() -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(TEMPLE_LAT,TEMPLE_LNG)));
            outOfBoundsDisplayed = false;
        });


        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 8);
        outOfBoundsDisplayed = popupWindow != null && popupWindow.isShowing();
    }

    private LatLng midPoint(List<LatLng> geopoints){

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

    private LatLng centerPoint(List<LatLng> geopoints){

        //Find midpoint of diagonal
        double lat1 = geopoints.get(0).latitude;
        double lat2 = geopoints.get(2).latitude;
        double lng1 = geopoints.get(0).longitude;
        double lng2 = geopoints.get(2).longitude;


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

    public interface onMapInteraction{
        void onStreetClick(Street street);
        void onLocationEnabled();
    }



}
