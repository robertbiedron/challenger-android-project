package com.alobha.challenger.ui.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alobha.challenger.R;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.data.entities.Competitor_Table;
import com.alobha.challenger.navigation.MFragmentManager;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.adapters.MyResultAdapter;
import com.alobha.challenger.utils.DialogFactory;
import com.alobha.challenger.utils.ImageUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 04.07.2016.
 */
public class NotificationResultsFragment extends BaseFragment {

    private static final String CHALLENGE_NOTIFICATION_RECEIVED = "CHALLENGE_NOTIFICATION_RECEIVED";

    private MyResultAdapter mAdapter;
    private List<Competitor> competitors;

    private long id;

    @Bind(R.id.rvScore)
    RecyclerView recyclerView;

    public static NotificationResultsFragment newInstance() {
        NotificationResultsFragment fragment = new NotificationResultsFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_results, container, false);
        ButterKnife.bind(this, view);

        competitors = new ArrayList<>();
        id = getArguments().getLong(NotificationsFragment.NOTIFICATION_ID);
        competitors = SQLite.select()
                .from(Competitor.class)
                .where(Competitor_Table.challenge_id.eq(id))
                .queryList();

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

        mAdapter.setCompetitors(competitors);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_loading)));
    }

    @Override
    protected void initializePresenter() {
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
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        super.onDestroy();
    }
}
