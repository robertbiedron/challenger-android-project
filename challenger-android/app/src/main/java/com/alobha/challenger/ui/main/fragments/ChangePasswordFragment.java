package com.alobha.challenger.ui.main.fragments;

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
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.UserResponse;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.presenters.ProfilePresenter;
import com.alobha.challenger.utils.DialogFactory;
import com.alobha.challenger.utils.validation.PasswordValidator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 23.06.2016.
 */
public class ChangePasswordFragment extends BaseFragment implements ProfilePresenter.View, TextView.OnEditorActionListener{

    private AlertDialog changePassDialog;
    private ProfilePresenter presenter;

    private PasswordValidator passwordValidator;

    @Bind(R.id.etOldPassword)
    EditText etOldPassword;

    @Bind(R.id.etNewPassword)
    EditText etNewPassword;

    @Bind(R.id.etConfirmPassword)
    EditText etConfirmPassword;

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_loading)));
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new ProfilePresenter();
        this.presenter.bindView(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_pass, container, false);
        ButterKnife.bind(this, view);

        etConfirmPassword.setOnEditorActionListener(this);
        passwordValidator = new PasswordValidator(etNewPassword, etConfirmPassword);

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
        changePassDialog = DialogFactory.createChangePassDialog(getActivity(), getNavigator());
    }

    @OnClick(R.id.btnChangePassword)
    void onButtonChangePasswordClick() {
        if (passwordValidator.isValid()) {
            presenter.callChangePassword(etOldPassword.getText().toString(),
                    etNewPassword.getText().toString());
        }
    }

    @OnClick(R.id.ll_change_pass)
    void onOutsideClick() {
        hideKeyboard();
    }

    @Override
    public void showLoadingProfileUi() {
        getLoadingDialog().show();
        hideKeyboard();
    }

    @Override
    public void showErrorProfileUi(@NonNull Throwable throwable) {
        hideLoadingDialog();
        Log.d("Change password", "error", throwable);
    }

    @Override
    public void showContentProfileUi(@NonNull UserResponse userResponse) {
        hideLoadingDialog();
        User user = userResponse.user;
        PersistentPreferences.getInstance().setLoggedUser(user);
        changePassDialog.show();
    }

    @Override
    public void showInvalidProfile(int status) {
        hideLoadingDialog();
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
            onButtonChangePasswordClick();
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
