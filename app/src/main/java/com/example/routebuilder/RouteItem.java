package com.example.routebuilder;

import java.io.Serializable;
import java.util.ArrayList;

public class RouteItem implements Serializable {
    private String mName;
    private String mStart;
    private String mDestination;
    private ArrayList<String> mWaypoints;
    private int mImageResource = R.drawable.ic_clear;
    public RouteItem(String name, String start, String end, ArrayList<String> waypoints){
        mName = name;
        mStart = start;
        mDestination = end;
        mWaypoints = waypoints;
    }

    public String getName(){
        return mName;
    }
    public String getStart(){
        return mStart;
    }
    public String getDestination(){
        return mDestination;
    }
    public int getWaypointSize(){
        return mWaypoints.size();
    }
    public ArrayList<String> getWaypoints(){
        return mWaypoints;
    }
    public int getImage(){
        return mImageResource;
    }
}
