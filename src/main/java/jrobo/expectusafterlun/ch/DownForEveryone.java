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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Lemire <christopher.lemire@gmail.com>
 */
public class DownForEveryone {

	/* For the HTTP Connection */
	private URL url;
	private URLConnection conn;
	private BufferedReader rd;

	/* Miscellaneous */
	private final String QUERY_URL = "https://www.downforeveryoneorjustme.com/";
	private boolean isup;

	public DownForEveryone() {
		/* For the HTTP Connection */
		url = null;
		conn = null;
		rd = null;

		/* Miscellaneous */
		isup = false;
	}

	/**
	 * Checks if the URL is up
	 *
	 * @param TESTURL URL to check
	 * @param COLORS Whether to use IRC COLORS for result returned
	 * @return Message for if URL is up or down
	 */
	protected String isUp(final String TESTURL, final boolean COLORS) {
		try {
			/* Create a URL obj from strings */
			url = new URL((QUERY_URL.concat(TESTURL)).replace(" ", "%20"));

			/* Debug */
			System.out.println("URL: " + url);

			conn = url.openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/4.0"); // Resolves the 403 error

			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line;
			while ((line = rd.readLine()) != null) {
//        It's just you.  <a href="http://google.com" class="domain">http://google.com</a></span> is up.
				if (line.contains("It's just you.  ") && line.contains(" is up.")) {
					isup = true;
//      It's not just you!  <a href="http://ggggggasdfgle.com" class="domain">http://ggggggasdfgle.com</a> looks down from here.
				} else if (line.contains("It's not just you!  ") && line.contains(" looks down from here.")) {
					isup = false;
				}
			}

			rd.close();

		} catch (MalformedURLException ex) {
			Logger.getLogger(DownForEveryone.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(DownForEveryone.class.getName()).log(Level.SEVERE, null, ex);
		}

		String result;

		if (isup) {
			if (COLORS) {
				result
					= MircColors.BOLD + MircColors.GREEN + TESTURL
					+ MircColors.CYAN + " IS UP!";
			} else {
				result = TESTURL + " IS UP!";
			}
		} else {
			if (COLORS) {
				result
					= MircColors.BOLD + MircColors.GREEN + TESTURL
					+ MircColors.CYAN + " IS DOWN!";
			} else {
				result = TESTURL + " IS DOWN!";
			}
		}
		return result;
	}

	/**
	 * A main method for testing this class
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: java DownForEveryone <url>");
			System.exit(-1);
		}
		System.out.println(new DownForEveryone().isUp(args[0], false));
	}
}
