package com.example.parkittemple;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parkittemple.database.ParkingLot;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class LotDetails extends Fragment {
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String POINTS = "points";
    private static final String LOT_NAME = "lot_name";
    private static final String LOT_DAYS = "lot_days";
    private static final String LOT_HOURS = "lot_hours";
    private static final String LOT_COST = "lot_cost";
    private static final String PI = "pi";

    private String lotName, lotDays, lotHours, lotCost, PiID;
    private TextView available_spots;
    private ArrayList<LatLng> points;
    private double lat, lng;
    private RelativeLayout back_dim_layout;
    private boolean isVisible;


    public LotDetails() {
        // Required empty public constructor
    }

    static LotDetails newInstance(ParkingLot lot) {
        LotDetails fragment = new LotDetails();
        Bundle args = new Bundle();
        args.putString(LOT_NAME, lot.getName());
        args.putString(LOT_DAYS, lot.getDaysOpen());
        args.putString(LOT_HOURS, lot.getHours());
        args.putString(LOT_COST, lot.getCosts());
        args.putString(PI, lot.getPiID());
        args.putParcelableArrayList(POINTS, (ArrayList<? extends Parcelable>) lot.getPoints());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;
        {
            lotName = bundle.getString(LOT_NAME);
            lotDays = bundle.getString(LOT_DAYS);
            lotHours = bundle.getString(LOT_HOURS);
            lotCost= bundle.getString(LOT_COST);
            points = bundle.getParcelableArrayList(POINTS);
            PiID = bundle.getString(PI);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_lot_details, container, false);
        view.setBackgroundColor(Color.WHITE);

        TextView name, days, hours, cost;

        name = view.findViewById(R.id.lot_name);
        name.setText(lotName);

        days = view.findViewById(R.id.lot_days);
        days.setText(lotDays);

        hours = view.findViewById(R.id.lot_hours);
        hours.setText(lotHours);

        cost = view.findViewById(R.id.lot_cost);
        cost.setText(lotCost);

        LatLng center = centerPoint(points);
        lat = center.latitude;
        lng = center.longitude;

        if (PiID == null){
            view.findViewById(R.id.avail_spots).setVisibility(View.GONE);
            view.findViewById(R.id.num_spots).setVisibility(View.GONE);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                available_spots.setText(String.valueOf(msg.what));
                return false;
            }
        });

        View view = getView();
        if (view != null) {
            available_spots = view.findViewById(R.id.num_spots);
            available_spots.setText("--");
            isVisible = true;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("pi").document("pi-2");
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        handler.sendEmptyMessage(Math.toIntExact((long) snapshot.get("available_spots")));
                        Log.d(TAG, "Current data: " + snapshot.getData());
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.google_maps_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);

        return true;
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

}
