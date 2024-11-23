package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class NextPositionRequest {
    public LngLat start;
    public  Double angle;

    public LngLat getStart(){
        return start;
    }
    public void setStart(LngLat start) { this.start = start; }
    public  Double getAngle(){
        return angle;
    }
    public void setAngle(Double angle) { this.angle = angle; }
}

