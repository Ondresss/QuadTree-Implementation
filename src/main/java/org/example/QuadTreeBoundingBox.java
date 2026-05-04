package org.example;

import java.util.ArrayList;
import java.util.List;

public class QuadTreeBoundingBox  {

    private List<Double> min;
    private List<Double> max;
    private int dimensions;

    public QuadTreeBoundingBox(ArrayList<Double> min,ArrayList<Double> max) {
        this.min = min;
        this.max = max;
        this.dimensions = min.size();
    }

    public ArrayList<Double> getMids() {
        ArrayList<Double> mids = new ArrayList<Double>(min.size());
        for (int i = 0; i < min.size(); ++i) {
            mids.add((this.min.get(i) + this.max.get(i)) / 2.0 );
        }
        return mids;
    }

    public List<Double> getMin() {
        return min;
    }

    public void setMin(ArrayList<Double> min) {
        this.min = min;
    }

    public List<Double> getMax() {
        return max;
    }

    public void setMax(ArrayList<Double> max) {
        this.max = max;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }
}
