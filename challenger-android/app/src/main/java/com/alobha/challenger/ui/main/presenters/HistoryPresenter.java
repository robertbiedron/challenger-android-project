package com.alobha.challenger.ui.main.presenters;

import android.support.annotation.NonNull;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.models.ChallengeResponse;
import com.alobha.challenger.ui.base.BasePresenter;
import com.alobha.challenger.ui.base.MvpView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mrNRG on 27.06.2016.
 */
public class HistoryPresenter extends BasePresenter<HistoryPresenter.View> {

    public interface View extends MvpView {
        void showLoadingHistoryUi();

        void showErrorHistoryUi(@NonNull Throwable throwable);

        void showContentHistoryUi(@NonNull ChallengeResponse challengeResponse);

        void showInvalidHistory(int status);
    }

    private ServerAPI serverAPI;

    public HistoryPresenter() {
        this.serverAPI = ServerAPI.Builder.build();
    }

    public void callGetChallenges() {
        String token = PersistentPreferences.getInstance().getUserToken();
        final View view = view();

        if (view != null) {
            view.showLoadingHistoryUi();

            subscriptions.add(serverAPI.getChallenges(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((challengeResponse) -> {
                        if (challengeResponse.status == 0) {
                            view.showContentHistoryUi(challengeResponse);
                        } else view.showInvalidHistory(challengeResponse.status);
                    }, throwable -> {
                        view.showErrorHistoryUi(throwable);
                    }));
        }
    }
}
