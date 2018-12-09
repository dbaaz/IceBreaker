package com.arbiter.droid.icebreakerprot1.location;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.content.Context;

import com.google.android.gms.location.LocationListener;

public class LocationProviderService extends Service implements LocationListener {

    private final Context mContext;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    protected LocationManager locationManager;

    public LocationProviderService(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    ContextWrapper context = new ContextWrapper(getBaseContext());
                    PackageManager pm = context.getPackageManager();
                    int hasPerm = pm.checkPermission(
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            context.getPackageName());
                    if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                (android.location.LocationListener) this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (isNetworkEnabled) {
                        ContextWrapper context = new ContextWrapper(getBaseContext());
                        PackageManager pm = context.getPackageManager();
                        int hasPerm = pm.checkPermission(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                context.getPackageName());
                        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                            if (location == null) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);

                                if (locationManager != null) {
                                    location = locationManager
                                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationProviderService.class);
        }
    }
     */

    public double getLatitude(){
        if (location != null){
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude(){
        if (location != null){
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
