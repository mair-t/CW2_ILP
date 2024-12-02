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
}
