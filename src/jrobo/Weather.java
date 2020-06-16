/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
 * Copyright (C) <2013> <BinaryStroke>
 * Copyright (C) <2013> <Muhammad Sajid>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 *
 * @author n0per, BullShark
 *
 */
package jrobo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author chris
 */
public class Weather {

    /*
     * For the JSON/DOC
     */
    //JSONObject jsonObject;
    /*
     * For the HTTP Connection
     */
    private URL url;
    private URLConnection conn;
    private OutputStreamWriter wr;
    private BufferedReader rd;
    // private GSONClass gsons = new GSONClass();

    /*
     * Miscelanous
     * XXX https://www.weather.gov/documentation/services-web-api
     * XXX https://api.weather.gov/points/{latitude},{longitude}
     * XXX Get the latitude, longitude using Google
     * XXX Not working for this longitude, latitude I found by googling...
     * XXX https://api.weather.gov/points/29.7438,98.4531
     */
    private static final String QUERY_URL = "https://api.wunderground.com/api/92c71a10c8515070/conditions/lang:EN/q/%s/%s.json";
    private String json;

    public Weather() {


        /*
         * For the HTTP Connection
         */
        url = null;
        conn = null;
        //TODO: Move BufferedReader declaration here
//    wr = null;
//    rd = null;

        /*
         * Miscelanous
         */
        json = "";
    }

    /**
     *
     * @param location
     * @param city
     * @return String
     */
    public String getJson(String location, String city) {
        try {
            /*
             * Create the query url from template
             */
            city = city.replace(" ", "_");
            location = location.replace(" ", "_");
            String weatherQuery = String.format(QUERY_URL, location, city);
            System.out.println("URL: " + weatherQuery);
            url = new URL(weatherQuery);

            System.out.println(url);

            conn = url.openConnection();

            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                json += line;
            }

            rd.close();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    public String getFormattedWeatherSummary(String json) {
        Gson gson = new GsonBuilder().create();
        WeatherJson weatherJson = gson.fromJson(json, WeatherJson.class);

        /*
         * put the data into a summary, with color
         */

        return weatherJson.toString();
    }

    /*
     * A main method for testing this class
     */
    public static void main(String[] args) {
        /*
         * if(args.length != 2) { System.err.println("Usage: java Weather
         * <location> <city>"); System.exit(-1);
    }
         */
        //System.out.println(new Weather().getXML(args[0]) );
        Weather w = new Weather();
        System.out.println(w.getFormattedWeatherSummary(w.getJson("Australia", "Melbourne")));
    }
} // EOF class
