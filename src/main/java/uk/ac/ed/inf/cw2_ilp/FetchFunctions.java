package uk.ac.ed.inf.cw2_ilp;

import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.cw2_ilp.dataTypes.NamedRegion;
import uk.ac.ed.inf.cw2_ilp.dataTypes.Pizza;
import uk.ac.ed.inf.cw2_ilp.dataTypes.Restaurant;

import java.util.List;

public class FetchFunctions {

    //fetch all restaurants from the rest service
    public static List<Restaurant> fetchRestaurants() {
        RestTemplate restTemplate = new RestTemplate();
        Restaurant[] restaurants = restTemplate.getForObject(Constants.BASE_URL + "restaurants", Restaurant[].class);
        assert restaurants != null;
        return List.of(restaurants);
    }

    //for a given pizza calculate which restaurant it came from
    public static Restaurant getRestaurantForPizza(String pizzaName, List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            for (Pizza pizza : restaurant.menu) {
                if (pizza.name.equalsIgnoreCase(pizzaName)) {
                    return restaurant;
                }
            }
        }
        return null;
    }

    //retrieve noFlyZones from the website
    public static List<NamedRegion> fetchNoFlyZones() {
        RestTemplate restTemplate = new RestTemplate();
        NamedRegion[] noFlyZones = restTemplate.getForObject(Constants.BASE_URL + "noFlyZones", NamedRegion[].class);
        assert noFlyZones != null;
        return List.of(noFlyZones);
    }

    //retrieve the central area
    public static NamedRegion fetchCentralArea(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(Constants.BASE_URL + "centralArea", NamedRegion.class);
    }

}
