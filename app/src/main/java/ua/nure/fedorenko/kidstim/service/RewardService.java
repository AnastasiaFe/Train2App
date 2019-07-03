package ua.nure.fedorenko.kidstim.service;

import java.util.ArrayList;
import java.util.List;

import ua.nure.fedorenko.kidstim.entity.Reward;
import ua.nure.fedorenko.kidstim.entity.RewardStatus;

public class RewardService {

    public List<Reward> getRewardsByStatus(RewardStatus rewardStatus, List<Reward> rewards) {
        List<Reward> filtered = new ArrayList<>();
        for (Reward reward : rewards) {
            if (reward.getStatus().equals(rewardStatus)) {
                filtered.add(reward);
            }
        }
        return filtered;
    }
}
