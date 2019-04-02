package com.example.routebuilder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button mButtonAddRoute;

    // RecyclerView variables
    private RecyclerView mRecyclerView;
    private RouteAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // List that stores the items needed for recyclerView
    ArrayList<RouteItem> routeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtons();
        routeList = new ArrayList<>();
        addNewRoute();
        ArrayList<String> array = new ArrayList<>();
        array.add("0");
        array.add("1");
        routeList.add(new RouteItem("name", "start", "end", array));
        buildRecycleView();
    }

    // Adds a new route to the recyclerView with data from the Map class
    private void addNewRoute() {

        if (getIntent().getStringArrayListExtra("route") != null
                && getIntent().getStringArrayListExtra("waypoints") != null){
            // Route name, start, destination
            ArrayList<String> route = getIntent().getStringArrayListExtra("route");
            // All waypoints
            ArrayList<String> waypoints = getIntent().getStringArrayListExtra("waypoints");
            // Create a routeItem with the necessary data
            routeList.add(new RouteItem(route.get(0), route.get(1), route.get(2),waypoints));
        }
    }

    private void buildRecycleView() {
        // Find view, create layoutmanager and adapter
        mRecyclerView = findViewById(R.id.recyclerRoutes);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RouteAdapter(routeList);
        // Set lm and adapter
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //Set the on click functions
        mAdapter.setOnItemClickListener(new RouteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                inflateMapWithSelected(position);
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }
    // Inflate the activity_map view with data from the selected position
    private void inflateMapWithSelected(int position){
        // Create intent
        Intent map = new Intent(getApplicationContext(), Map.class);
        ArrayList<String> route = new ArrayList<>();
        // Get the RouteItem at the position
        RouteItem Route = routeList.get(position);
        route.add(Route.getName());
        route.add(Route.getStart());
        route.add(Route.getDestination());
        // Put them into the map intent as StringArrayList
        map.putStringArrayListExtra("route", route);
        map.putStringArrayListExtra("waypoints", Route.getWaypoints());
        // Start activity
        startActivity(map);
        finish();
    }

    //Remove an item at the specified position
    private void removeItem(int position) {
        routeList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    // Initialize the buttons on the view
    private void setButtons() {
        mButtonAddRoute = findViewById(R.id.buttonAddRoute);
        mButtonAddRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a map intent and open its
                Intent intent = new Intent(getApplicationContext(), Map.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Add a new route to the routelist
    public void addToRouteList(RouteItem newRoute){
        routeList.add(newRoute);
        mAdapter.notifyDataSetChanged();
    }
}
