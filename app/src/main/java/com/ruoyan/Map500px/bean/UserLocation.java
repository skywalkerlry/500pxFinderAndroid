package com.ruoyan.map500px.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruoyan on 2/13/15.
 */
public class UserLocation {
    private Map<String, Double> userLocation = null;

    public UserLocation(double latitude, double longitude, double radius) {
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("radius", radius);
        this.setUserLocation(map);
    }

    public Map<String,Double> getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Map<String, Double> map) {
        if (userLocation!=null && !userLocation.isEmpty())
            userLocation.clear();
        userLocation = map;
    }
}
