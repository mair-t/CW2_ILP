package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class LngLatPair {
    public LngLat position1;
    public LngLat position2;

    public LngLat getPos1(){
        return position1;
    }

    public LngLat getPos2(){
        return position2;
    }

    public void setPos1(LngLat pos1){
        this.position1 = pos1;

    }
    public void setPos2(LngLat pos2){
        this.position2 = pos2;

    }


}
