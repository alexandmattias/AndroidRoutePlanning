package com.example.routebuilder;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    // Google map vars
    private MapView mapView;
    private GoogleMap gMap;
    String gKey;
    PolylineOptions polyLine;
    ArrayList<Marker> markers;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    // User input fields
    private EditText mRouteName;
    private EditText mRouteStart;
    private EditText mRouteDestination;
    private EditText mWaypoint;

    //Buttons
    private Button mSetStart;
    private Button mSetDestination;
    private Button mSetWaypoint;
    private Button mCreateRoute;
    private Button mBack;

    // RecyclerView
    private RecyclerView mRecyclerView;
    private WaypointAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<WaypointItem> mWaypointList;

    // Tracker if editing previous route
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mWaypointList = new ArrayList<>();
        markers = new ArrayList<>();
        gKey = "&key=" + getString(R.string.google_maps_key);

        buildGMap(savedInstanceState);
        initializeFields();
        buildRecyclerView();
    }

    // Load data sent from MainActivity
    private void loadData() {
        ArrayList<String> route;
        ArrayList<String> waypoints;
        if (getIntent().getStringArrayListExtra("route") != null){
            if (getIntent().getExtras() != null){
                position = getIntent().getExtras().getInt("position");
            }
            route = getIntent().getStringArrayListExtra("route");
            waypoints = getIntent().getStringArrayListExtra("waypoints");
            mRouteName.setText(route.get(0));
            mRouteStart.setText(route.get(1));
            mRouteDestination.setText(route.get(2));

            for (String waypoint : waypoints){
                mWaypointList.add(new WaypointItem(waypoint));
            }
            updateMarkers();
        }
    }

    // Initialize all buttons, Edittexts
    private void initializeFields() {
        // EditTexts
        mRouteName = findViewById(R.id.et_SetName);
        mRouteStart = findViewById(R.id.et_SetStart);
        mRouteDestination = findViewById(R.id.et_SetEnd);
        mWaypoint = findViewById(R.id.et_AddWaypoint);

        // Buttons
        mSetStart = findViewById(R.id.button_setStart);
        mSetDestination = findViewById(R.id.button_setEnd);
        mSetWaypoint = findViewById(R.id.button_addWaypoint);
        mCreateRoute = findViewById(R.id.button_createRoute);
        mBack = findViewById(R.id.button_back);

        // Button listeners
        // Start, Destionation, Waypoint, Create route
        mSetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMarkers();
            }
        });
        mSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMarkers();
            }
        });
        mSetWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWaypoint.getText() != null){
                    insertItemAtEnd(mWaypoint.getText().toString());
                    mWaypoint.setText("");
                    updateMarkers();

                } else {
                    Toast waypointToast = Toast.makeText(getApplicationContext(), "Requires an input", Toast.LENGTH_SHORT);
                    waypointToast.show();
                }
            }
        });
        mCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateMain();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }

    // Inflates MainActivity with all the data from the route added to the previous dataset
    // 2 Arraylists are sent alongside the intent: route, waypoints
    // Position is sent, -1 for a new route else the number passed when Map view was opened
    private void inflateMain() {
        if (!mRouteStart.getText().toString().equals("")
                && !mRouteDestination.getText().toString().equals("")
                && !mRouteName.getText().toString().equals("")){
            Intent main = new Intent(getApplicationContext(), MainActivity.class);

            String name = mRouteName.getText().toString();
            String start = mRouteStart.getText().toString();
            String dest = mRouteDestination.getText().toString();

            ArrayList<String> route = new ArrayList<>();
            route.add(name);
            route.add(start);
            route.add(dest);

            ArrayList<String> waypoints = new ArrayList<>();
            if (!mWaypointList.isEmpty()){
                for (WaypointItem item : mWaypointList){
                    waypoints.add(item.getName());
                }
                main.putStringArrayListExtra("waypoints", waypoints);
            }
            main.putExtra("position", position);
            main.putStringArrayListExtra("route", route);

            startActivity(main);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please make sure there is a name, start and destination", Toast.LENGTH_SHORT).show();

        }
    }

    // Necessary components for RecyclerView
    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView_waypoints);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new WaypointAdapter(mWaypointList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new WaypointAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
                updateMarkers();
            }
        });
    }

    // Insert and item to the end of the RecyclerView waypoint list
    public void insertItemAtEnd(String waypoint){
        mWaypointList.add(new WaypointItem(waypoint));
        mAdapter.notifyDataSetChanged();
    }

    // Remove an item at the specified location
    public void removeItem(int position){
        mWaypointList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    private void createExampleRoute() {
        mWaypointList.add(new WaypointItem("Karjaa"));
    }

    /*****************************************
     *
     * Google Map section
     *
     *****************************************/

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
        gMap.setMinZoomPreference(3);
        if (getIntent().getExtras() != null){
            loadData();
        } else {
            createExampleRoute();
        }
    }

    private void resetMap(){
        if (!markers.isEmpty()){
            gMap.clear();
        }
    }

    // Update markers on the map. Only updates the values which contain a text
    private void updateMarkers() {
        resetMap();
        MarkerOptions home = new MarkerOptions();
        MarkerOptions dest = new MarkerOptions();
        // Home marker
        CreateHomeMarker(home);
        createDestinationMarker(dest);
        createWaypointMarkers();

        // Updates polyLine to match the new places
        // Require a start and an end
        if (home.getPosition() != null && dest.getPosition() != null){
            drawPolyline();
        } else {
            Toast.makeText(getApplicationContext(), "Please make sure there is a start and destination", Toast.LENGTH_SHORT).show();
        }
    }

    // Loop through mWaypointList and add all the locations to the map as markers
    private void createWaypointMarkers() {
        if (!mWaypointList.isEmpty()){
            for (WaypointItem item : mWaypointList){
                markers.add(gMap.addMarker(new MarkerOptions().position(getLatLng(item.getName())).title(item.getName())));
            }
        }
    }

    // Add the destination as a green marker
    private void createDestinationMarker(MarkerOptions dest) {
        if (!mRouteDestination.getText().toString().equals("")){
            dest.position(getLatLng(mRouteDestination.getText().toString()))
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker mDest = gMap.addMarker(dest);
            markers.add(mDest);
        }
    }

    // Add the start as a blue marker
    private void CreateHomeMarker(MarkerOptions home) {
        if (!mRouteStart.getText().toString().equals("")){
            home.position(getLatLng(mRouteStart.getText().toString()))
                    .title("Home")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            Marker mhome = gMap.addMarker(home);
            markers.add(mhome);
            gMap.moveCamera(CameraUpdateFactory.newLatLng(home.getPosition()));
        }
    }

    // Use Google Geocoding API to convert a string to LatLng
    private LatLng getLatLng(String location) {
        String JSONresponse = "";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + location + gKey;

        // Try to get the json response
        try {
            JSONresponse = new HTTPGet().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (JSONresponse != null) {
            LatLng coordinates = null;
            // Pick out the coordinates of the location
            try {
                JSONObject object = new JSONObject(JSONresponse);
                Double latitude = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                Double longitude = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                coordinates = new LatLng(latitude, longitude);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not read the data!", Toast.LENGTH_LONG).show();
            }
            return coordinates;
        } else {
            Toast.makeText(getApplicationContext(), "No data found!", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    // Draw the shortest route on the map
    // Uses Google Directions API
    private void drawPolyline() {
        polyLine = new PolylineOptions();
        String url = "https://maps.googleapis.com/maps/api/directions/json?";
        String params = "origin=" + mRouteStart.getText().toString();
        params += "&destination=" + mRouteDestination.getText().toString();
        if (!mWaypointList.isEmpty()){
            params += "&waypoints=optimize:true";
            for (int i = 0; i < mWaypointList.size(); i++){
                params += "|"+mWaypointList.get(i).getName();
            }
        }
        String queryString = url + params + gKey;
        String overviewPolyline = getJSONPolylineOverview(queryString);
        // Decode the overview_polyline from JSON request
        List<LatLng> polyList = PolyUtil.decode(overviewPolyline);
        polyLine.addAll(polyList);
        gMap.addPolyline(polyLine);
    }

    // Executes the Directions API command, return with the overview_polyline
    private String getJSONPolylineOverview(String queryString) {
        String json = "";
        try {
            json = new HTTPGet().execute(queryString).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String poly = "";
        try {
            JSONObject obj = new JSONObject(json);
            poly = obj.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return poly;
    }
    // Google map functions end
    //----------------------------

}
