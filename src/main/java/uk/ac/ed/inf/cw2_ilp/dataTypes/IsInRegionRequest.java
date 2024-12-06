package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class IsInRegionRequest {
    public NamedRegion region;

    public LngLat position;

    public NamedRegion getRegion(){
        return region;
    }

    public LngLat getPosition(){
        return position;
    }

    public void setRegion(NamedRegion region) {
        this.region = region;
    }

    public void setPosition(LngLat position) {
        this.position = position;
    }
}
