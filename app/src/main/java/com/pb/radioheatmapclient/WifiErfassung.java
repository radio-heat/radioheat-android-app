package com.pb.radioheatmapclient;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.R.id.time;

/**
 * Created by pbaes on 23.11.2016.
 */
public class WifiErfassung {
    //Aktiviert das Debugen in dem Task //TODO: Debuging deaktivieren
    private static Boolean debugging = true;

    private Context cont;

    public WifiErfassung(Context context) {
        this.cont = context;
    }

    public JSONArray scannen() {
        WifiManager wifiManager = (WifiManager) cont.getSystemService(Context.WIFI_SERVICE);
        JSONArray ScanDaten = new JSONArray();
        //if (hasWifi) {
            for(ScanResult scandat : wifiManager.getScanResults()) {
                JSONObject scanResult = new JSONObject();
                try {
                    scanResult.put("Date", time);
                    scanResult.put("SSID", scandat.SSID);
                    scanResult.put("BSSID", scandat.BSSID);
                    scanResult.put("Weiss es nicht", scandat.capabilities);
                    scanResult.put("CenterFrequenz0", scandat.centerFreq0);
                    scanResult.put("CenterFrequenz1", scandat.centerFreq1);
                    scanResult.put("ChannalBreite", scandat.channelWidth);
                    scanResult.put("Frequenz", scandat.frequency);
                    scanResult.put("Level", scandat.level);
                    ScanDaten.put(scanResult);
                } catch (JSONException e) {
                    if (debugging == true) {
                        System.out.println("Fehler SCAN2JSON " + e);
                        continue;
                    }
                }
            }
       // }
        return ScanDaten;

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
