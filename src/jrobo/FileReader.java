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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.err;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author chris
 */
public class FileReader {

	/* Standard Java API Classes */
	private static String config_file = null;

	/* User-defined Classes */
	private static Config config;

	public FileReader() {
		config_file = "Config.json";
	}

	/**
	 * Opens a resource file inside the package, and fills the passed
	 * ArrayList
	 *
	 * @param fileName The file name inside the package to be opened
	 * @param listArr The array list to store the file lines to
	 * @return a True on success, and false on failure
	 */
	public boolean fileToArrayList(String fileName, ArrayList<String> listArr) {

		Thread.dumpStack();
		out.println("[+++]\tReading File (" + fileName + ")");

		BufferedReader br = new BufferedReader(new InputStreamReader(FileReader.class.getResourceAsStream(fileName)));

		String line = "";

		try {
			while ((line = br.readLine()) != null) {
				listArr.add(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		Collections.shuffle(listArr);

		return true;
	}

	/**
	 * gets the data from the configuration file
	 *
	 * @return Returns a Config object, with the settings from Config.json
	 * @since 2013-02-18
	 */
	public static Config getConfig() {

		Thread.dumpStack();
		out.println("[+++]\tReading Configuration File (Config.json)");

	        InputStream fileStream = FileReader.class.getResourceAsStream(config_file);

		if (fileStream == null) {
			err.println("[+++]\tError: " + config_file + " was not found");
			System.exit(1);
		}

		InputStreamReader fileStreamReader = new InputStreamReader(fileStream);
		BufferedReader br = new BufferedReader(fileStreamReader);
		String line, json;

		json = "";
		try {
			while ((line = br.readLine()) != null) {
				json += line;
			}

			br.close();

		} catch (IOException ex) {
			err.println("[+++]\tError:" + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		Gson gson = new Gson();
		config = gson.fromJson(json, Config.class);

		// Verifiying important settings for connection
		if (config.getName() == null) {
			err.println("[+++]\tError: Unable to find bot's nickname");
			System.exit(1);
		}

		if (config.getPass() == null) {
			err.println("[+++]\tError: Unable to find bot's password");
			System.exit(1);
		}

		if (config.getMasters() == null) {
			err.println("[+++]\tError: Unable to find bot's Masters");
			System.exit(1);
		}

		if (config.getCmdSymb() == '\u0000') {
			err.println("[+++]\tError: Unable to find bot's Command Symbol");
			System.exit(1);
		}

		if (config.getNetwork() == null) {
			err.println("[+++]\tError: Unable to find bot's Network");
			System.exit(1);
		}

		if (config.getChannel() == null) {
			err.println("[+++]\tError: Unable to find bot's Channel");
			System.exit(1);
		}

		if (config.getOpenWeatherMapKey() == null) {
			err.println("[+++]\tError: Unable to find bot's OpenWeatherMap API Key");
			//System.exit(1);
		}

		return config;
	}
} // EOF class
