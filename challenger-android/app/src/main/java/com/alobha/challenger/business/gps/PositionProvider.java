package com.alobha.challenger.business.gps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class PositionProvider implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = PositionProvider.class.getSimpleName();
    public static final int PERIOD = 5000;
    private static final long FASTEST_INTERVAL = 1000;
    protected GoogleApiClient mGoogleApiClient;

    public interface PositionListener {
        void onPositionUpdate(Location location);
    }

    public interface GPSConnectionListener {

    }

    private final PositionListener listener;

    protected final Context context;

    private long lastUpdateTime;

    public PositionProvider(Context context, PositionListener listener) {
        this.context = context;
        this.listener = listener;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    public void startUpdates() {
        Log.i(TAG, "starting updates");
        mGoogleApiClient.connect();
    }

    public void stopUpdates() {
        Log.i(TAG, "stopping updates");
        mGoogleApiClient.unregisterConnectionCallbacks(this);
        if (mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest request = new LocationRequest();
        request.setInterval(PERIOD);
        request.setFastestInterval(FASTEST_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        } catch (SecurityException e) {
            //TODO: Consider show "give access" notification
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        switch (i) {
            case CAUSE_NETWORK_LOST:
                break;

            case CAUSE_SERVICE_DISCONNECTED:
                break;
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    protected void updateLocation(Location location) {
        if (location != null && location.getTime() != lastUpdateTime && location.hasAccuracy() && location.getAccuracy() < 60) {
            //Log.i(TAG, "location new");
            lastUpdateTime = location.getTime();
            listener.onPositionUpdate(location);
        } else {
            //Log.i(TAG, location != null ? "location old" : "location nil");
        }
    }
}