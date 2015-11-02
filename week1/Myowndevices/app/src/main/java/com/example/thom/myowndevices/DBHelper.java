package com.example.thom.myowndevices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.thom.myowndevices.Device;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thom de Jong on 10/09/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Devicesdb";
    private static final String TABLE_NAME = "Devices";

    private static final int DATABASE_VERSION = 1;

    private static final String DEVICE_ID = "id";
    private static final String DEVICE_NAME = "name";
    private static final String DEVICE_TYPE = "type";

    private static final String CREATE_TABLES = "CREATE TABLE "
            + TABLE_NAME + "("
            + DEVICE_ID + " INTEGER PRIMARY KEY,"
            + DEVICE_NAME + " VARCHAR,"
            + DEVICE_TYPE + " VARCHAR" + ")";

    private static  final String GET_ALL_DEVICES = "SELECT * FROM " + TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // create new tables
        onCreate(db);
    }

    public void insertDevice(Device data){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEVICE_TYPE, data.Type);
        contentValues.put(DEVICE_NAME, data.Name);

        Long id = this.getWritableDatabase().insert(TABLE_NAME, null, contentValues);
        Log.i("deviceid",Long.toString(id));
    }

    public ArrayList<Device> getDevices(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(GET_ALL_DEVICES, null);
        ArrayList<Device> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                Device data = new Device();
                data.Id = c.getLong(c.getColumnIndex(DEVICE_ID));
                data.Name = c.getString(c.getColumnIndex(DEVICE_NAME));
                data.Type = c.getString(c.getColumnIndex(DEVICE_TYPE));
                result.add(data);
            }while (c.moveToNext());
        }
        return result;
    }

    public void deleteDevice(Device data){
        String delete = "DELETE FROM " + TABLE_NAME +" WHERE " + DEVICE_ID + " = '" + data.Id +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(delete);
    }

}
