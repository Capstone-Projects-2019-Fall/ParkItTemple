package com.example.parkittemple;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.parkittemple.database.Street;
import com.example.parkittemple.database.TempleMap;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.proto.TargetOuterClass;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RealTimeStreetsFragment.OnFragmentInteractionListener
    , StreetListFragment.OnFragmentInteractionListener, MapFragment.onMapInteraction{


    private static final String MAP_FRAGMENT = "map_fragment";
    private static final String REAL_TIME_FRAG = "real_time";
    private static final String STREET_LIST_FRAG = "street_list";
    private static final String TAG = "Main Activity";
    DrawerLayout drawer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;


    //Temporary map for testing
    public static TempleMap templeMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activlty_main);
        FirebaseApp.initializeApp(this);


        //Delete this after testing with temporary map
        Thread t = new Thread() {
            @Override
            public void run() {
                templeMap = new TempleMap();
            }
        };
        t.start();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);


        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.main_frame, new MapFragment(), MAP_FRAGMENT)
            .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Log.d(TAG, "onBackPressed: back stack = " + getSupportFragmentManager().getBackStackEntryCount());
            if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;
        String tag = "";
        boolean backStackFlag = true;

        switch (menuItem.getItemId()) {
            case R.id.real_time:
                tag = REAL_TIME_FRAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment != null){
                    backStackFlag = false;
                    break;
                } else {
                    fragment = new RealTimeStreetsFragment();
                }
                break;
            case R.id.street_list:
                tag = STREET_LIST_FRAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment != null){
                    backStackFlag = false;
                    break;
                } else {
                    fragment = new StreetListFragment();
                }
                break;
            default:
                break;
        }

        if (fragment != null) {
            Log.d(TAG, "onNavigationItemSelected: back stack flag = " + backStackFlag);
            Log.d(TAG, "onNavigationItemSelected: back stack count = " + getSupportFragmentManager().getBackStackEntryCount());
            Log.d(TAG, "onNavigationItemSelected: fragment = " + fragment.getTag());
            if (!backStackFlag) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment, tag).commitNow();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment, tag).addToBackStack(tag).commit();
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onStreetSelectedFromRealTimeFragment(Street street) {

    }

    @Override
    public void onStreetSelectedFromStreetListFragment(Uri uri) {

    }

    @Override
    public void onStreetClick(Street street) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, StreetDetailsFragment.newInstance(street)).addToBackStack(null).commit();
    }
}
