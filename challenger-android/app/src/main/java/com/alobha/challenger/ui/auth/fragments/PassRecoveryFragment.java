package com.alobha.challenger.ui.auth.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.ui.auth.presenters.PassRecoveryPresenter;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.utils.DialogFactory;
import com.alobha.challenger.utils.validation.EmailValidator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 15.06.2016.
 */
public class PassRecoveryFragment extends BaseFragment implements PassRecoveryPresenter.View, TextView.OnEditorActionListener {

    private AlertDialog recoveryDialog;
    private PassRecoveryPresenter presenter;

    private EmailValidator emailValidator;

    @Bind(R.id.etUsername)
    public EditText etUsername;

    public static PassRecoveryFragment newInstance() {
        PassRecoveryFragment fragment = new PassRecoveryFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restore_pass, container, false);
        ButterKnife.bind(this, view);

        etUsername.setOnEditorActionListener(this);
        emailValidator = new EmailValidator(etUsername);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_restore));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recoveryDialog = DialogFactory.createPassRecoverDialog(getActivity());
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_wait_pass_recover)));
    }

    @OnClick(R.id.btnRestore)
    public void onButtonRestoreClick() {
        hideKeyboard();
        if (emailValidator.isValid()) {
            presenter.callPassRecovery(etUsername.getText().toString());
        }
    }

    @OnClick(R.id.ll_restore)
    public void onOutsideClick() {
        hideKeyboard();
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new PassRecoveryPresenter();
        this.presenter.bindView(this);
    }

    @Override
    public void showLoadingPassRecoveryUi() {
        getLoadingDialog().show();
    }

    @Override
    public void showErrorPassRecoveryUi(@NonNull Throwable throwable) {
        hideLoadingDialog();
        Log.d("Password recovery", "error", throwable);
    }

    @Override
    public void showContentPassRecoveryUi() {
        recoveryDialog.show();
    }

    @Override
    public void showInvalidPassRecovery(int status) {
        hideLoadingDialog();
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
            onButtonRestoreClick();
        return false;
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        presenter.unbindView(this);
        super.onDestroy();
    }
}
