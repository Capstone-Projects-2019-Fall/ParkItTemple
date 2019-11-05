package com.example.parkittemple;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkittemple.database.Street;
import com.example.parkittemple.database.TempleMap;

import java.util.ArrayList;

public class StreetListRecyclerViewAdapter extends RecyclerView.Adapter<StreetListRecyclerViewAdapter.MyViewHolder>{

    private final TempleMap mStreets;


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
    StreetListRecyclerViewAdapter(TempleMap streets) {
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
        holder.street.setText(mStreets.getStreets().get(position).getStreetName());

    }


    @Override
    public int getItemCount() {
        return mStreets.getStreets().size();
    }
}
