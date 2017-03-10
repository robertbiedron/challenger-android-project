package com.alobha.challenger.ui.auth.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.UserResponse;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.ui.auth.presenters.RegisterPresenter;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.utils.DialogFactory;
import com.alobha.challenger.utils.validation.EmailValidator;
import com.alobha.challenger.utils.validation.PasswordValidator;
import com.alobha.challenger.utils.validation.PhoneValidator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 10.06.2016.
 */
public class RegisterFragment extends BaseFragment implements RegisterPresenter.View, TextView.OnEditorActionListener {

    private RegisterPresenter presenter;

    private EmailValidator emailValidator;
    private PasswordValidator passwordValidator;
    private PhoneValidator phoneValidator;

    @Bind(R.id.etUsername)
    public EditText etUsername;

    @Bind(R.id.etPhone)
    public EditText etPhone;

    @Bind(R.id.etNewPassword)
    public EditText etPassword;

    @Bind(R.id.swGender)
    public RadioGroup swGender;

    @Bind(R.id.btnRegister)
    public Button btnRegister;

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        etPassword.setOnEditorActionListener(this);
        emailValidator = new EmailValidator(etUsername);
        passwordValidator = new PasswordValidator(etPassword, null);
        phoneValidator = new PhoneValidator(etPhone);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_register));
    }

    @OnClick(R.id.btnRegister)
    public void onButtonRegisterClick() {
        if (emailValidator.isValid() & passwordValidator.isValid() & phoneValidator.isValid()) {
            presenter.callRegister(etUsername.getText().toString(),
                    etPassword.getText().toString(),
                    swGender.getCheckedRadioButtonId() == R.id.btnMale ? "Male" : "Female",
                    etPhone.getText().toString());
        }
    }

    @OnClick(R.id.ll_register)
    public void onOutsideClick() {
        hideKeyboard();
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_wait_registration)));
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new RegisterPresenter();
        this.presenter.bindView(this);
    }

    @Override
    public void showLoadingRegisterUi() {
        getLoadingDialog().show();
        hideKeyboard();
    }

    @Override
    public void showErrorRegisterUi(@NonNull Throwable throwable) {
        hideLoadingDialog();
        Log.d(getTag(), "error", throwable);
    }

    @Override
    public void showContentRegisterUi(@NonNull UserResponse userResponse) {
        hideLoadingDialog();
        User user = userResponse.user;
        PersistentPreferences.getInstance().setUserToken(userResponse.token);
        PersistentPreferences.getInstance().setLoggedUser(user);
        navigateToMainScreen();
    }

    private void navigateToMainScreen() {
        getNavigator().navigateToMainScreen(getActivity());
        getActivity().finish();
    }

    @Override
    public void showInvalidRegistration(int status) {
        hideLoadingDialog();
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
            hideKeyboard();
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
