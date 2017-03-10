package com.alobha.challenger.data.api;

import com.alobha.challenger.data.api.adapters.ArrayAdapter;
import com.alobha.challenger.data.api.adapters.ChallengeAdapter;
import com.alobha.challenger.data.api.adapters.DateAdapter;
import com.alobha.challenger.data.api.models.AnonymousResponse;
import com.alobha.challenger.data.api.models.AnonymousSyncResponse;
import com.alobha.challenger.data.api.models.ChallengeResponse;
import com.alobha.challenger.data.api.models.ContactsResponse;
import com.alobha.challenger.data.api.models.FamousSyncResponse;
import com.alobha.challenger.data.api.models.StatusResponse;
import com.alobha.challenger.data.api.models.TopResponse;
import com.alobha.challenger.data.api.models.UserResponse;
import com.alobha.challenger.data.entities.Challenge;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.Date;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;


/**
 * Created by mrNRG on 10.06.2016.
 */
public interface ServerAPI {

    String BASE_URL = "http://ec2-52-24-115-51.us-west-2.compute.amazonaws.com";

    String ANONYMOUS_NEW_ANONYMOUS = "/api/v1/anonymous/new-anonymous/";
    String ANONYMOUS_GET_ANONYMOUS_USERS = "/api/v1/anonymous/get-anonymous-users/";
    String ANONYMOUS_GET_FAMOUS_USERS = "/api/v1/anonymous/get-famous-users/";

    String CHALLENGES_FINISH_CHALLENGE = "/api/v1/challenges/finish-challenge/";
    String CHALLENGES_GET_USER_CHALLENGES = "/api/v1/challenges/get-user-challenges/";

    String PUSHES_SUBSCRIBE_FOR_PUSH = "/api/v1/pushes/subscribe-for-push/";

    String TOP_GET_FRIENDS_TOP = "/api/v1/top/get-friends-top/";
    String TOP_GET_ALL_TOP = "/api/v1/top/get-all-top/";
    String TOP_GET_NEAR_TOP = "/api/v1/top/get-near-top/";

    String USERS_EMAIL_LOGIN = "/api/v1/users/email-login/";
    String USERS_FACEBOOK_LOGIN = "/api/v1/users/facebook-login/";
    String USERS_EMAIL_REGISTRATION = "/api/v1/users/email-registration/";
    String USERS_EDIT_PROFILE = "/api/v1/users/edit-profile/";
    String USERS_CHANGE_AVATAR = "/api/v1/users/change-avatar/";
    String USERS_CHANGE_PASSWORD = "api/v1/users/change-password/";
    String USERS_SYNC_CONTACTS = "/api/v1/users/sync-contacts/";
    String USERS_RECOVER_PASSWORD = "/api/v1/users/recover-password/";
    String USERS_SYNC_FRIENDS = "/api/v1/users/sync-friends/";
    String USERS_GET_USER_CONTACTS = "/api/v1/users/get-user-contacts/";

    @POST(ANONYMOUS_NEW_ANONYMOUS)
    Observable<AnonymousResponse> loginAnonymous();

    @POST(ANONYMOUS_GET_ANONYMOUS_USERS)
    Observable<AnonymousSyncResponse> syncAnonymous();

    @POST(ANONYMOUS_GET_FAMOUS_USERS)
    Observable<FamousSyncResponse> syncFamous();

    @POST(CHALLENGES_FINISH_CHALLENGE)
    Call<ChallengeResponse> finishChallenge(@Body SyncChallenges contacts);

    @FormUrlEncoded
    @POST(CHALLENGES_GET_USER_CHALLENGES)
    Observable<ChallengeResponse> getChallenges(@Field("token") String token);

    @FormUrlEncoded
    @POST(PUSHES_SUBSCRIBE_FOR_PUSH)
    Call<StatusResponse> subscribeForGCM(@Field("token") String token,
                                               @Field("notification_id") String notificationToken,
                                               @Field("device_type") String deviceType);

    @FormUrlEncoded
    @POST(TOP_GET_FRIENDS_TOP)
    Observable<TopResponse> topFriends(@Field("token") String token);

    @FormUrlEncoded
    @POST(TOP_GET_ALL_TOP)
    Observable<TopResponse> topAll(@Field("token") String token);

    @FormUrlEncoded
    @POST(TOP_GET_NEAR_TOP)
    Observable<TopResponse> topNear(@Field("token") String token);

    @FormUrlEncoded
    @POST(USERS_EMAIL_LOGIN)
    Observable<UserResponse> login(@Field("email") String username,
                                   @Field("password") String password);

    @FormUrlEncoded
    @POST(USERS_FACEBOOK_LOGIN)
    Observable<UserResponse> facebookLogin(@Field("access_token") String accessToken);

    @FormUrlEncoded
    @POST(USERS_EMAIL_REGISTRATION)
    Observable<UserResponse> register(@Field("email") String username,
                                      @Field("password") String password,
                                      @Field("sex") String sex,
                                      @Field("phone") String phone);


    @FormUrlEncoded
    @POST(USERS_EDIT_PROFILE)
    Observable<UserResponse> editProfile(@Field("token") String token,
                                         @Field("email") String email,
                                         @Field("first_name") String firstName,
                                         @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST(USERS_CHANGE_AVATAR)
    Observable<UserResponse> changeAvatar(@Field("token") String token,
                                          @Field("avatar") String base64avatar
    );

    @FormUrlEncoded
    @POST(USERS_CHANGE_PASSWORD)
    Observable<UserResponse> changePassword(@Field("token") String token,
                                            @Field("old_password") String oldPassword,
                                            @Field("new_password") String newPassword);

    @POST(USERS_SYNC_CONTACTS)
    Call<StatusResponse> syncContacts(@Body SyncData contacts);

    @FormUrlEncoded
    @POST(USERS_RECOVER_PASSWORD)
    Observable<StatusResponse> recoverPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST(USERS_SYNC_FRIENDS)
    Call<StatusResponse> syncFriends(@Field("token") String userToken,
                                           @Field("access_token") String facebookToken);

    @FormUrlEncoded
    @POST(USERS_GET_USER_CONTACTS)
    Observable<ContactsResponse> getContacts(@Field("token") String token);


    class Builder {
        private static final OkHttpClient client = new OkHttpClient();
        private static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        static {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(interceptor);
        }

        public static Gson GSON = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateAdapter())
                .registerTypeAdapter(ArrayList.class, new ArrayAdapter())
                .registerTypeAdapter(Challenge.class, new ChallengeAdapter())
                .create();

        private static final Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        public static ServerAPI build() {
            return retrofit.create(ServerAPI.class);
        }
    }
}
