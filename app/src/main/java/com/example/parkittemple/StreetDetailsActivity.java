package com.example.parkittemple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

public class StreetDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;

    private  String[] mDays = {"Mon", "Tues", "Weds", "Thurs", "Fri", "Sat", "Sun"};
    private  double[] mProbs = {55.0, 61.2, 55.1, 45.9, 35.0, 75.5, 81.6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_details);

        recyclerView = (RecyclerView) findViewById(R.id.recview_street_details);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new SteetDetailsRecyclerViewAdapter(mDays,mProbs);
        recyclerView.setAdapter(mAdapter);
    }

}
