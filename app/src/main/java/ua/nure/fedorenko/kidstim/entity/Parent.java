package ua.nure.fedorenko.kidstim.entity;

import java.io.Serializable;
import java.util.List;

public class Parent extends User implements Serializable {

    private List<Child> children;

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Parent parent = (Parent) o;

        return children != null ? children.equals(parent.children) : parent.children == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }
}
