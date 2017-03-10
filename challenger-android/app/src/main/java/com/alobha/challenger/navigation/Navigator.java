package com.alobha.challenger.navigation;

import android.app.Activity;
import android.content.Intent;

import com.alobha.challenger.ui.auth.activities.AuthActivity;
import com.alobha.challenger.ui.main.activities.MainActivity;
import com.alobha.challenger.ui.start.TutorialActivity;

/**
 * Created by mrNRG on 14.06.2016.
 */
public class Navigator {
    public void navigateToTutorialScreen(Activity activity, int requestCode) {
        if (activity != null) {
            Intent intent = TutorialActivity.getCallingIntent(activity);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public void navigateToAuthScreen(Activity activity) {
        if (activity != null) {
            Intent intent = AuthActivity.getCallingIntent(activity);
            activity.startActivity(intent);
        }
    }

    public void navigateToMainScreen(Activity activity) {
        if (activity != null) {
            Intent intent = MainActivity.getCallingIntent(activity);
            activity.startActivity(intent);
        }
    }
}
