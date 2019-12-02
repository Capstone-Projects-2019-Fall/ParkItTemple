package com.example.parkittemple.database;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TempleMap implements Serializable {

    private List<Street> streets;

    public TempleMap() {
        streets = new ArrayList<>();
        loadMapFromDB();
        loadPiInfoFromDB();
        Log.d("Constraints", "ArrayList size: " + streets.size() + "\n");
    }

    public List<Street> getStreets() {
        return this.streets;
    }

    public void loadMapFromDB() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Task<QuerySnapshot> task = db.collection("streets").get();

        try {
            QuerySnapshot documents = Tasks.await(task);

            for (QueryDocumentSnapshot document : documents){

                Street street = convertToStreet(document);
                //logStreets(street);
                streets.add(street);
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.d(TAG, "get failed with ", e);
        }
    }

    public void loadPiInfoFromDB() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Street street : streets) {

            String piID = street.getPiID();

            if (piID.equals("null")) {
                continue;
            }

            Task<DocumentSnapshot> task = db.collection("pi").document(piID).get();

            try {

                DocumentSnapshot document = Tasks.await(task);

                Map<String, Object> dataMap = document.getData();

                Calculation calculation = new Calculation();

                calculation.setTotalSpots((String) dataMap.get("total_spots"));
                calculation.setAvailableSpots((String) dataMap.get("available_spots"));

                street.setCalculation(calculation);

                logStreet(street);

            } catch (ExecutionException | InterruptedException e) {
                Log.d(TAG, "get failed with ", e);
            }
        }
    }

    public Street convertToStreet(DocumentSnapshot documentSnapshot) {

        Street street = new Street();

        Map<String, Object> map = documentSnapshot.getData();

        street.setStreetName((String)map.get("street_name"));
        street.setPiID((String)map.get("pi"));

        /*for (String x : map.keySet()) {
            Log.d(TAG, "Data map key: " + x + "\n");
        }*/

        /*Map<String, Object> calculationMap = (Map)map.get("calculation");

        Calculation calculation = new Calculation();
        if (calculationMap != null) {
            calculation.setProbability((String) calculationMap.get("probability"));
            calculation.setAvailableSpots((String) calculationMap.get("available_spots"));
        }
        street.setCalculation(calculation); */

        /*for (String x : calculationMap.keySet()) {
            Log.d(TAG, "CALCULATION Data map key: " + x + "\n");
        }*/

        List<GeoPoint> geoPoints = (List)map.get("geopoints");
        if (!(geoPoints == null)) {
            street.setGeoPoints(geoPoints);
        }

        /*for (GeoPoint g : geoPoints) {
            Log.d(TAG, "GEOPOINT Data map long: " + g.getLongitude() + ". lat: " + g.getLatitude() + ".\n");
        }*/

        Map<String, Object> regulationMap = (Map)map.get("regulation");

        Regulation regulation = new Regulation();
        regulation.setNote((String)regulationMap.get("note"));
        regulation.setDescription((String)regulationMap.get("description"));
        regulation.setFree((boolean)regulationMap.get("free"));
        regulation.setMaxHours((String)regulationMap.get("max_hours"));
        regulation.setStart((Timestamp)regulationMap.get("start"));
        regulation.setEnd((Timestamp)regulationMap.get("end"));
        street.setRegulation(regulation);

        /*for (String x : regulationMap.keySet()) {
            Log.d(TAG, "REGULATION Data map key: " + x + "\n");
        }*/

        //logStreet(street);
        return street;
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

    public static void logStreet(Street s) {

        Log.d(TAG, "STREET Street name: " + s.getStreetName() + "\n");


        if (s.getGeoPoints() != null) {
            for (GeoPoint g : s.getGeoPoints()) {
                Log.d(TAG, "STREET GeoPoint: " + g.getLatitude() + ", " + g.getLongitude() + "\n");
            }
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
