package com.example.tinydns;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private static final String ServerURL = "https://172.17.0.2:5000";
    private static final String TAG = "tinydns";
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Starting app.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create an instance of the Fragment
        homeFragment hFragment = homeFragment.newInstance("asd", "das");

        // Add the fragment to the transaction
        fragmentTransaction.add(R.id.fragment_container , hFragment, "homeFragment");

        // Commit the transaction
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (id == R.id.action_home) {
            // Create an instance of the Fragment
            homeFragment hFragment = homeFragment.newInstance("asd", "das");
            // Add the fragment to the transaction
            fragmentTransaction.replace(R.id.fragment_container , hFragment, "homeFragment");
            fragmentTransaction.commit();
            return true;
        } else if (id == R.id.action_queries) {
            // Create an instance of the Fragment
            queriesFragment qFragment = queriesFragment.newInstance("asd", "das");
            // Add the fragment to the transaction
            fragmentTransaction.replace(R.id.fragment_container , qFragment, "queriesFragment");
            fragmentTransaction.commit();
            return true;
        } else if (id == R.id.action_vt_stats) {
            // Create an instance of the Fragment
            vtStatsFragment vFragment = vtStatsFragment.newInstance("asd", "das");
            // Add the fragment to the transaction
            fragmentTransaction.replace(R.id.fragment_container , vFragment, "vtStatsFragment");
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}