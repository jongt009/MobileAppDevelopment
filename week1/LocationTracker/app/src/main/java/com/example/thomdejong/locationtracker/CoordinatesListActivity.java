package com.example.thomdejong.locationtracker;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatesListActivity extends AppCompatActivity {

    private static ArrayAdapter arrayAdapter;
    private static List<String> itemNames;
    private static Map<String, CoordinateData> data = new HashMap<String, CoordinateData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates_list);


        itemNames = new ArrayList<>();
        ListView listView = (ListView)findViewById(R.id.CoordinatesListView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, itemNames);

        listView.setAdapter(arrayAdapter);

        Location location = new Location("kjhgkjh");
        location.setLatitude(10);
        location.setLongitude(100);

        addLocationData("asdffadsfasdf", location);
        addLocationData("asdffsadfasfdadsfasdf", new Location("newdata"));



        arrayAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent("com.example.thomdejong.locationtracker.CompassActivity");
                Bundle bundle = new Bundle();
                bundle.putString("coordinatename", arrayAdapter.getItem(position).toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coordinates_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bar_add_location) {
            Intent intent = new Intent("com.example.thomdejong.locationtracker.AddLocationActivity");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static CoordinateData getLocationData(String key){
        return data.get(key);
    }

    public static boolean addLocationData(String key, Location location){
        if(data.containsKey(key)){
            return false;
        }
        CoordinateData newData = new CoordinateData();
        newData.Name = key;
        newData.Location = location;

        data.put(key, newData);
        itemNames.add(key);
        arrayAdapter.notifyDataSetChanged();
        return true;
    }
}
