package com.pb.radioheatmapclient;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


import static com.pb.radioheatmapclient.MainActivity.debugging;

/**
 * Created by Patrick Bäselt on 27.02.17.
 */

public class Export {

    public static JSONObject get (LatLng messpunkt, JSONArray wlanNetworks){
          // Messdatensatz als JSON Array erstellen.
        JSONObject measurementdata = new JSONObject();
        // Zeitstempel zum Messdatensatz hinzufügen
        try{
            measurementdata.put("datetime",Timebase.get());
        } catch (JSONException e) {
        if (debugging == true) { System.out.println("Export Error: on adding datetime " + e);}
        }
        // Übergabe per Handfixierter Messpunkt
        if (messpunkt != null) {
            JSONObject location = new JSONObject();
            try{
                //location.names("test");
                location.put( "latitude", messpunkt.latitude);
                location.put("longitude", messpunkt.longitude);
                location.put("storey", 0 ); //ToDo: Unterstützung für Stockwerke
                if (debugging == true) {System.out.println("Export Info: creating JSON-Locationstamp is OK");}
                // Location zum Messdatensatz hinzufühgen.
                measurementdata.put("location",location);
                if (debugging == true) {System.out.println("Export Info: adding JSON-Locationstamp is OK");}
            }
            catch (JSONException e) {
                if (debugging == true) { System.out.println("Export Error: on setting Location " + e);}
            }

        } else {
            System.out.println("Export Error: keine location");
        }
        try {
            measurementdata.put("wlanNetworks", wlanNetworks);
        } catch (JSONException e) {
            if (debugging == true) { System.out.println("Export Error: on adding WlanNetworks " + e);}
        }

        return measurementdata;
    }
}

