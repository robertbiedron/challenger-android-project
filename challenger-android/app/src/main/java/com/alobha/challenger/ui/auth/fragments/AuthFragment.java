package com.alobha.challenger.ui.auth.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.AnonymousResponse;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.ui.auth.presenters.AnonymousLoginPresenter;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.utils.DialogFactory;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 10.06.2016.
 */
public class AuthFragment extends BaseFragment implements AnonymousLoginPresenter.View {
    private OnEventListener mCallback;

    private AnonymousLoginPresenter presenter;

    public static AuthFragment newInstance() {
        AuthFragment fragment = new AuthFragment();

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
            Log.e(AuthFragment.class.getSimpleName(), e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_wait_login)));
    }

    @OnClick(R.id.btnLogin)
    public void onButtonLoginClick() {
        mCallback.onEvent(GlobalConstants.LOGIN_BUTTON);
    }

    @OnClick(R.id.btnAnonymous)
    public void onButtonAnonymousClick() {
        presenter.callLoginAnonymous();
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new AnonymousLoginPresenter();
        this.presenter.bindView(this);
    }

    @Override
    public void showLoadingAnonymousLoginUi() {
        getLoadingDialog().show();
    }

    @Override
    public void showErrorAnonymousLoginUi(@NonNull Throwable throwable) {
        hideLoadingDialog();
        Log.d("Anonymous login", "error", throwable);
    }

    @Override
    public void showContentAnonymousLoginUi(@NonNull AnonymousResponse anonymousResponse) {
        hideLoadingDialog();
        User anonymous = anonymousResponse.anonymous;
        PersistentPreferences preferences = PersistentPreferences.getInstance();
        preferences.setUserToken(anonymousResponse.token);
        preferences.setLoggedUser(anonymous);
        preferences.setAnonymous(true);
        navigateToMainScreen();
    }

    private void navigateToMainScreen() {
        getNavigator().navigateToMainScreen(getActivity());
        getActivity().finish();
    }

    @Override
    public void showInvalidAnonymousLogin(int status) {
        hideLoadingDialog();
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        presenter.unbindView(this);
        super.onDestroy();
    }
}
