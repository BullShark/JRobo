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
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chris Lemire <goodbye300@aim.com>
 */
public class Weather {

	/*
	 * For the HTTP Connection
	 */
	private URL url;
	private URLConnection conn;
	private BufferedReader rd;

	/*
	 * OpenWeatherMap
	 * Guide for OpenWeatherMap API: https://web.stanford.edu/group/csp/cs22/using-an-api.pdf
	 * Example: https://api.openweathermap.org/data/2.5/find?q=Palo+Alto&units=imperial&type=accurate&mode=xml&APPID=api-key
	 * FIXME What other Type's are there?
	 */
	private final String QUERY_URL = "https://api.openweathermap.org";
	private String json;
	private final Config config;

	/*
	 * API URL Parameters
	 */
	private String cityName;
	private StateCode stateCode;
	private CountryCode countryCode;
	private final String apikey;
	private final Unit unit;
	private final Type type;
	private final Mode mode;

	public enum Unit {
		IMPERIAL, METRIC, STANDARD
	}

	public enum Type {
		ACCURATE
	}

	public enum Mode {
		JSON, XML, HTML
	}

	public enum StateCode {
		TX, FL, CA, NY
	}

	public enum CountryCode {
		US, TK, AU, UK
	}

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
	 */
	public Weather() {

		/*
		 * For the HTTP Connection
		 */
		url = null;
		conn = null;

		/*
		 * Miscellaneous
		 * TODO Exception handling needed: NullPointerException
		 */
		config = FileReader.getConfig();
		json = "";

		/*
		 * API URL Parameters
		 */
		cityName = "San Antonio";
		stateCode = StateCode.TX;
		countryCode = CountryCode.US;
		unit = Unit.IMPERIAL;
		type = Type.ACCURATE;
		mode = Mode.JSON;
		apikey = getApiKey();

	}

	/**
	 * Tries to determine the city name, state code and country code from a location string and returns the json for it if valid
	 * 
	 * @TODO Should I use greedy, reluctant or possessive quantifiers?
	 * @throws InvalidLocationException
	 * @param location
	 * @return BufferedReader
	 */
	public String getJson(String location) throws InvalidLocationException {

		cityName = "";
		stateCode = StateCode.TX;
		countryCode = CountryCode.US;

		String locationArr[];

		locationArr = location.split("\\s*,\\s*"); //@TODO More than 3 should give the help message for the weather command

		// There should be 0-2 commas and if locationArr is 0 then there's another problem
		if(locationArr.length < 1 || locationArr.length > 3) {
			throw new InvalidLocationException("Too many commas");
		}

		//return getJson(cityName, stateCode.name().toLowerCase(), countryCode.name().toLowerCase());

		return getJson(cityName, "", "");
	}

	/**
	 *
	 * @param cityName
	 * @param stateCode
	 * @param countryCode
	 * @return String
	 */
	public String getJson(String cityName, String stateCode, String countryCode) {
		try {
			if (cityName == null || stateCode == null || countryCode == null) {
				throw new NullPointerException();
			}

			String location;

			if (!cityName.equals("") && stateCode.equals("") && countryCode.equals("")) {
				location = cityName;
			} else if (!cityName.equals("") && !stateCode.equals("") && countryCode.equals("")) {
				location = cityName + "," + stateCode;
			} else if (!cityName.equals("") && !stateCode.equals("") && !countryCode.equals("")) {
				location = cityName + "," + stateCode + "," + countryCode;
			} else {
				location = this.cityName + "," + this.stateCode + "," + this.countryCode;
			}

			url = new URL(
				(QUERY_URL
					+ "/data/2.5/find"
					+ "?q=" + location
					+ "&units=" + "imperial"
					+ "&type=" + "accurate"
					+ "&mode=" + "json"
					+ "&appid=" + apikey
				).replaceAll(" ", "%20")
			);

			System.out.println("[+++] " + url);

			conn = url.openConnection();

			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line;
			while ((line = rd.readLine()) != null) {
				json += line;
			}

			rd.close();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return json;
	}

	/**
	 * Retrieve the data as a summary with irc color codes and formatting
	 * @param Json
	 * @return Formatted Json
	 */
	public String getFormattedWeatherSummary(String json) {

		WeatherJson weatherJson;

		try {
			java.lang.reflect.Type WeatherJsonT = new TypeToken<ArrayList<WeatherJson>>(){}.getType();  
			System.out.println("[+++]\tWeatherJson Type: " + WeatherJsonT);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			weatherJson = gson.fromJson(json, WeatherJson.class);

		} catch (JsonSyntaxException ex) {
			ex.printStackTrace();
			return "Unable to retrieve the weather";
		}

//		return weatherJson.toString();

		return weatherJson.getColorString();

	}

	/**
	 * 
	 * @return API Key for OpenWeatherMap retrieved from Config.json
	 */
	private String getApiKey() {
		return config.getOpenWeatherMapKey();
	}

	/*
	 * A main method for testing this class
	 */
	public static void main(String[] args) {

		Weather w = new Weather();
		try {
			System.out.println(w.getFormattedWeatherSummary(w.getJson("San Antonio, TX, US")));
		} catch (InvalidLocationException ex) {
			System.out.println("Invalid Location, Try Again");
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static class InvalidLocationException extends Exception {

		public InvalidLocationException(String not_supported_yet) {
		}
	}

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
	 */
	private class WeatherJson {

		public String message;
		public String cod;
		public int count;
		public List<WeatherListJsonItem> list;

		public String getColorString() {

			return String.format( "Result 1 / %d: %s", count, list.get(0).getColorString() );
		}

		/**
		 *
		 * @override
		 */
		@Override
		public String toString() {

			return "message: " + message + "\n"
				+ "cod: " + cod + "\n"
				+ "count: " + count + "\n"
				+ "list: " + list + "\n";
		}

		/**
		 *
		 * @author Chris Lemire <goodbye300@aim.com>
		 * @TODO Make rain a String
		 */
		protected class WeatherListJsonItem {

			public int id;
			public String name;
			public WeatherCoordJsonItem coord;
			public WeatherMainJsonItem main;
			public int dt;
			public WeatherWindJsonItem wind;
			public WeatherSysJsonItem sys;
			public Object rain;
			public String snow;
			public WeatherCloudsJsonItem clouds;
			public List<WeatherWeatherJsonItem> weather;

			public String getColorString() {

				String result = 
					"Weather for " +
					MircColors.BOLD + MircColors.GREEN + name + ", " + sys +  MircColors.NORMAL +
					" at " + coord.getColorString() + " " +
					main.getColorString() + 
					wind + 
					clouds + 
					weather;

				if(rain != null) {
					result += rain;
				}
				
				if(rain != null) {
					result += snow;
				}

				return result;
			}

			public String toString() {

				return "id: " + id + "\n" +
					"name: " + name + "\n" +
					"coord: " + coord + "\n" +
					"main: " + main + "\n" +
					"dt: " + dt + "\n" +
					"sys: " + sys + "\n" +
					"wind: " + wind + "\n" +
					"rain: " + rain + "\n" +
					"snow: " + snow + "\n" +
					"clouds: " + clouds + "\n" +
					"weather " + weather + "\n";

			}

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			public class WeatherCoordJsonItem {
	
				private float lat;
				private float lon;

				public String getColorString() {

					DecimalFormat fmt = new DecimalFormat("0.##");

					String result = 
						MircColors.BOLD + 
						MircColors.CYAN + "lat: " + 
						MircColors.GREEN + fmt.format(lat) + " " +
						MircColors.CYAN + "lon: " + 
						MircColors.GREEN + fmt.format(lon) + 
						MircColors.NORMAL;

					return result;
				}

				public String toString() {
					return String.format("lat: %s lon: %s", lat, lon);
				}
			} // EOF WeatherCoordJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			public class WeatherMainJsonItem {
	
				private float temp;
				private float feels_like;
				private float temp_min;
				private float temp_max;
				private int pressure;
				private String humidity;

				public String getColorString() {
/*
					String str = 
						String.format("Current temperature is %sF, Feels like %sF, ", temp, feels_like, temp_min, temp_max, pressure, humidity);
*/
					String result =
						MircColors.BOLD +
						"Current temperature is " + MircColors.GREEN + temp + "F, " + MircColors.NORMAL + MircColors.BOLD +
						"Feels like " + MircColors.GREEN + feels_like + "F" + MircColors.NORMAL + MircColors.BOLD + ", " +
						"Min / Max is " + MircColors.GREEN + temp_min + " / " + temp_max + MircColors.NORMAL + MircColors.BOLD + ", " +
						"Pressure is " + pressure + ", " +
						"Humidity is " + humidity;


					return result;

				}

				public String toString() {

					return "temp: " + temp + 
						", feels like: " + feels_like + 
						", temp_min: " + temp_min + 
						", temp_max: " + temp_max + 
						", pressure: " + pressure + 
						", humidity: " + humidity;

				}
			} // EOF WeatherSysJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			public class WeatherWindJsonItem {
	
				private float speed;
				private int deg;

				public String toString() {
					return "speed: " + speed + ", deg: " + deg;
				}
			} // EOF WeatherWindJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			public class WeatherSysJsonItem {
	
				private String country;

				public String toString() {

					return country;

					/*
					return "country: " + country;
					*/
				}
			} // EOF WeatherSysJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			public class WeatherCloudsJsonItem {
	
				public int all;

				public String toString() {
					return "all: " + all;
				}
			} // EOF WeatherCloudsJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			public class WeatherWeatherJsonItem {

				private int id;
				private String main;
				private String description;
				private String icon;

				public String toString() {
					return "id: " + id + ", main: " + main + ", description: " + description + ", icon: " + icon;
				}
			} // EOF WeatherWeatherJsonItem

		} // EOF WeatherListJsonItem

	} // EOF WeatherJson

} // EOF Weather
