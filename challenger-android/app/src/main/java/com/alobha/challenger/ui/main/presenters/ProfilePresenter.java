package com.alobha.challenger.ui.main.presenters;

import android.support.annotation.NonNull;

import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.models.UserResponse;
import com.alobha.challenger.ui.base.BasePresenter;
import com.alobha.challenger.ui.base.MvpView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mrNRG on 20.06.2016.
 */
public class ProfilePresenter extends BasePresenter<ProfilePresenter.View> {

    public interface View extends MvpView {
        void showLoadingProfileUi();

        void showErrorProfileUi(@NonNull Throwable throwable);

        void showContentProfileUi(@NonNull UserResponse userResponse);

        void showInvalidProfile(int status);
    }

    private ServerAPI serverAPI;

    public ProfilePresenter() {
        this.serverAPI = ServerAPI.Builder.build();
    }

    public void callProfileEdit(String email, String firstName, String phone) {
        String token = PersistentPreferences.getInstance().getUserToken();
        final View view = view();

        if (view != null) {
            view.showLoadingProfileUi();

            subscriptions.add(serverAPI.editProfile(token, email, firstName, phone)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((userResponse) -> {
                        if (userResponse.status == 0) {
                            view.showContentProfileUi(userResponse);
                        } else view.showInvalidProfile(userResponse.status);
                    }, throwable -> {
                        view.showErrorProfileUi(throwable);
                    }));
        }
    }

    public void callChangeAvatar(String base64avatar) {
        String token = PersistentPreferences.getInstance().getUserToken();
        final View view = view();

        if (view != null) {
            view.showLoadingProfileUi();

            subscriptions.add(serverAPI.changeAvatar(token, base64avatar)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((userResponse) -> {
                        if (userResponse.status == 0) {
                            view.showContentProfileUi(userResponse);
                        } else view.showInvalidProfile(userResponse.status);
                    }, throwable -> {
                        view.showErrorProfileUi(throwable);
                    }));
        }
    }

    public void callChangePassword(String oldPassword, String newPassword) {
        String token = PersistentPreferences.getInstance().getUserToken();
        final View view = view();

        if (view != null) {
            view.showLoadingProfileUi();

            subscriptions.add(serverAPI.changePassword(token, oldPassword, newPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((userResponse) -> {
                        if (userResponse.status == 0) {
                            view.showContentProfileUi(userResponse);
                        } else view.showInvalidProfile(userResponse.status);
                    }, throwable -> {
                        view.showErrorProfileUi(throwable);
                    }));
        }
    }
}
