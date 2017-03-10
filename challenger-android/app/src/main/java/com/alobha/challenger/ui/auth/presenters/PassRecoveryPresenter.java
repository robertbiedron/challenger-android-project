package com.alobha.challenger.ui.auth.presenters;

import android.support.annotation.NonNull;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.ui.base.BasePresenter;
import com.alobha.challenger.ui.base.MvpView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mrNRG on 17.06.2016.
 */
public class PassRecoveryPresenter extends BasePresenter<PassRecoveryPresenter.View> {

    public interface View extends MvpView {
        void showLoadingPassRecoveryUi();

        void showErrorPassRecoveryUi(@NonNull Throwable throwable);

        void showContentPassRecoveryUi();

        void showInvalidPassRecovery(int status);
    }

    private ServerAPI serverAPI;

    public PassRecoveryPresenter() {
        this.serverAPI = ServerAPI.Builder.build();
    }

    public void callPassRecovery(String email) {
        final View view = view();

        if (view != null) {
            view.showLoadingPassRecoveryUi();

            subscriptions.add(serverAPI.recoverPassword(email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((statusResponse) -> {
                        if (statusResponse.status == 0) {
                            view.showContentPassRecoveryUi();
                        } else view.showInvalidPassRecovery(statusResponse.status);
                    }, throwable -> {
                        view.showErrorPassRecoveryUi(throwable);
                    }));
        }
    }
}
