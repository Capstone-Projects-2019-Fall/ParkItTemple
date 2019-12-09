package com.example.parkittemple;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkittemple.database.Street;
import com.example.parkittemple.database.TempleMap;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class StreetListFragment extends Fragment {

    private TempleMap templeMap;
    private RecyclerView recyclerView;
    ArrayList<Street> newMap;


    public StreetListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        templeMap = MainActivity.templeMap;
        newMap = new ArrayList<>();
        for (Street street : templeMap.getStreets()){
            if (!street.getStreetName().equals("demostreet") && !street.getStreetName().equals("TEST")){
                newMap.add(street);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_street_list, container, false);

        if (newMap != null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recview_street_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(new StreetListRecyclerViewAdapter(newMap));
            Log.d(TAG, "onCreateView: street list view " + newMap.size());
        }

        return view;
    }



}
