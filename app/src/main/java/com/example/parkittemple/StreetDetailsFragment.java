package com.example.parkittemple;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkittemple.database.Street;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;
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
    private TextView notes, street_name, free, prob, alert_text;
    private Spinner hours, days;
    private RelativeLayout back_dim_layout;


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
            view.findViewById(R.id.prob_view).setVisibility(View.INVISIBLE);
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


        hours = view.findViewById(R.id.hours_spinner);
        prob = view.findViewById(R.id.percent);
        days = view.findViewById(R.id.days_spinner);

        ArrayAdapter<CharSequence> adapter_hour = ArrayAdapter.createFromResource(view.getContext(),R.array.hours, android.R.layout.simple_spinner_item);
        adapter_hour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hours.setAdapter(adapter_hour);
        hours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //TODO change prob based on hour here. Currently choosing prob at random
                int random = (int) (Math.random() * 100) % mProbs.length;
                prob.setText(mProbs[random]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter_days = ArrayAdapter.createFromResource(view.getContext(),R.array.days, android.R.layout.simple_spinner_item);
        adapter_days.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days.setAdapter(adapter_days);
        days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO change dataset based on day
                int random = (int) (Math.random() * 100) % mProbs.length;
                prob.setText(mProbs[random]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        SpannableString ss = new SpannableString(getString(R.string.alert_text));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                showPopup(view);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, getString(R.string.alert_text).length()- 8, getString(R.string.alert_text).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.link_blue)), getString(R.string.alert_text).length()- 8, getString(R.string.alert_text).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        alert_text = view.findViewById(R.id.alert_text_view);
        alert_text.setText(ss);
        alert_text.setMovementMethod(LinkMovementMethod.getInstance());


        // Inflate the layout for this fragment
        return view;
    }


    private void showPopup(View anchorView) {

        Spinner time_parked, notify;
        TextView date;
        Date curr = Calendar.getInstance().getTime();

        View view = getLayoutInflater().inflate(R.layout.popup_alert_conf, null);
        back_dim_layout = (RelativeLayout) anchorView.getRootView().findViewById(R.id.bac_dim_layout);

        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set an elevation value for popup window
        // Call requires API level 21
        popupWindow.setElevation(6.0f);

        date = view.findViewById(R.id.date);
        date.setText(curr.toString().substring(0,10));

        time_parked = view.findViewById(R.id.time_parked_spinner);
        ArrayAdapter<CharSequence> adapter_time_parked = ArrayAdapter.createFromResource(view.getContext(),R.array.hours, android.R.layout.simple_spinner_item);
        adapter_time_parked.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_parked.setAdapter(adapter_time_parked);
        time_parked.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        time_parked.setSelection(Integer.parseInt(curr.toString().substring(11,13)));

        notify = view.findViewById(R.id.notify_spinner);
        String[] notify_array = getResources().getStringArray(R.array.notify);
        ArrayAdapter<CharSequence> adapter_notify = ArrayAdapter.createFromResource(view.getContext(),R.array.notify, android.R.layout.simple_spinner_item);
        adapter_notify.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notify.setAdapter(adapter_notify);
        notify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        view.findViewById(R.id.confirm).setOnClickListener(v -> {
            popupWindow.dismiss();
            Toast.makeText(view.getContext(), "Got it! We'll notify you " + notify.getSelectedItem().toString() + " your time is up!", Toast.LENGTH_SHORT).show();
            anchorView.findViewById(R.id.alert_text_view).setVisibility(View.GONE);
        });


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);
        back_dim_layout.setVisibility(View.VISIBLE);
        popupWindow.setOnDismissListener(() -> back_dim_layout.setVisibility(View.GONE));


        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView.getRootView(), Gravity.CENTER, 0, 0);

    }
}
