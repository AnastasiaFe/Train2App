package ua.nure.fedorenko.kidstim.entity;

import java.io.Serializable;

public class Child extends User implements Serializable {

    private long dateOfBirth;

    private int gender;

    private int points;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Child child = (Child) o;

        if (dateOfBirth != child.dateOfBirth) return false;
        if (gender != child.gender) return false;
        return points == child.points;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (dateOfBirth ^ (dateOfBirth >>> 32));
        result = 31 * result + gender;
        result = 31 * result + points;
        return result;
    }
}
