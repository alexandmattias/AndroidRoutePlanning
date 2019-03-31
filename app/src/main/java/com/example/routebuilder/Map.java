package com.example.routebuilder;

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

import java.util.ArrayList;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    // Google map vars
    private MapView mapView;
    private GoogleMap gMap;

    // User input text fields for start, end and waypoint
    private EditText mRouteName;
    private EditText mRouteStart;
    private EditText mRouteDestionation;
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
        createMItemList();
        buildRecyclerView();
        setButtons();
    }

    private void setButtons() {
        // EditTexts for button usage
        mRouteStart = findViewById(R.id.et_SetStart);
        mRouteDestionation = findViewById(R.id.et_SetEnd);
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
                addMarker(mRouteDestionation.getText().toString());
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
        updateShortestRoute();
    }

    // Draw the shortest route on the map
    private void updateShortestRoute() {
        //TODO: Draw the shorest route using Directions API
    }

    // Google map functions end
    //----------------------------

}
