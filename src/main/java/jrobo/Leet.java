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

public class Leet {

    /* For the HTTP Connection */
    private URL url;
    private URLConnection conn;
    private OutputStreamWriter wr;
    private BufferedReader rd;
    private String fullUrl;

    /*
     * Miscelanous
     *@TODO Fix bug, if the url is not available, the bot will throw an exception and crash
     *
     * category can be omitted for the search
     * @TODO Use String.format("url %s %s %s") ???
     *
     * "https://expectusafterlun.ch/1337x.to/search/{query}/{page}/{category}/"
     */
    private static final String BASE_URL="http://expectusafterlun.ch:5000/1337x/";
    private String json;
    private final int MAX_RESULTS = 5;	
    private String query;
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
	private String category;

	/* For the Gson/Json */
	private Gson gson;

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
	 * @param search Is the category and search query
	 */
	public Leet(String search) throws NullPointerException {

		if(CONFIG == null) { 
			throw new NullPointerException("Config is not set and cannot retrieve The Torrent API Key");
		} else {
			this.CONFIG = CONFIG;
			API_KEY = getApiKey(); //@FIXME Does this method some times throw an exception?
		}

		/* For the HTTP Connection */
		url = null;
		conn = null;
		fullUrl = "";
		/* Miscelanous */
		json = "";
		/* For the Gson/Json */
		gson = new Gson();
		 /* Divide search into category and query */
		 try {
			category = search.split("\\s+", 2)[0];
			query = search.split("\\s+", 2)[1];
		 } catch(ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
			// There is no category. Search all.
			category = "";
			query = search;
		}

		 /*
		  * Set to empty string for all.
		  * Search all if category is not valid.
		  */
		 String[] categories = {"Movies", "TV", "Games", "Music", "Apps", "Documentaries", "Anime", "Other", "XXX"};

		 if(category.equalsIgnoreCase("All") ||
			!(Arrays.asList(categories)).contains(category) ) {
			
			 category = "";
		 }
	}

	/*
	 * curl -H"API_KEY:oTloaqhI5N17SBBD1fHhQlgGaf1Ne8uy" http://expectusafterlun.ch:5000/1337x/matrix/1/Movies
	 */
	public String getJson() {

		try {
			/* Create a URL obj from strings */
			if(!category.equals("")) {
				fullUrl
					= (BASE_URL
						+ query
						+ "/1/"
						+ category).replaceAll(" ", "%20");
			} else {
				fullUrl
					= (BASE_URL
						+ query
						+ "/1").replaceAll(" ", "%20");
			}

			url = new URL(fullUrl);

			/* Debug */
			System.out.println(fullUrl);

			conn = url.openConnection();
			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				json = json.concat(line);
			}
			rd.close();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (ConnectException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return json;
	}

	public String getFormattedResult(boolean hasColors) {

		LeetJsonItem[] results;

		try {

			gson = new GsonBuilder().setPrettyPrinting().create();
			results = gson.fromJson(this.getJson(), LeetBayJsonItem[].class);

		} catch (IllegalStateException | NullPointerException ex) {
			ex.printStackTrace();
			return "";
		}

		String output = "";

		if (hasColors) {
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
	 * @return API Key for Torrent API retrieved from Config.json
	 */
	private String getApiKey() {

		return CONFIG.getTorrentKey();
	}


	/*
 	 * A main method for testing this class
	 */
	public static void main(String[] args) {

		System.out.println(new Leet("Movies matrix reloaded").getFormattedResult(false));
	}

	public class LeetJsonItem {

 		public String date;
		public String href;
		public int leeches;
		public String name;
		public int seeds;
		public String size;
		public String user;
		public String tinyurl = "https://not.implemented.yet";

		public String getColorString() {
 			String mystring=
				MircColors.BOLD + name + " " +
				MircColors.GREEN + "<" + tinyurl + ">" +
				MircColors.NORMAL + MircColors.BOLD + " (" + size + 
				MircColors.GREEN + " S:" + seeds +
				MircColors.CYAN + " L:" + leeches + 
				MircColors.NORMAL + MircColors.BOLD + ")\n";
             
			return mystring;
		}

		public String toString() {
			String mystring= name + " <" + tinyurl + "> (" + size + " S:" + seeds + " L:" + leeches + ") \n";
			return mystring;
		}
	}
}
