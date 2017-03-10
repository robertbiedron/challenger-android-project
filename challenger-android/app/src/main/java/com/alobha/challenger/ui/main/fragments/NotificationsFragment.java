package com.alobha.challenger.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Challenge_Table;
import com.alobha.challenger.navigation.MFragmentManager;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.adapters.HistoryAdapter;
import com.alobha.challenger.ui.main.presenters.HistoryPresenter;
import com.alobha.challenger.utils.DialogFactory;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mrNRG on 24.06.2016.
 */
public class NotificationsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, HistoryAdapter.OnItemClickListener {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
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

    @Bind(R.id.cr_hint_view)
    TextView text_hint;

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();

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
        text_hint.setHint(getString(R.string.message_no_notifications));

        updateNotificationList();

        return view;
    }

    private void updateNotificationList() {
        SQLite.delete().from(Challenge.class).where(Challenge_Table.seen.eq(true));
        challenges = SQLite.select().from(Challenge.class)
                .where(Challenge_Table.seen.eq(false))
                .orderBy(Challenge_Table.start_date, false).queryList();
        setUpAdapter(challenges);
        mAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
        if (challenges.size() > 0) {
            ll_hint.setVisibility(View.GONE);
        } else ll_hint.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_notifications));
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_loading)));
    }

    @Override
    protected void initializePresenter() {
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
        updateNotificationList();
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position) {
        mCallback.onEvent(GlobalConstants.MY_RESULTS_FRAGMENT);
        Bundle args = new Bundle();
        long id = challenges.get(position).id;
        args.putLong(NOTIFICATION_ID, id);
        MFragmentManager.nextFragment(MFragmentManager.MAIN_NOTIFICATION_RESULTS_FRAGMENT, args);
        challenges.get(position).seen = true;
        challenges.get(position).save();
    }
}
