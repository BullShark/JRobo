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

package expectusafterlun.ch.jrobo;

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
 * UrbanDict's purpose is to turn formatted JSON search query results into formatted dictionary definitions from Urban Dictionary.
 * 
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class UrbanDict {

	/* Miscellaneous */
	private final String BASE_URL = "https://api.urbandictionary.com";
	private final String WORD;
	private final int LIMIT;
	private static final int DEFAULT_LIMIT = 3;

	/**
	 * Overloaded constructor that calls the other one with a default value 5 for LIMIT
	 * @param WORD The WORD used for retrieving the Urban Dictionary definition
	 */
	public UrbanDict(final String WORD) {
		// Default LIMIT is used when no LIMIT is given
		this(WORD, DEFAULT_LIMIT);
	}

	/**
	 * Initialize UrbanDict with the WORD to define and a LIMIT to the number of results
	 * @param WORD The WORD used for retrieving the Urban Dictionary definition
	 * @param LIMIT Limit the results -1 or LIMIT {@literal <= 0} means unlimited results
 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	public UrbanDict(final String WORD, final int LIMIT) {
		this.WORD = WORD;
		this.LIMIT = (LIMIT <= 0) ? DEFAULT_LIMIT : LIMIT;
	}

	/**
	 * Retrieves JSON dictionary results from urbandictionary.com
	 * @return JSON weather data
	 */
	public String getJson() {

		String json = "";
		final String URL = (BASE_URL
				+ "/v0/define"
				+ "?term=" + WORD).replace(" ", "%20");
		System.out.println(TermColors.info(URL));
			
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
			System.out.println(TermColors.info(json));
			return json;
		}
	}

	/**
	 * Retrieves the Urban Dictionary results formatted from the JSON results
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @param HASCOLORS Should the formatted output use colors
	 * @return The formatted summary result
	 */
	public String getFormattedUrbanDef(final boolean HASCOLORS) {

		String result = "";
		
		try {
			Type UrbanJsonT = new TypeToken<ArrayList<UrbanJson>>() {
				}.getType();
			System.out.println(TermColors.info("UrbanJson Type: " + UrbanJsonT));

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			UrbanJson urbanJson = gson.fromJson(this.getJson(), UrbanJson.class);

			urbanJson.sort();
			
			System.out.println(TermColors.info("urbanJson.getListSize(): " + urbanJson.getListSize()));

			urbanJson.setLimit(LIMIT);

			System.out.println( TermColors.info("urbanJson.getListSize(): " + urbanJson.getListSize()) );
			System.out.println( TermColors.info("UrbanJson Type: " + UrbanJsonT) );

			urbanJson.sort(); // Ascending order -> Decending order

			result = (HASCOLORS) ? urbanJson.getColorString() : urbanJson.toString();

		} catch (JsonSyntaxException | IllegalStateException | NullPointerException ex) {
			Logger.getLogger(UrbanDict.class.getName()).log(Level.SEVERE, null, ex);
			result = "{ \"list\": \"Unable to retrieve UrbanDict json data\" }";

		} finally {

			System.out.println( TermColors.info(result) );
			return result;
		}
	}

	/* 
	 */

	/**
	 * A main method for testing this class
	 *
	 * @param args The word for defining by Urban Dictionary
	 */
	public static void main(String[] args) {
	
		if (args.length == 0) {
			System.err.println("Usage: java UrbanDict <word>");
			System.exit(-1);
		}
		System.out.println(new UrbanDict(args[0]).getFormattedUrbanDef(false));
	} // EOF main

	/**
	 * Strings and ints representing JSON data
	 *
	 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
	 */
	private class UrbanJson {

		private int total;
		private String result_type;
		private List<UrbanJsonItem> list;

		/**
		 * Not part of the JSON 
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
				System.out.println(TermColors.info("UrbanJson.limit: " + this.limit));

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

//			list.forEach(System.out::println);

			for(UrbanJsonItem uji : list) {
				System.out.println(uji);
			}
		}
	
		private String getColorString() {

			//this.sort();
			String result = "";
		
			for (UrbanJsonItem uji : list) {
				result += uji.getColorString();
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
		 * @author Chris Lemire {@literal <goodbye300@aim.com>}
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

			/*
			 * From the Pattern class docs:
			 *
			 * \s 	A whitespace character: [ \t\n\x0B\f\r]
			 *
			 * [a-z&&[^bc]] 	a through z, except for b and c: [ad-z] (subtraction)
			 *
			 * \v 	A vertical whitespace character: [\n\x0B\f\r\x85\u2028\u2029]
			 *
			 * \V 	A non-vertical whitespace character: [^\v]
			 *
			 * \p{Blank} 	A space or a tab: [ \t]
			 *
			 * ^ 	The beginning of a line
			 *
			 * $ 	The end of a line
			 *
			 * \b 	A word boundary
			 *
			 * X+ 	X, one or more times
			 *
			 * X{n,} 	X, at least n times
			 *
			 * static final int MULTILINE : Enables multiline mode.
			 */
			private String getColorString() {
				 /* Replace one or more whitespace chars with a single space */
				definition = definition.replaceAll("\\s+", " ");

				String result =
					MircColors.NORMAL + MircColors.BOLD + MircColors.GREEN + "Thumbs:"
					+ MircColors.NORMAL + MircColors.BOLD + " (+" + thumbs_up + " -" + thumbs_down + ") "
					+ MircColors.NORMAL + MircColors.BOLD + MircColors.CYAN + "Definition:"
					+ MircColors.NORMAL + MircColors.BOLD + " " + definition + "\n";

				return result;
			}

			@Override
			public String toString() {
				definition = definition.replaceAll("\\s+", " ");

				return "Thumbs: (+" + thumbs_up + " -" + thumbs_down + ") Definition: " + definition + "\n";
			}
		} // EOF UrbanJsonItem
	} // EOF UrbanJson
} // EOF UrbanDict
