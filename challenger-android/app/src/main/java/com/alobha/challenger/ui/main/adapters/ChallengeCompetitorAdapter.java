package com.alobha.challenger.ui.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.utils.DefaultFormatter;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by mrNRG on 27.06.2016.
 */
public class ChallengeCompetitorAdapter extends RecyclerView.Adapter<ChallengeCompetitorAdapter.CompetitorViewHolder> {

    private Challenge challenge;
    private List<Competitor> competitors;
    private int position;

    private DecimalFormat distanceFormat = DefaultFormatter.distanceFormat;
    private DecimalFormat speedFormat = DefaultFormatter.speedFormat;
    private SimpleDateFormat timeFormat = DefaultFormatter.timeFormat;

    public ChallengeCompetitorAdapter() {
        this.competitors = new ArrayList<>();
    }

    @Override
    public CompetitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_challenge_competitor, parent, false);

        return new CompetitorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CompetitorViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        Competitor competitor = competitors.get(position);

        holder.tvPosition.setText(String.valueOf(competitor.position));

        String avatarURL = ServerAPI.BASE_URL + competitor.user.avatar;
        Picasso.with(context)
                .load(avatarURL)
                .placeholder(R.mipmap.avatar_placeholder)
                .fit().centerCrop().transform(new CropCircleTransformation())
                .into(holder.ivAvatar);
        holder.tvName.setText(competitor.user.first_name);
        holder.tvDistance.setText(String.format(context.getString(R.string.distance_wrapper), distanceFormat.format(competitor.distance / 1000)));
        holder.tvTime.setText(timeFormat.format(competitor.time));
        holder.tvSpeed.setText(String.format(context.getString(R.string.speed_wrapper), speedFormat.format(competitor.avg_speed)));
    }

    @Override
    public int getItemCount() {
        return this.competitors.size();
    }

    public class CompetitorViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvPosition)
        TextView tvPosition;

        @Bind(R.id.ivAvatar)
        ImageView ivAvatar;

        @Bind(R.id.tvName)
        TextView tvName;

        @Bind(R.id.tvDistance)
        TextView tvDistance;

        @Bind(R.id.tvTime)
        TextView tvTime;

        @Bind(R.id.tvSpeed)
        TextView tvSpeed;

        public CompetitorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
        this.competitors.clear();
        this.competitors.addAll(challenge.competitors);
        notifyDataSetChanged();
    }
}
