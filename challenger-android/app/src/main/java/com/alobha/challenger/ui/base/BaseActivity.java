package com.alobha.challenger.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alobha.challenger.R;

/**
 * Created by mrNRG on 13.06.2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected View content;
    protected boolean doubleBackToExitPressedOnce = false;

    protected void onCreate(int layoutId, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    protected void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getTag());
        transaction.commit();
    }

    protected void navigateBack() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (upIntent != null) {
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {
                NavUtils.navigateUpTo(this, upIntent);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public FragmentManager getCurrentFragmentManager() {
        return getSupportFragmentManager();
    }
}
