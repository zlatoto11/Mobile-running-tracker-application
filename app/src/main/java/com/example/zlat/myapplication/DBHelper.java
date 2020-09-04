package com.example.zlat.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_DISTANCE;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_ID;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_LATITUDE;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_LOCATION_ID;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_LONGITUDE;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_NAME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_SPEED;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_TIME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_AVG_SPEED;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_TYPE;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.LOCATION_TABLE_NAME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.TABLE_NAME;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "coursework4.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DATABASE_TABLE = "CREATE TABLE "
                + TABLE_NAME + "( "
                + GPS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GPS_NAME + " VARCHAR(64), "
                + GPS_TYPE + " TEXT, "
                + GPS_SPEED + " DOUBLE, "
                + GPS_DISTANCE + " FLOAT, "
                + GPS_AVG_SPEED + " DOUBLE, "
                + GPS_TIME + " INT(64) " + ")";

        db.execSQL(SQL_CREATE_DATABASE_TABLE);

//        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "  //Currently unused, kept in case of expanding the app.
//                + LOCATION_TABLE_NAME + "( "
//                + GPS_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + GPS_LATITUDE + " DOUBLE, "
//                + GPS_LONGITUDE + " DOUBLE )";
//
//        db.execSQL(SQL_CREATE_LOCATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
       // db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        onCreate(db);
    }
}
