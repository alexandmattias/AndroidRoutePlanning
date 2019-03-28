package com.example.routebuilder;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Route {
    Long name;
    Long start;
    Long destination;
    LatLng location;
    List <Route>  waypoints = new ArrayList<>();


    public Route(Long name){
        this.name = name;
    }
}
