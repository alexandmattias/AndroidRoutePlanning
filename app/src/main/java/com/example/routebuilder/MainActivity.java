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
    // Buttons
    private Button mButtonAddRoute;

    // RecyclerView variables
    private RecyclerView mRecyclerView;
    private RouteAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<RouteItem> routeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        routeList = new ArrayList<>();
        setButtons();
        loadFromFile();
        addNewRoute();
        buildRecycleView();
    }

    // Load routes previously saved
    private void loadFromFile() {
        // If a save file exists
        if( FileIO.loadRoutes(this) != null) {
            // Load the saved routes into the routeList
            routeList = FileIO.loadRoutes(this);
        }
    }

    // Saves files to the device
    private void saveToFile() {
        // Save the routeList to a file
        FileIO.saveRoutes(routeList, this);
    }

    // Adds a new route to the recyclerView with data from the Map class
    private void addNewRoute() {
        // Check that the gets exists
        if (getIntent().getStringArrayListExtra("route") != null){

            // Route name, start, destination
            ArrayList<String> waypoints = new ArrayList<>();
            ArrayList<String> route = getIntent().getStringArrayListExtra("route");
            // All waypoints
            if (getIntent().getStringArrayListExtra("waypoints") != null){
                waypoints = getIntent().getStringArrayListExtra("waypoints");
            }
            // Create a routeItem with the necessary data
            // -1 is sent if a new route was created, else it is the position when the route to update is
            if (getIntent().getExtras().getInt("position") != -1){
                int pos = getIntent().getExtras().getInt("position");
                // Remove the old route, and replace it with an updated version
                routeList.remove(pos);
                routeList.add(pos, new RouteItem(route.get(0), route.get(1), route.get(2),waypoints));
            } else {
                // 0 = name, 1 = start, 2 = destination
                routeList.add(new RouteItem(route.get(0), route.get(1), route.get(2),waypoints));
            }
        }
        saveToFile();
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
                saveToFile();
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
        saveToFile();
        Intent map = new Intent(getApplicationContext(), Map.class);
        ArrayList<String> route = new ArrayList<>();
        // Get the RouteItem at the position
        RouteItem Route = routeList.get(position);
        route.add(Route.getName());
        route.add(Route.getStart());
        route.add(Route.getDestination());
        // Put them into the map intent as StringArrayList
        map.putExtra("position", position);
        map.putStringArrayListExtra("route", route);
        map.putStringArrayListExtra("waypoints", Route.getWaypoints());
        map.putExtra("new", false);
        // Start activity
        startActivity(map);
        finish();
    }

    //Remove an item at the specified position
    private void removeItem(int position) {
        routeList.remove(position);
        saveToFile();
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
                intent.putExtra("new", true);
                startActivity(intent);
                saveToFile();
                finish();
            }
        });
    }
}
