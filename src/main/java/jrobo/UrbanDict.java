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
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Chris Lemire <goodbye300@aim.com>
 */
public class UrbanDict {

        /* Miscellaneous */
        private final String QUERY_URL = "https://api.urbandictionary.com";
        private final String WORD;
	private final int LIMIT;
	private static final int DEFAULT_LIMIT = 3;

	/**
	 * Overloaded constructor that calls the other one with a default value 5 for LIMIT
	 * @param word The WORD used for retrieving the Urban Dictionary definition
	 */
	public UrbanDict(final String word) {
		// Default LIMIT is used when no LIMIT is given
		this(word, DEFAULT_LIMIT);
        }

	/**
	 * @param word The WORD used for retrieving the Urban Dictionary definition
	 * @param limit Limit the results -1 or limit <= 0 means unlimited results
 	 * @author Chris Lemire <goodbye300@aim.com>
	 */
	public UrbanDict(final String word, final int limit) {
                WORD = word;
		LIMIT = (limit <= 0) ? DEFAULT_LIMIT : limit;
        }

        /**
         * @TODO https://blog.api.rakuten.net/top-10-best-dictionary-apis-oxford-urban-wordnik/
         * @return Json weather data
         */
        public String getJson() {

		String json = "";
		final String URL = (QUERY_URL
				+ "/v0/define"
				+ "?term=" + WORD).replace(" ", "%20");
		System.out.println("[+++]\t" + URL);
			
		/* Create a URL obj from strings */
		try ( BufferedReader br = new BufferedReader(new InputStreamReader(
			new URL(URL).openStream()))) {

			String line;

			while ((line = br.readLine()) != null) {
				json += line;
			}

		} catch (IOException ex) {
			Logger.getLogger(UrbanDict.class.getName()).log(Level.SEVERE, null, ex);
			json = "{ \"data\": \"Unable to retrieve UrbanDict json data\" }";

                } finally {
			System.out.println("[+++]\t" + json);
	                return json;

		}
        }

	/**
	 *
	 * @param hasColors
	 * @return
	 */
	public String getFormattedUrbanDef(final boolean hasColors) {

		String result = "";
                try {
                        Type UrbanJsonT = new TypeToken<ArrayList<UrbanJson>>() {
                        }.getType();
                        System.out.println("[+++]\tUrbanJson Type: " + UrbanJsonT);

                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        UrbanJson urbanJson = gson.fromJson(this.getJson(), UrbanJson.class);

			urbanJson.sort();
			
                        System.out.println("[+++]\turbanJson.getListSize(): " + urbanJson.getListSize());

			urbanJson.setLimit(LIMIT);

                        System.out.println("[+++]\turbanJson.getListSize(): " + urbanJson.getListSize());

			urbanJson.sort(); // Ascending order -> Decending order

			result = (hasColors) ? urbanJson.getColorString() : urbanJson.toString();
			return result;

                } catch (JsonSyntaxException | IllegalStateException | NullPointerException ex) {
			Logger.getLogger(UrbanDict.class.getName()).log(Level.SEVERE, null, ex);
			result = "{ \"list\": \"Unable to retrieve UrbanDict json data\" }";

                } finally {
			System.out.println("[+++]\t" + result);
			return result;

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
                System.out.println(new UrbanDict(args[0]).getFormattedUrbanDef(false));
        } // EOF main

        /**
         *
         * @author Christopher Lemire <christopher.lemire@gmail.com>
         */
        private class UrbanJson {

                private int total;
                private String result_type;
                private List<UrbanJsonItem> list;

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
		private int getLimit() {
			return limit;
		}

		/**
		 * @return size of ArrayList<UrbanJsonItem> list
		 */
		private int getListSize() {
			return list.size();
		}

		/**
		 * @param limit the limit to set
		 */
		private void setLimit(int limit) {
			if(limit > 0 && !list.isEmpty() && list != null) {
				this.limit = limit;
                                System.out.println("[+++]\tUrbanJson.limit: " + this.limit);

				//sort(); // subList() should be given a sorted List
				List<UrbanJsonItem> temp = list;

				list = temp.stream().limit( this.limit ).collect(Collectors.toList()); temp = null;
                        }
		}

                /** 
                 * Override and defines Comparator\<UrbanJsonItem\>().compare(UrbanJsonItem, UrbanJsonItem) 
                 * Used for sorting the ArrayList\<UrbanJsonItem\> by the number of thumbs_up
                 */
                private void sort() {

                        list.sort(new Comparator<UrbanJsonItem>() {

                                @Override
                                public int compare(UrbanJsonItem uji1, UrbanJsonItem uji2) {
                                        if (uji1.getThumbsUp() < uji2.getThumbsUp()) return 1; 
                                        if (uji1.getThumbsUp() > uji2.getThumbsUp()) return -1; 
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
	
                private String getColorString() {

			//this.sort();
                        String result = "";
                        for (UrbanJsonItem uji : list) {
                                result += uji.getColorString() + " ";
                        }

                        return result;
                }

		@Override
                public String toString() {

			//this.sort();
                        return "Total: " + total + " has result_type: " + result_type + " with list: " + list;
                }

                /**
                 *
                 * @author Christopher Lemire <goodbye300@aim.com>
                 */
                private class UrbanJsonItem {

                        private String definition;
                        private int thumbs_up;
                        private int thumbs_down;

                        /**
                         * @return the definition
                         */
                        private String getDefinition() { return definition; }

                        /**
                         * @return the thumbs_up
                         */
                        private int getThumbsUp() { return thumbs_up; }

                        /**
                         * @return the thumbs_down
                         */
                        private int getThumbsDown() { return thumbs_down; }

                        private String getColorString() {
                                definition = definition.replaceAll("\\r|\\n", " ");
                                definition = definition.replaceAll("\\s++", " ");

                                String result =
                                        MircColors.BOLD + MircColors.GREEN + "Thumbs:" +
                                        MircColors.NORMAL + MircColors.BOLD + " (+" + thumbs_up + " -" + thumbs_down + ") " +
                                        MircColors.NORMAL + MircColors.BOLD + MircColors.CYAN + "Definition:" +
                                        MircColors.NORMAL + MircColors.BOLD + " " + definition + "\n"; 

                                        return result;
                        }

			@Override
                        public String toString() {
                                definition = definition.replaceAll("[\\r\\n\\s]++", " ");

                                return "Thumbs: (+" + thumbs_up + " -" + thumbs_down + ") Definition: " + definition + "\n";
                        }
                } // EOF UrbanJsonItem
        } // EOF UrbanJson
} // EOF UrbanDict
