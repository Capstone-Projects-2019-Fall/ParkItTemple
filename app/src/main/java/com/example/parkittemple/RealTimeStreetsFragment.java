package com.example.parkittemple;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkittemple.database.ParkingLot;
import com.example.parkittemple.database.Regulation;
import com.example.parkittemple.database.Street;
import com.example.parkittemple.database.TempleMap;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class RealTimeStreetsFragment extends Fragment {

    // the fragment initialization parameters
    private static final String STREET_LIST = "param1";

    private TempleMap templeMap;
    private ArrayList<ParkingLot> lots;
    private RecyclerView recyclerView;


    public RealTimeStreetsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        templeMap = MapFragment.getTempleMap();
        lots = MapFragment.getLots();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_realtimestreets, container, false);

        if (templeMap != null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recview_street_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(new StreetListRecyclerViewAdapter(getRealTimeStreets()));
            Log.d(TAG, "onCreateView: street list view" + templeMap.getStreets().size());

        }


        return view;
    }


    private ArrayList<Object> getRealTimeStreets() {

        ArrayList<Object> streets = new ArrayList<>();
        for (Street street : templeMap.getStreets()){
            if (street.getPiID() != null){
                streets.add(street);
            }
        }

        for (ParkingLot lot : lots){
            if (lot.getPiID() != null){
                streets.add(lot);
            }
        }

        /*
        Street realTimeStreet = new Street();

        realTimeStreet.setStreetName("SHRO/SERC");

        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(39.981606,-75.153459));
        realTimeStreet.setGeoPoints(points);


        Regulation regulation = new Regulation();
        regulation.setNote("");
        regulation.setDescription("");
        regulation.setFree(true);
        regulation.setMaxHours(null);
        regulation.setStart(null);
        regulation.setEnd(null);
        realTimeStreet.setRegulation(regulation);

        streets.add(realTimeStreet);

         */

        /*
        streets.add(templeMap.getStreets().get(0));
        streets.add(templeMap.getStreets().get(1));
        streets.add(templeMap.getStreets().get(2));

         */


        return streets;
    }

}
