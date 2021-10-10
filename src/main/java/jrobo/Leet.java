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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class Leet {

	/* For the HTTP Connection */
	private final URL TORRENT_API_URL;
//	private final URLConnection CONN;
//	private final OutputStreamWriter WR;
//	private final BufferedReader RD;
	private final String FULL_URL;

	/*
	 * Miscelanous
	 *@TODO Fix bug, if TORRENT_API_URL is not available, the bot will throw an exception and crash
	 *
	 * category can be omitted for the search
	 *
	 * @TODO Use String.format("BASE_URL/{%s}/{%s}/{%s}", new String(), new String(), new String() ) ???
	 * "https://expectusafterlun.ch/1337x.to/search/{query}/{page}/{category}/"
	 */
	private static final String BASE_URL="http://expectusafterlun.ch:5000/1337x/search/";
	private final String JSON;
	private final int MAX_RESULTS = 5;	
	private final String QUERY;
	private final String API_KEY;
	private final Config CONFIG;

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
	 *	curl -H"API_KEY:<api key>" http://expectusafterlun.ch:5000/<query>/<page>/<category>
	 *
	 * example:
	 *
	 *	curl -H"API_KEY:oTloaqhI5N17SBBD1fHhQlgGaf1Ne8uy" http://152.89.107.76:5000/1337x/matrix/1/Movies
	 *
	 * Valid categories (case sensitive):
	 *
	 * 	Movies, TV, Games, Music, Apps, Documentaries, Anime, Other, XXX
	 *
	 * Omit category to search all.
	 */
	private final String CATEGORY;

	/* For the Gson/Json */
	private final Gson GSON;

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
	 * @param search Is the CATEGORY and search query
	 */
	public Leet(final Config CONFIG, final String SEARCH) throws NullPointerException {

		if(CONFIG == null) { 
			throw new NullPointerException("Config is not set and cannot retrieve The Torrent API Key");
		} else {
			this.CONFIG = CONFIG;
			API_KEY = getApiKey(); //@FIXME Does this method some times throw an exception?
		}

		/* For the HTTP Connection */
		URL = null;
//		CONN = null;
//		FULL_URL = "";

		/* Miscelanous */
//		JSON = "";

		/* For the Gson/Json */
//		GSON = null;

		 /* Divide search into category and query */
		 try {
			CATEGORY = SEARCH.split("\\s+", 2)[0];
			QUERY = SEARCH.split("\\s+", 2)[1];
		 } catch(ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
			// There is no category. Search all.
			CATEGORY = "";
			QUERY = SEARCH;
		}

		 /*
		  * Set to empty String for ALL.
		  * Search ALL if CATEGORY is not valid.
		  */
		 final String[] CATEGORIES = {"Movies", "TV", "Games", "Music", "Apps", "Documentaries", "Anime", "Other", "XXX"};

		 if(CATEGORY.equalsIgnoreCase("All") ||
			!(Arrays.asList(CATEGORIES)).contains(CATEGORY) ) {
			
			 CATEGORY = "";
		 }
	}

	/*
	 */
	/**
	 * curl -H"API_KEY:oTloaqhI5N17SBBD1fHhQlgGaf1Ne8uy" http://expectusafterlun.ch:5000/1337x/matrix/1/Movies
	 * @author Christopher Lemire <goodbye300@aim.com>
	 * @return JSON retrieved from the URL
	 */
	public String getJson() {

		try {
			/* Create a URL obj from strings */
			if(!CATEGORY.equals("")) {

	 			/* Use String.format(BASE_URL + "/{%s}/{%s}/{%s}", new String(), new String(), new String() );
	 	 		 * "https://expectusafterlun.ch/1337x.to/search/{query}/{page}/{category}/"
				 */
				FULL_URL = String.format(BASE_URL + "/{%s}/{%s}/{%s}",QUERY ,"1" ,CATEGORY);
			} else {
				// Exclude CATEGORY to search ALL
				FULL_URL = String.format(BASE_URL + "/{%s}/{%s}/",QUERY ,"1");
			}

			/* 
			 * String.replaceAll(" ", "%20");
			 * toURI() and URI.toURL().
			 */
			TORRENT_API_URL = new URI(FULL_URL).toURL();

			/* Debug */
			System.out.println("[***]\t" + FULL_URL);

			final HttpRequest.Builder REQUESTBUILDER = HttpRequest.newBuilder()
				.uri(URI.create(FULL_URL));

			REQUESTBUILDER.header("API_KEY", getApiKey());

			final HttpClient CLIENT = HttpClients.custom().setDefaultHeaders(REQUESTBUILDER.getFirstHeader("API_KEY")).build();

			final HttpRequest REQUEST = requestBuilder.build();

			final HttpResponse<String> RESPONSE =
				CLIENT.send(REQUEST, BodyHandlers.ofString());

//			CONN = TORRENT_API_URL.openConnection();

//			CONN.setRequestMethod("GET");

			// Get the response
//			RD = new BufferedReader(new InputStreamReader(CONN.getInputStream()));
//			String line = "";
//			while ((line = RD.readLine()) != null) {
//				json = json.concat(line);
//			}
//			RD.close();

			JSON = RESPONSE.body();

		} catch (MalformedURLException ex) {
			if(JSON.equals("") || JSON == null) {
				JSON = "{ \"data\": \"Unable to retrieve Torrent json data\" }";
			}
			ex.printStackTrace();
		} catch (ConnectException ex) {
			if(JSON.equals("") || JSON == null) {
				JSON = "{ \"data\": \"Unable to retrieve Torrent json data\" }";
			}
			ex.printStackTrace();
		} catch (IOException ex) {
			if(JSON.equals("") || JSON == null) {
				JSON = "{ \"data\": \"Unable to retrieve Torrent json data\" }";
			}
			System.err.println("Did you include the API_KEY in the HTTP Header?");
			ex.printStackTrace();
		}

		return JSON;
	}

	/**
	 *
	 * @author Christopher Lemire <goodbye300@aim.com>
	 * @return Formatted JSON data optionally with colors
	 */
	public String getFormattedResult(final boolean HAS_COLORS) {

		final LeetJsonItem[] RESULTS;

		try {

			GSON = new GsonBuilder().setPrettyPrinting().create();
			RESULTS = GSON.fromJson(this.getJson(), LeetJsonItem[].class);

		} catch (IllegalStateException | NullPointerException ex) {
			ex.printStackTrace();
			return "";
		}

		String output = "";

		if (HAS_COLORS) {
			for (LeetJsonItem result : results) {
				output += result.getColorString();
			}
		} else {
			for (LeetJsonItem result : results) {
				output += result.toString();
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
	 * @author Christopher Lemire <goodbye300@aim.com>
	 */
	public static void main(String[] args) {

		System.out.println(new Leet(new Config(), "Movies matrix reloaded").getFormattedResult(false));
	}

	/**
	 * Strings and ints representing JSON data
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
		public String tinyurl = "https://not.implemented.yet";

		/**
		 * A colored for IRC String representation of LeetJsonItem
		 * @author Christopher Lemire <goodbye300@aim.com>
		 */
		public String getColorString() {
 			final String MYSTRING =
				MircColors.BOLD + name + " " +
				MircColors.GREEN + "<" + tinyurl + ">" +
				MircColors.NORMAL + MircColors.BOLD + " (" + size + 
				MircColors.GREEN + " S:" + seeds +
				MircColors.CYAN + " L:" + leeches + 
				MircColors.NORMAL + MircColors.BOLD + ")\n";
             
			return MYSTRING;
		}

		/**
		 * A String representation of LeetJsonItem
		 * @author Christopher Lemire <goodbye300@aim.com>
		 */
		public String toString() {
			final String MYSTRING= name + " <" + tinyurl + "> (" + size + " S:" + seeds + " L:" + leeches + ") \n";
			return MYSTRING;
		}
	}
}
