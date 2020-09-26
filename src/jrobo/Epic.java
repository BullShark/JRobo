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
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author bullshark
 */
public class Epic {

	/* For the HTTP Connection */
	private URL url;
	private URLConnection conn;
	private OutputStreamWriter wr;
	private BufferedReader rd;

	/* Miscelanous
	 *
	 * TODO Fix bug, if the url is not available, the bot will throw an exception and crash
	 * Example: https://store-site-backend-static.ak.epicgames.com/freeGamesPromotions?locale=en-US&country=TR&allowCountries=TR
	 */
	private static final String QUERY_URL = "https://store-site-backend-static.ak.epicgames.com";
	private String json;
	private String locale = "en-US";
	private String countrycode = "TR";

	/* For the Gson/Json */
	private Gson gson;

	public Epic() {

		/* For the HTTP Connection */
		url = null;
		conn = null;

		/* Miscelanous */
		json = "";

		/* For the Gson/Json */
		gson = new Gson();
	}

	/**
	 *
	 * @return
	 */
	public String getJson() {
		try {
			/* Create a URL obj from strings */

			url = new URL(
				(QUERY_URL
					+ "/freeGamesPromotions"
					+ "?locale=" + locale
					+ "&country=" + countrycode
					+ "&allowCountries=" + countrycode
				).replaceAll(" ", "%20")
			);

			//System.out.println(url);

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
		} catch (ConnectException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		System.out.println(json);

		return json;
	}

	public String[] getFormattedEpic(final boolean hasColors, final int limit) {

		/*
		 * TODO Add try/catch to handle
		 * TODO The exception that no JSON is received
		 * TODO Look at PirateBay.java as an example
		 */
		gson = new GsonBuilder().setPrettyPrinting().create();
		EpicJson ej = gson.fromJson(this.getJson(), EpicJson.class);

		String[] outArr = new String[limit];
		int count = 0, index = limit;

		/* Fixes NullPointerException Bug that occurs if the URL DNE */
		try {
			if (hasColors) {
				for (EpicJsonItem eji : ej.list) {
					if (index > 0) {
						outArr[count++] = eji.getColorString();
						index--;
					} else {
						break;
					}
				}
			} else {
				for (EpicJsonItem eji : ej.list) {
					if (index > 0) {
						outArr[count++] = eji.toString();
						index--;
					} else {
						break;
					}
				}
			}
		} catch (NullPointerException ex) {
			ex.printStackTrace();

			// Last element
        		for (String element : outArr) {
				System.err.println(element);
			}

			outArr = { "Could not be retrieved!", };
			return outArr;
		}

		return outArr;
	}

	/*
         * A main method for testing this class
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			System.err.println("Usage: java Epic");
			System.exit(-1);
		}
		System.out.println(new Epic().getFormattedEpic(false, -1));
	} // EOF main
} // EOF class
