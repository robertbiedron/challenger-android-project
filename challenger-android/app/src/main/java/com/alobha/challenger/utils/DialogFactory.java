package com.alobha.challenger.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.alobha.challenger.R;
import com.alobha.challenger.business.gmc.RegistrationIntentService;
import com.alobha.challenger.business.gps.TrackingService;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.navigation.Navigator;
import com.facebook.login.LoginManager;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by mrNRG on 16.06.2016.
 */
public class DialogFactory {


    public static AlertDialog createAlertMessageNoGps(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.message_gps_disabled))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.dialog_yes_button_title), (dialog, id) -> {
                    activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton(activity.getString(R.string.dialog_no_button_title), (dialog, id) -> {
                    dialog.cancel();
                });
        return builder.create();
    }

    public static ProgressDialog createLoadingDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;
    }

    public static AlertDialog constructAlertDialogWithYesNoButton(Activity activity, String title,
                                                                  String message, final OnDialogYesNoButtonClickedListener onDialogButtonClickedListener) {
        return new AlertDialog.Builder(activity).setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_yes_button_title, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onDialogButtonClickedListener.onYesClick();
                    }
                })
                .setNegativeButton(R.string.dialog_no_button_title, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        onDialogButtonClickedListener.onNoClick();
                    }
                })
                .create();
    }

    public static void showSimpleDialog(Context context, String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.dialog_ok_button_title), null)
                .setCancelable(true)
                .create();
        dialog.show();
    }

    public static AlertDialog createPassRecoverDialog(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_restore))
                .setMessage(context.getString(R.string.message_pass_has_been_reset))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.dialog_ok_button_title),
                        (dialog, id) -> {
                            refreshActivity(context);
                        });

        return builder.create();
    }

    public static AlertDialog createProfileUpdateDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.title_profile))
                .setMessage(activity.getString(R.string.message_profile_has_been_updated))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.dialog_ok_button_title),
                        (dialog, id) -> {
                            //do nothing
                        });

        return builder.create();
    }

    public static AlertDialog createChangePassDialog(Activity activity, Navigator navigator) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.title_profile))
                .setMessage(activity.getString(R.string.message_pass_has_been_changed))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.dialog_ok_button_title),
                        (dialog, id) -> {
                            logOut(activity, navigator);
                        });

        return builder.create();
    }

    public static AlertDialog createSignOutDialog(Activity activity, Navigator navigator) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.title_sign_out))
                .setMessage(activity.getString(R.string.message_really_wanna_sign_out))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.dialog_yes_button_title),
                        (dialog, id) -> {
                            logOut(activity, navigator);
                        })
                .setNegativeButton(
                        activity.getString(R.string.dialog_cancel_button_title),
                        (dialog, id) -> {
                            //do nothing
                        }
                );

        return builder.create();
    }

    public static void showSnackBarShort(Activity activity, String text) {
        View content = activity.findViewById(R.id.content);
        Snackbar.make(content, text, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackBarLong(Activity activity, String text) {
        View content = activity.findViewById(R.id.content);
        Snackbar.make(content, text, Snackbar.LENGTH_LONG).show();
    }

    public static void showToastMessageShort(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showToastMessageLong(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private static void refreshActivity(Activity activity) {
        Intent intent = activity.getIntent();
        activity.startActivity(intent);
        activity.finish();
    }

    private static void logOut(Activity activity, Navigator navigator) {
        RegistrationIntentService.startService(activity, RegistrationIntentService.UNSUBSCRIBE_FROM_GCM);
        Delete.table(User.class);
        Delete.table(Challenge.class);
        Delete.table(Competitor.class);
        activity.stopService(new Intent(activity, TrackingService.class));
        LoginManager.getInstance().logOut();
        navigator.navigateToAuthScreen(activity);
        PersistentPreferences.getInstance().removeUser();
        activity.finish();
    }

    public interface OnDialogButtonClickedListener {
        void onOkClick();
    }

    public interface OnDialogYesNoButtonClickedListener {
        void onYesClick();

        void onNoClick();
    }
}
