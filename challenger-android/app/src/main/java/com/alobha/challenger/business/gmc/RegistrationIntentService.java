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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.models.StatusResponse;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import retrofit.Response;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    public static final String SUBSCRIBE_FOR_GCM = "com.alobha.challenger.data.action.subscribe_for_gcm";
    public static final String UNSUBSCRIBE_FROM_GCM = "com.alobha.challenger.data.action.unsubscribe_for_gcm";
    private PersistentPreferences preferences = PersistentPreferences.getInstance();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case SUBSCRIBE_FOR_GCM:
                    try {
                        // [START register_for_gcm]
                        // Initially this call goes out to the network to retrieve the token, subsequent calls
                        // are local.
                        // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
                        // See https://developers.google.com/cloud-messaging/android/start for details on this file.
                        // [START get_token]
/*                        InstanceID instanceID = InstanceID.getInstance(this);
                        String token = instanceID.getToken(getString(R.string.gcm_app_id),
                                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        // [END get_token]
                        Log.i(TAG, "GCM Registration Token: " + token);

                        sendRegistrationToServer(preferences.getUserToken(), token);

                        // You should store a boolean that indicates whether the generated token has been
                        // sent to your server. If the boolean is false, send the token to your server,
                        // otherwise your server should have already received the token.
                        preferences.setGcmTokenSent(true);
                        // [END register_for_gcm]

 */
                        Log.d("RegisterForGcm","process");
                    } catch (Exception e) {
                        Log.d(TAG, "Failed to complete token refresh", e);
                        // If an exception happens while fetching the new token or updating our registration data
                        // on a third-party server, this ensures that we'll attempt the update at a later time.
                        preferences.setGcmTokenSent(false);
                    }
                    break;
                case UNSUBSCRIBE_FROM_GCM:
                    try {
                        //send an empty gcm token to unsubscribe
                        sendRegistrationToServer(preferences.getUserToken(), "");
                        Log.i(TAG, "Unsubscribe from GCM");
                        PersistentPreferences.getInstance().removeToken();
                    } catch (Exception e) {
                        Log.d(TAG, "Failed to complete token refresh", e);
                        // If an exception happens while fetching the new token or updating our registration data
                        // on a third-party server, this ensures that we'll attempt the update at a later time.
                        preferences.setGcmTokenSent(false);
                    }
                    break;
            }
        }
    }

    private void sendRegistrationToServer(String token, String notificationToken) throws IOException {
        ServerAPI api = ServerAPI.Builder.build();
        Response<StatusResponse> response = api.subscribeForGCM(token, notificationToken, "Android").execute();
        if (response.body().status != 0) {
            throw new IllegalStateException("Not sent. Sending later.");
        }
    }

    public static void startService(Context context, String action) {
        Intent intent = new Intent(context, RegistrationIntentService.class);
        intent.setAction(action);
        context.startService(intent);
    }
}
