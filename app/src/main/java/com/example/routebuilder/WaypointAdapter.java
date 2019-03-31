package com.example.routebuilder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WaypointAdapter extends RecyclerView.Adapter<WaypointAdapter.WaypointViewHolder> {
    private ArrayList<WaypointListItem> mWaypointList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class WaypointViewHolder extends RecyclerView.ViewHolder{
        public ImageView mDeleteWaypoint;
        public TextView mTextView;

        public WaypointViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mDeleteWaypoint = itemView.findViewById(R.id.waypointButton);
            mTextView = itemView.findViewById(R.id.wayPointText);
            mDeleteWaypoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }


    public WaypointAdapter(ArrayList<WaypointListItem> waypointList){
        mWaypointList = waypointList;
    }
    @NonNull
    @Override
    public WaypointViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.waypoint_layout, viewGroup, false);
        WaypointViewHolder wvh = new WaypointViewHolder(v, mListener);
        return wvh;
    }

    @Override
    public void onBindViewHolder(@NonNull WaypointViewHolder waypointViewHolder, int i) {
        WaypointListItem currentItem = mWaypointList.get(i);
        waypointViewHolder.mDeleteWaypoint.setImageResource(currentItem.getImage());
        waypointViewHolder.mTextView.setText(currentItem.getmName());
    }

    @Override
    public int getItemCount() {
        return mWaypointList.size();
    }
}
