package ua.nure.fedorenko.kidstim.entity;

import java.io.Serializable;
import java.util.List;

public class Reward implements Serializable {

    private String id;
    private String description;
    private int points;
    private RewardStatus status;
    private Parent parent;
    private List<Child> children;

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public RewardStatus getStatus() {
        return status;
    }

    public void setStatus(RewardStatus status) {
        this.status = status;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reward)) return false;

        Reward reward = (Reward) o;

        if (getPoints() != reward.getPoints()) return false;
        if (!getId().equals(reward.getId())) return false;
        if (getDescription() != null ? !getDescription().equals(reward.getDescription()) : reward.getDescription() != null)
            return false;
        if (getStatus() != reward.getStatus()) return false;
        if (getParent() != null ? !getParent().equals(reward.getParent()) : reward.getParent() != null)
            return false;
        return getChildren() != null ? getChildren().equals(reward.getChildren()) : reward.getChildren() == null;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + getPoints();
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (getParent() != null ? getParent().hashCode() : 0);
        result = 31 * result + (getChildren() != null ? getChildren().hashCode() : 0);
        return result;
    }
}
