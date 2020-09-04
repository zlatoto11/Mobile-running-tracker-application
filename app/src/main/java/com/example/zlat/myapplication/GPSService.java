package com.example.zlat.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_DISTANCE;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_NAME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_SPEED;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_TIME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_AVG_SPEED;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_TYPE;

public class GPSService extends Service {
    private final IBinder binder = new MyBinder();
    Location startingLocation = null;
    Location lastLocation = null;
    int locationsChanged = 0;
    long startTime, currentTime, totalTime, newTime;
    float totalDistance = 0, speed, totalSpeed = 0, avgSpeed = 0;
    LocationManager locationManager;
    protected MyLocationListener myLocationListener = new MyLocationListener();
    private ContentResolver contentResolver;
    //GPSReceiver gps;
    BroadcastReceiver gps;
    String nameToSet = "Please Set A Name";
    String walk = "Walk";
    private final String CHANNEL_ID = "100";
    int NOTIFICATION_ID = 001;

    @Override
    public void onCreate() {
        super.onCreate();

        contentResolver = this.getContentResolver();
        gps = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.zlat.myapplication.UPDATE_MARKER");
        registerReceiver(gps,filter);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, // minimum time interval between updates
                    1, // minimum distance between updates, in metres
                    myLocationListener);
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }
    public class MyBinder extends Binder
    {
        public void saveData(){ // Allows the main activity to call this function to save data into the database.
            ContentValues addToDatabase = new ContentValues();
            addToDatabase.put(GPS_TIME,totalTime);
            addToDatabase.put(GPS_TYPE, walk);
            addToDatabase.put(GPS_DISTANCE,totalDistance);
            addToDatabase.put(GPS_SPEED,speed);
            addToDatabase.put(GPS_AVG_SPEED,avgSpeed);
            addToDatabase.put(GPS_NAME,nameToSet);
            contentResolver.insert(DatabaseContentProviderContract.gpsTable_URI,addToDatabase);
           // Log.d("g53mdp","INSIDE SAVE DATA");
        }

        public void startNotification(){
            createNotification();
        }

    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();   //Retrieve information to display and update marker and packages it into an intent
            double longitude = location.getLongitude();//which is broadcasted and received by the map activity to use.

            Intent updateMarkerIntent= new Intent();
            updateMarkerIntent.setAction("com.example.zlat.myapplication.UPDATE_MARKER");
            updateMarkerIntent.putExtra("latitude",latitude);
            updateMarkerIntent.putExtra("longitude",longitude);
            updateMarkerIntent.putExtra("distanceToDisplay",totalDistance);
            updateMarkerIntent.putExtra("timeToDisplay",totalTime);
            updateMarkerIntent.putExtra("speedToDisplay",speed);
            updateMarkerIntent.putExtra("avgSpeedToDisplay",avgSpeed);
            sendBroadcast(updateMarkerIntent);

//            ContentValues thingsToAddLocation = new ContentValues();  //Old code to continously update database with each location
//            thingsToAddLocation.put(GPS_LATITUDE,latitude);           //can be used to improve the app by drawing a map based on all location data after the run.
//            thingsToAddLocation.put(GPS_LONGITUDE,longitude);
//            contentResolver.insert(DatabaseContentProviderContract.locationTable_URI, thingsToAddLocation);

            if(lastLocation == null){   //If no location initialise locations to self and time to current.
                startingLocation = location;
                lastLocation = location;
                startTime = location.getTime();
                currentTime = startTime;
                speed = 0;
            }
            else{   // +1 to locations changed used for calculating averages.
                locationsChanged ++;
                float distanceToLast = location.distanceTo(lastLocation)/1000; // retrieving distance between locations in order to calculate total distance
                totalDistance = totalDistance + distanceToLast; //continually incremented to grab total
                speed = (lastLocation.distanceTo(location)) / ((location.getTime() - lastLocation.getTime())/1000); //speed = distance/time
                totalSpeed = totalSpeed + speed;    //total speed for calculating avg speed
                avgSpeed = totalSpeed/locationsChanged; //avg speed = totalSpeed/locations changed

                lastLocation=location;// last location set to current after calculation done

                newTime = currentTime;
                currentTime = location.getTime();
                totalTime =(currentTime - startTime)/1000;  // get in seconds. Time calculated by subtracting current epoch time away from start epoch time.
                // then divide by 1000 to get it in seconds from miliseconds.
                Log.d("g53mdp","Distance to = " +distanceToLast + " Time: " +totalTime);
            }
            Log.d("g53mdp", location.getLatitude() + " " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // information about the signal, i.e. number of satellites
            Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            // the user enabled (for example) the GPS
            Log.d("g53mdp", "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // the user disabled (for example) the GPS
            Log.d("g53mdp", "onProviderDisabled: " + provider);
        }
    }

    private void createNotification() {

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Running application is tracking";
            String description = "Click here to return to application.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);

            Intent intent = new Intent(this, MapsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            PendingIntent resultingActivityPendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT ); //Changed Flag code of activity as previous one would destroy main which
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,                        //caused main activity to be destroyed and service stopped. Now instead
                    CHANNEL_ID)                                                                                     //will just resume current activity without re-creating it. (FLAG_UPDATE_CURRENT)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Running application is tracking")
                    .setContentText("Click here to return to application")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            startForeground(NOTIFICATION_ID, mBuilder.build());
        }
      //  Log.d("test","Inside Notification Creator");
    }

    @Override
    public void onDestroy() {       //Stops broadcast service after run is finished.
        locationManager.removeUpdates(myLocationListener);
        unregisterReceiver(gps);
        super.onDestroy();
    }
}
