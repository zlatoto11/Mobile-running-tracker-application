package com.example.zlat.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView txtDisplayDistance, txtDisplayTime, txtDisplaySpeed, txtDisplayAvgSpeed;
    private GoogleMap mMap;
    private PolylineOptions myLines = new PolylineOptions()
            .width(25)
            .color(Color.BLUE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        txtDisplayDistance = findViewById(R.id.textDistance);
        txtDisplayTime = findViewById(R.id.textTime);
        txtDisplaySpeed = findViewById(R.id.textSpeed);
        txtDisplayAvgSpeed = findViewById(R.id.txtAvgSpeed);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        IntentFilter filter = new IntentFilter("com.example.zlat.myapplication.UPDATE_MARKER");
        registerReceiver(mapReceiver, filter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    BroadcastReceiver mapReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            //Log.d("g53mdp", "MapsActivity");
            // Toast.makeText(context, "RECEIVED AT MAPS.", Toast.LENGTH_LONG).show();
            double latitude = intent.getDoubleExtra("latitude", 0.0);   //Data received from the service displayed here
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            float distance = intent.getFloatExtra("distanceToDisplay", 0.0f);
            long time = intent.getLongExtra("timeToDisplay", 0);
            float speed = intent.getFloatExtra("speedToDisplay", 0.0f);
            float avgSpeed = intent.getFloatExtra("avgSpeedToDisplay", 0.0f);
            // Log.d("G53MDP", String.valueOf(speed));

            txtDisplaySpeed.setText("Current Speed: " + df.format(speed) + " m/s");
            txtDisplayAvgSpeed.setText("Average speed: " + df.format(avgSpeed) + " m/s");
            txtDisplayDistance.setText("Distance Travelled: " + df.format(distance) + " km");
            txtDisplayTime.setText("Time Taken: " + String.valueOf(time));

            LatLng myLatLng = new LatLng(latitude, longitude);  // Bundling latitude and longitude into a location, and drawing onto the map at the co-ordinates.
            myLines.add(myLatLng);
            mMap.clear();
            mMap.addPolyline(myLines);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18));
            mMap.addMarker(new MarkerOptions().position(myLatLng).title("You are here!"));
            Log.i("g53mdp", "location updated");
        }
    };

    public void stopTracking(View V) {
        Intent moveBackToMain = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, moveBackToMain);
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Please stop tracking to return to Main Menu", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.example.zlat.myapplication.UPDATE_MARKER");
        registerReceiver(mapReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the broadcast receiver
        unregisterReceiver(mapReceiver);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
