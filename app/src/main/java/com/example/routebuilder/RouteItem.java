package com.example.routebuilder;

import java.util.ArrayList;

public class RouteItem {
    private String mName;
    private String mStart;
    private String mEnd;
    private ArrayList<String> mWaypoints;
    private int mImageResource = R.drawable.ic_clear;
    public RouteItem(String name, String start, String end, ArrayList<String> waypoints){
        mName = name;
        mStart = start;
        mEnd = end;
        mWaypoints = waypoints;
    }

    public String getName(){
        return mName;
    }
    public String getStart(){
        return mStart;
    }
    public String getEnd(){
        return mEnd;
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
