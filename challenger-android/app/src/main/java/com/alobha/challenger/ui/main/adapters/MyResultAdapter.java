package com.alobha.challenger.ui.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.utils.DefaultFormatter;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by mrNRG on 27.06.2016.
 */
public class MyResultAdapter extends RecyclerView.Adapter<MyResultAdapter.MyResultsViewHolder> {

    private Challenge challenge;
    private List<Competitor> competitors;

    private SimpleDateFormat timeFormat = DefaultFormatter.timeFormat;
    private DecimalFormat speedFormat = DefaultFormatter.speedFormat;
    private DecimalFormat distanceFormat = DefaultFormatter.distanceFormat;
    private final Calendar calendar = Calendar.getInstance(DefaultFormatter.UTC);

    public MyResultAdapter() {
        this.competitors = new ArrayList<>();
    }

    @Override
    public MyResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_competitor, parent, false);

        return new MyResultsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyResultsViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Competitor competitor = competitors.get(position);

        holder.tvNumber.setText(String.valueOf(competitor.position));
        if (competitor.user.id == PersistentPreferences.getInstance().getUserId()) {
            holder.tvNumber.setBackground(context.getResources().getDrawable(R.drawable.score_color_accent));
        } else
            holder.tvNumber.setBackground(context.getResources().getDrawable(R.drawable.score_number_normal));

        String avatarURL = ServerAPI.BASE_URL + competitor.user.avatar;
        Picasso.with(context)
                .load(avatarURL)
                .placeholder(R.mipmap.avatar_placeholder)
                .fit().centerCrop().transform(new CropCircleTransformation())
                .into(holder.ivAvatar);

        holder.tvName.setText(competitor.user.first_name);
        holder.tvAvgSpeed.setText(String.format(context.getString(R.string.speed_wrapper), speedFormat.format(competitor.avg_speed)));
        holder.tvDistance.setText(String.format(context.getString(R.string.distance_wrapper), distanceFormat.format(competitor.distance / GlobalConstants.METERS_IN_KM)));
        calendar.setTimeInMillis(competitor.time);
        holder.tvTime.setText(timeFormat.format(calendar.getTime()));
    }

    @Override
    public int getItemCount() {
        return competitors.size();
    }

    public class MyResultsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvNumber)
        TextView tvNumber;

        @Bind(R.id.ivAvatar)
        ImageView ivAvatar;

        @Bind(R.id.tvName)
        TextView tvName;

        @Bind(R.id.tvSpeed)
        TextView tvAvgSpeed;

        @Bind(R.id.tvDistance)
        TextView tvDistance;

        @Bind(R.id.tvTime)
        TextView tvTime;

        public MyResultsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
        if (this.challenge != null) {
            setCompetitors(challenge.competitors);
        }
    }

    public void setCompetitors(List<Competitor> competitors) {
        this.competitors.clear();
        this.competitors.addAll(competitors);
    }
}
