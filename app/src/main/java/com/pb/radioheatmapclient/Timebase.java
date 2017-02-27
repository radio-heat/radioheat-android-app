package com.pb.radioheatmapclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.pb.radioheatmapclient.MainActivity.debugging;

/**
 * Created by Patrick Bäselt on 27.02.17.
 */

public class Timebase {

    public JSONObject get() {
        Date now = new Date();
        SimpleDateFormat format_day = new SimpleDateFormat("dd");
        SimpleDateFormat format_mounth = new SimpleDateFormat("MM");
        SimpleDateFormat format_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat format_hour = new SimpleDateFormat("hh");
        SimpleDateFormat format_minute = new SimpleDateFormat("mm");
        SimpleDateFormat format_second = new SimpleDateFormat("ss");
        // Für jede Messung einen Zeitstempel
        JSONObject datetime = new JSONObject();
        try {
            // Zeitstempel JSON Objekt mit Seperierten Daten befüllt
            datetime.put("day", Integer.parseInt(format_day.format(now)));
            datetime.put("month", Integer.parseInt(format_mounth.format(now)));
            datetime.put("year", Integer.parseInt(format_year.format(now)));
            datetime.put("hour", Integer.parseInt(format_hour.format(now)));
            datetime.put("minute", Integer.parseInt(format_minute.format(now)));
            datetime.put("second", Integer.parseInt(format_second.format(now)));
            // Zeitstempel als JSON Objekt in das JSON ARRAY
            return datetime;
        } catch (JSONException e) {
            if (debugging == true) {
                System.out.println("Error: creating JSON-Datetimestamp" + e);
            }
        }
    }
}