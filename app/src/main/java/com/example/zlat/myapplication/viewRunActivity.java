package com.example.zlat.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_DISTANCE;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_ID;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_NAME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_SPEED;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_TIME;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_AVG_SPEED;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.GPS_TYPE;
import static com.example.zlat.myapplication.DatabaseContentProviderContract.gpsTable_URI;

public class viewRunActivity extends AppCompatActivity {
    int id;
    TextView listviewDistance, listviewSpeed,listviewTime, listviewAvgSpeed, listviewid;
    Button deletebtn;
    EditText edittextRunName;
    Switch switchToToggle;
    DecimalFormat df;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_run);

        listviewid = findViewById(R.id.listviewRunID);
        listviewDistance = findViewById(R.id.listViewDistance);
        listviewSpeed = findViewById(R.id.listviewSpeed);
        listviewTime = findViewById(R.id.listviewTime);
        listviewAvgSpeed = findViewById(R.id.listviewAvgSpeed);


        deletebtn = findViewById(R.id.btnDelete);
        edittextRunName = findViewById(R.id.edittextRunName);
        switchToToggle = findViewById(R.id.runOrWalkSwitch);


        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            id = bun.getInt("id");
            String[] projection = new String[] {
                    GPS_ID,
                    GPS_NAME,
                    GPS_TYPE,
                    GPS_SPEED,
                    GPS_DISTANCE,
                    GPS_AVG_SPEED,
                    GPS_TIME

            };

            Cursor c = getContentResolver().query(gpsTable_URI,projection ,GPS_ID + " = " + id,null,null);

            if (c.moveToFirst()) {

                edittextRunName.setText(c.getString(1));
                String run = "Run";
                if (c.getString(2).equals(run)) {
                    switchToToggle.setChecked(true);
                    switchToToggle.setText("Run");
                }
                listviewid.setText("Run ID: " + c.getString(0));
                listviewSpeed.setText("End of Run Speed: " + df.format(c.getFloat(3)) + " m/s");
                listviewDistance.setText("Total Distance run: " + df.format(c.getFloat(4)) + " km");
                listviewAvgSpeed.setText("Average Speed For The Run: " + df.format(c.getFloat(5)) + " m/s");
                listviewTime.setText("Total Time Taken: " + c.getString(6) + " seconds");
            }
        }
        }

    public void toggleSwitch(View V){
        if (switchToToggle.isChecked()) {
            switchToToggle.setText("Run");
        } else{
            switchToToggle.setText("Walk");
        }
    }

    public void updateRun(View V) {

        String recipeRunName = edittextRunName.getText().toString();
        if (recipeRunName.length() < 64) {       //ensures user inputs between 1 and 5
            ContentValues newValues = new ContentValues();
            newValues.put(DatabaseContentProviderContract.GPS_NAME, recipeRunName);
            newValues.put(DatabaseContentProviderContract.GPS_TYPE, switchToToggle.getText().toString());
            getContentResolver().update(DatabaseContentProviderContract.gpsTable_URI, newValues, "_id = " + id, null);
            Intent intent = new Intent(viewRunActivity.this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(viewRunActivity.this, "Run name can only be up to 64 characters in length", Toast.LENGTH_SHORT).show();      // tell user to put number between 1-5
        }
    }

    public void deleteButton(View V){
        Bundle bun = getIntent().getExtras();
        int id = bun.getInt("id");
        getContentResolver().delete(DatabaseContentProviderContract.gpsTable_URI, "_id =" + id, null);
        finish();
    }
}