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
import com.alobha.challenger.data.api.models.TopResponse;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.utils.DefaultFormatter;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by mrNRG on 27.06.2016.
 */
public class ScoreboardAdapter extends RecyclerView.Adapter<ScoreboardAdapter.ScoreboardViewHolder> {

    private TopResponse topResponse;
    private List<User> topUsers;
    private int position;

    private DecimalFormat distanceFormat = DefaultFormatter.distanceFormat;
    private DecimalFormat speedFormat = DefaultFormatter.speedFormat;

    public ScoreboardAdapter() {
        this.topUsers = new ArrayList<>();
    }

    @Override
    public ScoreboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_scoreboard, parent, false);

        return new ScoreboardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ScoreboardViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        User user = topUsers.get(position);

        this.position = position + 1;

        holder.tvPosition.setText(String.valueOf(this.position));
        if (this.position == topResponse.userPosition) {
            holder.tvPosition.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        else holder.tvPosition.setTextColor(context.getResources().getColor(android.R.color.darker_gray));


        String avatarURL = ServerAPI.BASE_URL + user.avatar;
        Picasso.with(context)
                .load(avatarURL)
                .placeholder(R.mipmap.avatar_placeholder)
                .fit().centerCrop().transform(new CropCircleTransformation())
                .into(holder.ivAvatar);
        holder.tvName.setText(user.first_name);
        //holder.tvTotal.setText(String.format(context.getString(R.string.distance_wrapper), distanceFormat.format(user.distance)));
        holder.tvAvgDistance.setText(String.format(context.getString(R.string.distance_wrapper), distanceFormat.format(user.avg_distance)));
        holder.tvAvgSpeed.setText(String.format(context.getString(R.string.speed_wrapper), speedFormat.format(user.avg_speed)));
        holder.tvTotal.setText(String.format(context.getString(R.string.speed_wrapper), distanceFormat.format(user.avg_distance*user.avg_speed)));
    }

    @Override
    public int getItemCount() {
        return this.topUsers.size();
    }

    public class ScoreboardViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvPosition)
        TextView tvPosition;

        @Bind(R.id.ivAvatar)
        ImageView ivAvatar;

        @Bind(R.id.tvName)
        TextView tvName;

        @Bind(R.id.tvDistance)
        TextView tvTotal;

        @Bind(R.id.tvTime)
        TextView tvAvgDistance;

        @Bind(R.id.tvSpeed)
        TextView tvAvgSpeed;

        public ScoreboardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setTopUsers(TopResponse topResponse) {
        this.topResponse = topResponse;
        this.topUsers = topResponse.topUsers;
    }
}
