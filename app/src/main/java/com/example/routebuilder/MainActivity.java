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
    ArrayList<RouteItem> routeList;
    private RecyclerView mRecyclerView;
    private RouteAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtons();
        routeList = new ArrayList<>();
        ArrayList<String> array = new ArrayList<>();
        array.add("0");
        array.add("1");
        routeList.add(new RouteItem("name", "start", "end", array));

        buildRecycleView();
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
        //TODO: Inflate a map view with the info from routeList.get(position)
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
            }
        });
    }
}
