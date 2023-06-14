package com.example.tinydns;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private static final String ServerURL = "https://172.17.0.2:5000";
    private static final String TAG = "tinydns";
    private TextView textfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Starting app.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textfield = (TextView) findViewById(R.id.textfield);
        textfield.setText("reqContent");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create an instance of your TableFragment
        queriesFragment qFragment = new queriesFragment();

        // Add the fragment to the transaction
        //fragmentTransaction.add(R.id.fragmentContainer, qFragment, "queriesFragment");

        // Commit the transaction
        fragmentTransaction.commit();
/*        tinyHttpRequest tHttp = new tinyHttpRequest(ServerURL);

        Thread httpCall = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = tHttp.makeHttpRequest("/authenticate");
                Log.d(TAG, "response " + response);

                String newresponse = tHttp.makeHttpRequest("/queries_raw");
                Log.d(TAG, "response chart " + newresponse);
                // Perform UI updates on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "response in run ");
                        //textfield.setText(newresponse); // Update the UI element
                    }
                });
            }
        });
        httpCall.start();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }


}