package com.alobha.challenger.ui.main.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.business.gmc.RegistrationIntentService;
import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.navigation.MFragmentManager;
import com.alobha.challenger.navigation.Navigator;
import com.alobha.challenger.ui.auth.presenters.AnonymousLoginPresenter;
import com.alobha.challenger.ui.base.BaseActivity;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.utils.DialogFactory;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by mrNRG on 13.06.2016.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, BaseFragment.OnEventListener ,View.OnClickListener{

    public static final String CURRENT_NAVIGATION_ITEM = "CURRENT_NAVIGATION_ITEM";
    public static boolean active = false;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private Navigator navigator;
    private PersistentPreferences preferences;

    private CircleImageView ivAvatar;
    private TextView tvName;

    private View toolbar_bottom;
    private FrameLayout fl_content;

    private AlertDialog signOutDialog;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_main, savedInstanceState);
        initialize();
        if (savedInstanceState == null) {
            initMainFragment();
        } else {
            initCurrentFragment(savedInstanceState.getInt(CURRENT_NAVIGATION_ITEM, 0));
        }
        if (!PersistentPreferences.getInstance().isGcmTokenSent()) {
            RegistrationIntentService.startService(this, RegistrationIntentService.SUBSCRIBE_FOR_GCM);
        }



        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("FacebookShare","Success");
            }

            @Override
            public void onCancel() {
                Log.d("FacebookShare","Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FacebookShare","error");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_NAVIGATION_ITEM, MFragmentManager.getItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        checkForNotifications();
    }

    private void checkForNotifications() {
        long challengeId = getIntent().getLongExtra("challenge_id", -1);
        if (challengeId != -1) {
            MFragmentManager.selectNavigationItem(R.id.notifications);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    private void initCurrentFragment(int id) {
        MFragmentManager.selectNavigationItem(id);
    }

    private void initMainFragment() {
        MFragmentManager.selectNavigationItem(R.id.new_challenge);
    }

    private void initialize() {
        navigator = new Navigator();
        preferences = PersistentPreferences.getInstance();

        fl_content = (FrameLayout) findViewById(R.id.content);
        toolbar_bottom = findViewById(R.id.toolbar_bottom);
        signOutDialog = DialogFactory.createSignOutDialog(this, navigator);

        initNavigationView();
        MFragmentManager.init(this, navigationView);
    }

    private int getActionBarSize() {
        int mActionBarSize;
        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return mActionBarSize;
    }

    private void initNavigationView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        Button btn = (Button) navigationView.getHeaderView(0).findViewById(R.id.iv_edit);
        btn.setOnClickListener(this);

        manageMenuAccessibility(navigationView);
        initNavigationViewHeader();
    }

    private void initNavigationViewHeader() {
        ivAvatar = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
        tvName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvName);


        String avatarURL = ServerAPI.BASE_URL + preferences.getAvatar();
        String name = preferences.getFirstName();
        Picasso.with(this)
                .load(avatarURL)
                .placeholder(R.mipmap.avatar_placeholder)
                .fit().centerCrop().transform(new CropCircleTransformation())
                .into(ivAvatar);
        tvName.setText(name);
    }

//    @OnClick(R.id.iv_edit)
//    public void onEditClick(){
//        Toast.makeText(getBaseContext(),"Edit Button is clicked",Toast.LENGTH_SHORT).show();
//        hideBottomToolbar();
//        MFragmentManager.nextFragment(MFragmentManager.MAIN_PROFILE_FRAGMENT, null);
//
//    }

    private void manageMenuAccessibility(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        if (preferences.isAnonymous()) {
            //menu.findItem(R.id.profile).setEnabled(false);
            menu.findItem(R.id.scoreboard).setEnabled(false);
            menu.findItem(R.id.invite).setEnabled(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (preferences.getActiveChallenge() != -1) {
            return false;
        }
        MFragmentManager.setItem(item.getItemId());

        switch (item.getItemId()) {
//            case R.id.profile:
//                hideBottomToolbar();
//                MFragmentManager.nextFragment(MFragmentManager.MAIN_PROFILE_FRAGMENT, null);
//                break;

            case R.id.new_challenge:
                hideBottomToolbar();
                if (preferences.isActiveChallengeFinished()) {
                    MFragmentManager.nextFragment(MFragmentManager.MAIN_MY_RESULTS_FRAGMENT, null);
                } else
                    MFragmentManager.nextFragment(MFragmentManager.MAIN_NEW_CHALLENGE_FRAGMENT, null);
                break;

            case R.id.scoreboard:
                showBottomToolbar();
                MFragmentManager.nextFragment(MFragmentManager.MAIN_SCOREBOARD_FRAGMENT, null);
                break;

            case R.id.history:
                showBottomToolbar();
                MFragmentManager.nextFragment(MFragmentManager.MAIN_HISTORY_FRAGMENT, null);
                break;

            case R.id.notifications:
                // TODO: 01.07.2016  implement notifications
//                showBottomToolbar();
//                DialogFactory.showSnackBarLong(this, getString(R.string.message_later_feature));
                MFragmentManager.nextFragment(MFragmentManager.MAIN_NOTIFICATION_FRAGMENT, null);
                break;
            case R.id.share:
                facebook_share();
                break;
            case R.id.invite:
                //shareAppLink();

                invite_friend();
                break;

            case R.id.sign_out:
                signOutDialog.show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void facebook_share(){

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentTitle("To my friends:")
                    .setContentDescription("I'd like to let you know my speed.")
                    .build();

            shareDialog.show(content);
        }

    }

    private void invite_friend(){

        String appLinkUrl, previewImageUrl;

        appLinkUrl = getString(R.string.google_play_link);
        previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }

    private void shareAppLink() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.title_join_me) + " " + getString(R.string.google_play_link));
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.title_share_via)));
    }


    private void showBottomToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fl_content.getLayoutParams();
        params.bottomMargin = getActionBarSize();
        fl_content.setLayoutParams(params);
        toolbar_bottom.setVisibility(View.VISIBLE);
    }

    private void hideBottomToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fl_content.getLayoutParams();
        params.bottomMargin = 0;
        fl_content.setLayoutParams(params);
        toolbar_bottom.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (MFragmentManager.getFragment() != null) {
            MFragmentManager.getFragment().onActivityResult(requestCode, resultCode, data);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onEvent(int requestCode) {
        switch (requestCode) {
            case GlobalConstants.CHANGE_PASSWORD_BUTTON:
                MFragmentManager.nextFragment(MFragmentManager.MAIN_CHANGE_PASSWORD_FRAGMENT, null);
                break;
            case GlobalConstants.USER_PROFILE_UPDATED:
                initNavigationViewHeader();
                break;
            case GlobalConstants.PRECIOUS_CHALLENGES_BUTTON:
                MFragmentManager.selectNavigationItem(R.id.history);
                break;
            case GlobalConstants.MY_RESULTS_FRAGMENT:
                hideBottomToolbar();
                break;
            case GlobalConstants.NOTIFICATION_RESULTS_FRAGMENT:
                hideBottomToolbar();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        } else {
            if (MFragmentManager.getFragmentStackSize() > 0) {
                MFragmentManager.mainFragment();
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                DialogFactory.showToastMessageShort(this, getString(R.string.exit_hint));

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
    }

    @Override
    protected void onPause() {
        drawer.closeDrawer(navigationView);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.iv_edit:
                hideBottomToolbar();
                MFragmentManager.nextFragment(MFragmentManager.MAIN_PROFILE_FRAGMENT, null);
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
    }
}
