package com.example.thomdejong.locationtracker;

import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    private LocationManager locationManager;
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

    private Runnable updateVisualOnUIThread;

    private Timer timer;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] mGravity;
    private float[] mGeomagnetic = new float[9];

    private float magneticRotation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        textView = (TextView)findViewById(R.id.textViewDistance);
        imageView = (ImageView) findViewById(R.id.Compassneedle);

        initializeActionBar();

        createLocationListener();

        String coordinateName = getIntent().getExtras().getString("coordinatename");
        CoordinateData coordinateData = CoordinatesListActivity.getLocationData(coordinateName);
        targetLocation = coordinateData.Location;
        startLocationListener();

        ((TextView) findViewById(R.id.LocationTextView)).setText(coordinateName);

        startVisualTimer();
        initalizeVisualUICodeBlock();
        updateCompassVisual();


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    private void createLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null) {
                    currentLocation = location;
                    targetRotation = location.bearingTo(targetLocation);
                }
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
        currentLocation = new Location("empty");
    }

    private void initalizeVisualUICodeBlock(){
        updateVisualOnUIThread = new Runnable() {
            @Override
            public void run() {
                float finalTargetRotation = targetRotation - magneticRotation;

                if(Math.abs(finalTargetRotation - currentRotation) > 180) {
                    if(finalTargetRotation > currentRotation) {
                        finalTargetRotation -= 360;
                    }else{
                        finalTargetRotation += 360;
                    }
                }

                if(Math.abs(currentRotation) > 360){
                    currentRotation = (currentRotation + 360) % 360;
                }

                float maxRotation = Math.min(2.5f, speed * 1.11f + 0.01f);
                float deltaRotation = clamp((finalTargetRotation - currentRotation) / 35f, -maxRotation, maxRotation);
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
        };
    }

    private void startVisualTimer() {
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
    }

    private void initializeActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void startLocationListener(){
        int res = checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, locationListener);
    }

    private int dpToPx(int dp)
    {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    private  void initialImageMatrixSetup(int dpX, int dpY, ImageView view){
// Get the ImageView and its bitmap
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
                public void run() {initialImageMatrixSetup(250, 250, (ImageView) findViewById(R.id.Compassneedle));;
                }
            });
        }

        runOnUiThread(updateVisualOnUIThread);
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
        mSensorManager.unregisterListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationListener();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
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

    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                magneticRotation = (float)Math.toDegrees(orientation[0]);
            }
        }
    }

}
