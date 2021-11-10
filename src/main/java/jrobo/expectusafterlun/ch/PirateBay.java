/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
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
package jrobo.expectusafterlun.ch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PirateBay retrieves JSON results from a thepiratebay python flask API as JSON, formats the results, and sends them to IRC
 * 
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class PirateBay {

    /* For the HTTP Connection */
    private URL url;
    private URLConnection conn;
    private BufferedReader rd;
    private String fullUrl;
    //TODO Fix bug, if the url is not available, the bot will throw an exception and crash
    private static final String BASE_URL = "http://odin.root.sx/thepiratebay.php";

    /* Miscellaneous */
    private final String s_name;
    private String s_switch="s";
    private final int MAX_RESULTS = 5;

    /* For the Gson/Json */
    private Gson gson;
    private String json;

    /**
     * Get the switch used for sorting the results, the query to be searched and set up the connection to the PirateBay API
     * @param command Made up of the sort type and search query, sort by seeds, peers, date, or name
     */
    public PirateBay(String command) {
        String[] splitArray = command.split("\\s+");
        String argument="";
        if (splitArray.length > 0) {
            switch (splitArray[0]) {
                case "-s":
                    argument=splitArray[0];
                    s_switch = "s";
                    break;
                case "-l":
                    argument=splitArray[0];
                    s_switch = "l";
                    break;
                case "-d":
                    argument=splitArray[0];
                    s_switch = "d";
                    break;
                case "-n":
                    argument=splitArray[0];
                    s_switch = "n";
                default:
                    s_switch = "s";
                    break;
            }
            command=command.replace(argument, "");
        }
        if(command.length()>0) {
            s_name =command;
        } else {
            s_name ="blackhats";
        }
        /* For the HTTP Connection */
        url = null;
//        conn = null;
        rd = null;
        fullUrl = "";
        /* Miscelanous */
        json = "";
        /* For the Gson/Json */
        gson = new Gson();
    }

    /**
     * Gets JSON data for a search query to The Pirate Bay Python Flask API
     *
     * @author Christopher Lemire {@literal <goodbye300@aim.com>}
     * @return JSON retrieved from the URL
     */
    public String getJson() {
        try {
            /* Create a URL obj from strings */
            fullUrl =
		    (BASE_URL + 
			    "?name=" + s_name + 
			    "&orderby=" + s_switch + 
			    "&limit=" + MAX_RESULTS
		    ).replaceAll(" ", "%20");

            url = new URL(fullUrl);

            /* Debug */
            System.out.println(fullUrl);

            conn = url.openConnection();
            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                json = json.concat(line);
            }
            rd.close();
        } catch (MalformedURLException | ConnectException | FileNotFoundException ex) {
            Logger.getLogger(PirateBay.class.getName()).log(Level.SEVERE, null, ex);
            json = "{ \"data\": \"Unable to retrieve Torrent json data\" }";

        } finally {
            return json;
        }

    }

    /**
     * The formatted search results from ThePirateBay JSON API with colors and formatting if HASCOLORS
     * 
     * @author Christopher Lemire {@literal <goodbye300@aim.com>}
     * @param HASCOLORS True for IRC colors, false otherwise
     * @return Formatted JSON data optionally with colors
     */
    public String getFormattedResult(final boolean HASCOLORS) {

        PirateBayJsonItem[] results;
        String output = "";

        try {

            gson = new GsonBuilder().setPrettyPrinting().create();
            results = gson.fromJson(this.getJson(), PirateBayJsonItem[].class);

            if(HASCOLORS) {
                for (PirateBayJsonItem result : results) {
                    output += result.getColorString();
                }
            } else {
                 for (PirateBayJsonItem result : results) {
                     output += result.toString();
                 }
            }
        } catch(IllegalStateException | NullPointerException | JsonSyntaxException ex) {
          Logger.getLogger(PirateBay.class.getName()).log(Level.SEVERE, null, ex);
          output = "{ \"data\": \"Unable to retrieve Torrent json data\" }";

        } finally {

            return output;
        }
    }

    /**
     * A main method for testing this class
     * No args, searches matrix reloaded for you sorted by seeds
     * @param args The search query for thepiratebay.org used by the API
     */
    public static void main(String[] args) {

        if(args.length == 0) {
            System.out.println(new PirateBay("-s matrix reloaded").getFormattedResult(false));
        } else {
            System.out.println(new PirateBay(Arrays.toString(args)).getFormattedResult(false));
        }

    } // EOF main

    /**
     * Strings and ints representing JSON data
     *
     * @author Christopher Lemire {@literal <goodbye300@aim.com>}
     */
    public class PirateBayJsonItem {
        public String type;
        public String name;
        public String url;
        public String tinyurl;
        public String Uploaded;
        public String Size;
        public String ULed;
        public String seeders;
        public String leechers;

        /**
         * A colored for IRC String representation of LeetJsonItem
         *
         * @author Christopher Lemire {@literal <goodbye300@aim.com>}
         * @return Colored for IRC String representing LeetJsonItem
         */
        public String getColorString() {
            final String RESULT =
              MircColors.BOLD + name + " " +
              MircColors.GREEN + "<" + tinyurl + ">" +
              MircColors.NORMAL + MircColors.BOLD + " (" + Size + 
              MircColors.GREEN + " S:" + seeders +
              MircColors.CYAN + " L:" + leechers + 
              MircColors.NORMAL + MircColors.BOLD + ")\n";
             
            return RESULT;
        }

        /**
         * A String representation of LeetJsonItem
         *
         * @author Christopher Lemire {@literal <goodbye300@aim.com>}
         * @return A summary without colors and formatting of LeetJsonItem
         */
        @Override
        public String toString() {
            final String RESULT = name + " <" + tinyurl + "> (" + Size + " S:" + seeders + " L:" + leechers + ") \n";
            return RESULT;
        }
    }
}
