package com.alobha.challenger.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.ui.base.BaseActivity;
import com.alobha.challenger.ui.main.adapters.MyResultAdapter;
import com.alobha.challenger.ui.main.fragments.ChangePasswordFragment;
import com.alobha.challenger.ui.main.fragments.HistoryFragment;
import com.alobha.challenger.ui.main.fragments.MapChallengeFragment;
import com.alobha.challenger.ui.main.fragments.MyResultsFragment;
import com.alobha.challenger.ui.main.fragments.NewChallengeFragment;
import com.alobha.challenger.ui.main.fragments.NotificationResultsFragment;
import com.alobha.challenger.ui.main.fragments.NotificationsFragment;
import com.alobha.challenger.ui.main.fragments.ProfileFragment;
import com.alobha.challenger.ui.main.fragments.ScoreboardFragment;

import java.util.Stack;

/**
 * Created by mrNRG on 28.06.2016.
 */
public class MFragmentManager {

    private static Activity activity;
    private static NavigationView navigationView;
    private static FragmentManager fragmentManager;
    private static Stack<Integer> fragmentStack;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static final int MAIN_PROFILE_FRAGMENT = 1;
    public static final int MAIN_CHANGE_PASSWORD_FRAGMENT = 2;
    public static final int MAIN_NEW_CHALLENGE_FRAGMENT = 3;
    public static final int MAIN_SCOREBOARD_FRAGMENT = 4;
    public static final int MAIN_HISTORY_FRAGMENT = 5;
    public static final int MAIN_MY_RESULTS_FRAGMENT = 6;
    public static final int MAIN_NOTIFICATION_FRAGMENT = 7;
    public static final int MAIN_MAP_CHALLENGE_FRAGMENT = 8;
    public static final int MAIN_NOTIFICATION_RESULTS_FRAGMENT = 9;

    private static Fragment fragment = null;
    private static int item;

    public static void init(Activity activity, NavigationView navigationView) {
        MFragmentManager.activity = activity;
        MFragmentManager.navigationView = navigationView;
        fragmentStack = new Stack<>();
        fragmentManager = ((BaseActivity) MFragmentManager.activity).getCurrentFragmentManager();
    }

    public static void nextFragment(int fragmentId, Bundle arguments) {
        fragmentStack.push(fragmentId);
        Bundle args;
        if (arguments != null) {
            args = arguments;
        } else args = new Bundle();

        String backStackTag = null;
        args.putInt(ARG_SECTION_NUMBER, fragmentId);

        switch (fragmentId) {
            case MAIN_PROFILE_FRAGMENT:
                fragment = ProfileFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;

            case MAIN_CHANGE_PASSWORD_FRAGMENT:
                fragment = ChangePasswordFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;

            case MAIN_NEW_CHALLENGE_FRAGMENT:
                fragment = NewChallengeFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;

            case MAIN_SCOREBOARD_FRAGMENT:
                fragment = ScoreboardFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;

            case MAIN_HISTORY_FRAGMENT:
                fragment = HistoryFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;

            case MAIN_MY_RESULTS_FRAGMENT:
                PersistentPreferences.getInstance().setActiveChallengeFinished(false);
                fragment = MyResultsFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;

            case MAIN_NOTIFICATION_FRAGMENT:
                fragment = NotificationsFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;

            case MAIN_MAP_CHALLENGE_FRAGMENT:
                fragment = MapChallengeFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;

            case MAIN_NOTIFICATION_RESULTS_FRAGMENT:
                fragment = NotificationResultsFragment.newInstance();
                backStackTag = fragment.getClass().getSimpleName();
                break;
        }
        commitFragment(fragment, args, backStackTag);
    }

    private static void commitFragment(Fragment fragment, Bundle args, String backStackTag) {
        if (fragment != null) {
            fragment.setArguments(args);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.addToBackStack(backStackTag);
            ft.replace(R.id.content, fragment);
            ft.commit();
        }
    }

    public static void mainFragment() {
        int id = fragmentStack.pop();
        switch (id) {
//            case MAIN_CHANGE_PASSWORD_FRAGMENT:
//                selectNavigationItem(R.id.profile);
//                break;
            case MAIN_MY_RESULTS_FRAGMENT:
                selectNavigationItem(R.id.history);
                break;
            case MAIN_NOTIFICATION_RESULTS_FRAGMENT:
                selectNavigationItem(R.id.notifications);
                break;
            case MAIN_MAP_CHALLENGE_FRAGMENT:
                fragmentStack.clear();
                break;
            default:
                selectNavigationItem(R.id.new_challenge);
                fragmentStack.clear();
                break;
        }
    }

    public static int getFragmentStackSize() {
        return fragmentStack.size();
    }

    public static Fragment getFragment() {
        return fragment;
    }

    public static void setFragment(Fragment fragment) {
        MFragmentManager.fragment = fragment;
    }

    public static void selectNavigationItem(int id) {
        navigationView.getMenu().performIdentifierAction(id, 0);
        navigationView.setCheckedItem(id);
    }

    public static void setItem(int id) {
        item = id;
    }

    public static int getItem() {
        return item;
    }
}
