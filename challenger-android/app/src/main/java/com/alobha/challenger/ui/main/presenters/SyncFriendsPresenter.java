package com.alobha.challenger.ui.main.presenters;

import android.support.annotation.NonNull;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.models.AnonymousSyncResponse;
import com.alobha.challenger.data.api.models.ContactsResponse;
import com.alobha.challenger.data.api.models.FamousSyncResponse;
import com.alobha.challenger.ui.base.BasePresenter;
import com.alobha.challenger.ui.base.MvpView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mrNRG on 29.06.2016.
 */
public class SyncFriendsPresenter extends BasePresenter<SyncFriendsPresenter.View> {

    public interface View extends MvpView {
        void showLoadingUi();

        void showErrorUi(@NonNull Throwable throwable);

        void showContacts(@NonNull ContactsResponse contactsResponse);

        void showAnonymous(@NonNull AnonymousSyncResponse anonymousSyncResponse);

        void showFamous(@NonNull FamousSyncResponse famousSyncResponse);

        void showInvalid(int status);
    }

    private ServerAPI serverAPI;

    public SyncFriendsPresenter() {
        this.serverAPI = ServerAPI.Builder.build();
    }

    public void callGetContacts() {
        String token = PersistentPreferences.getInstance().getUserToken();
        final View view = view();

        if (view != null) {
            view.showLoadingUi();

            subscriptions.add(serverAPI.getContacts(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((contactsResponse) -> {
                        if (contactsResponse.status == 0) {
                            view.showContacts(contactsResponse);
                        } else view.showInvalid(contactsResponse.status);
                    }, throwable -> {
                        view.showErrorUi(throwable);
                    }));
        }
    }

    public void callGetAnonymous() {
        final View view = view();

        if (view != null) {
            view.showLoadingUi();

            subscriptions.add(serverAPI.syncAnonymous()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((anonymousSyncResponse) -> {
                        if (anonymousSyncResponse.status == 0) {
                            view.showAnonymous(anonymousSyncResponse);
                        } else view.showInvalid(anonymousSyncResponse.status);
                    }, throwable -> {
                        view.showErrorUi(throwable);
                    }));
        }
    }

    public void callGetFamous() {
        final View view = view();

        if (view != null) {
            view.showLoadingUi();

            subscriptions.add(serverAPI.syncFamous()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((famousSyncResponse) -> {
                        if (famousSyncResponse.status == 0) {
                            view.showFamous(famousSyncResponse);
                        } else view.showInvalid(famousSyncResponse.status);
                    }, throwable -> {
                        view.showErrorUi(throwable);
                    }));
        }
    }


}
