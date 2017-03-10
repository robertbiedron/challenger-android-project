package com.alobha.challenger.data.api.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alobha.challenger.data.entities.User;

import java.util.Date;

public class PersistentPreferences {

    private static final String LOGGED_IN = "logged_in";
    private static final String USER_ID = "user_id";
    private static final String USER_TOKEN = "user_token";
    private static final String USER_USERNAME = "user_username";
    private static final String USER_FIRST_NAME = "user_first_name";
    private static final String USER_LAST_NAME = "user_last_name";
    private static final String USER_AVATAR = "user_avatar";
    private static final String USER_PHONE = "user_phone";
    private static final String USER_SOURCE = "user_source";
    private static final String USER_SEX = "user_sex";
    private static final String ANONYMOUS = "anonymous";
    private static final String ACTIVE_CHALLENGE = "active_challenge";
    private static final String ACTIVE_CHALLENGE_PAUSED = "active_challenge_paused";
    private static final String ACTIVE_CHALLENGE_FINISHED = "active_challenge_finished";
    private static final String AVG_SPEED = "avg_speed";
    private static final String AVG_DISTANCE = "avg_distance";
    private static final String DISTANCE = "distance";
    private static final String LAST_DATE = "last_date";
    private static final String TUTORIAL_SHOWN = "tutorial_shown";
    private static final String GCM_TOKEN_SENT = "gcm_token_sent";
    private static final String GCM_TOKEN = "gcm_token";

    private static PersistentPreferences instance;
    private SharedPreferences sharedPreferences;

    private PersistentPreferences(Context appContext) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public static PersistentPreferences getInstance() {
        return instance;
    }

    public static synchronized void createInstance(Context appContext) {
        if (instance == null) {
            instance = new PersistentPreferences(appContext);
        }
    }

    public User getLoggedUser() {
        User user = new User();
        user.id = getUserId();
        user.email = getUsername();
        user.first_name = getFirstName();
        user.last_name = getLastName();
        user.phone = getUserPhone();
        user.source = getUserSource();
        user.avatar = getAvatar();
        user.sex = getSex();
        user.avg_distance = getAvgDistance();
        user.distance = getDistance();
        user.avg_speed = getAvgSpeed();
        user.last_challenge = getLastDate();
        return user;
    }

    public void setLoggedUser(User user) {
        setUserId(user.id);
        setUsername(user.email);
        setFirstName(user.first_name);
        setLastName(user.last_name);
        setUserPhone(user.phone);
        setUserSource(user.source);
        setAvatar(user.avatar);
        setSex(user.sex);
        setAvgDistance(user.avg_distance);
        setDistance(user.distance);
        setAvgSpeed(user.avg_speed);
        setLastDate(user.last_challenge);
        setLoggedIn(true);
    }

    public void removeUser() {
//        removeToken();
        removeSource();
        removeUserId();
        removeUsername();
        removeFirstName();
        removeLastName();
        removeUserPhone();
        removeAvatar();
        removeSex();
        removeActiveChallenge();
        removeActiveChallengePaused();
        removeAvgSpeed();
        removeDistance();
        removeAvgDistance();
        removeLastDate();
        setAnonymous(false);
        setLoggedIn(false);
        setActiveChallengeFinished(false);
        setGcmTokenSent(false);
    }

    public void setUserSource(String source) {
        putString(USER_SOURCE, source);
    }

    public String getUserSource() {
        return getString(USER_SOURCE, "");
    }

    public void setSex(String sex) {
        putString(USER_SEX, sex);
    }

    public String getSex() {
        return getString(USER_SEX, "");
    }

    public void setLoggedIn(boolean value) {
        putBoolean(LOGGED_IN, value);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(LOGGED_IN, false);
    }

    public void setActiveChallengePaused(boolean value) {
        putBoolean(ACTIVE_CHALLENGE_PAUSED, value);
    }

    public boolean isActiveChallengePaused() {
        return sharedPreferences.getBoolean(ACTIVE_CHALLENGE_PAUSED, false);
    }


    public void setAnonymous(boolean value) {
        putBoolean(ANONYMOUS, value);
    }

    public boolean isAnonymous() {
        return sharedPreferences.getBoolean(ANONYMOUS, false);
    }


    public long getUserId() {
        return getLong(USER_ID, 0);
    }

    public void setUserId(long id) {
        putLong(USER_ID, id);
    }

    public String getUsername() {
        return getString(USER_USERNAME, "");
    }

    public void setUsername(String username) {
        putString(USER_USERNAME, username);
    }

    public String getFirstName() {
        return getString(USER_FIRST_NAME, "");
    }

    public void setFirstName(String firstName) {
        putString(USER_FIRST_NAME, firstName);
    }

    public String getLastName() {
        return getString(USER_LAST_NAME, "");
    }

    public void setLastName(String lastName) {
        putString(USER_LAST_NAME, lastName);
    }

    public String getUserToken() {
        return getString(USER_TOKEN, "");
    }

    public void setUserToken(String token) {
        putString(USER_TOKEN, token);
    }

    public String getUserPhone() {
        return getString(USER_PHONE, "");
    }

    public void setUserPhone(String phone) {
        putString(USER_PHONE, phone);
    }

    public String getAvatar() {
        return getString(USER_AVATAR, "");
    }

    public void setAvatar(String avatar) {
        putString(USER_AVATAR, avatar);
    }

    public long getActiveChallenge() {
        return getLong(ACTIVE_CHALLENGE, -1);
    }

    public void setActiveChallenge(long activeChallenge) {
        putLong(ACTIVE_CHALLENGE, activeChallenge);
    }

    public float getAvgSpeed() {
        return getFloat(AVG_SPEED, 0);
    }

    public void setAvgSpeed(float avgSpeed) {
        putFloat(AVG_SPEED, avgSpeed);
    }

    public float getAvgDistance() {
        return getFloat(AVG_DISTANCE, 0);
    }

    public void setAvgDistance(float avgDistance) {
        putFloat(AVG_DISTANCE, avgDistance);
    }

    public float getDistance() {
        return getFloat(DISTANCE, 0);
    }

    public void setDistance(float distance) {
        putFloat(DISTANCE, distance);
    }

    public void setLastDate(Date date) {
        if (date != null)
            putLong(LAST_DATE, date.getTime());
    }

    public Date getLastDate() {
        long millis = getLong(LAST_DATE, -1);
        return millis != -1 ? new Date(millis) : null;
    }

    public void setTutorialShown(boolean shown) {
        putBoolean(TUTORIAL_SHOWN, shown);
    }

    public boolean isActiveChallengeFinished() {
        return getBoolean(ACTIVE_CHALLENGE_FINISHED, false);
    }

    public void setActiveChallengeFinished(boolean finished) {
        putBoolean(ACTIVE_CHALLENGE_FINISHED, finished);
    }

    public boolean isTutorialShown() {
        return getBoolean(TUTORIAL_SHOWN, false);
    }

    public String getGcmToken() {
        return getString(GCM_TOKEN, null);
    }

    public void setGcmToken(String token) {
        putString(GCM_TOKEN, token);
    }

    public boolean isGcmTokenSent() {
        return getBoolean(GCM_TOKEN_SENT, false);
    }

    public void setGcmTokenSent(boolean token) {
        putBoolean(GCM_TOKEN_SENT, token);
    }

    public void removeActiveChallenge() {
        remove(ACTIVE_CHALLENGE);
    }

    public void removeActiveChallengePaused() {
        remove(ACTIVE_CHALLENGE_PAUSED);
    }

    public void removeUserId() {
        remove(USER_ID);
    }

    public void removeToken() {
        remove(USER_TOKEN);
    }

    public void removeUsername() {
        remove(USER_USERNAME);
    }

    public void removeFirstName() {
        remove(USER_FIRST_NAME);
    }

    public void removeLastName() {
        remove(USER_LAST_NAME);
    }

    public void removeUserPhone() {
        remove(USER_PHONE);
    }

    public void removeAvatar() {
        remove(USER_AVATAR);
    }

    public void removeAvgSpeed() {
        remove(AVG_SPEED);
    }

    public void removeDistance() {
        remove(DISTANCE);
    }

    public void removeAvgDistance() {
        remove(AVG_DISTANCE);
    }

    public void removeLastDate() {
        remove(LAST_DATE);
    }

    public void removeSource() {
        remove(USER_SOURCE);
    }

    public void removeSex() {
        remove(USER_SEX);
    }

    public String getString(String name, String def) {
        return sharedPreferences.getString(name, def);
    }

    public int getInt(String name, int def) {
        return sharedPreferences.getInt(name, def);
    }

    public long getLong(String name, long def) {
        return sharedPreferences.getLong(name, def);
    }

    public boolean getBoolean(String name, boolean def) {
        return sharedPreferences.getBoolean(name, def);
    }

    public float getFloat(String name, float def) {
        return sharedPreferences.getFloat(name, def);
    }

    public String putString(String name, String value) {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putString(name, value);
        preferencesEditor.apply();
        return value;
    }

    public void putInt(String name, int value) {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putInt(name, value);
        preferencesEditor.apply();
    }

    public void putLong(String name, long value) {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(name, value);
        preferencesEditor.apply();
    }

    public void putBoolean(String name, boolean value) {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putBoolean(name, value);
        preferencesEditor.apply();
    }

    public void putFloat(String name, float value) {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putFloat(name, value);
        preferencesEditor.apply();
    }

    public void remove(String name) {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.remove(name);
        preferencesEditor.apply();
    }
}
