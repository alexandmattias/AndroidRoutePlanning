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
    Polyline line;
    ArrayList<Marker> markers;

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
    private Button mBack;

    // RecyclerView waypoints
    private RecyclerView mRecyclerView;
    private WaypointAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<WaypointItem> mWaypointList;

    private int position = -1;


    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mWaypointList = new ArrayList<>();
        markers = new ArrayList<>();
        buildGMap(savedInstanceState);
        gKey = "&key=" + getString(R.string.google_maps_key);
        setButtons();
        buildRecyclerView();
    }

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
            addMarkers();
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
        mBack = findViewById(R.id.button_back);
        // Start location button listener
        mSetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarkers();
            }
        });
        // Destination button listener
        mSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarkers();
            }
        });
        mSetWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWaypoint.getText() != null){
                    insertItemAtEnd(mWaypoint.getText().toString());
                    mWaypoint.setText("");
                    addMarkers();

                } else {
                    Toast waypointToast = Toast.makeText(getApplicationContext(), "Requires an input", Toast.LENGTH_SHORT);
                    waypointToast.show();
                }
            }
        });
        mCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRouteStart.getText().toString().equals("")
                        && !mRouteDestination.getText().toString().equals("")){
                    String name;
                    name = mRouteName.getText().toString();
                    String start = mRouteStart.getText().toString();
                    String dest = mRouteDestination.getText().toString();
                    ArrayList<String> waypoints = new ArrayList<>();
                    ArrayList<String> route = new ArrayList<>();
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    route.add(name);
                    route.add(start);
                    route.add(dest);
                    if (!mWaypointList.isEmpty()){
                        for (WaypointItem item : mWaypointList){
                            waypoints.add(item.getName());
                        }
                        main.putStringArrayListExtra("waypoints", waypoints);
                    }
                    main.putExtra("position", position);
                    main.putStringArrayListExtra("route", route);

                    startActivity(main);
                    // Finish activity
                    finish();
                }
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(back);
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
                addMarkers();
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

    private void createExampleRoute() {
        mWaypointList.add(new WaypointItem("Karjaa"));
    }

    //-----------------------------------
    // Google map functions
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
    private void addMarkers() {
        resetMap();
        MarkerOptions home = new MarkerOptions();
        MarkerOptions dest = new MarkerOptions();
        if (!mRouteStart.getText().toString().equals("")){
            home.position(getLatLng(mRouteStart.getText().toString()))
                    .title("Home")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            Marker mhome = gMap.addMarker(home);
            markers.add(mhome);
            gMap.moveCamera(CameraUpdateFactory.newLatLng(home.getPosition()));
        }
        if (!mRouteDestination.getText().toString().equals("")){
            dest.position(getLatLng(mRouteDestination.getText().toString()))
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker mDest = gMap.addMarker(dest);
            markers.add(mDest);
        }
        if (!mWaypointList.isEmpty()){
            for (WaypointItem item : mWaypointList){
                markers.add(gMap.addMarker(new MarkerOptions().position(getLatLng(item.getName())).title(item.getName())));
            }
        }
        if (home.getPosition() != null && dest.getPosition() != null){
            updateShortestRoute();
        }
    }

    // Add a marker to the google map
    private LatLng getLatLng(String location) {
        String JSONresponse = new String();
        String input = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + location + gKey;
        try {
            JSONresponse = new HTTPGet().execute(input).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (JSONresponse != null) {
            return JSONreturnData(JSONresponse);
        } else {
            Toast.makeText(getApplicationContext(), "Hittade inget data!", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public LatLng JSONreturnData(String JSONData) {
        LatLng coordinates = null;
        try {
            JSONObject object = new JSONObject(JSONData);
            Double latitude = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            Double longitude = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            coordinates = new LatLng(latitude, longitude);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Kunde inte tyda data!", Toast.LENGTH_LONG).show();
        }
        return coordinates;
    }

    // Draw the shortest route on the map
    private void updateShortestRoute() {
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
        List<LatLng> list = decodePoly(poly);

        for (int z = 0; z < list.size() - 1; z++) {
            LatLng src = list.get(z);
            LatLng dest = list.get(z + 1);
            line = gMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude))
                    .width(5).color(Color.BLUE).geodesic(true));
        }

    }

    // https://stackoverflow.com/questions/17425499/how-to-draw-interactive-polyline-on-route-google-maps-v2-android
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
    // Google map functions end
    //----------------------------

}
