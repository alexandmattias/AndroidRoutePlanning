package com.example.routebuilder;

import android.app.Activity;
import android.content.Context;

import com.example.routebuilder.Route;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class FileIO {

    public static void saveRoutes(HashMap<Long, Route>routes, Activity activity){

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

    public static HashMap<Long, Route> loadRoutes(Activity activity){

        HashMap<Long,Route>routes = new HashMap<>();

        try {
            FileInputStream fis = activity.openFileInput("routes");
            ObjectInputStream oip = new ObjectInputStream(fis);
            routes = (HashMap<Long, Route>)oip.readObject();
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
