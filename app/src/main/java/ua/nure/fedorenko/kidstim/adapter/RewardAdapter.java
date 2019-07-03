package ua.nure.fedorenko.kidstim.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.nure.fedorenko.kidstim.R;
import ua.nure.fedorenko.kidstim.entity.Child;
import ua.nure.fedorenko.kidstim.entity.Reward;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    private List<Reward> rewards = new ArrayList<>();
    @Nullable
    private RewardListener rewardListener;

    public void setRewards(List<Reward> r) {
        rewards.clear();
        rewards.addAll(r);
        notifyDataSetChanged();
    }

    public void setRewardListener(@Nullable RewardListener rewardListener) {
        this.rewardListener = rewardListener;
    }

    @Override
    public RewardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.reward_recycle_view_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RewardAdapter.ViewHolder holder, int position) {
        Reward reward = rewards.get(position);
        holder.rewardDescTextView.setText(reward.getDescription());
        holder.rewardStatusTextView.setText(reward.getStatus().toString());
        holder.rewardPointsTextView.setText(String.valueOf(reward.getPoints()));
        List<Child> children = reward.getChildren();
        if (children == null) {
            children = Collections.emptyList();
        }
        holder.childrenPicturesLayout.removeAllViews();
        for (Child child : children) {
            holder.childrenPicturesLayout.addView(rewardListener.onGetImage(child.getEmail()));
        }
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rewardDescTextView)
        TextView rewardDescTextView;

        @BindView(R.id.rewardStatusTextView)
        TextView rewardStatusTextView;

        @BindView(R.id.rewardPointsTextView)
        TextView rewardPointsTextView;
        @BindView(R.id.childrenPicturesLayout)
        LinearLayout childrenPicturesLayout;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @OnClick(R.id.deleteRewardButton)
        void onDeleteRewardButtonClick() {
            AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.reward_deleting)
                    .setMessage(R.string.reward_delete_confirmation)
                    .setPositiveButton(R.string.delete, (dialog1, whichButton) -> {
                        if (rewardListener != null) {
                            rewardListener.onDeleteReward(rewards.get(getAdapterPosition()).getId());
                        }
                        dialog1.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog12, which) -> dialog12.dismiss())
                    .create();
            dialog.show();
        }

        @OnClick(R.id.showEditRewardButton)
        void onShowEditTaskButtonClick() {
            if (rewardListener != null) {
                rewardListener.onEditReward(rewards.get(getAdapterPosition()).getId());
            }
        }
    }

    public interface RewardListener {
        void onEditReward(String rewardId);

        ImageView onGetImage(@NonNull String photo);

        void onDeleteReward(String rewardId);
    }
}
