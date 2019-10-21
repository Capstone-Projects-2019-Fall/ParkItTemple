package com.example.parkittemple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class StreetDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;

    //TODO
    /** Load these from the database*/
    //These are the items that need to be displayed to the user.
    //This is a very basic setup for testing purposes.
    private  String[] mDays = getResources().getStringArray(R.array.days); //An array of days of the week. Can leave as is or load from db
    private  double[] mProbs = {55.0, 61.2, 55.1, 45.9, 35.0, 75.5, 81.6}; //An array of probabilities for parking on it's index's respective day.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_details);

        recyclerView = (RecyclerView) findViewById(R.id.recview_street_details);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new StreetDetailsRecyclerViewAdapter(mDays,mProbs);
        recyclerView.setAdapter(mAdapter);
    }

}
