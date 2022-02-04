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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DownForEveryone uses an internet site to let you know if a ddos is working to take down a web server.
 * @since 2021-10-11
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class DownForEveryone {

	/* For the HTTP Connection */
	private URL url;
	private URLConnection conn;
	private BufferedReader rd;
	private InputStreamReader isr;

	/* Miscellaneous */
	private final String BASE_URL = "https://isitup.org/";
	private boolean isup;

	/**
	 * Initializes the global variables used for by DownForEveryone,
	 * url, conn, rd, isr, and isup
	 * 
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	public DownForEveryone() {
		/* For the HTTP Connection */
		url = null;
		conn = null;
		rd = null;
		isr = null;

		/* Miscellaneous */
		isup = false;
	}

	/**
	 * Checks if the url is up
	 *
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @param TESTURL url to check
	 * @param COLORS Whether to use IRC COLORS for result returned
	 * @return Message for if url is up or down
	 */
	protected String isUp(final String TESTURL, final boolean COLORS) {
		try {
			/* Create a url obj from strings */
			url = new URL((BASE_URL.concat(TESTURL)).replace(" ", "%20"));

			/* Debug */
			System.out.println(TermColors.info("URL: " + url));

			conn = url.openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/4.0"); // Resolves the 403 error

			// Get the response
			isr = new InputStreamReader(conn.getInputStream());
			rd = new BufferedReader(isr);

			String line;
			while ((line = rd.readLine()) != null) {
				if (line.contains(" is up.")) {
					isup = true;
				} else if (line.contains("seems to be down")) {
					isup = false;
				}
			}

			rd.close();

		} catch (MalformedURLException ex) {
			Logger.getLogger(DownForEveryone.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(DownForEveryone.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				if(rd != null) { rd.close(); }
				if(isr != null) { isr.close(); }
					
			} catch (IOException ex) {
				Logger.getLogger(DownForEveryone.class.getName()).log(Level.SEVERE, null, ex);
			}
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
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: java DownForEveryone <url>");
			System.exit(-1);
		}
		System.out.println(new DownForEveryone().isUp(args[0], false));
	}
}
