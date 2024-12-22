package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class LngLat {
    public Double lng;
    public Double lat;


    public Double getLng(){
        return this.lng;
    }

    public Double getLat(){
        return this.lat;
    }

    public void setLng(Double lng){
        this.lng = lng;
    }

    public void setLat(Double lat){
        this.lat = lat;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof LngLat){
            if (this.lng == ((LngLat)obj).lng && this.lat == ((LngLat)obj).lat){
                return true;
            }
        }
        return false;
    }

}
