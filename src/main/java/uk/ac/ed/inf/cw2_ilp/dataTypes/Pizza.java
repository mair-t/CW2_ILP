package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class Pizza {

    public String name;

    public int priceInPence;

    public String getName(){
        return name;
    }

    public int getPriceInPence(){return priceInPence;}

    public void setName(String name){this.name = name;}

    public void setPriceInPence(int price){this.priceInPence = price;}
}
