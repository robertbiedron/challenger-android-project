package com.alobha.challenger.ui.auth.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.UserResponse;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.ui.auth.presenters.LoginPresenter;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.utils.DialogFactory;
import com.alobha.challenger.utils.validation.EmailValidator;
import com.alobha.challenger.utils.validation.PasswordValidator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 10.06.2016.
 */
public class LoginFragment extends BaseFragment implements FacebookCallback<LoginResult>,
        LoginPresenter.View,
        TextView.OnEditorActionListener {

    private OnEventListener mCallback;

    private LoginPresenter presenter;

    private CallbackManager callbackManager;
    private EmailValidator emailValidator;
    private PasswordValidator passwordValidator;


    @Bind(R.id.etUsername)
    public EditText etUsername;

    @Bind(R.id.etNewPassword)
    public EditText etPassword;

    @Bind(R.id.login_button)
    public LoginButton btnFacebookLogin;

    @Bind(R.id.btnLogin)
    public Button btnLogin;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEventListener) context;
        } catch (ClassCastException e) {
            Log.e(getTag(), e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        btnFacebookLogin.setReadPermissions("user_friends", "public_profile", "email");
        btnFacebookLogin.setFragment(this);

        etPassword.setOnEditorActionListener(this);
        emailValidator = new EmailValidator(etUsername);
        passwordValidator = new PasswordValidator(etPassword, null);

        btnFacebookLogin.setEnabled(true);
        btnLogin.setEnabled(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_log_in));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.registerCallback(callbackManager, this);
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_wait_login)));
    }

    @OnClick(R.id.btnLogin)
    public void onButtonLoginClick() {
        if (emailValidator.isValid() & passwordValidator.isValid()) {
            presenter.callLogin(etUsername.getText().toString(),
                    etPassword.getText().toString());
        }
    }

    @OnClick(R.id.btnSignUp)
    public void onButtonRegisterClick() {
        mCallback.onEvent(GlobalConstants.REGISTER_BUTTON);
    }

    @OnClick(R.id.btnForgotPassword)
    public void onButtonAnonymousClick() {
        mCallback.onEvent(GlobalConstants.PASSWORD_RECOVERY_BUTTON);
    }

    @OnClick(R.id.ll_login)
    public void onOutsideClick() {
        hideKeyboard();
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new LoginPresenter();
        this.presenter.bindView(this);
    }

    @Override
    public void showLoadingLoginUi() {
        getLoadingDialog().show();
        hideKeyboard();
    }

    @Override
    public void showErrorLoginUi(@NonNull Throwable throwable) {
        hideLoadingDialog();
        Log.d(getTag(), "error", throwable);
    }

    @Override
    public void showContentLoginUi(@NonNull UserResponse userResponse) {
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
    public void showInvalidLogin(int status) {
        hideLoadingDialog();
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d("Facebook", "success");
        if (loginResult != null) {
            presenter.callFacebookLogin(loginResult);
            btnFacebookLogin.setEnabled(false);
            btnLogin.setEnabled(false);
        }
    }

    @Override
    public void onCancel() {
        Log.d("Facebook", "cancel");
    }

    @Override
    public void onError(FacebookException error) {
        Log.d("Facebook", "error", error);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
            onButtonLoginClick();
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        presenter.unbindView(this);
        super.onDestroy();
    }
}
