package com.alobha.challenger.ui.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.utils.DefaultFormatter;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by mrNRG on 27.06.2016.
 */
public class NewChallengeAdapter extends RecyclerView.Adapter<NewChallengeAdapter.NewChallengeViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<User> mData;
    private SortedSet<User> selected;
    private OnItemClickListener mListener;

    public OnItemClickListener getListener() {
        return mListener;
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private SimpleDateFormat dateFormat = DefaultFormatter.dateFormat;
    private DecimalFormat distanceFormat = DefaultFormatter.distanceFormat;
    private DecimalFormat speedFormat = DefaultFormatter.speedFormat;
    private final Calendar calendar = Calendar.getInstance(DefaultFormatter.UTC);

    public NewChallengeAdapter(List<User> mData) {
        this.mData = mData;
        this.selected = new TreeSet<>();
    }

    @Override
    public NewChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_friend, parent, false);

        return new NewChallengeViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(NewChallengeViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        User user = mData.get(position);

        String avatarURL = ServerAPI.BASE_URL + user.avatar;

        Picasso.with(context)
                .load(avatarURL)
                .placeholder(R.mipmap.avatar_placeholder)
                .fit().centerCrop().transform(new CropCircleTransformation())
                .into(holder.ivAvatar);

        holder.tvName.setText(user.first_name);
        holder.tvAvgSpeed.setText(String.format(context.getString(R.string.speed_wrapper), speedFormat.format(user.avg_speed)));
        holder.tvAvgDistance.setText(String.format(context.getString(R.string.distance_wrapper), distanceFormat.format(user.avg_distance)));
        if (user.last_challenge != null) {
            holder.tvLastRun.setText(dateFormat.format(user.last_challenge));
        }
        holder.btnAdd.setChecked(selected.contains(user));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NewChallengeViewHolder extends RecyclerView.ViewHolder {

        private PersistentPreferences preferences;
        private Context context;
        private OnItemClickListener mListener;

        @Bind(R.id.ivAvatar)
        ImageView ivAvatar;

        @Bind(R.id.tvName)
        TextView tvName;

        @Bind(R.id.tvSpeed)
        TextView tvAvgSpeed;

        @Bind(R.id.tvTime)
        TextView tvAvgDistance;

        @Bind(R.id.tvLastRun)
        TextView tvLastRun;

        @Bind(R.id.btnAdd)
        ToggleButton btnAdd;

        public NewChallengeViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            context = itemView.getContext();
            preferences = PersistentPreferences.getInstance();
            mListener = listener;
            ButterKnife.bind(this, itemView);

            btnAdd.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (mListener != null) {
                    mListener.onItemClick(getAdapterPosition());
                }
                User item = getItem(getAdapterPosition());
                if (isChecked) {
                    if (preferences.isAnonymous() && !item.source.equals("Anonymous")) {
                        btnAdd.setChecked(false);
                        if (item.source.equals("Famous"))
                            Toast.makeText(context, context.getString(R.string.please_sign_up_to_add_famous), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, context.getString(R.string.please_sign_up_to_add_others), Toast.LENGTH_LONG).show();
                    } else {
                        if (!selected.contains(item)) {
                            selected.add(item);
                        }
                    }
                } else if (selected.contains(item)) {
                    selected.remove(item);
                }
            });
        }
    }

    public void setData(List<User> friends) {
        this.mData = friends;
    }

    public SortedSet<User> getData() {
        SortedSet<User> tmp = new TreeSet<>((lhs, rhs) -> {
            return lhs.first_name.compareToIgnoreCase(rhs.first_name);
        });
        tmp.addAll(mData);
        return tmp;
    }

    public User getItem(int position) {
        return mData.get(position);
    }

    public SortedSet<User> getSelected() {
        return selected;
    }
}
