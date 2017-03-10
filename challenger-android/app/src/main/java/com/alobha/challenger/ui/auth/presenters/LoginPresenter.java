package com.alobha.challenger.ui.auth.presenters;

import android.support.annotation.NonNull;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.models.UserResponse;
import com.alobha.challenger.ui.base.BasePresenter;
import com.alobha.challenger.ui.base.MvpView;
import com.facebook.login.LoginResult;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mrNRG on 16.06.2016.
 */
public class LoginPresenter extends BasePresenter<LoginPresenter.View> {

    public interface View extends MvpView {
        void showLoadingLoginUi();

        void showErrorLoginUi(@NonNull Throwable throwable);

        void showContentLoginUi(@NonNull UserResponse userResponse);

        void showInvalidLogin(int status);
    }

    private ServerAPI serverAPI;

    public LoginPresenter() {
        this.serverAPI = ServerAPI.Builder.build();
    }

    public void callLogin(String username, String password) {
        final View view = view();

        if (view != null) {
            view.showLoadingLoginUi();

            subscriptions.add(serverAPI.login(username, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((userResponse) -> {
                        if (userResponse.status == 0) {
                            view.showContentLoginUi(userResponse);
                        } else view.showInvalidLogin(userResponse.status);
                    }, throwable -> {
                        view.showErrorLoginUi(throwable);
                    }));
        }
    }

    public void callFacebookLogin(LoginResult loginResult) {
        final View view = view();

        if (view != null) {
            view.showLoadingLoginUi();

            subscriptions.add(serverAPI.facebookLogin(loginResult.getAccessToken().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((userResponse) -> {
                        if (userResponse.status == 0) {
                            view.showContentLoginUi(userResponse);
                        } else view.showInvalidLogin(userResponse.status);
                    }, throwable -> {
                        view.showErrorLoginUi(throwable);
                    }));
        }
    }
}
