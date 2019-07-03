package ua.nure.fedorenko.kidstim.entity;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {

    private String id;
    private String description;
    private TaskStatus status;
    private long creationDate;
    private long expirationDate;
    private int points;
    private Parent parent;

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public List<Child> children;

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (creationDate != task.creationDate) return false;
        if (expirationDate != task.expirationDate) return false;
        if (points != task.points) return false;
        if (!id.equals(task.id)) return false;
        if (description != null ? !description.equals(task.description) : task.description != null)
            return false;
        if (status != task.status) return false;
        return parent != null ? parent.equals(task.parent) : task.parent == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (int) (creationDate ^ (creationDate >>> 32));
        result = 31 * result + (int) (expirationDate ^ (expirationDate >>> 32));
        result = 31 * result + points;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }
}
