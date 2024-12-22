package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class Node {

    private LngLat position;
    private double f;
    private double g;
    private Node parent;

    public LngLat getPosition() {
        return position;
    }

    public void setPosition(LngLat position) {
        this.position = position;
    }

    public double getF() {
        return f;
    }

    public void setF(double g, double h) {
        this.f = g + h;
    }

    public double getG() {
        return g;
    }
    public void setG(double g) {
        this.g = g;
    }
    public Node getParent() {
        return parent;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }
}
