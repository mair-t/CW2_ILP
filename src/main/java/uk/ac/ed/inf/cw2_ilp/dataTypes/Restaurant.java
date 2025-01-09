package uk.ac.ed.inf.cw2_ilp.dataTypes;
import java.time.DayOfWeek;
import java.util.List;


public class Restaurant {

    public LngLat location;

    public String name;

    public List<DayOfWeek> openingDays;

    public Pizza[] menu;

    public List<DayOfWeek> getOpeningDays() {
        return openingDays;
    }

    public LngLat getLocation() {
        return location;
    }

    public Pizza[] getMenu() {return menu;}

    public String getName() {return name;}

    public void setLocation(LngLat location) {this.location = location;}
    public void setMenu(Pizza[] menu) {this.menu = menu;}
    public void setName(String name) {this.name = name;}
    public void setOpeningDays(List<DayOfWeek> openingDays) {
        this.openingDays = openingDays;
    }
}
