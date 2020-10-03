/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
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
package jrobo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chris Lemire <goodbye300@aim.com>
 */
public class UrbanDict {

        /* For the HTTP Connection */
        private URL url;
        private URLConnection conn;
        private OutputStreamWriter wr;
        private BufferedReader rd;

        /* Miscelanous */
        private final String QUERY_URL = "https://api.urbandictionary.com";
        private String def;
        private String json;
        private String word;

        /* For the Gson/Json */
        private Gson gson;

        public UrbanDict(String word) {

                /* For the HTTP Connection */
                url = null;
                conn = null;
                this.word = word;

                /* Miscelanous */
                json = "";

                /* For the Gson/Json */
                gson = new Gson();

        }

        /**
         *
         * @return json
         */
        public String getJson() {
                try {
                        /* Create a URL obj from strings */
                        url = new URL(
                                (QUERY_URL
                                        + "/v0/define"
                                        + "?term=" + word).replace(" ", "%20") //FIXME String.replace() or String.replaceAll()
                        );

                        System.out.println("[+++]\tURL: " + url);

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

                //System.out.println("JSON: " + json);
                return json;
        }

        public String getFormattedUrbanDef(final boolean hasColors, final int limit) {

                /*
		 * TODO Add try/catch to handle
		 * TODO The exception that no JSON is received
                 */
                UrbanJson urbanJson;
                try {
                        Type UrbanJsonT = new TypeToken<ArrayList<UrbanJson>>() {
                        }.getType();
                        System.out.println("[+++]\tUrbanJson Type: " + UrbanJsonT);

                        gson = new GsonBuilder().setPrettyPrinting().create();
                        urbanJson = gson.fromJson(this.getJson(), UrbanJson.class);

                } catch (JsonSyntaxException | IllegalStateException ex) {
                        ex.printStackTrace();
                        return "Unable to retrieve the weather";
                }

                /* Handles NullPointerException that occurs if the URL DNE */
//		return urbanJson.toString();
                return urbanJson.getColorString();

        }

        /*
   * A main method for testing this class
         */
        public static void main(String[] args) {
                if (args.length == 0) {
                        System.err.println("Usage: java UrbanDict <word>");
                        System.exit(-1);
                }
                System.out.println(new UrbanDict(args[0]).getFormattedUrbanDef(false, -1));
        } // EOF main

        /**
         *
         * @author Christopher Lemire <christopher.lemire@gmail.com>
         */
        public class UrbanJson {

                private int total;
                private String result_type;
                public List<UrbanJsonItem> list;

                /* Not part of the Json */
                private int limit;

                /** 
                 *
                 */
                public UrbanJson() {
                        
                /*
                        List<Movie> movies = Arrays.asList(
                                new Movie("Lord of the rings", 8.8, true),
                                new Movie("Back to the future", 8.5, false),
                                new Movie("Carlito's way", 7.9, true),
                                new Movie("Pulp fiction", 8.9, false));

                        movies.sort(new Comparator<Movie>() {

                                @Override
                                public int compare(Movie m1, Movie m2) {
                                        if (m1.getStarred() == m2.getStarred()) {
                                                return 0;
                                        }
                                        return m1.getStarred() ? -1 : 1;
                                }
                        });
                        ​
                        movies.forEach(System.out::println);
                 */


                }

                /**
                 *
                 * @param limit
                 */
                public UrbanJson(final int limit) {
                        super();
                        this.limit = limit;
                }

                public String getColorString() {
                        String result = "";
                        for (UrbanJsonItem uji : list) {
                                result += uji.getColorString() + " ";
                        }

                        return result;
                }

                /**
                 *
                 * @override
                 */
                public String toString() {
                        return "Total: " + total + " has result_type: " + result_type();
                }

                /**
                 *
                 * @author Christopher Lemire <goodbye300@aim.com>
                 */
                public class UrbanJsonItem {

                        private String definition;
                        private int thumbs_up;
                        private int thumbs_down;

                        /**
                         * @return the definition
                         */
                        public String getDefinition() { return definition; }

                        /**
                         * @return the thumbs_up
                         */
                        public int getThumbs_up() { return thumbs_up; }

                        /**
                         * @return the thumbs_down
                         */
                        public int getThumbs_down() { return thumbs_down; }

                        public String getColorString() {
                                setDefinition(getDefinition().replaceAll("\\r|\\n", " "));
                                setDefinition(getDefinition().replaceAll("\\s++", " "));
                                String mystring
                                        = MircColors.BOLD + MircColors.GREEN + "Thumbs:"
                                        + MircColors.NORMAL + MircColors.BOLD + " (+" + getThumbs_up() + " -" + getThumbs_down() + ") "
                                        + MircColors.NORMAL + MircColors.BOLD + MircColors.CYAN + "Definition:"
                                        + MircColors.NORMAL + MircColors.BOLD + " " + getDefinition() + "\n";
                                return mystring;
                        }

                        public String toString() {
                                setDefinition(getDefinition().replaceAll("\\r|\\n", " "));
                                setDefinition(getDefinition().replaceAll("\\s++", " "));
                                return "Thumbs: (+" + getThumbs_up() + " -" + getThumbs_down() + ") Definition: " + getDefinition() + "\n";

                        }
                }
        }
} // EOF class
