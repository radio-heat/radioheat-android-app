package com.pb.radioheatmapclient;

import com.pb.radioheatmapclient.MainActivity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class GPSErfassung implements LocationListener {

    private final Context c;

    //Variable, zum speichern, ob GPS aktiv ist
    private boolean GPSaktiv = false;

    //Die Variable Location wird später benötigt und beinhaltet den Längen- und Breitengrad
    private Location position;


    //Die Variable LocationManager wird später benötigt und ermöglicht die Positionsbestimmung
    protected LocationManager locationManager;

    public GPSErfassung(Context context) {
        this.c = context;
    }

    public void setPosition() {
        locationManager = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
        //ließt den GPS Status aus (Aktiv = Ja/Nein)
        GPSaktiv = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GPSaktiv) {
            if (position == null) {
                //Bestimmung der GPS Position, sofern GPS eingeschaltet ist. Die Daten werden später in der Variable "position" gespeichert.
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        position = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            }
        }
    }

    //Beendet GPS Bestimmung
    public void stopGPSposition() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(GPSErfassung.this);
        }
    }

    //Gibt den Breitengrad als Double zurück
    public double getBreitengrad(){
        if (position != null){
            return position.getLatitude();
        }else{
            return 0;
        }
    }

    //Gibt den Längengrad als double zurück
    public double getLaengengrad(){
        if(position != null){
            return position.getLongitude();
        }else{
            return 0;
        }
    }

    //Gibt zurück, ob GPS eingeschaltet ist (true oder false)
    public boolean isOn() {
        return this.GPSaktiv;
    }

    public void onResume(){
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        position = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}