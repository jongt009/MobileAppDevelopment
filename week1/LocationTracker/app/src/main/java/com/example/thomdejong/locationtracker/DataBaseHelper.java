package com.example.thomdejong.locationtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thom de Jong on 10/09/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LocationTrackerDatabase";
    private static final String TABLE_NAME = "Locations";

    private static final int DATABASE_VERSION = 2;

    private static final String LOCATIONS_NAME = "Name";
    private static final String LOCATIONS_LONGTITUDE = "Longtitude";
    private static final String LOCATIONS_LATITUDE = "Lenghtitude";

    private static final String CREATE_TABLES = "CREATE TABLE "
            + TABLE_NAME + "("
            + LOCATIONS_NAME + " VARCHAR PRIMARY KEY,"
            + LOCATIONS_LONGTITUDE + " VARCHAR,"
            + LOCATIONS_LATITUDE + " VARCHAR" + ")";

    private static  final String GET_ALL_LOCATIONS = "SELECT * FROM " + TABLE_NAME;

    public DataBaseHelper(Context context) {
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

    public void insertLocationToDatabase(CoordinateData data){
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATIONS_NAME, data.Name);
        contentValues.put(LOCATIONS_LATITUDE, data.Location.getLatitude());
        contentValues.put(LOCATIONS_LONGTITUDE, data.Location.getLongitude());

        this.getWritableDatabase().insert(TABLE_NAME, null, contentValues);
    }

    public List<CoordinateData> getAllLocationsFromDatabase(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(GET_ALL_LOCATIONS, null);
        ArrayList<CoordinateData> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                CoordinateData data = new CoordinateData();
                data.Name = c.getString(c.getColumnIndex(LOCATIONS_NAME));
                data.Location.setLongitude(Double.parseDouble(c.getString(c.getColumnIndex(LOCATIONS_LONGTITUDE))));
                data.Location.setLatitude(Double.parseDouble(c.getString(c.getColumnIndex(LOCATIONS_LATITUDE))));
                result.add(data);
            }while (c.moveToNext());
        }
        return result;
    }

    public void updateLocation(String originalName, CoordinateData data){

    }

    public void deleteLocation(String name){
        String delete = "DELETE FROM " + TABLE_NAME +" WHERE " + LOCATIONS_NAME + " = '" + name +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(delete);
    }

}
