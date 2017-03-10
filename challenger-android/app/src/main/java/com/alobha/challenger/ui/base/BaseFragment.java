package com.alobha.challenger.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.alobha.challenger.navigation.Navigator;

/**
 * Created by mrNRG on 10.06.2016.
 */
public abstract class BaseFragment extends Fragment {

    private Navigator navigator;
    private ProgressDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
        initializeNavigator();
        initializeLoadingDialog();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initializeNavigator() {
        navigator = new Navigator();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract void initializeLoadingDialog();

    protected abstract void initializePresenter();

    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder windowToken = null;
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            windowToken = view.getWindowToken();
        }
        if (windowToken != null) {
            inputManager.hideSoftInputFromWindow(windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    protected Navigator getNavigator() {
        return navigator;
    }

    protected ProgressDialog getLoadingDialog() {
        return loadingDialog;
    }

    public void setLoadingDialog(ProgressDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
    }

    public void hideLoadingDialog() {
        if (getLoadingDialog() != null && getLoadingDialog().isShowing()) {
            getLoadingDialog().dismiss();
        }
    }

    public interface OnEventListener {
        void onEvent(int requestCode);
    }
}
