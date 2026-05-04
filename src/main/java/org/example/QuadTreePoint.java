package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuadTreePoint {

    private List<Double> coords = null;

    public QuadTreePoint(ArrayList<Double> coords) {
        this.coords = coords;
    }

    public List<Double> getCoords() {
        return coords;
    }

    public void setCoords(List<Double> coords) {
        this.coords = coords;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuadTreePoint that = (QuadTreePoint) o;
        return Objects.equals(coords, that.coords);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(coords);
    }
}
