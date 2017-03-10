package com.alobha.challenger.ui.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.utils.DefaultFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 27.06.2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<Challenge> challenges;
    private OnItemClickListener mListener;

    public OnItemClickListener getListener() {
        return mListener;
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private SimpleDateFormat dateFormat = DefaultFormatter.dateFormat;
    private SimpleDateFormat timeFormat = DefaultFormatter.timeFormat;
    private DecimalFormat decimalFormat = DefaultFormatter.distanceFormat;
    private final Calendar calendar = Calendar.getInstance(DefaultFormatter.UTC);

    public HistoryAdapter(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_challenge, parent, false);

        return new HistoryViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        Challenge challenge = challenges.get(position);

        holder.tvDate.setText(dateFormat.format(challenge.end_date));
        holder.tvDistance.setText(String.format(context.getString(R.string.distance_wrapper), decimalFormat.format(challenge.distance / GlobalConstants.METERS_IN_KM)));
        calendar.setTimeInMillis(challenge.time);
        holder.tvTime.setText(timeFormat.format(calendar.getTime()));

        if (challenge.competitors != null) {
            Competitor userCompetitor = challenge.getCompetitorById(PersistentPreferences.getInstance().getUserId());
            if (userCompetitor != null) {
                holder.tvPosition.setText(String.valueOf(userCompetitor.position));
            } else holder.tvPosition.setText("0");
        }
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private OnItemClickListener mListener;

        @Bind(R.id.tvDate)
        TextView tvDate;

        @Bind(R.id.tvDistance)
        TextView tvDistance;

        @Bind(R.id.tvTime)
        TextView tvTime;

        @Bind(R.id.tvPosition)
        TextView tvPosition;

        public HistoryViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ll_view_challenge_history)
        public void onChallengeItemClick() {
            if (mListener != null) {
                mListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }
}
