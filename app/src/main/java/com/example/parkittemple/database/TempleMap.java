package com.example.parkittemple.database;

import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TempleMap {

    private List<Street> streets;

    public TempleMap() {
        streets = new ArrayList<>();
        loadMapFromDB();
        Log.d("Constraints", "ArrayList size: " + streets.size() + "\n");
    }

    public List<Street> getStreets() {
        return this.streets;
    }


    public void loadMapFromDB() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        //DocumentReference docRef = db.collection("streets").document("DjLiJHWNKM8XGmdnAZkv");


        /*docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        Street street = convertToStreet(document);
                        streets.add(street);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }); */

        db.collection("streets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete( Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Street street = convertToStreet(document);
                                getStreets().add(street);

                                //Log.d(TAG, "Adding street: " + document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public Street convertToStreet(DocumentSnapshot documentSnapshot) {

        Street street = new Street();

        Map<String, Object> map = documentSnapshot.getData();

        street.setStreetName((String)map.get("street_name"));

        for (String x : map.keySet()) {
            Log.d(TAG, "Data map key: " + x + "\n");
        }

        Map<String, Object> calculationMap = (Map)map.get("calculation");

        Calculation calculation = new Calculation();
        calculation.setProbability((String)calculationMap.get("probability"));
        calculation.setAvailableSpots((String)calculationMap.get("available_spots"));
        street.setCalculation(calculation);

        for (String x : calculationMap.keySet()) {
            Log.d(TAG, "CALCULATION Data map key: " + x + "\n");
        }

        List<GeoPoint> geoPoints = (List)map.get("geopoints");
        street.setGeoPoints(geoPoints);

        for (GeoPoint g : geoPoints) {
            Log.d(TAG, "GEOPOINT Data map long: " + g.getLongitude() + ". lat: " + g.getLatitude() + ".\n");
        }

        Map<String, Object> regulationMap = (Map)map.get("regulation");

        Regulation regulation = new Regulation();
        regulation.setNote((String)regulationMap.get("note"));
        regulation.setDescription((String)regulationMap.get("description"));
        regulation.setFree((boolean)regulationMap.get("free"));
        regulation.setMaxHours((String)regulationMap.get("max_hours"));
        regulation.setStart((Timestamp)regulationMap.get("start"));
        regulation.setEnd((Timestamp)regulationMap.get("end"));
        street.setRegulation(regulation);

        for (String x : regulationMap.keySet()) {
            Log.d(TAG, "REGULATION Data map key: " + x + "\n");
        }

        return street;
       // streets.add(street);
        //logStreets(streets);
    }

    public static void logStreets(List<Street> streets) {

        int count = 0;

        for (Street s : streets) {

            count++;

            Log.d(TAG, "STREET " + count + "object data: \n");
            Log.d(TAG, "STREET Street name: " + s.getStreetName() + "\n");

            for (GeoPoint g : s.getGeoPoints()) {
                Log.d(TAG, "STREET GeoPoint: " + g.getLatitude() + ", " +  g.getLongitude() + "\n");
            }

            Log.d(TAG, "STREET Available spots: " + s.getCalculation().getAvailableSpots() + "\n");
            Log.d(TAG, "STREET Probability: " + s.getCalculation().getProbability() + "\n");

            Log.d(TAG, "STREET Description: " + s.getRegulation().getDescription() + "\n");
            Log.d(TAG, "STREET Note: " + s.getRegulation().getNote() + "\n");
            Log.d(TAG, "STREET IsFree: " + s.getRegulation().isFree() + "\n");
            Log.d(TAG, "STREET Start: " + s.getRegulation().getStart() + "\n");
            Log.d(TAG, "STREET End: " + s.getRegulation().getEnd() + "\n");
            Log.d(TAG, "STREET Max hours: " + s.getRegulation().getMaxHours() + "\n");

        }
    }
}