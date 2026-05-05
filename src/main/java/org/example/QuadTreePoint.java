package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuadTreePoint {

    private List<Double> coords = null;
    private QuadTreeData data;


    public QuadTreePoint(ArrayList<Double> coords,QuadTreeData data) {
        this.coords = coords;
        this.data = data;
    }

    public List<Double> getCoords() {
        return coords;
    }

    public void setCoords(List<Double> coords) {
        this.coords = coords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuadTreePoint that = (QuadTreePoint) o;
        if (this.coords.size() != that.coords.size()) return false;
        double epsilon = 1e-6;
        for (int i = 0; i < coords.size(); i++) {
            if (Math.abs(this.coords.get(i) - that.coords.get(i)) > epsilon) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(coords);
    }

    public QuadTreeData getData() {
        return data;
    }

    public void setData(QuadTreeData data) {
        this.data = data;
    }
}
