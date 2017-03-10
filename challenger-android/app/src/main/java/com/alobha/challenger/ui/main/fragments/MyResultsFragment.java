package com.alobha.challenger.ui.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.ChallengeResponse;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.navigation.MFragmentManager;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.adapters.MyResultAdapter;
import com.alobha.challenger.ui.main.presenters.HistoryPresenter;
import com.alobha.challenger.utils.DialogFactory;
import com.alobha.challenger.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 04.07.2016.
 */
public class MyResultsFragment extends BaseFragment implements HistoryPresenter.View {

    private static final String CHALLENGE_NOTIFICATION_RECEIVED = "CHALLENGE_NOTIFICATION_RECEIVED";
    private HistoryPresenter presenter;
    private MyResultAdapter mAdapter;
    private List<Challenge> challenges;

    private int position;

    @Bind(R.id.rvScore)
    RecyclerView recyclerView;

    public static MyResultsFragment newInstance() {
        MyResultsFragment fragment = new MyResultsFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_results, container, false);
        ButterKnife.bind(this, view);

        challenges = new ArrayList<>();
        challenges = (List<Challenge>) getArguments().getSerializable(CHALLENGE_NOTIFICATION_RECEIVED);
        position = getArguments().getInt(HistoryFragment.HISTORY_ITEM);

        setUpAdapter();

        return view;
    }

    private void setUpAdapter() {
        mAdapter = new MyResultAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_my_result));
        presenter.callGetChallenges();
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_loading)));
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new HistoryPresenter();
        this.presenter.bindView(this);
    }
    
    @OnClick(R.id.btnPreviousChallenges)
    public void onButtonScoreboardClick() {
        MFragmentManager.selectNavigationItem(R.id.scoreboard);
    }

    @OnClick(R.id.btnShare)
    public void onButtonShareClick() {
//        DialogFactory.showToastMessageShort(getActivity(), getString(R.string.message_later_feature));
        ImageUtil.takeAndShareScreenshot(getActivity());
    }

    @Override
    public void showLoadingHistoryUi() {
        getLoadingDialog().show();
        hideKeyboard();
    }

    @Override
    public void showErrorHistoryUi(@NonNull Throwable throwable) {
        hideLoadingDialog();
        Log.d(getTag(), "error", throwable);
    }

    @Override
    public void showContentHistoryUi(@NonNull ChallengeResponse challengeResponse) {
        hideLoadingDialog();
        challenges = challengeResponse.challenges;
        Collections.reverse(challenges);
        if (challenges.size() > 0) {
            mAdapter.setChallenge(challenges.get(position));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showInvalidHistory(int status) {
        hideLoadingDialog();
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        presenter.unbindView(this);
        super.onDestroy();
    }
}
