package com.pb.radioheatmapclient;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.pb.radioheatmapclient.MainActivity.debugging;

/**
 * Created by s153102 on 27.02.17.
 */

public class Export {

    public static JSONArray get (LatLng messpunkt, JSONArray wlanNetworks){
          // Messdatensatz als JSON Array erstellen.
        JSONArray measurementdata = new JSONArray();
        // Zeitstempel zum Messdatensatz hinzufügen
        measurementdata.put(Timebase.get());
        if (debugging == true) {System.out.println("WiFiWorker Info: adding JSON-Datetimestamp is OK");}
        // Übergabe per Handfixierter Messpunkt
        if (messpunkt != null) {
            JSONObject location = new JSONObject();
            try{
                location.put( "latitude", messpunkt.latitude);
                location.put("longitude", messpunkt.longitude);
                location.put("storey", 0 ); //ToDo: Unterstützung für Stockwerke
                if (debugging == true) {System.out.println("WiFiWorker Info: creating JSON-Locationstamp is OK");}
                // Location zum Messdatensatz hinzufühgen.
                measurementdata.put(location);
                if (debugging == true) {System.out.println("WiFiWorker Info: adding JSON-Locationstamp is OK");}
            }
            catch (JSONException e) {
                if (debugging == true) { System.out.println("WiFiWorker Error: Fehler SCAN2JSON " + e);}
            }

        } else {
            System.out.println("Export Error: keine location");
        }
        measurementdata.put(wlanNetworks);
        return measurementdata;
    }
}

