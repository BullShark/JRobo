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

package jrobo.expectusafterlun.ch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Leet retrieves JSON results from a Leetx.to Python Flask API as JSON, formats the results, and sends them to IRC
 * 
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class Leet {

	/* 
	 * Example to test this class:
	 * curl -H"API_KEY:oTloaqhI5N17SBBD1fHhQlgGaf1Ne8uy"
	 * http://expectusafterlun.ch:5000/1337x/matrix/1/Movies
	 *
	 * CATEGORY can be omitted for the SEARCH
	 *
	 * Use String.format("BASE_URL/{%s}/{%s}/{%s}", new String(), new String(), new String() )
	 * "https://expectusafterlun.ch/1337x/{QUERY}/{PAGENUM}/{CATEGORY}/"
	 *
	 * For the HTTP Connection
	 */
	private static final String BASE_URL = "http://expectusafterlun.ch:5000/1337x";
//	private static final String BASE_URL = "http://202.61.205.246:5000/1337x";
//	private static final String BASE_URL = "http://localhost:5000/1337x";
	private final String API_KEY;
	private String fullUrl;
	private String query;

	/*
	 * Miscellaneous
	 */
	private final int MAX_RESULTS = 3;
	private final Config CONFIG;
	private final char PAGENUM = '1';

	/*
	 * Test this API with curl:
	 *
	 *	curl -H"API_KEY:<api key>" http://expectusafterlun.ch:5000/<QUERY>/<PAGENUM>/<CATEGORY>
	 *
	 * Example:
	 *
	 *	curl -H"API_KEY:oTloaqhI5N17SBBD1fHhQlgGaf1Ne8uy" http://152.89.107.76:5000/1337x/matrix/1/Movies
	 *
	 * Valid CATEGORIES (case sensitive):
	 *
	 * 	Movies, TV, Games, Music, Apps, Documentaries, Anime, Other, XXX, All
	 *
	 * Omit CATEGORY to search ALL.
	 */
	private String category;

	/* For the Gson/Json */
	private Gson gson;
	private String json;

	/**
	 * Constructor that expects a CONFIG and SEARCH made up of a category and query used to contact the API
	 *
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @param SEARCH Is the CATEGORY and search QUERY
	 * @param CONFIG Object representing the configuration for JRobo, used to retrieve API_KEY
	 */
	public Leet(final Config CONFIG, final String SEARCH) throws NullPointerException {

		if (CONFIG == null) {
			API_KEY = "";
			throw new NullPointerException("CONFIG is not set and cannot retrieve the Torrent API_KEY");
		} else {
			this.CONFIG = CONFIG;
			if (getApiKey() != null) {
				API_KEY = getApiKey();
			} else {
				API_KEY = ""; // Key could not be retrieved
				throw new NullPointerException("The API_KEY could not be retrieved from CONFIG");
			}
		}

		/* 
		 * For the HTTP Connection
		 */
		fullUrl = null;

		/* Miscellaneous */
		json = null;

		/* For the Gson/Json */
		gson = null;

		 /**
		  * Set to empty String for ALL.
		  * Search ALL if CATEGORY is not valid.
		  */
		final String[] CATEGORIES = {"Movies", "TV", "Games", "Music", "Apps", "Documentaries", "Anime", "Other", "XXX"};

		/* Divide SEARCH into CATEGORY and QUERY */
		try {
			/*
			 * Only set the category if SEARCH contains a category.
			 */
			if(Arrays.asList(CATEGORIES).contains(SEARCH.split("\\s+", 2)[0])) {
				category = SEARCH.split("\\s+", 2)[0];
				query = SEARCH.split("\\s+", 2)[1];
			} else if(SEARCH.startsWith("All")) {
				category = "";
				query = SEARCH.split("\\s+", 2)[1];
			} else {
				category = "";
				query = SEARCH;
			}

		} catch (ArrayIndexOutOfBoundsException ex) {
			// There is no CATEGORY. Search ALL.
			category = "";
			query = SEARCH;
			Logger.getLogger(Leet.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Gets JSON data for a search query to the Leetx Python Flask API
	 *
	 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
	 * @return JSON retrieved from the URL
	 */
	public String getJson() {

		try {
			/* 
			 * Use String.format(BASE_URL + "/{%s}/{%s}/{%s}", new String(), new String(), new String() );
 	 		 * "https://expectusafterlun.ch/1337x/{QUERY}/{PAGENUM}/{CATEGORY}/"
			 *
			 * Do not URL encode this because the server does that
			 *
			 * @todo Something is going wrong with torrent.py category is not set.
			 * .leet James Bond
			 * ^JRobo^
			 * { "data": "Unable to retrieve Torrent json data" }
			 */
			if (!category.equals("")) {
				fullUrl = String.format(BASE_URL + "/%s/%s/%s/seeders/desc", URLEncoder.encode(query, StandardCharsets.UTF_8.toString()), PAGENUM, category);
			} else {
				// Exclude CATEGORY to search ALL
                                    // If no category, the defaults in torrent.py are used for page num, sort by, and sort order
				fullUrl = String.format(BASE_URL + "/%s", URLEncoder.encode(query, StandardCharsets.UTF_8.toString()));
			}

			/* Debug */
			System.out.println(TermColors.colorInfo("fullUrl:   " + fullUrl));

//			HttpClient client = HttpClient.newHttpClient();
			HttpClient client = HttpClient.newBuilder()
                                                            .connectTimeout(Duration.ofSeconds(30))
                                                            .build();

			HttpRequest request = HttpRequest.newBuilder()
                                    .header("Content-Type", "application/json")
				.setHeader("API_KEY", API_KEY)
				.uri(URI.create(fullUrl))
                                    .timeout(Duration.ofSeconds(30))
				.build();

			HttpResponse<String> response;
			response = client.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));

		         System.out.println(TermColors.colorInfo("Response status code: " + response.statusCode()));
			json = response.body();
			
		} catch (IOException ex) {
			System.err.println("Did you include the API_KEY in the HTTP Header?");
			Logger.getLogger(Leet.class.getName()).log(Level.SEVERE, null, ex);

		} catch (InterruptedException ex) {
			Logger.getLogger(Leet.class.getName()).log(Level.SEVERE, null, ex);

		} finally {
			if (json == null) {
				json = "{ \"data\": \"Unable to retrieve Torrent json data\" }";
			}

		         System.out.println(TermColors.colorInfo("json:   " + json));
		}


		return json;
	}

	/**
	 * The formatted search results from Leetx JSON API with colors and formatting if HASCOLORS
	 * 
	 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
	 * @param HASCOLORS True for IRC colors, false otherwise
	 * @return Formatted JSON data optionally with colors
	 */
	public String getFormattedResult(final boolean HASCOLORS) {

		final LeetJsonItem[] RESULTS;

		try {

			gson = new GsonBuilder().setPrettyPrinting().create();
			RESULTS = gson.fromJson(this.getJson(), LeetJsonItem[].class);

		} catch (IllegalStateException | NullPointerException | JsonSyntaxException ex) {
			Logger.getLogger(Leet.class.getName()).log(Level.SEVERE, null, ex);
			json = "{ \"data\": \"Unable to retrieve Torrent json data\" }";
			return json;
		}

		String output = "";
		int count = 0;

		if (HASCOLORS) {
			for (LeetJsonItem result : RESULTS) {
				output += result.getColorString();
				count++;
				if(MAX_RESULTS <= count) {
					break;
				}
			}
		} else {
			for (LeetJsonItem result : RESULTS) {
				output += result.toString();
				count++;
				if(MAX_RESULTS <= count) {
					break;
				}
			}
		}

		return output;
	}

	/**
	 * Gets the API_Key for the flask torrent API
	 *
	 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
	 * @return API Key for Torrent API retrieved from Config.json
	 */
	private String getApiKey() {

		return CONFIG.getTorrentKey();
	}

	/**
	 * A main method for testing this class
	 *
	 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
	 * @param args Command line args
	 */
	public static void main(String[] args) {

		System.out.println(new Leet(new Config(), "Movies matrix reloaded").getFormattedResult(false));
	}

	/**
	 * Strings and ints representing JSON data
	 *
	 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
	 */
	public class LeetJsonItem {

		/**
		 * The date the torrent was submitted
		 */
		public String date;

		/**
		 * The full URL to the torrent
		 */
		public String href;

		/**
		 * The number of leeches for this torrent
		 */
		public int leeches;

		/**
		 * The name of this torrent
		 */
		public String name;

		/**
		 * The number of seeds for this torrent
		 */
		public int seeds;

		/**
		 * How much space this torrent takes up
		 */
		public String size;

		/**
		 * The user who uploaded this torrent
		 */
		public String user;

		/**
		 * A shortened version of the href url
		 */
		public String tinyurl;

		/**
		 * A colored for IRC String representation of LeetJsonItem
		 *
		 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
		 * @return Colored for IRC String representing LeetJsonItem
		 */
		public String getColorString() {
			final String MYSTRING
				= MircColors.BOLD + name + " "
				+ MircColors.GREEN + "<" + tinyurl + ">"
				+ MircColors.NORMAL + MircColors.BOLD + " (" + size
				+ MircColors.GREEN + " S:" + seeds
				+ MircColors.CYAN + " L:" + leeches
				+ MircColors.NORMAL + MircColors.BOLD + ")\n";

			return MYSTRING;
		}

		/**
		 * A String representation of LeetJsonItem
		 *
		 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
		 * @return A summary without colors and formatting of LeetJsonItem
		 */
		@Override
		public String toString() {
			final String MYSTRING = name + " <" + tinyurl + "> (" + size + " S:" + seeds + " L:" + leeches + ") \n";
			return MYSTRING;
		}
	}
}
