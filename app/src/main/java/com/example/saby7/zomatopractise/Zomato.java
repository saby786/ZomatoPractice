
package com.example.saby7.zomatopractise;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Zomato {

    @SerializedName("nearby_restaurants")
    @Expose
    private List<NearbyRestaurant> nearbyRestaurants = null;

    public List<NearbyRestaurant> getNearbyRestaurants() {
        return nearbyRestaurants;
    }

    public void setNearbyRestaurants(List<NearbyRestaurant> nearbyRestaurants) {
        this.nearbyRestaurants = nearbyRestaurants;
    }

}
