package com.arbiter.droid.icebreakerprot1.location;

import android.Manifest;
import android.app.Service;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.content.Context;

import com.google.android.gms.location.LocationListener;

public class LocationProviderService extends Service implements LocationListener {

    private static final long UPDATE_DIS = 10;
    private static final long UPDATE_TIME = 500 * 60 * 1; //30 seconds

    protected LocationManager locationManager;
    private final Context mContext;

    Location location; // location

    double latitude; // latitude

    double longitude; // longitude
    
    private void hasPermission() {

    }
    
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
                if (isNetworkEnabled) {
                    ContextWrapper context = new ContextWrapper(getBaseContext());
                    PackageManager pm = context.getPackageManager();
                    int hasPerm = pm.checkPermission(
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            context.getPackageName());
                    if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                UPDATE_TIME,
                                UPDATE_DIS,
                                (android.location.LocationListener) this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                setLatitude(location.getLatitude());
                                setLongitude(location.getLongitude());
                            }
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
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
                                        UPDATE_TIME,
                                        UPDATE_DIS, (android.location.LocationListener) this);

                                if (locationManager != null) {
                                    location = locationManager
                                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    if (location != null) {
                                        setLatitude(location.getLatitude());
                                        setLongitude(location.getLongitude());
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
