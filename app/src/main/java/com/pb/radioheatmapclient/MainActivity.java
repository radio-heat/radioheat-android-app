package com.pb.radioheatmapclient;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/*
    Hauptklasse hier wir das Layout gerufen, die Karte Aufgebaut,
 */
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    /*
    Deklarationen und Initalisierungen
    */
    public static Boolean debugging = true;   //TODO: Debuging deaktivieren
    private GoogleMap gMap;
    private GPSLocation GPS;
    private WiFiWorker SCAN;
    private Button cmdScanWifi = null;
    private Button cmdExportData = null;
    private Button cmdlocateMe = null;
    private TextView txtOutput = null;
    private LatLng messpunkt;
    private JSONArray measurementdata = null;
    private int viewzoom;
    protected String serverIP = "http://82.165.75.129:8080/add"; //TODO: Serveradresse ändern

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standort_scan);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Zoomstufe für Kartendarstellung
        viewzoom = 40;
        // Erzeugen einer Position mit Log und Lat
        GPS = new GPSLocation(this);
        GPS.setPosition();
        // Erzeugen WifiScanOnjekts
        SCAN = new WiFiWorker(this);
        // Anlegen der Buttons
        cmdlocateMe = (Button) findViewById(R.id.cmdlocateMe);
        cmdExportData = (Button) findViewById(R.id.cmdExportData);
        cmdScanWifi = (Button) findViewById(R.id.cmdScanWifi);
        // Anlegen der textfeld fürs Debuggen
        txtOutput = (TextView) findViewById(R.id.txtOutput);
        // bei Click löschen des Default Markers und setzen der Resume Position
        if(GPS.getBreitengrad()==0){GPS.onResume();}
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
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picker, viewzoom));
                Toast.makeText(getApplicationContext(), "Position aktualisiert", Toast.LENGTH_LONG).show();
                if (debugging == true) {System.out.println("MainActivity info: letzte bekannte Position wurde gerufen");}
            }
        });
        // bei Click wird eine Messung durchgeführt und Angezeigt
        cmdScanWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messpunkt != null) {
                    measurementdata = SCAN.scan();
                    Toast.makeText(getApplicationContext(), "Scan erfolgreich", Toast.LENGTH_LONG).show();
                    if (debugging == true) {System.out.println("MainActivity info: Scan OK");}
                } else {
                    // Hole die letzte Position im System
                    GPS.onResume();
                    messpunkt = new LatLng(GPS.getBreitengrad(), GPS.getLaengengrad());
                    System.out.println(messpunkt.latitude + " " + messpunkt.longitude);
                    Toast.makeText(getApplicationContext(), "Scan ohne Präzisierte Position", Toast.LENGTH_LONG).show();
                    if(debugging==true){System.out.println("MainActivity info: WiFi Scan nur mit unpräzisierter Location");}
                    measurementdata = SCAN.scan();
                }
            }
        });
        // bei Click wird eine Messung durchgeführt und soll dann in die Datenbank mit LatLng geschrieben werden
        cmdExportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test= "";
                if (messpunkt == null) {
                    Toast.makeText(getApplicationContext(), "Bitte Position präzisieren", Toast.LENGTH_LONG).show();
                    if (debugging == true) {System.out.println("MainActivity info: Position muss präzisiert werden");}
                } else {
                    if(measurementdata!=null){
                        new JSONTask().execute(serverIP, Export.get(messpunkt,measurementdata).toString(), "was");
                        //new JSONTask().execute(serverIP, Export.get(messpunkt,;, "was");
                    }else{
                        if (debugging == true) {System.out.println("MainActivity info: Scan vor dem Senden "+ test);}
                        new JSONTask().execute(serverIP, Export.get(messpunkt,SCAN.scan()).toString(), " ");
                        Toast.makeText(getApplicationContext(), "Automatisch vor senden gescannt", Toast.LENGTH_LONG).show();
                    }
                }
            }
/*            private void writeStream(OutputStream out) {
                if (debugging == true) {System.out.println("Export Datensatz: " + messpunkt.latitude + " " + messpunkt.longitude + " " + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + " " + SCAN.scan() + "TODO Datenbankanbindung");}
            }*/
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
    /*private void addHeatMap() {

        List<LatLng> list = null;

        // Get the data: latitude/longitude positions of police stations.
        try {
            list =  new getTask().execute("http://82.165.75.129:8080/measurement/list", Export.get(messpunkt,measurementdata).toString(), "was");
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = gMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
    */
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
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picker, viewzoom));
        } else {
            //Wenn kein GPS verfühgbar
            LatLng picker = new LatLng(51.3132, 12.376);
            gMap.addMarker(new MarkerOptions().position(picker).title("Fehler"));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(picker, 10));
            Toast.makeText(getApplicationContext(), "Fehler: Standortdienste sind Deaktiviert!!", Toast.LENGTH_LONG).show();
            if (debugging == true) {System.out.println("MainActivity error: Standortdienst sind Deaktiviert");}
        }
        //addHeatMap();
         gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener(){
            @Override
            public void onMarkerDragStart(Marker marker) {

            }
            @Override
            public void onMarkerDrag(Marker marker) {
                messpunkt = marker.getPosition();
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                messpunkt = marker.getPosition();
                Toast.makeText(getApplicationContext(), "Ihre Position ist: "+messpunkt.toString(), Toast.LENGTH_SHORT).show();
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

