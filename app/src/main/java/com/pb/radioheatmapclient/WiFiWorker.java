package com.pb.radioheatmapclient;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.pb.radioheatmapclient.MainActivity.debugging;

/**
 * Created by Patrick Bäselt on 23.11.2016
 * - Erfassung der Daten mit WiFiManger
 * - Ausgleider der Zeitstempels in Timestamp.java
 * - JSON gepackter Export der Daten
 */
public class WifiErfassung {
    //Aktiviert das Debugen in dem Task

    private Context cont;

    public WifiErfassung(Context context) {
        this.cont = context;
    }

    public JSONArray scannen(LatLng messpunkt){
        WifiManager wifiManager = (WifiManager) cont.getSystemService(Context.WIFI_SERVICE);
        JSONArray measurementdata = new JSONArray();
        measurementdata.put(Timebase.get());

        // Übergabe per Handfixierter Messpunkt
        if (messpunkt != null) {
            JSONObject location = new JSONObject();
            try{
                location.put( "latitude", messpunkt.latitude);
                location.put("longitude", messpunkt.longitude);
                location.put("storey", 0 );
                measurementdata.put(location);
            } catch (JSONException e) {
                if (debugging == true) {
                    System.out.println("Fehler SCAN2JSON " + e);
                }
            }
        } else {
            System.out.println("WiFi Error: keine location");
        }

        for(ScanResult scandat : wifiManager.getScanResults()) {
                // Für Jedes Netzwerk einen eigenen Datensatz
                JSONObject wlanNetwork = new JSONObject();
                try {
                    wlanNetwork.put("bssid", scandat.BSSID);
                    wlanNetwork.put("frequency", scandat.frequency);
                    wlanNetwork.put("ssid", scandat.SSID);
                    wlanNetwork.put("strength", scandat.level);
                    //wlanNetwork.put("Weiss es nicht", scandat.capabilities);
                    //wlanNetwork.put("CenterFrequenz0", scandat.centerFreq0);
                    //wlanNetwork.put("CenterFrequenz1", scandat.centerFreq1);
                    //wlanNetwork.put("ChannalBreite", scandat.channelWidth);
                    measurementdata.put(wlanNetwork);
                } catch (JSONException e) {
                    if (debugging == true) {
                        System.out.println("Fehler SCAN2JSON " + e);
                        continue;
                    }
                }
            }
        return measurementdata;

                   /* //List<ScanResult> results = new ArrayList<>();
        List<ScanResult> scanres = wifiManager.getScanResults();

        List<ScanResult> results = new ArrayList<>();
        for(int i=0; i<=scanres.lastIndexOf();i++){
        scanResult.put(scanres.get(i).centerFreq0.toString());
        results.add(scanres.get(i).SSID.toString());
        results.add(scanres.get(i).BSSID.toString());
        results.add(scanres.get(i).capabilities.toString());
        results.add(scanres.get(i).centerFreq1().toString());
        results.add(scanres.get(i).channelWidth().toString());
        results.add(scanres.get(i).frequency().toString());
        results.add(scanres.get(i).level().toString());
        results.add(scanres.get(i).operatorFriendlyName.toString());
        results.add(scanres.get(i).timestamp().toString());
        results.add(scanres.get(i).venueName.toString());
        }
        JSONArray jsArray = new JSONArray(results);
*/
        /* scanres.get(1).centerFreq0, scanres.get(1).SSID, scanres.get(1).BSSID, scanres.get(1).capabilities;
        scanres.get(1).centerFreq1;
        scanres.get(1).channelWidth;
        scanres.get(1).frequency;
        scanres.get(1).level;
        scanres.get(1).operatorFriendlyName;
        scanres.get(1).timestamp;
        scanres.get(1).venueName;
        //scanres.centerFreq0;
        //scanres.SSID;
        //results.add();
        //results.toString();
        //WiFiDetail wiFiDetail = new WiFiDetail(scanres.SSID, scanres.BSSID, scanres.capabilities);
        //results.add(wiFiDetail);
        */

    }

}
