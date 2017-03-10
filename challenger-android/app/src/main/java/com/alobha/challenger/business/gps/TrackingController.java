package com.alobha.challenger.business.gps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alobha.challenger.business.receivers.ChallengeReceiver;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.services.SyncService;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Calendar;
import java.util.Collections;

import static com.alobha.challenger.GlobalConstants.CHALLENGE_NEW_CHALLENGE;
import static com.alobha.challenger.GlobalConstants.METERS_IN_KM;
import static com.alobha.challenger.GlobalConstants.SECONDS_IN_HOUR;

public class TrackingController implements PositionProvider.PositionListener {

    private static final String TAG = TrackingController.class.getSimpleName();
    private static final int WAKE_LOCK_TIMEOUT = 60 * 1000;
    public static final int GPS_INTERVAL = 5000; // 5 seconds
    public static final String RECEIVE_POSITION_UPDATE = "com.alobha.challenger.RECEIVE_POSITION_UPDATE";

    private Context context;

    private ProcessingHandler handler;

    private PositionProvider positionProvider;

    private PowerManager.WakeLock wakeLock;

    private final Challenge challenge;
    private float distance;
    private long time;
    private Location lastLocation;

    private void lock() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            wakeLock.acquire();
        } else {
            wakeLock.acquire(WAKE_LOCK_TIMEOUT);
        }
    }

    private void unlock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public TrackingController(Context context, Challenge challenge) {
        this.context = context;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        WakefulThread thread = new WakefulThread(wakeLock, "LocationProcessor");
        thread.start();
        handler = new ProcessingHandler(thread.getLooper());
        positionProvider = new PositionProvider(context, this);
        this.challenge = challenge;
    }

    public void start() {
        Log.i(TAG, "challenge started:" + this.challenge.id);
        distance = challenge
                .getCompetitorById(PersistentPreferences.getInstance().getUserId())
                .distance;
        time = this.challenge.time;
        PersistentPreferences.getInstance().setActiveChallengePaused(false);
        updateChallenge(challenge);
        try {
            positionProvider.startUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
    }

    public void pause() {
        Log.i(TAG, "challenge paused:" + challenge.id);
        PersistentPreferences.getInstance().setActiveChallengePaused(true);
        challenge.end_date = Calendar.getInstance().getTime();
        challenge.time = time;
        positionProvider.stopUpdates();
        updateChallenge(challenge);
    }

    public void complete() {
        Log.i(TAG, "challenge completed:" + challenge.id);
        positionProvider.stopUpdates();
        challenge.end_date = Calendar.getInstance().getTime();
        if (time > 0)
            challenge.time = time;
        challenge.completed = true;
        PersistentPreferences instance = PersistentPreferences.getInstance();
        instance.setActiveChallengePaused(false);
        instance.removeActiveChallenge();
        instance.removeActiveChallengePaused();
        SyncService.setChallenge(challenge);
        SyncService.startService(context, SyncService.SEND_CHALLENGE);
    }

    public void stop() {
        try {
            positionProvider.stopUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
        Log.i(TAG, "stopped:" + challenge.id);
        handler.removeCallbacksAndMessages(null);
        unlock();
        context = null;
    }

    @Override
    public void onPositionUpdate(final Location currentLocation) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentLocation != null) {
                    long timeOffset = 0;
                    if (lastLocation != null) {
                        timeOffset = currentLocation.getTime() - lastLocation.getTime();
                        if (timeOffset <= GPS_INTERVAL) {
                            time += timeOffset;
                            distance += SphericalUtil.computeDistanceBetween(
                                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        } else timeOffset = 0;
                    } else {
                        challenge.start_date = Calendar.getInstance().getTime();
                    }

                    lastLocation = currentLocation;

                    if (timeOffset > 0) {
                        for (int i = 0; i < challenge.competitors.size(); i++) {
                            Competitor c = challenge.competitors.get(i);
                            if (c.user.id != PersistentPreferences.getInstance().getUserId()) {
                                float offsetDelta = c.user.avg_speed * (float) timeOffset / SECONDS_IN_HOUR * METERS_IN_KM;
                                if (c.distance < challenge.distance) {
                                    c.distance += offsetDelta / METERS_IN_KM;
                                    c.avg_speed = c.user.avg_speed;
                                    c.time = time;
                                } else {
                                    c.distance = challenge.distance;
                                    c.avg_speed = c.user.avg_speed;
                                    c.setLocation(currentLocation);
                                    c.time = time;
                                    TrackingService.startService(context, ChallengeReceiver.RECEIVE_CHALLENGE_COMPLETED, challenge);
                                }
                            } else {
                                if (distance < challenge.distance) {
                                    c.avg_speed = distance / time * SECONDS_IN_HOUR;
                                    c.distance = distance;
                                    c.setLocation(currentLocation);
                                    c.time = time;
                                    Log.d(TAG, "Distance left: " + (challenge.distance - distance));
                                } else {
                                    c.distance = challenge.distance;
                                    c.setLocation(currentLocation);
                                }
                            }
                            challenge.competitors.set(i, c);
                        }
                        challenge.time = time;

                        Collections.sort(challenge.competitors);
                        for (int i = 0; i < challenge.competitors.size(); i++) {
                            challenge.competitors.get(i).position = i + 1;
                        }
                        if (distance >= challenge.distance) {
                            TrackingService.startService(context, ChallengeReceiver.RECEIVE_CHALLENGE_COMPLETED, challenge);
                        }
                        updateChallenge(challenge);
                    }
                }
            }
        });
    }

    private void updateChallenge(Challenge challenge) {
        Intent intent = new Intent(RECEIVE_POSITION_UPDATE);
        intent.putExtra(CHALLENGE_NEW_CHALLENGE, challenge);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    private void log(String action, Location location) {
        if (location != null) {
            action += " (" +
                    " time:" + location.getTime() / METERS_IN_KM +
                    " lat:" + location.getLatitude() +
                    " lon:" + location.getLongitude() + ")";
        }
        Log.i(TAG, action);
    }

    private void write(Location location) {
        log("write", location);
        lock();
        unlock();
    }
}