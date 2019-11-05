package com.example.parkittemple;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.parkittemple.database.Street;
import com.example.parkittemple.database.TempleMap;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static android.content.ContentValues.TAG;


public class RealTimeStreetsFragment extends Fragment {

    // the fragment initialization parameters
    private static final String STREET_LIST = "param1";

    private ArrayList<Street> streets;
    private TempleMap templeMap;
    private Handler handler;
    RecyclerView recyclerView;

    private OnFragmentInteractionListener parentActivity;

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
            recyclerView.setAdapter(new StreetListRecyclerViewAdapter(templeMap));
            Log.d(TAG, "onCreateView: street list view" + templeMap.getStreets().size());
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            parentActivity = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
    }


    public interface OnFragmentInteractionListener {

        void onStreetSelectedFromRealTimeFragment(Street street);
    }
}
