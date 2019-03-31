package com.example.routebuilder;

public class WaypointListItem {
    private String mName;
    private int mImageResource = R.drawable.ic_clear_black_24dp;
    public WaypointListItem(String name){
        mName = name;
    }

    public String getmName(){
        return mName;
    }

    public int getImage(){
        return mImageResource;
    }
}
