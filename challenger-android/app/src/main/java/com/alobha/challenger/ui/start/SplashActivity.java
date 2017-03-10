package com.alobha.challenger.ui.start;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.navigation.Navigator;
import com.alobha.challenger.ui.base.BaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mrNRG on 13.06.2016.
 */
public class SplashActivity extends BaseActivity {
    public static final int DELAY = 2000;
    private PersistentPreferences preferences;
    private Timer timer;
    private Navigator navigator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = PersistentPreferences.getInstance();
        navigator = new Navigator();
        if (readyToGo()) {
            timer = new Timer();
            timer.schedule(new UpdateTimeTask(), DELAY);
        }
    }

    protected boolean readyToGo() {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int status = availability.isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            return (true);
        } else if (availability.isUserResolvableError(status)) {
            Dialog errorDialog = availability.getErrorDialog(this, status, 0);
            errorDialog.show();
        } else {
            Toast.makeText(this, R.string.no_fused, Toast.LENGTH_LONG).show();
            finish();
        }
        return false;
    }

    private class UpdateTimeTask extends TimerTask {
        public void run() {
            if (preferences.isTutorialShown()) {
                moveToNextScreen();
            } else {
                navigator.navigateToTutorialScreen(SplashActivity.this, 12);
            }

        }
    }

    private void moveToNextScreen() {
        if (preferences.isLoggedIn())
            navigator.navigateToMainScreen(SplashActivity.this);
        else
            navigator.navigateToAuthScreen(SplashActivity.this);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12) {
            if (resultCode == RESULT_OK) {
                moveToNextScreen();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        timer.purge();
        super.onDestroy();
    }
}
