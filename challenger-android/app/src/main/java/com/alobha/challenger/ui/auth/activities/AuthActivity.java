package com.alobha.challenger.ui.auth.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.ui.auth.fragments.AuthFragment;
import com.alobha.challenger.ui.auth.fragments.LoginFragment;
import com.alobha.challenger.ui.auth.fragments.PassRecoveryFragment;
import com.alobha.challenger.ui.auth.fragments.RegisterFragment;
import com.alobha.challenger.ui.base.BaseActivity;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.utils.DialogFactory;
import com.facebook.FacebookSdk;

/**
 * Created by mrNRG on 10.06.2016.
 */
public class AuthActivity extends BaseActivity implements BaseFragment.OnEventListener {

    private int content;

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CONTACTS = 2;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, AuthActivity.class);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        content = R.id.content;
        super.onCreate(R.layout.activity_template, savedInstanceState);

        if (savedInstanceState == null) {
            initialize();
        }
    }

    private void initialize() {
        addFragment(content, AuthFragment.newInstance());

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DialogFactory.showSnackBarShort(this, getString(R.string.permission_granted));
                } else {
                    DialogFactory.showSnackBarShort(this, getString(R.string.permission_denied));
                }
                break;
            }

            case REQUEST_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DialogFactory.showSnackBarShort(this, getString(R.string.permission_granted));
                } else {
                    DialogFactory.showSnackBarShort(this, getString(R.string.permission_denied));
                }
                break;
            }
        }
    }

    @Override
    public void onEvent(int requestCode) {
        switch (requestCode) {
            case GlobalConstants.LOGIN_BUTTON:
                replaceFragment(content, LoginFragment.newInstance());
                break;
            case GlobalConstants.REGISTER_BUTTON:
                replaceFragment(content, RegisterFragment.newInstance());
                break;
            case GlobalConstants.PASSWORD_RECOVERY_BUTTON:
                replaceFragment(content, PassRecoveryFragment.newInstance());
                break;
        }
    }
}
