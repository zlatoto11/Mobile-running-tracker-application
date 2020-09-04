package com.example.zlat.myapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DatabaseContentProvider extends ContentProvider {
    private DBHelper dbHelper = null;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DatabaseContentProviderContract.AUTHORITY, "database_table", 1); //in case of need of multiple tables
        uriMatcher.addURI(DatabaseContentProviderContract.AUTHORITY, "database_table/#", 2);
        uriMatcher.addURI(DatabaseContentProviderContract.AUTHORITY, "location_table", 2);
        uriMatcher.addURI(DatabaseContentProviderContract.AUTHORITY, "location_table/#", 2);
    }
    @Override
    public boolean onCreate() {
        Log.d("g53mdp", "contentprovider oncreate");
        this.dbHelper = new DBHelper(this.getContext(), "coursework4.db", null, 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {
        String tableName = null;
        switch (uriMatcher.match(uri)) {    //used for multiple tables , in case of need for expansions
            case 1:
                tableName = "database_table";
                break;
            case 2:
                tableName = "location_table";
                break;
        }
        return dbHelper.getReadableDatabase().query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = null;
        switch (uriMatcher.match(uri)) {    //used for multiple tables , in case of need for expansions
            case 1:
                tableName = "database_table";
                break;
            case 2:
                tableName = "location_table";
                break;
        }

        long id = db.insert(tableName, null, values);
        Uri nu = ContentUris.withAppendedId(uri, id);
        Log.d("g53mdp", nu.toString());
        return nu;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = null;
        switch (uriMatcher.match(uri)) {    //used for multiple tables , inc ase of need for expansions
            case 1:
                tableName = "database_table";
                break;
            case 2:
                tableName = "location_table";
                break;
        }
        return dbHelper.getWritableDatabase().delete(tableName,selection,selectionArgs); //Todo: "CHANGE NAMES WHEN FIGURED OUT"
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]
            selectionArgs)  {
        String tableName = null;
        switch (uriMatcher.match(uri)) {    //used for multiple tables , incase of need for expansions
            case 1:
                tableName = "database_table";
                break;
            case 2:
                tableName = "location_table";
                break;
        }
        return dbHelper.getWritableDatabase().update(tableName, values, selection,selectionArgs); //Todo: "CHANGE NAMES WHEN FIGURED OUT"
    }
}
