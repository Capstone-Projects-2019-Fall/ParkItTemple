package com.example.parkittemple;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkittemple.database.ParkingLot;
import com.example.parkittemple.database.Street;

import java.util.ArrayList;

public class StreetListRecyclerViewAdapter extends RecyclerView.Adapter<StreetListRecyclerViewAdapter.MyViewHolder>{

    private final ArrayList<Object> mStreets;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView street;
        View mView;

        MyViewHolder(View view) {
            super(view);
            street = view.findViewById(R.id.street_name);
            mView = view;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    StreetListRecyclerViewAdapter(ArrayList<Object> streets) {
        mStreets = streets;
    }


    @NonNull
    @Override
    public StreetListRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.street_listview, parent, false);
        StreetListRecyclerViewAdapter.MyViewHolder vh = new StreetListRecyclerViewAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StreetListRecyclerViewAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mStreets.get(position) instanceof Street) {
            holder.street.setText(((Street) mStreets.get(position)).getStreetName());
            holder.mView.setOnClickListener(v -> {
                if (getItemCount() > 2) { // 2 = number of RPis
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.main_frame, StreetDetailsFragment.newInstance((Street) mStreets.get(position)))
                            .commit();
                } else {
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.main_frame, RealTimeStreetDetailsFragment.newInstance((Street) mStreets.get(position)))
                            .commit();
                }
            });
        } else {
            holder.street.setText(((ParkingLot) mStreets.get(position)).getName());
            holder.mView.setOnClickListener(v -> {
                if (getItemCount() > 2) { // 2 = number of RPis
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.main_frame, LotDetails.newInstance((ParkingLot) mStreets.get(position)))
                            .commit();
                } else {
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.main_frame, LotDetails.newInstance((ParkingLot) mStreets.get(position)))
                            .commit();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mStreets.size();
    }

}
