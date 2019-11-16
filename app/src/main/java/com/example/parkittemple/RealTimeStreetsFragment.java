package com.example.parkittemple;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkittemple.database.Street;
import com.example.parkittemple.database.TempleMap;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class RealTimeStreetsFragment extends Fragment {

    // the fragment initialization parameters
    private static final String STREET_LIST = "param1";

    private TempleMap templeMap;
    private RecyclerView recyclerView;


    public RealTimeStreetsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        templeMap = MainActivity.templeMap;

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


    private ArrayList<Street> getRealTimeStreets() {

        ArrayList<Street> streets = new ArrayList<>();
        streets.add(templeMap.getStreets().get(0));
        streets.add(templeMap.getStreets().get(1));
        streets.add(templeMap.getStreets().get(2));

        return streets;
    }

}
