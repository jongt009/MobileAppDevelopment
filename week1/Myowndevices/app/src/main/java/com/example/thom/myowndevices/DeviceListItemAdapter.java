package com.example.thom.myowndevices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by thomd on 29-10-2015.
 */
public class DeviceListItemAdapter extends BaseAdapter {

    private ArrayList<Device> deviceArrayList;
    private Context context;
    private LayoutInflater inflater;

    public DeviceListItemAdapter(ArrayList<Device> list, Context context){
        this.deviceArrayList = list;
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return deviceArrayList.size();
    }

    @Override
    public Device getItem(int position) {
        return deviceArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deviceArrayList.get(position).Id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        //Check if the row is new
        if (row == null) {
            //Inflate the layout if it didn't exist yet
            row = inflater.inflate(R.layout.single_device_item, parent, false);

            //Create a new view holder instance
            holder = new ViewHolder(row);

            //set the holder as a tag so we can get it back later
            row.setTag(holder);
        } else {
            //The row isn't new so we can reuse the view holder
            holder = (ViewHolder) row.getTag();
        }

        //Populate the row
        holder.populateRow(getItem(position));

        return row;
    }

    class ViewHolder{
        private TextView name;
        private TextView type;

        //initialize the variables
        public ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.deviceName);
            type = (TextView) view.findViewById(R.id.deviceType);
        }

        public void populateRow(Device device){
            name.setText(device.Name);
            type.setText(device.Type);
        }

    }
}

