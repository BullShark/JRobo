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
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.err;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FileReader is used to supply the Config class with configuration read in from
 * Config.json. It also reads in Jokes JRobo uses and shuffles them in random
 * order.
 *
 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
 */
public class FileReader {

	/* Standard Java API Classes */
	private final static String CONFIGFILE = "Config.json";

	/* Path to resources */
	private final static String PATH = "src/main/resources/jrobo/expectusafterlun/ch/";

	/* User-defined Classes */
	private static Config config;

	/* Miscellaneous */
	private static boolean ranOnce = false;

	/**
	 * Set the filename for CONFIGFILE and initialize config to null
	 *
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	public FileReader() {
		config = null;
	}

	/**
	 * Opens a resource file inside the package and fills the passed
	 * ArrayList
	 *
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @param FILENAME The file name inside the package to be opened
	 * @param LISTARR The array list to store the file lines to
	 * @return A true on success and false on failure
	 */
	protected boolean fileToArrayList(final String FILENAME, final ArrayList<String> LISTARR) {
		out.println(TermColors.colorInfo("Reading File (" + PATH + FILENAME + ")"));
		out.println(TermColors.colorInfo("Absolute Path: " + new File(PATH + FILENAME).getAbsolutePath()));
		out.println(TermColors.colorInfo("System User Directory: " + PATH + System.getProperty("user.dir")));
		out.println(TermColors.colorInfo("Reading File (" + PATH + FILENAME + ")"));

		try {
			File file = new File(PATH + FILENAME);
			try (Scanner myReader = new Scanner(file)) {
				if (FILENAME.equals(PATH + CONFIGFILE)) {
					ranOnce = true;
				}

				while (myReader.hasNextLine()) {
					LISTARR.add(myReader.nextLine());
				}
			}

		} catch (IOException ex) {
			Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);

		}

		Collections.shuffle(LISTARR);

		return true;
	}

	/**
	 * Gets the data from the configuration file
	 *
	 * @return Returns a Config object, with the settings from Config.json
	 * @since 2013-02-18
	 */
	protected Config getConfig() {

		/* 
         		 * Should only be null if this method has not already been ran once 
		 * Or the constructor has never been called
		 */
		if (config != null || ranOnce) {
			//Thread.dumpStack();
			out.println(TermColors.colorInfo("Reusing Config because it's != null or " + PATH + CONFIGFILE + " has already been read once"));
			return config;
		}

		out.println(TermColors.colorInfo("Reading Configuration File (" + PATH.concat(CONFIGFILE) + ")"));
		out.println(TermColors.colorInfo("Absolute path: " + new File((PATH.concat(CONFIGFILE))).getAbsolutePath()));
		out.println(TermColors.colorInfo("System user directory: " + System.getProperty("user.dir").concat(PATH)));


		//Thread.dumpStack();

		String json = "";

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(CONFIGFILE)))) {

			while (reader.ready()) {
				json += reader.readLine();
			}

			System.out.println(TermColors.colorInfo(json));

			Gson gson = new Gson();
			config = gson.fromJson(json, Config.class);

			ranOnce = true;

		} catch (JsonSyntaxException | NullPointerException | IOException  ex) {
			Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(1);
		}

		// Verifiying important settings for connection
		if (config.getName()
			== null) {
			err.println(TermColors.colorInfo("Error: Unable to find bot's Nickname"));
			System.exit(1);
		}

		if (config.getPass()
			== null) {
			err.println(TermColors.colorInfo("Error: Unable to find bot's Password"));
			System.exit(1);
		}

		if (config.getMasters()
			== null) {
			err.println(TermColors.colorInfo("Error: Unable to find bot's Masters"));
			System.exit(1);
		}

		if (config.getCmdSymb()
			== '\u0000') {
			err.println(TermColors.colorInfo("Error: Unable to find bot's Command Symbol"));
			System.exit(1);
		}

		if (config.getNetwork()
			== null) {
			err.println(TermColors.colorInfo("Error: Unable to find bot's Network"));
			System.exit(1);
		}

		if (config.getChannel()
			== null) {
			err.println(TermColors.colorInfo("Error: Unable to find bot's Channel"));
			System.exit(1);
		}

		if (config.getOpenWeatherMapKey()
			== null) {
			err.println(TermColors.colorInfo("Error: Unable to find bot's OpenWeatherMap API Key"));
			//System.exit(1);
		}

		return config;
	} // EOF getConfig()
} // EOF class
