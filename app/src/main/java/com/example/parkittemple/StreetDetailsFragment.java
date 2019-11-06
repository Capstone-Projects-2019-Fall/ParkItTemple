package com.example.parkittemple;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parkittemple.database.Street;

import static com.example.parkittemple.MapFragment.DESCRIPTION;
import static com.example.parkittemple.MapFragment.FREE;
import static com.example.parkittemple.MapFragment.STREET_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class StreetDetailsFragment extends Fragment {

    //TODO
    /** Load these from the database*/
    //These are the items that need to be displayed to the user.
    //This is a very basic setup for testing purposes.
    private  String[] mDays; //An array of days of the week. Can leave as is or load from db
    private  String[] mProbs; //An array of probabilities for parking on it's index's respective hour.
    private  String[] mHours;
    private  String streetName, description;
    private  boolean isFree;
    private TextView notes, street_name, free;


    public StreetDetailsFragment() {
        // Required empty public constructor
    }

    public static StreetDetailsFragment newInstance(Street street) {
        StreetDetailsFragment fragment = new StreetDetailsFragment();
        Bundle args = new Bundle();
        args.putString(STREET_NAME, street.getStreetName());
        args.putString(DESCRIPTION, street.getRegulation().getDescription());
        args.putBoolean(FREE, street.getRegulation().isFree());
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
            description = bundle.getString(DESCRIPTION);
            isFree = bundle.getBoolean(FREE);
        }
        mDays = getResources().getStringArray(R.array.days);
        mHours = getResources().getStringArray(R.array.hours);
        mProbs = getResources().getStringArray(R.array.probs);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_street_details, container, false);


        street_name = view.findViewById(R.id.street_name);
        street_name.setText(streetName);
        notes = view.findViewById(R.id.notes_val);
        notes.setText(description);
        free = view.findViewById(R.id.free);
        if (description.equals("No parking.") || description.equals("To-do.")){
            view.findViewById(R.id.reg_sign).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.recyclerview_street_details).setVisibility(View.INVISIBLE);
            free.setText("");
        } else {

            if (isFree) {
                free.setText(getResources().getString(R.string.free_park));
            } else {
                free.setText(getResources().getString(R.string.paid_park));
            }
        }

        if (description.equals("No regulation.")){
            view.findViewById(R.id.reg_sign).setVisibility(View.INVISIBLE);
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_street_details);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new StreetDetailsListViewAdapter(mDays, mProbs, mHours));

        // Inflate the layout for this fragment
        return view;
    }

}
