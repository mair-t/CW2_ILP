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
}
