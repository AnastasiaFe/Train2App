package ua.nure.fedorenko.kidstim.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.nure.fedorenko.kidstim.entity.Reward;
import ua.nure.fedorenko.kidstim.entity.RewardStatus;
import ua.nure.fedorenko.kidstim.R;

public class ChildRewardAdapter extends RecyclerView.Adapter<ChildRewardAdapter.ViewHolder> {

    private List<Reward> rewards = new ArrayList<>();
    @Nullable
    private ChildRewardAdapter.RewardListener rewardListener;

    public void setRewards(List<Reward> r) {
        rewards.clear();
        rewards.addAll(r);
        notifyDataSetChanged();
    }

    public void setRewardListener(@Nullable ChildRewardAdapter.RewardListener rewardListener) {
        this.rewardListener = rewardListener;
    }

    @Override
    public ChildRewardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.child_reward_recycle_view_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ChildRewardAdapter.ViewHolder holder, int position) {
        Reward reward = rewards.get(position);
        holder.rewardDescTextView.setText(reward.getDescription());
        holder.rewardStatusTextView.setText(reward.getStatus().toString());
        holder.rewardPointsTextView.setText(String.valueOf(reward.getPoints()));
        if (reward.getStatus().equals(RewardStatus.RECEIVED)) {
            holder.requestRewardButton.setEnabled(false);
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

        @BindView(R.id.requestReward)
        Button requestRewardButton;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.requestReward)
        void onRequestRewardButtonClick() {
            if (rewardListener != null) {
                rewardListener.onRequestReward(rewards.get(getAdapterPosition()));
            }
        }
    }

    public interface RewardListener {
        void onRequestReward(Reward reward);
    }
}
