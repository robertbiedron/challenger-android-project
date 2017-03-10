package com.alobha.challenger.data.api.services;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.SyncChallenges;
import com.alobha.challenger.data.api.SyncData;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.models.ChallengeResponse;
import com.alobha.challenger.data.api.models.StatusResponse;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.navigation.MFragmentManager;
import com.alobha.challenger.ui.main.activities.MainActivity;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by mrNRG on 28.06.2016.
 */
public class SyncService extends IntentService {
    public static final String TAG = SyncService.class.getSimpleName();
    public static final String SYNC_FRIENDS = "com.alobha.challenger.data.action.sync_friends";
    public static final String SEND_CHALLENGE = "com.alobha.challenger.data.action.send_challenge";
    public static final String SYNC_CONTACTS = "com.alobha.challenger.data.action.sync_contacts";

    private ServerAPI SERVER_API = ServerAPI.Builder.build();
    private PersistentPreferences preferences = PersistentPreferences.getInstance();
    private static Challenge challenge;

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case SYNC_FRIENDS:
                    syncFriends();
                    break;
                case SYNC_CONTACTS:
                    syncContacts();
                    break;
                case SEND_CHALLENGE:
                    if (challenge != null) {
                        sendChallenge();
                    } else Log.e(TAG, "Set challenge first!");
                    break;
            }
        }
    }

    private void syncFriends() {
        String token = preferences.getUserToken();
        if (preferences.getUserSource().equals("Facebook")) {
            AccessToken accessToken = FacebookSdk.isInitialized() ? AccessToken.getCurrentAccessToken() : null;
            String accessTokenString = "";
            if (accessToken != null)
                accessTokenString = accessToken.getToken();

            Call<StatusResponse> syncCall = SERVER_API.syncFriends(token, accessTokenString);
            Response<StatusResponse> response;
            try {
                response = syncCall.execute();
                Log.i(TAG, "friends data synchronization has been completed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void sendChallenge() {
        PersistentPreferences preferences = PersistentPreferences.getInstance();
        if (preferences.getActiveChallenge() == -1) {
            SyncChallenges challenges = new SyncChallenges();
            String token = preferences.getUserToken();
            challenges.token = token;
            challenges.challenges = new ArrayList<>();
            challenges.challenges.add(challenge);
            preferences.removeActiveChallenge();
            preferences.setActiveChallengeFinished(true);

            Call<ChallengeResponse> challengeSync = SERVER_API.finishChallenge(challenges);
            try {
                Response<ChallengeResponse> challengesResponse = challengeSync.execute();
                if (challengesResponse.body() != null && challengesResponse.body().status == 0) {
                    if (challengesResponse.body().challenges != null && challengesResponse.body().challenges.size() > 0)
                        PersistentPreferences.getInstance().setLoggedUser(challengesResponse.body().challenges.get(0).owner);
                    if (MainActivity.active) {
                        MFragmentManager.nextFragment(MFragmentManager.MAIN_MY_RESULTS_FRAGMENT, null);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void syncContacts() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            ArrayList<String> phones = new ArrayList<>();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phones.add(PhoneNumberUtils.normalizeNumber(phoneNo));
                        }
                        pCur.close();
                    }
                }
            }
            String userToken = PersistentPreferences.getInstance().getUserToken();
            Call<StatusResponse> syncCall = SERVER_API.syncContacts(new SyncData(userToken, phones));
            try {
                syncCall.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startService(Context context, String action) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void setChallenge(Challenge challenge) {
        SyncService.challenge = challenge;
    }
}
