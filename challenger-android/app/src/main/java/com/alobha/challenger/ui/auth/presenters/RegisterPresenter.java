package com.alobha.challenger.ui.auth.presenters;

import android.support.annotation.NonNull;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.models.UserResponse;
import com.alobha.challenger.ui.base.BasePresenter;
import com.alobha.challenger.ui.base.MvpView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mrNRG on 16.06.2016.
 */
public class RegisterPresenter extends BasePresenter<RegisterPresenter.View> {

    public interface View extends MvpView {
        void showLoadingRegisterUi();

        void showErrorRegisterUi(@NonNull Throwable throwable);

        void showContentRegisterUi(@NonNull UserResponse userResponse);

        void showInvalidRegistration(int status);
    }

    private ServerAPI serverAPI;

    public RegisterPresenter() {
        this.serverAPI = ServerAPI.Builder.build();
    }

    public void callRegister(String email, String password, String sex, String phone) {
        final View view = view();

        if (view != null) {
            view.showLoadingRegisterUi();

            subscriptions.add(serverAPI.register(email, password, sex, phone)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userResponse -> {
                        if (userResponse.status == 0) {
                            view.showContentRegisterUi(userResponse);
                        } else view.showInvalidRegistration(userResponse.status);
                    }, throwable -> {
                        view.showErrorRegisterUi(throwable);
                    }));
        }
    }
}
