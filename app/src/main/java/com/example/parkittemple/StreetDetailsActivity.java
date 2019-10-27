package com.example.parkittemple;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public class StreetDetailsActivity extends AppCompatActivity {

    //TODO
    /** Load these from the database*/
    //These are the items that need to be displayed to the user.
    //This is a very basic setup for testing purposes.
    private  String[] mDays; //An array of days of the week. Can leave as is or load from db
    private  String[] mProbs; //An array of probabilities for parking on it's index's respective hour.
    private  String[] mHours;
    private TextView notes, street_name, street_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_details);
        mDays = getResources().getStringArray(R.array.days);
        mHours = getResources().getStringArray(R.array.hours);
        mProbs = getResources().getStringArray(R.array.probs);


        street_name = findViewById(R.id.street_name);
        street_name.setText(getIntent().getStringExtra("street_name"));
        street_num = findViewById(R.id.street_number);
        notes = findViewById(R.id.notes_val);
        notes.setText(getString(R.string.sample_note));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recview_street_details);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new StreetDetailsRecyclerViewAdapter(mDays, mProbs, mHours));
    }

}
