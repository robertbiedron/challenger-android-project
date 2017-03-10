package com.alobha.challenger.ui.main.presenters;

import android.support.annotation.NonNull;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.models.TopResponse;
import com.alobha.challenger.ui.base.BasePresenter;
import com.alobha.challenger.ui.base.MvpView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mrNRG on 04.07.2016.
 */
public class ScoreboardPresenter extends BasePresenter<ScoreboardPresenter.View> {

    public interface View extends MvpView {
        void showLoadingScoreboardUi();

        void showErrorScoreboardUi(@NonNull Throwable throwable);

        void showContentScoreboardUi(@NonNull TopResponse topResponse);

        void showInvalidScoreboard(int status);
    }

    private ServerAPI serverAPI;

    public ScoreboardPresenter() {
        this.serverAPI = ServerAPI.Builder.build();
    }

    public void callGetTopFriends() {
        String token = PersistentPreferences.getInstance().getUserToken();
        final View view = view();

        if (view != null) {
            view.showLoadingScoreboardUi();

            subscriptions.add(serverAPI.topFriends(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((topResponse) -> {
                        if (topResponse.status == 0) {
                            view.showContentScoreboardUi(topResponse);
                        } else view.showInvalidScoreboard(topResponse.status);
                    }, throwable -> {
                        view.showErrorScoreboardUi(throwable);
                    }));
        }
    }

    public void callGetTopAll() {
        String token = PersistentPreferences.getInstance().getUserToken();
        final View view = view();

        if (view != null) {
            view.showLoadingScoreboardUi();

            subscriptions.add(serverAPI.topAll(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((topResponse) -> {
                        if (topResponse.status == 0) {
                            view.showContentScoreboardUi(topResponse);
                        } else view.showInvalidScoreboard(topResponse.status);
                    }, throwable -> {
                        view.showErrorScoreboardUi(throwable);
                    }));
        }
    }

    public void callGetTopNear() {
        String token = PersistentPreferences.getInstance().getUserToken();
        final View view = view();

        if (view != null) {
            view.showLoadingScoreboardUi();

            subscriptions.add(serverAPI.topNear(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((topResponse) -> {
                        if (topResponse.status == 0) {
                            view.showContentScoreboardUi(topResponse);
                        } else view.showInvalidScoreboard(topResponse.status);
                    }, throwable -> {
                        view.showErrorScoreboardUi(throwable);
                    }));
        }
    }
}
