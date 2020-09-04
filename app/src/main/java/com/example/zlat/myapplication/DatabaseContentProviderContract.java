package com.example.zlat.myapplication;

import android.net.Uri;

public class DatabaseContentProviderContract {
    public static final String TABLE_NAME = "database_table";
    public static final String LOCATION_TABLE_NAME = "location_table";


    //Storing all table column name values in one place.
    public static final String GPS_NAME = "gps_name";
    public static final String GPS_TYPE = "gps_type";
    public static final String GPS_DISTANCE = "gps_distance";
    public static final String GPS_AVG_SPEED = "gps_avg_speed";
    public static final String GPS_SPEED = "gps_speed";
    public static final String GPS_ID = "_id";
    public static final String GPS_TIME = "gps_time";

    public static final String GPS_LOCATION_ID = "_id";
    public static final String GPS_LONGITUDE = "gps_longitude";
    public static final String GPS_LATITUDE = "gps_latitude";

    //Directing Authority over application.
    public static final String AUTHORITY = "com.example.zlat.myapplication.DatabaseContentProvider";

    //URI's for the different tables of the database.
    public static final Uri gpsTable_URI = Uri.parse("content://"+AUTHORITY+"/database_table");
    public static final Uri locationTable_URI = Uri.parse("content://"+AUTHORITY+"/location_table");
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");
}
