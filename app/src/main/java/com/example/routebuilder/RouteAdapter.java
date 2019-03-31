package com.example.routebuilder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private ArrayList<RouteItem> mRouteList;
    private OnItemClickListener mListener;

    // Called on in another class to set on click functions
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    // Called on in another class to set on click functions
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    // Viewholder for the RecyclerView
    public static class RouteViewHolder extends RecyclerView.ViewHolder{
        public TextView mName;
        public TextView mStart;
        public TextView mEnd;
        public TextView mWaypoints;
        public ImageView mRouteDelete;

        public RouteViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            mName = itemView.findViewById(R.id.routeName);
            mStart = itemView.findViewById(R.id.routeStart);
            mEnd = itemView.findViewById(R.id.routeEnd);
            mWaypoints = itemView.findViewById(R.id.routeWaypoints);
            mRouteDelete = itemView.findViewById(R.id.routeDelete);
            // When clicking the X
            mRouteDelete.setOnClickListener(new View.OnClickListener() {
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
            // When clicking the whole view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public RouteAdapter(ArrayList<RouteItem> routeList){
        mRouteList = routeList;
    }

    //----------------------------------------------------
    // Overrides for the ViewHolder class
    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_layout, viewGroup, false);
        RouteViewHolder rvh = new RouteViewHolder(v, mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeViewHolder, int i) {
        RouteItem currentItem = mRouteList.get(i);
        routeViewHolder.mRouteDelete.setImageResource(currentItem.getImage());
        routeViewHolder.mName.setText(currentItem.getName());
        routeViewHolder.mStart.setText(currentItem.getStart());
        routeViewHolder.mEnd.setText(currentItem.getEnd());
        routeViewHolder.mWaypoints.setText(String.valueOf(currentItem.getWaypointSize()));
    }

    @Override
    public int getItemCount() {
        return mRouteList.size();
    }
}
