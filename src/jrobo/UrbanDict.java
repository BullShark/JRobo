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
import java.util.Comparator;
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

                        System.out.println("[+++]\t" + url);

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

                UrbanJson urbanJson;
                try {
                        Type UrbanJsonT = new TypeToken<ArrayList<UrbanJson>>() {
                        }.getType();
                        System.out.println("[+++]\tUrbanJson Type: " + UrbanJsonT);

                        gson = new GsonBuilder().setPrettyPrinting().create();
                        urbanJson = gson.fromJson(this.getJson(), UrbanJson.class);

			urbanJson.sort();
			urbanJson.setLimit(limit);

			if(hasColors) {
                		return urbanJson.getColorString();
			} else {
				return urbanJson.toString();
			}

                } catch (JsonSyntaxException | IllegalStateException | NullPointerException ex) {
                        ex.printStackTrace();
                        return "Unable to retrieve the weather";
                }
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

                /**
		 * Not part of the Json 
		 * Used for limiting the number of results
		 * A limit of zero means there's no limit
		 * @since 2020-10-03
		 */
                private int limit = 0;

		/**
		 * @return the limit
		 */
		public int getLimit() {
			return limit;
		}

		/**
		 * @param limit the limit to set
		 */
		public void setLimit(int limit) {
			this.limit = limit;
		}

                /** 
                 * 
                 * The constructor overrides and defines Comparator<UrbanJsonItem>().compare(UrbanJsonItem, UrbanJsonItem) 
                 * Used for sorting the ArrayList<UrbanJsonItem> by the number of thumbs_up
                 *
                 * @param limit Limits the number of results
                 */

                public void sort() {

                        list.sort(new Comparator<UrbanJsonItem>() {

                                @Override
                                public int compare(UrbanJsonItem uji1, UrbanJsonItem uji2) {
                                        if (uji1.getThumbsDown() < uji2.getThumbsDown()) return -1; 
                                        if (uji1.getThumbsDown() > uji2.getThumbsDown()) return 1; 
                                        else return 0; 
                                }
                        });

			/**
			 * @TODO Check the output. Is it sorted?
			 * @TODO Remove after testing
			 */
			Thread.dumpStack();
                        list.forEach(System.out::println);
		}
	
                public String getColorString() {

			//this.sort();
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

			//this.sort();
                        return "Total: " + total + " has result_type: " + result_type + " with list: " + list;
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
                        public int getThumbsUp() { return thumbs_up; }

                        /**
                         * @return the thumbs_down
                         */
                        public int getThumbsDown() { return thumbs_down; }

                        public String getColorString() {
                                definition = definition.replaceAll("\\r|\\n", " ");
                                definition = definition.replaceAll("\\s++", " ");

                                String result =
                                        MircColors.BOLD + MircColors.GREEN + "Thumbs:" +
                                        MircColors.NORMAL + MircColors.BOLD + " (+" + thumbs_up + " -" + thumbs_down + ") " +
                                        MircColors.NORMAL + MircColors.BOLD + MircColors.CYAN + "Definition:" +
                                        MircColors.NORMAL + MircColors.BOLD + " " + definition + "\n"; 

                                        return result;
                        }

                        public String toString() {
                                definition = definition.replaceAll("[\\r\\n\\s]++", " ");

                                return "Thumbs: (+" + thumbs_up + " -" + thumbs_down + ") Definition: " + definition + "\n";
                        }
                } // EOF UrbanJsonItem
        } // EOF UrbanJson
} // EOF UrbanDict
