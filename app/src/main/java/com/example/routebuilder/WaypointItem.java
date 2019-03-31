package com.example.routebuilder;

public class WaypointItem {
    private String mName;
    private int mImageResource = R.drawable.ic_clear;
    public WaypointItem(String name){
        mName = name;
    }

    public String getmName(){
        return mName;
    }

    public int getImage(){
        return mImageResource;
    }
}
