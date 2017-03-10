/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alobha.challenger.business.gmc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.ui.main.activities.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

public class NotificationGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String json = data.getString("data");
        PersistentPreferences preferences = PersistentPreferences.getInstance();
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + json);
        Challenge received = ServerAPI.Builder.GSON.fromJson(json, Challenge.class);
        received.synced = 1;
        received.seen = false;
        received.save();

        if (received.exists()) {
            for (int j = 0; j < received.competitors.size(); j++) {
                Competitor competitor = received.competitors.get(j);
                User user = competitor.user;
                if (user != null) {
                    user.save();
                    if (user.id == preferences.getUserId())
                        preferences.setLoggedUser(user);
                }
                competitor.challenge = received;
                competitor.save();
            }
        }
        sendNotification(received);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param challenge GCM challenge received.
     */
    private void sendNotification(Challenge challenge) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("challenge_id", challenge.id);
        intent.setAction("ShowNotification");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.inner_logo);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.inner_logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("You were challenged by " + challenge.owner.first_name)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
