package org.example;

public class QuadTreeD {

    private int dimension = 0;
    private QuadTreeNode root = null;

    public QuadTreeD(int dimension,int nodeCapacity,QuadTreeBoundingBox initialBoundingBox,int maxDepth) {
        this.dimension = dimension;
        this.root = new QuadTreeNode(dimension,nodeCapacity,initialBoundingBox,maxDepth);
    }
    public void insert(QuadTreePoint point) {
        this.root.insert(point);
    }

}
