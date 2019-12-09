package com.example.parkittemple;


import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.parkittemple.database.Calculation;
import com.example.parkittemple.database.ParkingLot;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    static final int MY_PERMISSIONS_LOCATION = 1;
    private static final String PID1 = "pi-1";
    private static final String PID2 = "pi-2";
    private onMapInteraction parent;
    private static final String DISCLAIMER = "disclaimer";
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
    private boolean cleared, showPolylines, showDisclaimer;
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
    private static TempleMap tm;
    private static ArrayList<ParkingLot> lots;
    private Handler handler;
    private RelativeLayout back_dim_layout;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        showDisclaimer = prefs.getBoolean(DISCLAIMER,false);

        Thread t = new Thread() {
            @Override
            public void run() {
                tm = new TempleMap();
                lots = new ArrayList<>();
                String[] lot_names = getResources().getStringArray(R.array.parking_lots);
                String[] lot_days = getResources().getStringArray(R.array.parking_lots_days);
                String[] lot_hours = getResources().getStringArray(R.array.parking_lots_hours);
                String[] lot_costs = getResources().getStringArray(R.array.parking_lots_costs);
                String[] lot_address = getResources().getStringArray(R.array.parking_lots_addresses);
                String[] lot_points = getResources().getStringArray(R.array.parking_lots_points);

                for (int i = 0; i < lot_names.length - 1; i++){
                    lots.add(new ParkingLot(lot_names[i], lot_days[i], lot_hours[i], lot_costs[i], lot_address[i], toLatLngList(lot_points[i])));
                }
                handler.sendEmptyMessage(1);
            }

            private ArrayList<LatLng> toLatLngList(String lot_point) {
                Log.d(TAG, "toLatLngList: lot_point : " + lot_point);
                String[] points = lot_point.split(",");
                Log.d(TAG, "toLatLngList: points : " + points);
                ArrayList<LatLng> geoPoints = new ArrayList<>();
                geoPoints.add(new LatLng(Double.valueOf(points[0]),Double.valueOf(points[1])));
                geoPoints.add(new LatLng(Double.valueOf(points[2]),Double.valueOf(points[3])));
                geoPoints.add(new LatLng(Double.valueOf(points[4]),Double.valueOf(points[5])));
                geoPoints.add(new LatLng(Double.valueOf(points[6]),Double.valueOf(points[7])));

                return geoPoints;
            }
        };
        t.start();

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
                root.findViewById(R.id.temple_button).setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Roger that! Going to campus!", Toast.LENGTH_SHORT).show();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(TEMPLE_LAT,TEMPLE_LNG)));
                });

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

            });
            mMap.setOnInfoWindowClickListener(marker -> {
                if (marker.getTag() instanceof Street) {
                    if (!showDisclaimer){
                        showDisclaimer(getView());
                    } else
                        parent.onStreetClick((Street) marker.getTag());
                }
                else
                    parent.onLotClick((ParkingLot) marker.getTag());
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
                ParkingLot lot = (ParkingLot) polygonX.getTag();
                //Add marker with info window
                assert lot != null;
                Marker testMarker = mMap.addMarker(new MarkerOptions()
                        .position(centerPoint(polygonX.getPoints()))
                        .title(lot.getName()));
                testMarker.setTag(lot);
                testMarker.showInfoWindow();


            });

            handler = new Handler(msg -> {
                if (msg.what== 1) {
                    Log.d(TAG, "onCreate: TempleMap size = " + tm.getStreets().size());

                    for (int i = 0; i < lots.size(); i++){
                        if (lots.get(i).getName().equals("Norris Street Lot")){
                            lots.get(i).setPiID(PID2);
                        }
                        Polygon parkingLot = mMap.addPolygon(new PolygonOptions()
                                .add(lots.get(i).getPoints().get(0),     //top right
                                        lots.get(i).getPoints().get(1),  //top left
                                        lots.get(i).getPoints().get(2),  //bottom left
                                        lots.get(i).getPoints().get(3))  //bottom right
                                .fillColor(getResources().getColor(R.color.blue_semi_trans))
                                .clickable(true)
                                .strokeWidth(1f));
                        parkingLot.setTag(lots.get(i));

                    }

                    for (int i = 0; i < tm.getStreets().size(); i++) {

                        if (!tm.getStreets().get(i).getStreetName().equals("demostreet") || !tm.getStreets().get(i).getStreetName().equals("TEST")){
                            if (tm.getStreets().get(i).getStreetName().equals("13th Polett to Norris")){
                                tm.getStreets().get(i).setPiID(PID1);
                            }
                            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                    .clickable(true)
                                    .width(20));
                            if (tm.getStreets().get(i).getGeoPoints() != null)
                            polyline.setPoints(geoToLatLng(tm.getStreets().get(i).getGeoPoints()));
                            polyline.setTag(tm.getStreets().get(i));
                            polyline.setColor(setPolylineColor(tm.getStreets().get(i)));
                        }
                    }
                    return true;
                }
                return false;
            });

        });


        return root;
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
    public void onStart() {
        super.onStart();
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
    public void onDestroy() {
        mMap.clear();
        super.onDestroy();
    }

    //Converts a list of firebase geopoints to a list of googlemaps latlng
    public static List<LatLng> geoToLatLng(List<GeoPoint> geoPoints) {
        ArrayList<LatLng> list = new ArrayList<>();
        for (int i = 0; i < geoPoints.size(); i++ ){
            list.add(new LatLng(geoPoints.get(i).getLatitude(), geoPoints.get(i).getLongitude()));
        }
        return list;
    }

    private int setPolylineColor(Object polyTag) {
        Date curr = Calendar.getInstance().getTime();
        int pos = Integer.parseInt(curr.toString().substring(11,13));
        String prob = getResources().getStringArray(R.array.probs)[pos];
        switch (prob) {
            case "Very Likely":
                return ContextCompat.getColor(getContext(), R.color.street_green);
            case "Likely":
                return ContextCompat.getColor(getContext(), R.color.street_yellow);
            case "Not Likely":
                return ContextCompat.getColor(getContext(), R.color.street_orange);
            default:
                return ContextCompat.getColor(getContext(), R.color.street_red);
        }

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

    private void showDisclaimer(View anchorView) {

        View view = getLayoutInflater().inflate(R.layout.popup_disclaimer,null);
        back_dim_layout = (RelativeLayout) anchorView.getRootView().findViewById(R.id.bac_dim_layout);

        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set an elevation value for popup window
        // Call requires API level 21
        popupWindow.setElevation(6.0f);


        view.findViewById(R.id.disc_accept).setOnClickListener(v -> {
            popupWindow.dismiss();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(DISCLAIMER, true);
            edit.commit();
            showDisclaimer = true;

        });


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);
        back_dim_layout.setVisibility(View.VISIBLE);
        popupWindow.setOnDismissListener(() -> back_dim_layout.setVisibility(View.GONE));


        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);

    }

    public void setTotalSpots(String piID, long totalSpots) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Street s : tm.getStreets()) {

            if (s.getPiID().equals(piID)) {

                if (s.getCalculation() == null) {
                    Calculation c = new Calculation();
                    c.setTotalSpots(totalSpots);
                    s.setCalculation(c);
                } else {
                    s.getCalculation().setTotalSpots(totalSpots);
                }

                /*
                 Database write:
                 */

                DocumentReference piDoc = db.collection("pi").document(piID);

                piDoc
                        .update("total_spots", totalSpots)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        }
    }

    public static TempleMap getTempleMap(){
        return tm;
    }
    public static ArrayList<ParkingLot> getLots(){
        return lots;
    }

    public void resetPiOne() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Street s : tm.getStreets()) {

            if (s.getPiID().equals("pi-1")) {
                s.setPiID("null");

                 /*
                 Database write:
                 */

                String streetID = null;

                Task<QuerySnapshot> task = db.collection("streets")
                        .whereEqualTo("pi", "pi-1").get();

                try {
                    QuerySnapshot documents = Tasks.await(task);

                    for (QueryDocumentSnapshot document : documents){

                        streetID = document.getId();
                        break; //only one is connected at a time
                    }
                } catch (ExecutionException | InterruptedException e) {
                    Log.d(TAG, "get failed with ", e);
                }

                DocumentReference piDoc = db.collection("streets").document(streetID);

                piDoc
                        .update("pi", "null")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });

            }
        }
    }

    public void resetPiTwo() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Street s : tm.getStreets()) {

            if (s.getPiID().equals("pi-2")) {
                s.setPiID("null");

                 /*
                 Database write:
                 */

                String streetID = null;

                Task<QuerySnapshot> task = db.collection("streets")
                        .whereEqualTo("pi", "pi-2").get();

                try {
                    QuerySnapshot documents = Tasks.await(task);

                    for (QueryDocumentSnapshot document : documents){

                        streetID = document.getId();
                        break; //only one is connected at a time
                    }
                } catch (ExecutionException | InterruptedException e) {
                    Log.d(TAG, "get failed with ", e);
                }

                DocumentReference piDoc = db.collection("streets").document(streetID);

                piDoc
                        .update("pi", "null")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        }
    }

    public void setPi(String piID, String streetName) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Street s : tm.getStreets()) {

            if (s.getStreetName().equals(streetName)) {
                s.setPiID(piID);


                /*
                 Database write:
                 */

                String streetID = null;

                Task<QuerySnapshot> task = db.collection("streets")
                        .whereEqualTo("street_name", streetName).get();

                try {
                    QuerySnapshot documents = Tasks.await(task);

                    for (QueryDocumentSnapshot document : documents){

                        streetID = document.getId();
                        break; //only one is connected at a time
                    }
                } catch (ExecutionException | InterruptedException e) {
                    Log.d(TAG, "get failed with ", e);
                }

                DocumentReference piDoc = db.collection("streets").document(streetID);

                piDoc
                        .update("pi", piID)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        }
    }

    public interface onMapInteraction{
        void onStreetClick(Street street);
        void onLotClick(ParkingLot lot);
        void onLocationEnabled();
    }



}
