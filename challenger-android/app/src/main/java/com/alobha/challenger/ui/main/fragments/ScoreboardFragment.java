package com.alobha.challenger.ui.main.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.TopResponse;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.adapters.ScoreboardAdapter;
import com.alobha.challenger.ui.main.presenters.ScoreboardPresenter;
import com.alobha.challenger.utils.DefaultFormatter;
import com.alobha.challenger.utils.DialogFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mrNRG on 04.07.2016.
 */
public class ScoreboardFragment extends BaseFragment
        implements ScoreboardPresenter.View,
        SwipeRefreshLayout.OnRefreshListener,
        RadioGroup.OnCheckedChangeListener {

    private ScoreboardPresenter presenter;
    private ScoreboardAdapter mAdapter;
    private List<User> topUsers;

    private PersistentPreferences preferences;
    private DecimalFormat distanceFormat = DefaultFormatter.distanceFormat;
    private DecimalFormat speedFormat = DefaultFormatter.speedFormat;

    @Bind(R.id.rgUserCategory)
    RadioGroup rgUserCategory;

//    @Bind(R.id.tvNumber)
//    TextView tvNumber;
//
//    @Bind(R.id.tvDistance)
//    TextView tvTotal;
//
//    @Bind(R.id.tvTime)
//    TextView tvAvgDistance;
//
//    @Bind(R.id.tvSpeed)
//    TextView tvAvgSpeed;

    @Bind(R.id.rvUsers)
    RecyclerView recyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    public static ScoreboardFragment newInstance() {
        ScoreboardFragment fragment = new ScoreboardFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scoreboard, container, false);
        preferences = PersistentPreferences.getInstance();
        ButterKnife.bind(this, view);

        rgUserCategory.setOnCheckedChangeListener(this);

        topUsers = new ArrayList<>();
        setUpAdapter();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_scoreboard));

        if (preferences.isAnonymous()) {
            rgUserCategory.check(R.id.rbOverall);
        } else {
            if (savedInstanceState == null)
                rgUserCategory.check(R.id.rbFriends);
        }
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_loading)));
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new ScoreboardPresenter();
        this.presenter.bindView(this);
    }

    @Override
    public void showLoadingScoreboardUi() {
        refreshLayout.setRefreshing(false);
        getLoadingDialog().show();
        hideKeyboard();
    }

    @Override
    public void showErrorScoreboardUi(@NonNull Throwable throwable) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();
        Log.d(getTag(), "error", throwable);
    }

    @Override
    public void showContentScoreboardUi(@NonNull TopResponse topResponse) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();

        setUserData(topResponse);
        topUsers = topResponse.topUsers;

        mAdapter.setTopUsers(topResponse);
        mAdapter.notifyDataSetChanged();
    }

    private void setUserData(TopResponse topResponse) {
        //tvNumber.setText(topResponse.userPosition != 0 ? String.valueOf(topResponse.userPosition) : "1");
        //tvTotal.setText(String.format(getContext().getString(R.string.distance_wrapper), distanceFormat.format(preferences.getDistance())));
        //tvAvgDistance.setText(String.format(getContext().getString(R.string.distance_wrapper), distanceFormat.format(preferences.getAvgDistance())));
        //tvAvgSpeed.setText(String.format(getContext().getString(R.string.speed_wrapper), speedFormat.format(preferences.getAvgSpeed())));
    }


    @Override
    public void showInvalidScoreboard(int status) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    private void setUpAdapter() {
        mAdapter = new ScoreboardAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        presenter.unbindView(this);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        updateUserList();
    }

    private void updateUserList() {
        loadTop();
    }

    private void loadTop() {
        switch (rgUserCategory.getCheckedRadioButtonId()) {
            case R.id.rbOverall:
                presenter.callGetTopAll();
                break;

            case R.id.rbFriends:
                presenter.callGetTopFriends();
                break;

//            case R.id.rbSimilar:
//                presenter.callGetTopNear();
//                break;
        }
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
}
