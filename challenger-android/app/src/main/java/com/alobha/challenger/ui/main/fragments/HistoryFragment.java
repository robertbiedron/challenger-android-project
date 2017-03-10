package com.alobha.challenger.ui.main.fragments;

import android.content.Context;
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
import android.widget.LinearLayout;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.ChallengeResponse;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.navigation.MFragmentManager;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.adapters.HistoryAdapter;
import com.alobha.challenger.ui.main.presenters.HistoryPresenter;
import com.alobha.challenger.utils.DialogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mrNRG on 24.06.2016.
 */
public class HistoryFragment extends BaseFragment implements HistoryPresenter.View, SwipeRefreshLayout.OnRefreshListener, HistoryAdapter.OnItemClickListener {

    public static final String HISTORY_ITEM = "HISTORY_ITEM";
    private OnEventListener mCallback;

    private HistoryPresenter presenter;
    private HistoryAdapter mAdapter;
    private List<Challenge> challenges;

    @Bind(R.id.rvScore)
    RecyclerView recyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.ll_hint)
    LinearLayout ll_hint;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEventListener) context;
        } catch (ClassCastException e) {
            Log.e(getTag(), e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        challenges = new ArrayList<>();
        setUpAdapter(challenges);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_history));
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

    @Override
    public void showLoadingHistoryUi() {
        refreshLayout.setRefreshing(false);
        getLoadingDialog().show();
        hideKeyboard();
    }

    @Override
    public void showErrorHistoryUi(@NonNull Throwable throwable) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();
        ll_hint.setVisibility(View.VISIBLE);
        Log.d(getTag(), "error", throwable);
    }

    @Override
    public void showContentHistoryUi(@NonNull ChallengeResponse challengeResponse) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();
        challenges = challengeResponse.challenges;
        Collections.reverse(challenges);
        mAdapter.setChallenges(challenges);
        mAdapter.notifyDataSetChanged();

        if (challenges.size() > 0) {
            ll_hint.setVisibility(View.GONE);
        } else ll_hint.setVisibility(View.VISIBLE);
    }

    @Override
    public void showInvalidHistory(int status) {
        refreshLayout.setRefreshing(false);
        hideLoadingDialog();
        ll_hint.setVisibility(View.VISIBLE);
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    private void setUpAdapter(List<Challenge> challengeList) {
        mAdapter = new HistoryAdapter(challengeList);
        mAdapter.setOnClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
    }

    @Override
    public void onRefresh() {
        presenter.callGetChallenges();
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
        mCallback.onEvent(GlobalConstants.MY_RESULTS_FRAGMENT);
        Bundle args = new Bundle();
        args.putInt(HISTORY_ITEM, position);
        MFragmentManager.nextFragment(MFragmentManager.MAIN_MY_RESULTS_FRAGMENT, args);
    }
}
