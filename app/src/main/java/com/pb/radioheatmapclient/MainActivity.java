package com.pb.radioheatmapclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import static com.pb.radioheatmapclient.R.id.txtOutput;

/*
    Hauptklasse hier wir das Layout gerufen, die Karte Aufgebaut,
 */
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    /*
    Deklarationen und Initalisierungen
    */
    private GoogleMap gMap;
    private GPSErfassung GPS;
    private WifiErfassung SCAN;
    private Button cmdScanWifi = null;
    private Button cmdExportData = null;
    private Button cmdlocateMe = null;
    private TextView txtOutput = null;
    private LatLng messpunkt;
    private JSONArray measurementdata = null;
    protected String serverIP = "http://82.165.75.129:8080/add";
    private static Boolean debugging = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standort_scan);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Erzeugen einer Position mit Log und Lat
        GPS = new GPSErfassung(this);
        GPS.setPosition();
        // Erzeugen WifiScanOnjekts
        SCAN = new WifiErfassung(this);
        // Anlegen der Buttons
        cmdlocateMe = (Button) findViewById(R.id.cmdlocateMe);
        cmdExportData = (Button) findViewById(R.id.cmdExportData);
        cmdScanWifi = (Button) findViewById(R.id.cmdScanWifi);
        // Anlegen der textfeld fürs Debuggen
        txtOutput = (TextView) findViewById(R.id.txtOutput);
        // bei Click löschen des Default Markers und setzen der Resume Position
        cmdlocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPS.onResume();
                gMap.clear();
                LatLng picker = new LatLng(GPS.getBreitengrad(), GPS.getLaengengrad());
                gMap.addMarker(new MarkerOptions()
                        .position(picker)
                        .title("Präzesier mich")
                        .flat(true)
                        //.rotation(1)
                        .draggable(true)
                );
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picker, 19));
                //TODO Implementierung
                if (debugging == true) {System.out.println("Resumed");}
            }
        });
        // bei Click wird eine Messung durchgeführt und Angezeigt
        cmdScanWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messpunkt != null) {
                measurementdata = SCAN.scannen(messpunkt);
                }
                if(debugging==true){System.out.println("WiFi Scan Ergenisse:" + measurementdata);}
            }
        });
        // bei Click wird eine Messung durchgeführt und soll dann in die Datenbank mit LatLng geschrieben werden
        cmdExportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messpunkt == null) {
                    if (debugging == true) {System.out.println("Position wählen");}
                } else {
                    new JSONTask().execute(serverIP, measurementdata.toString(), "was");
                    if (debugging == true) {System.out.println("Export Datensatz: " + messpunkt.latitude + " " + messpunkt.longitude + " " + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + " " + SCAN.scannen(messpunkt) + "TODO Datenbankanbindung");}
                }
            }

            private void writeStream(OutputStream out) {
                if (debugging == true) {System.out.println("Export Datensatz: " + messpunkt.latitude + " " + messpunkt.longitude + " " + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + " " + SCAN.scannen(messpunkt) + "TODO Datenbankanbindung");}
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GPS.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GPS.stopGPSposition();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                gMap.setMyLocationEnabled(true);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            // Oder geben wir lieber eine Fehlermeldung aus?
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (GPS.isOn()) {
            LatLng picker = new LatLng(GPS.getBreitengrad(), GPS.getLaengengrad());
            gMap.addMarker(new MarkerOptions()
                    .position(picker)
                    .title("Präzesier mich")
                    .flat(true)
                    //.rotation(1)
                    .draggable(true)
            );
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picker, 19));
        } else {
            //Wenn kein GPS verfühgbar
            LatLng picker = new LatLng(51.3132, 12.376);
            gMap.addMarker(new MarkerOptions().position(picker).title("Default"));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picker, 20));
        }
        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener(){
            @Override
            public void onMarkerDragStart(Marker marker) {

            }
            @Override
            public void onMarkerDrag(Marker marker) {
                messpunkt = marker.getPosition();
                txtOutput.setText("Deine Position"+messpunkt.toString());
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                messpunkt = marker.getPosition();
                txtOutput.setText("Messpunkt gespeichert "+messpunkt.toString());
            }
        });
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        /*
        LatLng selectedPicker = new LatLng(selectedLocalitaet.getLatitude(), selectedLocalitaet.getLongitude());
        gMap.addMarker(new MarkerOptions()
                .position(selectedPicker)
                .title("Export!!")
                .rotation(180)
        );
        */
    }
}

