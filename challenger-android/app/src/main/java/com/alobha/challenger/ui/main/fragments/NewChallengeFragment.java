package com.alobha.challenger.ui.main.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alobha.challenger.ChallengerApp;
import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.AnonymousSyncResponse;
import com.alobha.challenger.data.api.models.ContactsResponse;
import com.alobha.challenger.data.api.models.FamousSyncResponse;
import com.alobha.challenger.data.api.services.SyncService;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.navigation.MFragmentManager;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.activities.MainActivity;
import com.alobha.challenger.ui.main.adapters.NewChallengeAdapter;
import com.alobha.challenger.ui.main.presenters.SyncFriendsPresenter;
import com.alobha.challenger.utils.DefaultFormatter;
import com.alobha.challenger.utils.DialogFactory;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 29.06.2016.
 */
public class NewChallengeFragment extends BaseFragment
        implements SyncFriendsPresenter.View,
        SwipeRefreshLayout.OnRefreshListener,
        NewChallengeAdapter.OnItemClickListener,
        RadioGroup.OnCheckedChangeListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        DiscreteSeekBar.OnProgressChangeListener {


    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CONTACTS = 2;

    private Dialog permissionDialog;

    private LocationManager locationManager;
    private AlertDialog noGpsDialog;

    private SyncFriendsPresenter presenter;
    private NewChallengeAdapter mAdapter;
    private List<User> friends;

    private DecimalFormat decimalFormat = DefaultFormatter.distanceFormat;
    private double distance;

    private PersistentPreferences preferences;
    private User u1;
    private User u2;

    @Bind(R.id.rgUserCategory)
    RadioGroup rgUserCategory;

    @Bind(R.id.svSearch)
    SearchView svSearch;

    @Bind(R.id.rvUsers)
    RecyclerView recyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.ll_distance)
    LinearLayout ll_distance;

    @Bind(R.id.ll_hint)
    LinearLayout ll_hint;

    @Bind(R.id.cr_hint_view)
    TextView text_hint;

    @Bind(R.id.sbDistance)
    DiscreteSeekBar sbDistance;

    @Bind(R.id.tvKilometersHint)
    TextView tvKilometersHint;

    @Bind(R.id.btnBegin)
    Button btnBegin;

    public static NewChallengeFragment newInstance() {
        NewChallengeFragment fragment = new NewChallengeFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            syncUsers();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        noGpsDialog = DialogFactory.createAlertMessageNoGps(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_challenge, container, false);
        preferences = PersistentPreferences.getInstance();
        ButterKnife.bind(this, view);

        rgUserCategory.setOnCheckedChangeListener(this);

        svSearch.setOnQueryTextListener(this);
        svSearch.setOnCloseListener(this);

        ll_distance.setVisibility(View.GONE);

        sbDistance.setOnProgressChangeListener(this);
        sbDistance.setTrackColor(getResources().getColor(R.color.colorAccent));
        sbDistance.setScrubberColor(getResources().getColor(R.color.colorPrimary));
        sbDistance.setThumbColor(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorAccent));

        tvKilometersHint.setText(String.format(getString(R.string.distance_wrapper), decimalFormat.format(distance)));

        friends = new ArrayList<>();
        setUpAdapter(friends);

        return view;
    }

    private double convertDistance(int intDistance) {
        return intDistance * 0.2;
    }

    @OnClick(R.id.btnBegin)
    public void onButtonBeginClick() {
        if (mAdapter.getSelected().size() > 0) {
            ll_distance.setVisibility(View.VISIBLE);
            if (distance > 0) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    noGpsDialog.show();
                } else if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        || (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    requestLocationPermission();
                } else
                    startNewChallenge();
            } else
                DialogFactory.showSnackBarShort(getActivity(), getString(R.string.choose_distance));
        } else DialogFactory.showSnackBarShort(getActivity(), getString(R.string.choose_opponent));

    }

    private void requestLocationPermission() {
        boolean ACCESS_COARSE_LOCATION = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean ACCESS_FINE_LOCATION = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (ACCESS_FINE_LOCATION || ACCESS_COARSE_LOCATION) {
            permissionDialog = DialogFactory.constructAlertDialogWithYesNoButton(getActivity(),
                    getString(R.string.location_permission_dialog_title),
                    getString(R.string.location_permission_dialog_message),
                    new DialogFactory.OnDialogYesNoButtonClickedListener() {

                        @Override
                        public void onYesClick() {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        }

                        @Override
                        public void onNoClick() {
                            permissionDialog.dismiss();
                        }
                    });
            permissionDialog.setCancelable(false);
            permissionDialog.show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    private void requestContactsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
            permissionDialog = DialogFactory.constructAlertDialogWithYesNoButton(getActivity(),
                    getString(R.string.contacts_permission_dialog_title),
                    getString(R.string.contacts_permission_dialog_message),
                    new DialogFactory.OnDialogYesNoButtonClickedListener() {

                        @Override
                        public void onYesClick() {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
                        }

                        @Override
                        public void onNoClick() {
                            permissionDialog.dismiss();
                        }
                    });
            permissionDialog.setCancelable(false);
            permissionDialog.show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
        }
    }

    private void startNewChallenge() {
        final Challenge newChallenge = new Challenge();
        List<User> userList = new ArrayList<>(mAdapter.getSelected());
        List<Competitor> competitors = new ArrayList<>();

//        newChallenge.owner = preferences.getLoggedUser();
        newChallenge.host = preferences.getUserId();
        newChallenge.distance = (float) distance * 1000;
        // add host like a competitor
        Competitor ownerCompetitor = new Competitor();
        ownerCompetitor.user = preferences.getLoggedUser();
        competitors.add(ownerCompetitor);

        for (int k = 0; k < userList.size(); k++) {
            Competitor c = new Competitor();
            c.user = userList.get(k);
            competitors.add(c);
        }
        newChallenge.competitors = competitors;
        preferences.setActiveChallenge(newChallenge.id);

        openMapFragment(newChallenge);
    }

    private void openMapFragment(Challenge newChallenge) {
        Bundle args = new Bundle();
        args.putSerializable(GlobalConstants.CHALLENGE_NEW_CHALLENGE, newChallenge);
        MFragmentManager.nextFragment(MFragmentManager.MAIN_MAP_CHALLENGE_FRAGMENT, args);
    }

    private void setUpAdapter(List<User> friends) {
        mAdapter = new NewChallengeAdapter(friends);
        mAdapter.setOnClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_new_challenge));

    //    if (preferences.isAnonymous()) {
//            rgUserCategory.check(R.id.rbAnonymous);
   //     } else {
            if (savedInstanceState == null)
                rgUserCategory.check(R.id.rbContacts);
       // }

        u1 = new User();
        u2 = new User();
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_loading)));
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new SyncFriendsPresenter();
        this.presenter.bindView(this);
    }

    @Override
    public void showLoadingUi() {
        getLoadingDialog().show();
        hideKeyboard();
    }

    @Override
    public void showErrorUi(@NonNull Throwable throwable) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();
        ll_hint.setVisibility(View.VISIBLE);
        Log.d(getTag(), "error", throwable);
    }

    @Override
    public void showContacts(@NonNull ContactsResponse contactsResponse) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();
        friends = contactsResponse.contacts;

        mAdapter.setData(friends);
        mAdapter.notifyDataSetChanged();

        showHintIfNeeded();
    }

    private void showHintIfNeeded() {
        if (friends.size() == 0) {
            ll_hint.setVisibility(View.VISIBLE);
        } else ll_hint.setVisibility(View.GONE);
    }

    @Override
    public void showAnonymous(@NonNull AnonymousSyncResponse anonymousResponse) {
        refreshLayout.setRefreshing(false);
        ll_hint.setVisibility(View.GONE);
        hideLoadingDialog();
        friends = anonymousResponse.anonymous;

        mAdapter.setData(friends);
        mAdapter.notifyDataSetChanged();

        showHintIfNeeded();
    }

    @Override
    public void showFamous(@NonNull FamousSyncResponse famousSyncResponse) {
        refreshLayout.setRefreshing(false);
        ll_hint.setVisibility(View.GONE);
        hideLoadingDialog();
        friends = famousSyncResponse.famous;

        mAdapter.setData(friends);
        mAdapter.notifyDataSetChanged();

        showHintIfNeeded();
    }

    @Override
    public void showInvalid(int status) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();
        ll_hint.setVisibility(View.VISIBLE);
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        presenter.unbindView(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position) {
        if (ll_distance.getVisibility() != View.VISIBLE) {
            btnBegin.setText(getString(R.string.begin_label));
            ll_distance.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        updateUserList();
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        distance = convertDistance(value);
        tvKilometersHint.setText(String.format(getString(R.string.distance_wrapper), decimalFormat.format(distance)));
        seekBar.setIndicatorFormatter(decimalFormat.format(distance));
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button;
        for (int i = 0; i < group.getChildCount(); i++) {
            button = (RadioButton) group.getChildAt(i);
            if (button.getId() == checkedId) {
                button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                updateUserList();
            } else {
                button.setPaintFlags(button.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
            }
        }
    }

    private void updateUserList() {
        loadCompetitors();
    }

    private void syncUsers() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestContactsPermission();
        } else {
            SyncService.startService(getContext(), SyncService.SYNC_CONTACTS);
        }
        SyncService.startService(getContext(), SyncService.SYNC_FRIENDS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DialogFactory.showSnackBarShort(getActivity(), getString(R.string.permission_granted));
                } else {
                    DialogFactory.showSnackBarShort(getActivity(), getString(R.string.permission_denied));
                }
                break;
            }

            case REQUEST_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DialogFactory.showSnackBarShort(getActivity(), getString(R.string.permission_granted));
                } else {
                    DialogFactory.showSnackBarShort(getActivity(), getString(R.string.permission_denied));
                }
                break;
            }
        }
    }

    public void loadCompetitors() {
        switch (rgUserCategory.getCheckedRadioButtonId()) {
            case R.id.rbContacts:
                text_hint.setHint(getString(R.string.message_no_friends));
                presenter.callGetContacts();
                break;

            case R.id.rbFamous:
                text_hint.setHint(getString(R.string.message_no_famous));
                presenter.callGetFamous();
                break;

//            case R.id.rbAnonymous:
//                text_hint.setHint(getString(R.string.message_no_anonymous));
//                presenter.callGetAnonymous();
//                break;
        }
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setFilter(newText);
        return false;
    }

    private void setFilter(String newText) {
        u1.first_name = newText;
        u2.first_name = newText + Character.MAX_VALUE;
        SortedSet<User> tmpSet;
        List<User> tmpList = new ArrayList<>();
        if (mAdapter != null) {
            tmpSet = mAdapter.getData().subSet(u1, u2);
            tmpList.addAll(tmpSet);

            if (newText.equals("")) mAdapter.setData(friends);
            else mAdapter.setData(tmpList);
            mAdapter.notifyDataSetChanged();
        }
    }
}
