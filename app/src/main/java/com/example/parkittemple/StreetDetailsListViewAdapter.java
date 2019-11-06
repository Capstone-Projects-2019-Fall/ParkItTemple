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

public class StreetDetailsListViewAdapter extends RecyclerView.Adapter<StreetDetailsListViewAdapter.MyViewHolder>{

    private final String[] mDays;
    private final String[] mProbs;
    private final String[] mHours;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView day;
        TextView prob;
        Spinner hours;
        View mView;

        MyViewHolder(View view) {
            super(view);
            day = view.findViewById(R.id.day);
            prob = view.findViewById(R.id.percent);
            hours = view.findViewById(R.id.hours_spinner);
            mView = view;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    StreetDetailsListViewAdapter(String[] day, String[] probability, String[] hours) {
        mDays = day;
        mProbs = probability;
        mHours = hours;
    }


    @NonNull
    @Override
    public StreetDetailsListViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.street_info_listview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.day.setText(mDays[position]);
        holder.prob.setText(mProbs[position]);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(holder.mView.getContext(),R.array.hours, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.hours.setAdapter(adapter);
        holder.hours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //TODO change prob based on hour here. Currently choosing prob at random
                int random = (int) (Math.random() * 100) % mProbs.length;
                holder.prob.setText(mProbs[random]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //if (position % 2 == 0){
        //    holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.light_gray));
        //}
    }


    @Override
    public int getItemCount() {
        return mDays.length;
    }
}
