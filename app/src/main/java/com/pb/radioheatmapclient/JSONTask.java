package com.pb.radioheatmapclient;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pbaes on 29.12.2016.
 */

public class JSONTask extends AsyncTask<String, JSONArray, String>{
    //Aktiviert das Debugen in dem Task //TODO: Debuging deaktivieren
    private static Boolean debugging = true;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("INFO - HintergrundTask gestarted");
        //Toast.makeText(MainActivity,"Json Data werden gesendet",Toast.LENGTH_LONG).show();
    }

    @Override
    protected  String doInBackground(String... params ){
        // JSON Datensatz zum Testen, TODO: druch JSONObjekt welche aus LIST gespeist wird ersetzen
        //String json = "{\"Messung\": { \"Zeit\": \"genaue Zeit\"} }";
        String json = params[1];
        // ÜbermittlungsArrays
        byte[] sendenA;
        //byte[] empfangsA; TODO:Entfernen wenn nich gebraucht
        String zeile=null;
        BufferedReader reader = null;
        HttpURLConnection ServerConnection = null;
        try {
            // Übergabe JSON Daten in Array mit UTF-8 Codierung
            sendenA = json.getBytes("UTF-8");
                         if(debugging==true){System.out.println("INFO - Exportdatensatz: "+ json +"\nINFO - Übermittlungsdatenstrom: "+sendenA);}
            // Übergabe Parameter-0 als url vom Typ URL
            URL url = new URL(params[0]);
                        if(debugging==true){System.out.println("INFO - Sendeadresse: "+ url);}
            // Öffnen und Configuration der ServerConnection
            ServerConnection = (HttpURLConnection) url.openConnection();
                        if(debugging==true){System.out.println("INFO - ServerSocket steht");}
            // Legt die RequestMethod auf Post fest;
            ServerConnection.setDoOutput(true);
            ServerConnection.setUseCaches(false);
            // Damit der Datenstrom begrenzt wird muss die Länge des Array übergeben werden
            ServerConnection.setFixedLengthStreamingMode(sendenA.length);
                        if(debugging==true){System.out.println("INFO - Exportbytes:"+sendenA.length);}
            ServerConnection.setRequestProperty("Content-Type", "application/json");
            // Datenausgangsstrom gepuffert
            OutputStream OStream = new BufferedOutputStream(ServerConnection.getOutputStream());
                        if(debugging==true){System.out.println("INFO - OStream: Okay");}
            // Schreiben der Daten
            OStream.write(sendenA);
            // Übermitteln und Rücksetzen
            OStream.flush();
                        if(debugging==true){System.out.println("INFO - Übermittlung: Okay");}
            //InputStream IStream = new BufferedInputStream(ServerConnection.getInputStream());

            reader = new BufferedReader(new InputStreamReader(ServerConnection.getInputStream()));
                        if(debugging==true){System.out.println("INFO - IStreamReader: Okay");}

            //Schreibe Zeilenweise HTTP Req in Console solange eine Zeile kommt
            while ((zeile = reader.readLine()) != null) {
                System.out.println("INFO - Serverantwort: " + zeile+ " \n");
            }
        }
        catch (MalformedURLException e) {
            System.out.println("FEHLER - 01 MalformURLExpetion "+e);
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("FEHLER - 02 IOExeption "+e);
            e.printStackTrace();
        }
        finally {
            if (ServerConnection != null){
                System.out.println("INFO - Trennung des Socket");
                ServerConnection.disconnect();
            }
        }
        return "erfolgreich";
    }
    @Override
    protected  void onPostExecute(String ergebnis){
        super.onPostExecute(ergebnis);
        System.out.println("INFO - HintergrundTask "+ergebnis+ " erledig");
    }
}


