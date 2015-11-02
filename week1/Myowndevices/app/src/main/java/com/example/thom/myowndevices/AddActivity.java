package com.example.thom.myowndevices;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by thomd on 29-10-2015.
 */
public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((TextView) findViewById(R.id.name)).getText().toString();
                String type = ((TextView) findViewById(R.id.type)).getText().toString();

                //checks if fields are not empty
                if(name.length() < 1 || type.length() < 1){
                    Snackbar.make(view, "Enter name and type", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                // get helper and add device to db then finish activity
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                Device device = new Device();

                device.Name = name;
                device.Type = type;

                dbHelper.insertDevice(device);

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // No menu is required so no inflation is required

        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
