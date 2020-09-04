package com.example.zlat.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_DISTANCE;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_ID;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_NAME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_SPEED;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_TIME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_AVG_SPEED;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_TYPE;

public class MainActivity extends AppCompatActivity {

    private GPSService.MyBinder gpsService = null;
    ServiceConnection serviceConnection = null;
    Intent startInt;
    static final int ACTIVITY_MAPS_ACTIVITY = 1;
    Button btnstartRun,btnDistance,btnTime;
    SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnstartRun = findViewById(R.id.buttonStartTracking);
        startInt = new Intent(this, GPSService.class);
        btnDistance = findViewById(R.id.btnSortDistance);
        btnTime = findViewById(R.id.btnSortTime);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        displayRun();

        final ListView listView = findViewById(R.id.runListView);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {     //Receives ID and sends it off to viewRunActivity.
                TextView tv = view.findViewById(R.id.textView_ID);        //Used to target specific data to display.

                Intent intent = new Intent(MainActivity.this,viewRunActivity.class);
                Bundle dataToSend = new Bundle();

                dataToSend.putInt("id", Integer.valueOf(tv.getText().toString()));

                intent.putExtras(dataToSend);
                startActivity(intent);

            }
        });
    }

    public void displayRun() {       //refreshes screen and updates listView
        String[] projection = new String[] {
                GPS_ID,
                GPS_NAME,
                GPS_TYPE,
                GPS_SPEED,
                GPS_DISTANCE,
                GPS_AVG_SPEED,
                GPS_TIME
        };
        String[] columnsToDisplay = new String[]{
                GPS_ID,
                GPS_NAME,
                GPS_TYPE,
                GPS_DISTANCE,
                GPS_TIME
        };
        int[] displayTo = new int[]{
                R.id.textView_ID,
                R.id.textView_Name,
                R.id.textView_Type,
                R.id.textViewDistance,
                R.id.textViewTopSpeed,
        };

        Cursor cursor = getContentResolver().query(DatabaseContentProviderContract.gpsTable_URI, projection, null, null, null);

        dataAdapter = new SimpleCursorAdapter(this, R.layout.row_layout,
                cursor,
                columnsToDisplay,
                displayTo,
                0);

        ListView listView = (ListView) findViewById(R.id.runListView);
        listView.setAdapter(dataAdapter);
    }

    public void sortByDistance(View V) { // sorted by GPS_DISTANCE in descending order by adding to sortOrder

        String[] projection = new String[] {
                GPS_ID,
                GPS_NAME,
                GPS_TYPE,
                GPS_SPEED,
                GPS_DISTANCE,
                GPS_AVG_SPEED,
                GPS_TIME
        };
        String[] columnsToDisplay = new String[]{
                GPS_ID,
                GPS_NAME,
                GPS_TYPE,
                GPS_DISTANCE,
                GPS_TIME
        };
        int[] displayTo = new int[]{
                R.id.textView_ID,
                R.id.textView_Name,
                R.id.textView_Type,
                R.id.textViewDistance,
                R.id.textViewTopSpeed,
        };

        Cursor cursor = getContentResolver().query(DatabaseContentProviderContract.gpsTable_URI, projection, null, null, GPS_DISTANCE + " DESC");   //sort by title ascending

        dataAdapter = new SimpleCursorAdapter(this, R.layout.row_layout,
                cursor,
                columnsToDisplay,
                displayTo,
                0);

        ListView listView = (ListView) findViewById(R.id.runListView);
        listView.setAdapter(dataAdapter);
    }

    public void sortByTime(View V) { // sorted by GPS_TIME in descending order by adding to sortOrder

        String[] projection = new String[] {
                GPS_ID,
                GPS_NAME,
                GPS_TYPE,
                GPS_SPEED,
                GPS_DISTANCE,
                GPS_AVG_SPEED,
                GPS_TIME
        };
        String[] columnsToDisplay = new String[]{
                GPS_ID,
                GPS_NAME,
                GPS_TYPE,
                GPS_DISTANCE,
                GPS_TIME
        };
        int[] displayTo = new int[]{
                R.id.textView_ID,
                R.id.textView_Name,
                R.id.textView_Type,
                R.id.textViewDistance,
                R.id.textViewTopSpeed,
        };

        Cursor cursor = getContentResolver().query(DatabaseContentProviderContract.gpsTable_URI, projection, null, null, GPS_TIME + " DESC");   //sort by title ascending

        dataAdapter = new SimpleCursorAdapter(this, R.layout.row_layout,
                cursor,
                columnsToDisplay,
                displayTo,
                0);

        ListView listView = (ListView) findViewById(R.id.runListView);
        listView.setAdapter(dataAdapter);
    }

    public void startTracking(View V) {

        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder
                        service) {
                    Log.d("g53mdp", "MainActivity onServiceConnected");
                    gpsService = (GPSService.MyBinder) service;
                    gpsService.startNotification();
                }
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.d("g53mdp", "MainActivity onServiceDisconnected");
                    Log.d("g53mdp", "MainActivity onServiceDisconnected");
                    gpsService = null;
                }
            };
            this.bindService(startInt,
                    serviceConnection, Context.BIND_AUTO_CREATE);
        }

        Intent movetoMaps = new Intent(this,MapsActivity.class);
        startActivityForResult(movetoMaps, ACTIVITY_MAPS_ACTIVITY);
        Toast.makeText(getApplicationContext(), "The recording has been started", Toast.LENGTH_SHORT).show();
        btnstartRun.setEnabled(false);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        if (requestCode == ACTIVITY_MAPS_ACTIVITY){       //if coming back from activity with this request code
            if (resultCode == RESULT_OK){   // And this result code do the following.
                gpsService.saveData();
                displayRun();
                unbindService(serviceConnection);
                stopService(startInt);
                serviceConnection = null;
                Toast.makeText(getApplicationContext(), "The recording has been stopped", Toast.LENGTH_SHORT).show();
                btnstartRun.setEnabled(true);
            }
            else if (resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Left without stopping run", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        Log.d("g53mdp", "MainActivity onDestroy");
    }
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d("g53mdp", "MainActivity onPause");
    }

    @Override
    protected void onResume() {
        displayRun();
        super.onResume();
        Log.d("g53mdp", "MainActivity onResume");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.d("g53mdp", "MainActivity onStart");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.d("g53mdp", "MainActivity onStop");
    }
}
