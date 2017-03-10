package com.alobha.challenger.business.gps;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.business.receivers.ChallengeReceiver;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.ui.main.activities.MainActivity;

public class TrackingService extends Service {
    private static final String TAG = TrackingService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;
    private TrackingController trackingController;
    private Challenge challenge;

    private static Notification createNotification(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification.Builder builder = new Notification.Builder(context);
        Notification notification = builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("You are now running a challenge!").build();

        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            notification.priority = Notification.PRIORITY_MIN;
        }

        return notification;
    }

    @Override
    public void onCreate() {
        startForeground(NOTIFICATION_ID, createNotification(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "service started");
        if (trackingController == null) {
            Bundle bundle = intent.getExtras();
            challenge = (Challenge) bundle.getSerializable(GlobalConstants.CHALLENGE_NEW_CHALLENGE);
            trackingController = new TrackingController(this, challenge);
        }
        ChallengeReceiver.sendBroadcast(this, intent.getAction(), challenge);
        switch (intent.getAction()) {
            case ChallengeReceiver.RECEIVE_CHALLENGE_COMPLETED:
                trackingController.complete();
                stopSelf();
                break;
            case ChallengeReceiver.RECEIVE_CHALLENGE_PAUSED:
                trackingController.pause();
                stopSelf();
                break;
            case ChallengeReceiver.RECEIVE_CHALLENGE_STARTED:
                trackingController.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service destroy");
        stopForeground(true);
        if (trackingController != null) {
            trackingController.stop();
            trackingController = null;
        }
    }

    public static void startService(Context context, String action, @NonNull Challenge challenge) {
        Intent intent = new Intent(context, TrackingService.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(GlobalConstants.CHALLENGE_NEW_CHALLENGE, challenge);

        intent.putExtras(bundle);
        intent.setAction(action);
        context.startService(intent);
    }

}