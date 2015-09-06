package com.example.thomdejong.locationtracker;

import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddLocationActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText editText;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlocation);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        editText = (EditText)findViewById(R.id.editableLocationName);
        saveButton = (Button)findViewById(R.id.saveChangesButton);

        int res = checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location bestLocation = new Location("none");
        for(String providerName: locationManager.getProviders(true)){
            Location location = locationManager.getLastKnownLocation(providerName);
            if(location == null) continue;
            if(bestLocation.getAccuracy() < location.getAccuracy()){
                bestLocation = location;
            }
        }
        location = bestLocation;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CoordinatesListActivity.addLocationData(editText.getText().toString(), location)){
                    Toast.makeText(getApplicationContext(), "Location '"+ editText.getText().toString() + "' added", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Location '"+ editText.getText().toString() + "' already exists", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addlocation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                super.onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
