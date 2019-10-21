package com.example.parkittemple;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SteetDetailsRecyclerViewAdapter extends RecyclerView.Adapter<SteetDetailsRecyclerViewAdapter.MyViewHolder>{

    private final String[] mDays;
    private final double[] mProbs;
    Resources resources;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView day;
        TextView prob;
        View mView;
        MyViewHolder(View view) {
            super(view);
            day = view.findViewById(R.id.day);
            prob = view.findViewById(R.id.percent);
            mView = view;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    SteetDetailsRecyclerViewAdapter(String[] day, double[] probability) {
        mDays = day;
        mProbs = probability;
    }


    @NonNull
    @Override
    public SteetDetailsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.street_info_listview, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.day.setText(mDays[position]);
        holder.prob.setText(mProbs[position] + "%");

        if (position % 2 == 0){
            holder.mView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.light_gray));
        }
    }


    @Override
    public int getItemCount() {
        return mDays.length;
    }
}
