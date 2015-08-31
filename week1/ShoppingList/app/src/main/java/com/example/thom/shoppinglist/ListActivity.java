package com.example.thom.shoppinglist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> items;
    private EditText addItemEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        addItemEditText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.listView);

        items = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        items.add("Google");
        items.add("Apple");

        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String) parent.getItemAtPosition(position);
                Toast.makeText(ListActivity.this, "Clicked: " + clickedItem, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_bar_menu_delete_all) {
            items.clear();
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        //Cast menuinfo into adapter menuinfo
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        String clickedItemName = (String)listView.getItemAtPosition(info.position);

        //Add options from the resource file
        getMenuInflater().inflate(R.menu.context_menu, menu);

        //find the menu
        MenuItem deleteButton = menu.findItem(R.id.context_menu_delete_item);

        //get original delete button title
        String originalTitle = deleteButton.getTitle().toString();

        //Make a new title combining the original title and the name of the clicked list item
        deleteButton.setTitle(originalTitle + " '" + clickedItemName + "'?");

        super.onCreateContextMenu(menu, view, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete_item) {
            AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            items.remove(itemInfo.position);
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    public  void addListItem(View view){
        //Get the user text from the textfield
        String text = addItemEditText.getText().toString();

        //Check if some text has been added
        if (!(TextUtils.isEmpty(text))) {
            //Add the text to the adapter
            items.add(text);

            //Notify the adapter that the action_bar_menu of data has changed and the view should be updated
            adapter.notifyDataSetChanged();

            //Clear the EditText for the next item
            addItemEditText.setText("");
        } else {
            //Show a message to the user if the textfield is empty
            Toast.makeText(ListActivity.this, "Please enter some text in the textfield", Toast.LENGTH_LONG).show();
        }

    }

}
