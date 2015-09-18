package com.example.thomdejong.locationtracker;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
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

public class CoordinatesListActivity extends AppCompatActivity{

    private static ArrayAdapter arrayAdapter;
    private static List<String> itemNames;
    private static Map<String, CoordinateData> data = new HashMap<String, CoordinateData>();
    private static DataBaseHelper dbHelper;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates_list);

        itemNames = new ArrayList<>();
        listView = (ListView)findViewById(R.id.CoordinatesListView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, itemNames);

        listView.setAdapter(arrayAdapter);

        Location location = new Location("kjhgkjh");
        location.setLatitude(10);
        location.setLongitude(100);

        registerForContextMenu(listView);

        dbHelper = new DataBaseHelper(getApplicationContext());


        for(CoordinateData data: dbHelper.getAllLocationsFromDatabase()){
            addLocationData(data.Name, data.Location, false);
        }

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

    public static boolean addLocationData(String key, Location location, boolean addToDatabase) {
        if (data.containsKey(key)) {
            return false;
        }
        CoordinateData newData = new CoordinateData();
        newData.Name = key;
        newData.Location = location;

        data.put(key, newData);
        itemNames.add(key);
        arrayAdapter.notifyDataSetChanged();
        if (addToDatabase) {
            dbHelper.insertLocationToDatabase(newData);
        }
        return true;
    }

    public static boolean removeLocationData(String key){
        if(!data.containsKey(key)){
            return false;
        }
        itemNames.remove(key);
        data.remove(key);
        dbHelper.deleteLocation(key);
        arrayAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        //Cast menuinfo into adapter menuinfo
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        String clickedItemName = (String)listView.getItemAtPosition(info.position);

        //Add options from the resource file
        getMenuInflater().inflate(R.menu.context_menu_coordinates_list, menu);

        //find the menu
        MenuItem editButton = menu.findItem(R.id.context_menu_edit_item);
        MenuItem deleteButton = menu.findItem(R.id.context_menu_delete_item);

        //get original delete button title
        String originalTitle = deleteButton.getTitle().toString();

        //Make a new title combining the original title and the name of the clicked list item
        deleteButton.setTitle("Delete" + " '" + clickedItemName + "'?");
        //editButton.setTitle("Edit" + " '" + clickedItemName + "'?");

        editButton.setTitle("NA");

        super.onCreateContextMenu(menu, view, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete_item) {
            AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            removeLocationData(itemNames.get(itemInfo.position));
            return true;
        }

        return super.onContextItemSelected(item);
    }


}
