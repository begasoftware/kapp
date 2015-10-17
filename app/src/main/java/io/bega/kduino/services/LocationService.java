package io.bega.kduino.services;

import java.util.EventListener;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.afollestad.materialdialogs.MaterialDialog;

import io.bega.kduino.R;


public class LocationService {
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public static final int UNKNOWN_LOCATION = -999;
    private static final int LOCATION_THRESHOLD_MILLISECONDS = 0;
    private static final int LOCATION_THRESHOLD_METERS = 0;

    private LocationManager locationManager;
    private Context context;
    private Location currentBestLocation = null;

    private newBestLocationListener mNewBestLocationListener;

    public LocationService( Context context){
        // Local Copy
        this.context = context;

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
    }
    /*
     * Gets if Location is Enabled
     */
    public boolean isLocationEnabled(){
        boolean retorn = false;
        if (locationManager != null){
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                retorn = true;
            }
        }
        return retorn;
    }



    public void displayPromptForEnablingGPS(
            final Activity activity)
    {
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.dialog_search_gps_title)
                .content(R.string.dialog_search_gps_message)
                .positiveColorRes(R.color.colorPrimaryDark)
                .neutralColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .positiveText(R.string.ok_dialog)
                .negativeText(R.string.cancel_dialog)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onAny(MaterialDialog dialog) {
                        super.onAny(dialog);
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        activity.startActivity(new Intent(action));
                        super.onPositive(dialog);
                    }
                })
                .build();
        dialog.show();

    }



    /**
     * Start Listen Location Updates
     */
    public void StartListen(){
        if (locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_THRESHOLD_MILLISECONDS, LOCATION_THRESHOLD_METERS, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_THRESHOLD_MILLISECONDS, LOCATION_THRESHOLD_METERS, locationListener);
        }
    }
    /**
     * Stop Listening Location Updates
     */
    public void StopListen(){
        locationManager.removeUpdates(locationListener);
    }
    /**
     * Gets de Latitude of Last Location
     */
    public double getLatitude(){
        if (currentBestLocation != null) return currentBestLocation.getLatitude();
        else return UNKNOWN_LOCATION;
    }
    /**
     * Gets de Latitude of Last Location
     */
    public double getLongitude(){
        if (currentBestLocation != null) return currentBestLocation.getLongitude();
        else return UNKNOWN_LOCATION;
    }
    /**
     * Gets de Latitude of Last Location
     */
    public float getAccuracy(){
        if (currentBestLocation != null) return currentBestLocation.getAccuracy();
        else return UNKNOWN_LOCATION;
    }
    /**
     * Define a listener that responds to location updates
     */
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (currentBestLocation == null) {
                currentBestLocation = location;
                if (mNewBestLocationListener != null) mNewBestLocationListener.newBestLocation(currentBestLocation);
            } else if (isBetterLocation(location)) {
                currentBestLocation = location;
                if (mNewBestLocationListener != null) mNewBestLocationListener.newBestLocation(currentBestLocation);
            }
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };
    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     */
    private boolean isBetterLocation(Location location) {


        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    /****************************************/
    /***** I n t e r f a c e s **************/
    /****************************************/
	/*
	 * Listener to inform for a new best location
	 */
    public interface newBestLocationListener extends EventListener{
        public void newBestLocation (Location currentBestLocation);
    }
    /*
     * Sets New Best Location Listener
     */
    public void setNewBestLocationListener(newBestLocationListener eventListener) {
        mNewBestLocationListener = eventListener;
    }
}

