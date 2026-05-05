package org.example;

import java.util.ArrayList;
import java.util.List;
public class QuadTreeNode {
    private int dimension = 0;
    private List<QuadTreeNode> children = null;
    private int capacity = 4;
    private List<QuadTreePoint> points = null;
    private QuadTreeBoundingBox boundingBox = null;
    private int maxDepth = 10;


    public QuadTreeNode(int dimension,int capacity,QuadTreeBoundingBox boundingBox,int maxDepth) {
        this.dimension = dimension;
        this.children = new ArrayList<QuadTreeNode>(1 << dimension);
        this.capacity = capacity;
        this.points = new ArrayList<QuadTreePoint>(this.capacity);
        this.boundingBox = boundingBox;
        this.maxDepth = maxDepth;
    }


    public QuadTreePoint pointSearch(QuadTreePoint point) {
        if(!this.children.isEmpty()) {
            ArrayList<Double> mids = this.boundingBox.getMids();
            int index = this.getPointIndex(point,mids);
            return this.children.get(index).pointSearch(point);
        }
        for(QuadTreePoint p : this.points) {
            if(p.equals(point)) return p;
        }
        return null;
    }

    public List<QuadTreePoint> rangeScan(QuadTreeBoundingBox boundingBox) {
        List<QuadTreePoint> results = new ArrayList<>();
        Boolean doesBoundingIntersects = this.boundingBox.intersects(boundingBox);
        if(!doesBoundingIntersects) {
            return results;
        }
        if(!this.children.isEmpty()) {
            for(QuadTreeNode child : this.children) {
                results.addAll(child.rangeScan(boundingBox));
            }
        } else {
            results.addAll(this.points);
        }
        return results;
    }

    public void insert(QuadTreePoint point) {
        if (!children.isEmpty()) {
            ArrayList<Double> mids = this.boundingBox.getMids();
            int index = this.getPointIndex(point,mids);
            this.children.get(index).insert(point);
            return;
        }
        if(this.alreadyHasPoint(point)) return;
        this.points.add(point);
        if(this.points.size() > capacity && this.maxDepth > 0) {
            this.divide();
        }
    }

    private Boolean alreadyHasPoint(QuadTreePoint point) {
        for(QuadTreePoint p : this.points) {
            if(p.equals(point)) return true;
        }
        return false;
    }


    private void divide() {
        ArrayList<Double> mids = this.boundingBox.getMids();
        int noChildren = 1 << this.dimension;
        for (int i = 0; i < noChildren; ++i) {
            ArrayList<Double> newMins = new ArrayList<Double>(mids.size());
            ArrayList<Double> newMaxs = new ArrayList<Double>(mids.size());

            for (int d = 0; d < this.dimension; ++d) {
                if( ((i >> d) & 1) == 1 ) {
                    newMins.add(mids.get(d));
                    newMaxs.add(this.boundingBox.getMax().get(d));
                } else {
                    newMins.add(this.boundingBox.getMin().get(d));
                    newMaxs.add(mids.get(d));
                }
            }

            this.children.add(new QuadTreeNode(this.dimension,this.capacity,new QuadTreeBoundingBox(newMins,newMaxs),this.maxDepth-1));
        }

        for (QuadTreePoint point : this.points) {
            int index = this.getPointIndex(point,mids);
            this.children.get(index).insert(point);
        }
        this.points.clear();
    }

    private int getPointIndex(QuadTreePoint point,ArrayList<Double> mids) {
        int index = 0;
        for (int d = 0; d < this.dimension; d++) {
            if (point.getCoords().get(d) > mids.get(d)) {
                index |= (1 << d);
            }
        }
        return index;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public List<QuadTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<QuadTreeNode> children) {
        this.children = children;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<QuadTreePoint> getPoints() {
        return points;
    }

    public void setPoints(List<QuadTreePoint> points) {
        this.points = points;
    }

    public QuadTreeBoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(QuadTreeBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}


