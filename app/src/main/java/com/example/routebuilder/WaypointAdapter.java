package com.example.routebuilder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class WaypointAdapter extends RecyclerView.Adapter<WaypointAdapter.WaypointViewHolder> {
    private ArrayList<ListItem> mWaypointList;
    public static class WaypointViewHolder extends RecyclerView.ViewHolder{
        public ImageButton mImageButton;
        public TextView mTextView;

        public WaypointViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageButton = itemView.findViewById(R.id.waypointButton);
            mTextView = itemView.findViewById(R.id.wayPointText);
        }
    }

    public WaypointAdapter(ArrayList<ListItem> waypointList){
        mWaypointList = waypointList;
    }
    @NonNull
    @Override
    public WaypointViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.waypoint_layout, viewGroup, false);
        WaypointViewHolder wvh = new WaypointViewHolder(v);
        return wvh;
    }

    @Override
    public void onBindViewHolder(@NonNull WaypointViewHolder waypointViewHolder, int i) {
        ListItem currentItem = mWaypointList.get(i);
        waypointViewHolder.mImageButton.setImageResource(currentItem.getImage());
        waypointViewHolder.mTextView.setText(currentItem.getmName());
    }

    @Override
    public int getItemCount() {
        return mWaypointList.size();
    }
}
