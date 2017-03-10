package com.alobha.challenger.business.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.ui.main.fragments.MapChallengeFragment;

public abstract class ChallengeReceiver extends BroadcastReceiver {

    public static final String RECEIVE_CHALLENGE_STARTED = "com.alobha.challenger.RECEIVE_CHALLENGE_STARTED";
    public static final String RECEIVE_CHALLENGE_COMPLETED = "com.alobha.challenger.RECEIVE_CHALLENGE_COMPLETED";
    public static final String RECEIVE_CHALLENGE_PAUSED = "com.alobha.challenger.RECEIVE_CHALLENGE_PAUSED";

    public static final IntentFilter INTENT_FILTER = new IntentFilter();

    {
        INTENT_FILTER.addAction(RECEIVE_CHALLENGE_STARTED);
        INTENT_FILTER.addAction(RECEIVE_CHALLENGE_COMPLETED);
        INTENT_FILTER.addAction(RECEIVE_CHALLENGE_PAUSED);
    }

    private static final String TAG = ChallengeReceiver.class.getSimpleName();

    public static void sendBroadcast(Context context, String action, Challenge challenge) {
        Intent intent = new Intent(action);
        intent.putExtra(GlobalConstants.CHALLENGE_NEW_CHALLENGE, challenge);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public ChallengeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long challengeId = intent.getLongExtra(MapChallengeFragment.CHALLENGE_ID, -1);
        switch (intent.getAction()) {
            case RECEIVE_CHALLENGE_STARTED:
                handleChallengeStarted(challengeId);
                break;
            case RECEIVE_CHALLENGE_COMPLETED:
                handleChallengeCompleted(challengeId);
                break;
            case RECEIVE_CHALLENGE_PAUSED:
                handleChallengePaused(challengeId);
                break;
        }
    }



    protected abstract void handleChallengePaused(long challengeId);

    protected abstract void handleChallengeCompleted(long challengeId);

    protected abstract void handleChallengeStarted(long challengeId);

}