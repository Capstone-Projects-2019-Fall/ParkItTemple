package com.example.parkittemple.database;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TempleMap {

    private List<Street> streets;

    public TempleMap() {
        streets = new ArrayList<>();
        loadMapFromDB();
        loadPiFromDB();
        //loadPiInfoFromDB();
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

   public void loadPiFromDB() {

       FirebaseFirestore db = FirebaseFirestore.getInstance();

       db.collection("pi")
               //.whereArrayContains("pi", "pi")
               //.whereEqualTo("state", "CA")
               .addSnapshotListener(new EventListener<QuerySnapshot>() {
                   @Override
                   public void onEvent(@Nullable QuerySnapshot snapshots,
                                       @Nullable FirebaseFirestoreException e) {
                       if (e != null) {
                           Log.w(TAG, "listen:error", e);
                           return;
                       }

                       for (QueryDocumentSnapshot doc : snapshots) {

                           if (doc.get("available_spots") != null) {
                               String piID = doc.getId();
                               long takenSpots;
                               try{
                                   takenSpots = (long) doc.get("available_spots");
                               } catch (ClassCastException class_e){
                                   takenSpots = 0;
                               }

                               Log.d(TAG, "PiID: " + piID);
                               Log.d(TAG, "Taken Spots: " + takenSpots);


                               for (Street street : streets) {
                                   if ((street.getPiID() != null) && street.getPiID().equals(piID)) {

                                       if (street.getCalculation() == null) {
                                           Calculation calculation = new Calculation();
                                           calculation.setTakenSpots(takenSpots);
                                           street.setCalculation(calculation);
                                       } else {
                                           street.getCalculation().setTakenSpots(takenSpots);
                                       }
                                       logStreet(street);
                                   }
                               }
                           }
                       }
                   }
               });

   }

    public Street convertToStreet(DocumentSnapshot documentSnapshot) {

        Street street = new Street();

        Map<String, Object> map = documentSnapshot.getData();

        street.setStreetName((String)map.get("street_name"));

        //street.setPiID((String)map.get("pi"));

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

        Log.d(TAG, "STREET Taken spots: " + s.getCalculation().getTakenSpots() + "\n");
        Log.d(TAG, "STREET Available spots: " + s.getCalculation().getAvailableSpots() + "\n");
        Log.d(TAG, "STREET Probability: " + s.getCalculation().getProbability() + "\n");

        Log.d(TAG, "STREET Description: " + s.getRegulation().getDescription() + "\n");
        Log.d(TAG, "STREET Note: " + s.getRegulation().getNote() + "\n");
        Log.d(TAG, "STREET IsFree: " + s.getRegulation().isFree() + "\n");
        Log.d(TAG, "STREET Start: " + s.getRegulation().getStart() + "\n");
        Log.d(TAG, "STREET End: " + s.getRegulation().getEnd() + "\n");
        Log.d(TAG, "STREET Max hours: " + s.getRegulation().getMaxHours() + "\n");

    }

    public ArrayList<String> getStreetNames() {

        ArrayList<String> streetNames = new ArrayList<>();

        for (Street s : streets) {
            streetNames.add(s.getStreetName());
        }

        return streetNames;
    }

    public void setTotalSpots(String piID, long totalSpots) {

        for (Street s : streets) {

            if (s.getPiID().equals(piID)) {

                if (s.getCalculation() == null) {
                    Calculation c = new Calculation();
                    c.setTotalSpots(totalSpots);
                    s.setCalculation(c);
                } else {
                    s.getCalculation().setTotalSpots(totalSpots);
                }
            }
        }
    }



    public void resetPiTwo() {

        for (Street s : streets) {

            if (s.getPiID().equals("pi-2")) {
                s.setPiID("null");
            }
        }
    }

    public void setPi(String piID, String streetName) {

        for (Street s : streets) {

            if (s.getStreetName().equals(streetName)) {
                s.setPiID(piID);
            }
        }
    }
}

/*
//LOT
        if (((String)map.get("street_name")).equals("demolot")){

                street.setStreetName("SERC Parking Lot 7");

                Map<String, Object> calculationMap = (Map) map.get("calculation");

        Calculation calculation = new Calculation();
        if (calculationMap != null) {
        calculation.setProbability((String) calculationMap.get("probability"));
        calculation.setAvailableSpots((String) calculationMap.get("available_spots"));
        }
        street.setCalculation(calculation);

        }

 */