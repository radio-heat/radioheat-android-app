package com.pb.radioheatmapclient;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.pb.radioheatmapclient.MainActivity.debugging;

/**
 * Created by Patrick Bäselt on 23.11.2016
 * - Erfassung der Daten mit WiFiManger
 * - JSON-Array mit wlanNetworks wird zurückgegeben
 */

public class WiFiWorker {
    //Aktiviert das Debugen in dem Task
    private Context cont;
    private int i=0;
    public WiFiWorker(Context context) {
        this.cont = context;
    }
    public JSONArray scan(){
        // Android WifiManager
        WifiManager wifiManager = (WifiManager) this.cont.getSystemService(Context.WIFI_SERVICE);
        JSONArray wlanNetworks = new JSONArray();
        for(ScanResult scandat : wifiManager.getScanResults()) {
            // Für Jedes Netzwerk einen eigenen Datensatz
            JSONObject wlanNetwork = new JSONObject();
            i++;
            try {
                wlanNetwork.put("bssid", scandat.BSSID);
                wlanNetwork.put("frequency", scandat.frequency);
                wlanNetwork.put("ssid", scandat.SSID);
                wlanNetwork.put("strength", scandat.level);
                //wlanNetwork.put("Weiss es nicht", scandat.capabilities);
                //wlanNetwork.put("CenterFrequenz0", scandat.centerFreq0);
                //wlanNetwork.put("CenterFrequenz1", scandat.centerFreq1);
                //wlanNetwork.put("ChannalBreite", scandat.channelWidth);
                wlanNetworks.put(wlanNetwork);
            } catch (JSONException e) {
                if (debugging == true) {System.out.println("WiFiWorker error: creating JSON-wlanNetwork DatensatzNr"+i+"   "+ e);continue;}
            }
        }
        System.out.println("WiFiWorker info: JSON-Array wlanNetworks("+wlanNetworks.length()+") übergeben. -> " + wlanNetworks);
        i=0;
        return wlanNetworks;
    }
}
