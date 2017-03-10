package com.alobha.challenger.ui.auth.presenters;

import android.support.annotation.NonNull;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.models.AnonymousResponse;
import com.alobha.challenger.ui.base.BasePresenter;
import com.alobha.challenger.ui.base.MvpView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mrNRG on 17.06.2016.
 */
public class AnonymousLoginPresenter extends BasePresenter<AnonymousLoginPresenter.View> {

    public interface View extends MvpView {
        void showLoadingAnonymousLoginUi();

        void showErrorAnonymousLoginUi(@NonNull Throwable throwable);

        void showContentAnonymousLoginUi(@NonNull AnonymousResponse anonymousResponse);

        void showInvalidAnonymousLogin(int status);
    }

    private ServerAPI serverAPI;

    public AnonymousLoginPresenter() {
        this.serverAPI = ServerAPI.Builder.build();
    }

    public void callLoginAnonymous() {
        final View view = view();

        if (view != null) {
            view.showLoadingAnonymousLoginUi();

            subscriptions.add(serverAPI.loginAnonymous()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((anonymousResponse) -> {
                        if (anonymousResponse.status == 0) {
                            view.showContentAnonymousLoginUi(anonymousResponse);
                        } else view.showInvalidAnonymousLogin(anonymousResponse.status);
                    }, throwable -> {
                        view.showErrorAnonymousLoginUi(throwable);
                    }));
        }
    }
}
