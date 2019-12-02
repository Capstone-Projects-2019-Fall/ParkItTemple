package com.example.parkittemple;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parkittemple.database.Street;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.parkittemple.MapFragment.DESCRIPTION;
import static com.example.parkittemple.MapFragment.FREE;
import static com.example.parkittemple.MapFragment.STREET_NAME;
import static com.example.parkittemple.MapFragment.geoToLatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class LotDetails extends Fragment {
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String POINTS = "points";

    private String streetName;
    private int total_spots, available_spots;
    private Parcelable[] points;
    private RelativeLayout back_dim_layout;


    public LotDetails() {
        // Required empty public constructor
    }

    static LotDetails newInstance(Street street) {
        LotDetails fragment = new LotDetails();
        Bundle args = new Bundle();
        args.putString(STREET_NAME, street.getStreetName());
        args.putParcelableArrayList(POINTS, (ArrayList<? extends Parcelable>) geoToLatLng(street.getGeoPoints()));
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;
        {
            streetName = bundle.getString(STREET_NAME);
            points = bundle.getParcelableArray(POINTS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_lot_details, container, false);



        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.google_maps_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Create a Uri from an intent string. Use the result to create an Intent.
        //Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        //Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Make the Intent explicit by setting the Google Maps package
       //mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
       //startActivity(mapIntent);

        return true;
    }

}
