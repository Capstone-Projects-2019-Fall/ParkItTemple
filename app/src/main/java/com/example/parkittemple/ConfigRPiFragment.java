package com.example.parkittemple;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.parkittemple.database.ParkingLot;
import com.example.parkittemple.database.Street;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigRPiFragment extends Fragment {

    ArrayList<String> streetnames;
    String streetname;


    public ConfigRPiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        streetnames = new ArrayList<>();

        for (Street street: MapFragment.getTempleMap().getStreets()){
            if (!street.getStreetName().equals("demostreet") && !street.getStreetName().equals("TEST"))
                streetnames.add(street.getStreetName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config_rpi, container, false);

        Spinner streets = view.findViewById(R.id.street_list_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, streetnames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        streets.setAdapter(spinnerArrayAdapter);
        streets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    streetname = ((TextView) view).getText().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPiOne();
                for (Street street : MapFragment.getTempleMap().getStreets()){
                    if (street.getStreetName().equals(streetname)){
                        street.setPiID("pi-1");
                    }
                }
            }
        });



        return view;
    }

    public void resetPiOne() {

        for (Street s : MapFragment.getTempleMap().getStreets()) {

            if ((s.getPiID() != null) && s.getPiID().equals("pi-1")) {
                s.setPiID(null);
            }
        }
    }

}
