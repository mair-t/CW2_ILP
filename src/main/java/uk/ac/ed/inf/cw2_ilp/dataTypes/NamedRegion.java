package uk.ac.ed.inf.cw2_ilp.dataTypes;

import java.util.List;

public class NamedRegion {

    public String name;
    public List<LngLat> vertices;

    public String getName(){
        return name;
    }
    public List<LngLat> getVertices(){
        return vertices;
    }
}