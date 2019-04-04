package com.example.routebuilder;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileIO {

    public static void saveRoutes(ArrayList<RouteItem> routes, Activity activity){
        System.out.println("Saving routes");
        try {
            FileOutputStream fos = activity.openFileOutput("routes", Context.MODE_PRIVATE);
            ObjectOutput oop = new ObjectOutputStream(fos);
            oop.writeObject(routes);
            oop.close();
            fos.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<RouteItem> loadRoutes(Activity activity){
        System.out.println("Loading routes");
        ArrayList<RouteItem>routes = new ArrayList<>();

        try {
            FileInputStream fis = activity.openFileInput("routes");
            ObjectInputStream oip = new ObjectInputStream(fis);
            routes = (ArrayList<RouteItem>)oip.readObject();
            oip.close();
            fis.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return routes;
    }

}
