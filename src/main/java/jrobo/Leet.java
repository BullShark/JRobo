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
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Leet {

	/* Miscellaneous
	 *@TODO Fix bug:
	 *	if url is not available,
	 * 	the bot will throw an Exception and crash
	 *
	 * CATEGORY can be omitted for the SEARCH
	 *
	 * Use String.format("BASE_URL/{%s}/{%s}/{%s}", new String(), new String(), new String() )
	 * "https://expectusafterlun.ch/1337x.to/search/{QUERY}/{PAGENUM}/{CATEGORY}/"
	 */
	private static final String BASE_URL = "http://expectusafterlun.ch:5000/1337x";
	private URL url;
	private String fullUrl;
	private String json;
	private final int MAX_RESULTS = 3;
	private String query;
	private final String API_KEY;
	private final Config CONFIG;
	private final String PAGENUM = "1";

	/*
	We can also filter by categories:
		<option value="/category-search/test/Movies/1/">
		<option value="/category-search/test/TV/1/">
		<option value="/category-search/test/Games/1/">
		<option value="/category-search/test/Music/1/">
		<option value="/category-search/test/Apps/1/">
		<option value="/category-search/test/Documentaries/1/">
		<option value="/category-search/test/Anime/1/">
		<option value="/category-search/test/Other/1/">
		<option value="/category-search/test/XXX/1/">

	@app.route('/1337x/<query>/<page>/<category>', methods=['GET'])
	def api_1337x(query, page=1, category=None):
	if request.headers.get("API_KEY") != app.config["API_KEY"]:
	 */

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
	 * 	Movies, TV, Games, Music, Apps, Documentaries, Anime, Other, XXX
	 *
	 * Omit CATEGORY to search ALL.
	 */
	private String category;

	/* For the Gson/Json */
	private Gson gson;

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
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
		 * Do not set some of these because they are constants.
		 */
		url = null;
		fullUrl = null;

		/* Miscelanous */
		json = null;

		/* For the Gson/Json */
		gson = null;

		/* Divide SEARCH into CATEGORY and QUERY */
		try {
			category = SEARCH.split("\\s+", 2)[0];
			query = SEARCH.split("\\s+", 2)[1];
		} catch (ArrayIndexOutOfBoundsException ex) {
			// There is no CATEGORY. Search ALL.
			category = "";
			query = SEARCH;
			Logger.getLogger(Leet.class.getName()).log(Level.SEVERE, null, ex);
		}

		/**
		  * Set to empty String for ALL.
		  * Search ALL if CATEGORY is not valid.
		  */
		final String[] CATEGORIES = {"Movies", "TV", "Games", "Music", "Apps", "Documentaries", "Anime", "Other", "XXX"};

		if (category.equalsIgnoreCase("All")
			|| !(Arrays.asList(CATEGORIES)).contains(category)) {

			category = "";
		}
	}

	/**
	 * curl -H"API_KEY:oTloaqhI5N17SBBD1fHhQlgGaf1Ne8uy"
	 * http://expectusafterlun.ch:5000/1337x/matrix/1/Movies
	 *
	 * @author Christopher Lemire <goodbye300@aim.com>
	 * @return json retrieved from the url
	 */
	public String getJson() {

		try {
			/* Use String.format(BASE_URL + "/{%s}/{%s}/{%s}", new String(), new String(), new String() );
 	 		 * "https://expectusafterlun.ch/1337x.to/{QUERY}/{PAGENUM}/{CATEGORY}/"
			 */
			if (!category.equals("")) {

				fullUrl = String.format(BASE_URL + "/%s/%s/%s", URLEncoder.encode(query, StandardCharsets.UTF_8.toString()), PAGENUM, category);
			} else {
				// Exclude CATEGORY to search ALL
				fullUrl = String.format(BASE_URL + "/%s/%s/", URLEncoder.encode(query, StandardCharsets.UTF_8.toString()), PAGENUM);
			}

			/* Debug */
			if(url != null) {
				System.out.println("[***]\turl:" + url.toString());
			} else {
				System.out.println("[***]\turl:" + null);
			}
			System.out.println("[***]\tfullUrl:" + fullUrl);

			/*
			 * Create a URL obj from String
			 *
			 * String.replaceAll(" ", "%20");
			 * toURI() and URI.toURL().
			 *
			 * Example: URL url = new URL("http://example.com/hello%20world");
			 */

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.headers("Content-Type", "application/json")
				.setHeader("API_KEY", API_KEY)
				.uri(URI.create(fullUrl))
				.build();

			HttpResponse<String> response;
			response = client.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
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
		}

		return json;
	}

	/**
	 * Use toString() on All json Items to make some formatted colored or no colors output
	 * 
	 * @author Christopher Lemire <goodbye300@aim.com>
	 * @param HAS_COLORS True for IRC colors, false otherwise
	 * @return Formatted json data optionally with colors
	 */
	public String getFormattedResult(final boolean HAS_COLORS) {

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

		if (HAS_COLORS) {
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
	 *
	 * @author Christopher Lemire <goodbye300@aim.com>
	 * @return API Key for Torrent API retrieved from Config.json
	 */
	private String getApiKey() {

		return CONFIG.getTorrentKey();
	}

	/**
	 * A main method for testing this class
	 *
	 * @author Christopher Lemire <goodbye300@aim.com>
	 * @param args Command line args
	 */
	public static void main(String[] args) {

		System.out.println(new Leet(new Config(), "Movies matrix reloaded").getFormattedResult(false));
	}

	/**
	 * Strings and ints representing json data
	 *
	 * @author Christopher Lemire <goodbye300@aim.com>
	 */
	public class LeetJsonItem {

		public String date;
		public String href;
		public int leeches;
		public String name;
		public int seeds;
		public String size;
		public String user;
//		public String tinyurl = "https://not.implemented.yet";

		/**
		 * A colored for IRC String representation of LeetJsonItem
		 *
		 * @author Christopher Lemire <goodbye300@aim.com>
		 * @return Colored for IRC String representing LeetJsonItem
		 */
		public String getColorString() {
			final String MYSTRING
				= MircColors.BOLD + name + " "
//				+ MircColors.GREEN + "<" + tinyurl + ">"
				+ MircColors.GREEN + "<" + href + ">"
				+ MircColors.NORMAL + MircColors.BOLD + " (" + size
				+ MircColors.GREEN + " S:" + seeds
				+ MircColors.CYAN + " L:" + leeches
				+ MircColors.NORMAL + MircColors.BOLD + ")\n";

			return MYSTRING;
		}

		/**
		 * A String representation of LeetJsonItem
		 *
		 * @author Christopher Lemire <goodbye300@aim.com>
		 * @return A summary without colors of UrbanJsonItem
		 */
		@Override
		public String toString() {
//			final String MYSTRING = name + " <" + tinyurl + "> (" + size + " S:" + seeds + " L:" + leeches + ") \n";
			final String MYSTRING = name + " <" + href + "> (" + size + " S:" + seeds + " L:" + leeches + ") \n";
			return MYSTRING;
		}
	}
}
