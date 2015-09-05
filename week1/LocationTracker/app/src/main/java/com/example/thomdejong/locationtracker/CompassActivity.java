package com.example.thomdejong.locationtracker;

import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class CompassActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private String bestProvider;
    private LocationListener locationListener;
    private Location currentLocation;
    private Location targetLocation;
    private TextView textView;

    private ImageView imageView;
    private float currentRotation;
    private float targetRotation;
    private Matrix rotationMatrix;
    private float xPivot, yPivot;
    private float speed;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        textView = (TextView)findViewById(R.id.textViewDistance);
        imageView = (ImageView) findViewById(R.id.Compassneedle);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        String coordinateName = getIntent().getExtras().getString("coordinatename");
        CoordinateData coordinateData = CoordinatesListActivity.getLocationData(coordinateName);
        targetLocation = coordinateData.Location;
        Log.i("Location", targetLocation.toString());
        int res = checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res == PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "granted");
            startLocationListener();
        } else {
            Log.i("permission", "denied");
        }
        targetRotation = 0;
        currentRotation = 0;
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateCompassVisual();
                }
                // Call first after 0.5 seconds and 30x per second
            }, 500, 33);
        }
        updateCompassVisual();

    }

    private void startLocationListener(){
        int res = checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

            if(locationListener == null){
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLocation = location;
                    targetRotation = location.bearingTo(targetLocation);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    stopLocationListener();
                    startLocationListener();
                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }

        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);

        locationListener.onLocationChanged(locationManager.getLastKnownLocation(bestProvider));

        locationManager.requestLocationUpdates(bestProvider, 0,0.1f, locationListener);
    }

    private int dpToPx(int dp)
    {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    private  void initialImageMatrixSetup(int dpX, int dpY){
// Get the ImageView and its bitmap
        ImageView view = (ImageView) findViewById(R.id.Compassneedle);
        Drawable drawing = view.getDrawable();
        view.setScaleType(ImageView.ScaleType.MATRIX);
        if (drawing == null) {
            return; // Checking for null & return, as suggested in comments
        }
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int xBounding = dpToPx(dpX);
        int yBounding = dpToPx(dpY);


        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) xBounding) / width;
        float yScale = ((float) yBounding) / height;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable result = new BitmapDrawable(getResources(),scaledBitmap);

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        rotationMatrix = new Matrix();
        xPivot = scaledBitmap.getWidth() / 2;
        yPivot = scaledBitmap.getHeight() /2;
        rotationMatrix.postRotate(90, xPivot, yPivot);

        view = null;
        drawing = null;
        scaledBitmap = null;
        result = null;
    }
    public void updateCompassVisual(){
        if(rotationMatrix == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initialImageMatrixSetup(250, 250);
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(Math.abs(targetRotation - currentRotation) > 180) {
                    if(targetRotation > currentRotation) {
                        targetRotation -= 360;
                    }else{
                        targetRotation += 360;
                    }
                }

                float maxRotation = Math.min(2f, speed * 1.15f + 0.01f);
                float deltaRotation = clamp((targetRotation - currentRotation) / 35f, -maxRotation, maxRotation);
                speed = Math.abs(deltaRotation);
                currentRotation += deltaRotation;

                rotationMatrix.postRotate(deltaRotation, xPivot, yPivot);
                imageView.setImageMatrix(rotationMatrix);

                float distance = currentLocation.distanceTo(targetLocation);
                String distanceString;

                if(distance < 100){
                    distanceString = String.format("%.0f",distance) + "m";
                }else{
                    distance /= 1000;
                    distanceString = String.format("%.1f",distance) + "km";
                }
                textView.setText(distanceString);
            }
        });


    }

    private float clamp(float value, float min, float max){
        value = Math.max(min, value);
        value = Math.min(max, value);
        return value;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationListener();
    }

    private void stopLocationListener(){
        int res = checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res == PackageManager.PERMISSION_GRANTED) {
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        timer.purge();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compass, menu);
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
            stopLocationListener();
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
