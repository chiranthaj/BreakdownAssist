package lk.steps.breakdownassist.GpsTracker;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import lk.steps.breakdownassist.DBHandler;
import lk.steps.breakdownassist.Globals;


/**
 * Created by JagathPrasanga on 2017-09-23.
 */

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";

    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private String defaultUploadWebsite= "1.1.1.1/fgcfg";

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        currentlyProcessingLocation= false;
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }
        Log.e(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    protected void SaveLocation(Location location) {
        // formatted for mysql datetime format
      //  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      //  Globals.timeFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(location.getTime());
        String timestamp = Globals.timeFormat.format(date);

        SharedPreferences sharedPreferences = this.getSharedPreferences("GPSTRACKER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        float totalDistanceInMeters = sharedPreferences.getFloat("totalDistanceInMeters", 0f);

        boolean firstTimeGettingPosition = sharedPreferences.getBoolean("firstTimeGettingPosition", true);
        float distance = 0;
        if (firstTimeGettingPosition) {
            editor.putBoolean("firstTimeGettingPosition", false);
        } else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(sharedPreferences.getFloat("previousLatitude", 0f));
            previousLocation.setLongitude(sharedPreferences.getFloat("previousLongitude", 0f));

            Location currentLocation = new Location("");
            currentLocation.setLatitude((float)location.getLatitude());
            currentLocation.setLongitude((float)location.getLongitude());

            distance = currentLocation.distanceTo(previousLocation);
            totalDistanceInMeters += distance;
            editor.putFloat("totalDistanceInMeters", totalDistanceInMeters);
        }

        editor.putFloat("previousLatitude", (float)location.getLatitude());
        editor.putFloat("previousLongitude", (float)location.getLongitude());
        editor.apply();


        String distanceTxt ="0.0";
        if (totalDistanceInMeters > 0) {
            distanceTxt= String.format("%.1f", totalDistanceInMeters);
        } else {
            distanceTxt= "0.0";
        }

        String latTxt = Double.toString(location.getLatitude());
        String lonTxt = Double.toString(location.getLongitude());
        String speedTxt = Double.toString(location.getSpeed());
        String accuracyTxt = String.format("%.1f",location.getAccuracy());
        String altitudeTxt = Double.toString(location.getAltitude());
        String directionTxt = Double.toString(location.getBearing());

        Log.d(TAG, "GPSTEST accu="+accuracyTxt+"lat="+latTxt+", lon="+lonTxt+", speed="+speedTxt+",distance="+distance);
        //Log.d(TAG, "longitude="+lonTxt);
        //Log.d(TAG, "speed="+speedTxt);
        //Log.d(TAG, "distance="+distance);
        if(location.getAccuracy() < 50.0f){
            Toast.makeText(this, "GPS Tracker accu="+accuracyTxt+" lat="+latTxt+", lon="+lonTxt+", speed="+speedTxt+",distance="+distance, Toast.LENGTH_LONG).show();
            DBHandler dbHandler = new DBHandler(this, null, null, 1);
            dbHandler.addTrackPoint(timestamp,latTxt,lonTxt,speedTxt,accuracyTxt,altitudeTxt,directionTxt,distanceTxt);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (location.getAccuracy() < 500.0f) {
                stopLocationUpdates();
                SaveLocation(location);
            }
        }else{
            Log.e(TAG, "position: null");
        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(googleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }
}
