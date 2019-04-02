package com.example.routebuilder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    // Google map vars
    private MapView mapView;
    private GoogleMap gMap;

    // User input text fields for start, end and waypoint
    private EditText mRouteName;
    private EditText mRouteStart;
    private EditText mRouteDestination;
    private EditText mWaypoint;

    // Buttons to set start, destionation and waypoint
    private Button mSetStart;
    private Button mSetDestination;
    private Button mSetWaypoint;
    // Button the create the route
    private Button mCreateRoute;

    // RecyclerView waypoints
    private RecyclerView mRecyclerView;
    private WaypointAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<WaypointItem> mWaypointList;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        buildGMap(savedInstanceState);
        setButtons();
        if (getIntent().getExtras() != null){
            loadData();
        } else {
            createMItemList();
        }
        buildRecyclerView();


    }

    private void loadData() {
        mWaypointList = new ArrayList<>();
        ArrayList<String> route;
        ArrayList<String> waypoints;
        if (getIntent().getStringArrayListExtra("route") != null){
            route = getIntent().getStringArrayListExtra("route");
            waypoints = getIntent().getStringArrayListExtra("waypoints");
            mRouteName.setText(route.get(0));
            mRouteStart.setText(route.get(1));
            mRouteDestination.setText(route.get(2));

            for (String waypoint : waypoints){
                mWaypointList.add(new WaypointItem(waypoint));
            }
        }


    }

    private void setButtons() {
        // EditTexts for button usage
        mRouteName = findViewById(R.id.et_SetName);
        mRouteStart = findViewById(R.id.et_SetStart);
        mRouteDestination = findViewById(R.id.et_SetEnd);
        mWaypoint = findViewById(R.id.et_AddWaypoint);
        // Buttons
        mSetStart = findViewById(R.id.button_setStart);
        mSetDestination = findViewById(R.id.button_setEnd);
        mSetWaypoint = findViewById(R.id.button_addWaypoint);
        mCreateRoute = findViewById(R.id.button_createRoute);
        // Start location button listener
        mSetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(mRouteStart.getText().toString());
            }
        });
        // Destination button listener
        mSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(mRouteDestination.getText().toString());
            }
        });
        mSetWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWaypoint.getText() != null){
                    addMarker(mWaypoint.getText().toString());
                    insertItemAtEnd(mWaypoint.getText().toString());
                    mWaypoint.setText("");

                } else {
                    Toast waypointToast = Toast.makeText(getApplicationContext(), "Requires an input", Toast.LENGTH_SHORT);
                    waypointToast.show();
                }
            }
        });
        mCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name;
                name = mRouteName.getText().toString();
                String start = mRouteStart.getText().toString();
                String dest = mRouteDestination.getText().toString();
                ArrayList<String> waypoints = new ArrayList<>();
                ArrayList<String> route = new ArrayList<>();
                route.add(name);
                route.add(start);
                route.add(dest);
                for (WaypointItem item : mWaypointList){
                    waypoints.add(item.getName());
                }
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                main.putStringArrayListExtra("route", route);
                main.putStringArrayListExtra("waypoints", waypoints);
                startActivity(main);
                // Finish activity
                finish();
            }
        });
    }


    // RecyclerView setup
    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView_waypoints);
        // Set layoutManager and Adapter
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new WaypointAdapter(mWaypointList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // Click listener on X to delete the item
        mAdapter.setOnItemClickListener(new WaypointAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    //----------------------------------------
    // Functions for removing/adding/changing items in the WaypointList

    // Insert an item at a specific location
    public void insertItem(int position, String waypoint){
        mWaypointList.add(position, new WaypointItem(waypoint));
        mAdapter.notifyItemInserted(position);
    }
    // Insert and item to the end of the RecyclerView list
    public void insertItemAtEnd(String waypoint){
        mWaypointList.add(new WaypointItem(waypoint));
        mAdapter.notifyDataSetChanged();
    }
    // Remove an item at the specified location
    public void removeItem(int position){
        mWaypointList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    private void createMItemList() {
        mWaypointList = new ArrayList<>();
        mWaypointList.add(new WaypointItem("Karjaa"));
    }

    //-----------------------------------
    // Google map function overrides
    private void buildGMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMinZoomPreference(12);
        LatLng hki = new LatLng(60.1699, 24.9384);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(hki));
    }
    // Add a marker to the google map
    private void addMarker(String location) {
        //TODO: Use Geocoding API to get LatLng and place a marker
    }

    // Draw the shortest route on the map
    private void updateShortestRoute() {
        //TODO: Draw the shorest route using Directions API
        String YOUR_API_KEY = "&key=" + String.valueOf(R.string.google_maps_key);
        String start = "http://maps.googleapis.com/maps/api/directions/outputFormat?json";
        String params = "&origin=" + mRouteStart.getText().toString();
        params += "&destination=" + mRouteDestination.getText().toString();
        if (!mWaypointList.isEmpty()){
            params += "&waypoints=optimize:true";
            for (int i = 0; i < mWaypointList.size(); i++){
                params += "|"+mWaypointList.get(i).getName();
            }
            params += YOUR_API_KEY;
        }
        String finalString = start + params + YOUR_API_KEY;
    }

    // Google map functions end
    //----------------------------

}
