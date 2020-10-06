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
import java.lang.reflect.Type;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chris Lemire <goodbye300@aim.com>
 * @TODO Use try-with-resources instead
 */
public class Weather {

	/*
	 * OpenWeatherMap API URL and URL Parameters
	 *
	 * Guide for OpenWeatherMap API: https://web.stanford.edu/group/csp/cs22/using-an-api.pdf
	 * Example: https://api.openweathermap.org/data/2.5/find?q=Palo+Alto&units=imperial&type=accurate&mode=xml&APPID=api-key
	 */
	private final String QUERY_URL = "https://api.openweathermap.org";
	private final Config config;
	private String cityName;
	private String stateCode;
	private String countryCode;
	private final String apikey;

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
	 * @TODO Remove this and do not use FileReader.getConfig() in the constructors
	 */
//	public Weather() {
//		Weather(null);
//	}

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
	 * @param config
	 */
	public Weather(Config config) {

		cityName = "San Antonio";
		stateCode = "TX";
		countryCode = "US";

		this.config = config;
		try {
			if(this.config == null) { 
//				this.config = FileReader.getConfig(); //@TODO Don't use this
			}

		} catch (NullPointerException ex) {
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("[+++]\tNullPointerException: Unable to set config and cannot retrieve The OpenWeatherMap API Key");

		} finally {
			apikey = getApiKey(); //@FIXME Does this method some times throw an exception?

		}
	}

	/**
	 * Tries to determine the city name, state code and country code from a
	 * location string and returns the json for it if valid
	 *
	 * @TODO Should I use greedy, reluctant or possessive quantifiers?
	 * @throws InvalidLocationException for an invalid location
	 * @param location as "city name", "city name, country code", or "city name, state code, country code" 
	 * @return json weather data
	 */
	public String getJson(String location) throws InvalidLocationException {

		//@TODO More than 3 should give the help message for the weather command
		//@TODO Catch the InvalidLocationException from BotCommand and print the help message there
		String locationArr[];
		location = "New Braunfels"; //@TODO Test to see what a location with no commas does
		locationArr = location.split("\\s*,\\s*");
		String result;

		// There should be 0-2 commas and if locationArr is 0 then there's another problem
		try {
			if (locationArr == null || locationArr.length < 1 || locationArr.length > 3) {
				throw new InvalidLocationException("Invalid Location: Too many commas or empty string");
			}

			return getJson(cityName, "", "");
		} catch (InvalidLocationException ex) {
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("[+++]\tInvalid Location: Using default location instead");
//			return getJson(this.cityName, this.stateCode, this.countryCode);

			return getJson(cityName, "", "");
		} finally {
			System.out.println("[+++]\tlocationArr: " + Arrays.toString(locationArr));
			System.out.println("[+++]\tcityName: " + cityName);
			System.out.println("[+++]\tstateCode: " + stateCode);
			System.out.println("[+++]\tcountryCode: " + countryCode);
			System.out.println("[+++]\tlocation: " + location);

		}
	}

	/**
	 *
	 * @param cityName
	 * @param stateCode
	 * @param countryCode
	 * @return String
	 */
	public String getJson(String cityName, String stateCode, String countryCode) {

		/* Set the location parameter used by the api from the city name, state code, and country code */
		String location;
		try {
			if (cityName == null || stateCode == null || countryCode == null) {
				throw new InvalidLocationException("Received null for location");
			}
			if ( !cityName.equals("") && stateCode.equals("") && countryCode.equals("") ) {
			
				System.out.println("[+++]\tcityName is set, stateCode unset, countryCode unset");
				location = cityName;
			} else if ( !cityName.equals("") && !stateCode.equals("") && countryCode.equals("") )
			{
				System.out.println("[+++]\tcityName is set, stateCode set, countryCode unset");
				location = cityName + "," + stateCode;
			} else if ( cityName.equals("") && stateCode.equals("") && countryCode.equals("") ) 
			{
				System.out.println("[+++]\tcityName is unset, stateCode unset, countryCode unset");
				location = cityName + "," + stateCode + "," + countryCode;
			} else {
				throw new InvalidLocationException("Location is not valid");
			}
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidLocationException ex) {
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
			// Use the default location that is set in the constructor
			System.out.println("[+++]\tUsing default location");
			location = this.cityName + "," + this.stateCode + "," + this.countryCode;
		} finally { 
			System.out.println("[+++]\tcityName: " + cityName);
			System.out.println("[+++]\tstateCode: " + stateCode);
			System.out.println("[+++]\tcountryCode: " + countryCode);
			System.out.println("[+++]\tlocation: " + location);
		}

		String url = (QUERY_URL
				+ "/data/2.5/" + "find" // Possible values: find, weather, forecast
				+ "?q=" + location
				+ "&units=" + "imperial"
				+ "&type=" + "accurate"
				+ "&mode=" + "json"
				+ "&lang=" + "en"
				+ "&appid=" + apikey).replaceAll(" ", "%20");

		System.out.println("[+++]\t" + url);

		String json = "";

		/* Create a URL obj from strings */
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
			new URL(url).openStream()))) {

			String line;

			while ((line = br.readLine()) != null) {
				json += line;
			}
		}catch (IOException ex) {
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
			json = "{ \"data\": \"Unable to retrieve Weather json data\" }";
			//throw new RuntimeException(e);
		}
		 finally {
			System.out.println("[+++]\t" + json);
			return json;
		}
	}

	/**
	 * Retrieve the data as a summary with irc color codes and formatting
	 *
	 * @param Json
	 * @return Formatted Json
	 */
	public String getFormattedWeatherSummary(final String json) {

		WeatherJson weatherJson;

		try {
			Type WeatherJsonT = new TypeToken<ArrayList<WeatherJson>>() {}.getType();
			System.out.println("[+++]\tWeatherJson Type: " + WeatherJsonT);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			weatherJson = gson.fromJson(json, WeatherJson.class);

		} catch (JsonSyntaxException ex) {
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("[+++]\tInvalid Json: Does not match the Json code or wrong type");
			return "Unable to retrieve the weather";
		} finally {
			//TODO Remove all the other returns and put return here, close streams
			if(hasColors) {
				return weatherJson.getColorString();
			} else {
				return weatherJson.toString();
			}
		}


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

		Weather w = new Weather(null);
		try {
			System.out.println(w.getFormattedWeatherSummary(w.getJson("San Antonio,TX,US")));
		} catch (InvalidLocationException ex) {
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("[+++]\tInvalid Location: Try Again");
		}
	}

	public static class InvalidLocationException extends Exception {

		public InvalidLocationException(String message) {
		}
	}

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
	 * @TODO Retrieve sunrise and sunset info
	 */
	private class WeatherJson {

		private String message;
		private String cod;
		private int count;
		private ArrayList<WeatherListJsonItem> list;

		public String getColorString() {

			String result
				= MircColors.BOLD
				+ "Result " + MircColors.GREEN + "1" + MircColors.NORMAL + MircColors.BOLD + " / "
				+ MircColors.GREEN + count + MircColors.NORMAL + MircColors.BOLD + ": "
				+ MircColors.NORMAL;

			try {
				result += list.get(0).getColorString();

			} catch (IndexOutOfBoundsException ex) {
				Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
				System.err.println("[+++]\tCould not get json for list at index 0");
			} finally {
				return result;
			}
		}

		/**
		 *
		 * @override
		 */
		@Override
		public String toString() {

			return "message: " + message + ", "
				+ "cod: " + cod + ", "
				+ "count: " + count + ", "
				+ "list: " + list;
		}

		/**
		 *
		 * @author Chris Lemire <goodbye300@aim.com>
		 * @TODO Make rain a String
		 */
		private class WeatherListJsonItem {

			private int id;
			private String name;
			private WeatherCoordJsonItem coord;
			private WeatherMainJsonItem main;
			private int dt;
			private WeatherWindJsonItem wind;
			private WeatherSysJsonItem sys;
			private Object rain;
			private String snow;
			private WeatherCloudsJsonItem clouds;
			private ArrayList<WeatherWeatherJsonItem> weather;

			public String getColorString() {

				String result
					= MircColors.BOLD
					+ "Weather for "
					+ MircColors.GREEN + name + ", "
					+ MircColors.NORMAL
					+ sys.getColorString() + " "
					+ coord.getColorString() + " "
					+ main.getColorString() + ", "
					+ wind.getColorString() + ", "
					+ clouds.getColorString() + " ";

				if (rain != null) {

					result
						+= MircColors.BOLD
						+ ", "
						+ MircColors.CYAN
						+ "Rain: " + MircColors.GREEN + rain + " "
						+ MircColors.NORMAL;
				}

				if (snow != null) {

					result
						+= MircColors.BOLD
						+ ", "
						+ MircColors.CYAN
						+ "Snow: " + MircColors.GREEN + snow + " "
						+ MircColors.NORMAL;
				}

				for (WeatherWeatherJsonItem element : weather) {
					result += element.getColorString() + " ";
				}

//				result = result + "\n" + 
//					"https://s.w-x.co/staticmaps/wu/radsum/county_loc/sat/20200930/0500z.gif";
				return result;
			}

			public String toString() {

				return "id: " + id + ", "
					+ "name: " + name + ", "
					+ "coord: " + coord + ", "
					+ "main: " + main + ", "
					+ "dt: " + dt + ", "
					+ "sys: " + sys + ", "
					+ "wind: " + wind + ", "
					+ "rain: " + rain + ", "
					+ "snow: " + snow + ", "
					+ "clouds: " + clouds + ", "
					+ "weather " + weather;

			}

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			private class WeatherCoordJsonItem {

				private float lat;
				private float lon;

				public String getColorString() {

					DecimalFormat fmt = new DecimalFormat("0.##");

					String result
						= MircColors.BOLD
						+ MircColors.CYAN + "Lat: "
						+ MircColors.GREEN + fmt.format(lat) + " "
						+ MircColors.CYAN + "Lon: "
						+ MircColors.GREEN + fmt.format(lon)
						+ MircColors.NORMAL;

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
			private class WeatherMainJsonItem {

				private float temp;
				private float feels_like;
				private float temp_min;
				private float temp_max;
				private int pressure;
				private String humidity;

				public String getColorString() {

					String result
						= MircColors.BOLD
						+ MircColors.CYAN + "Current temperature" + MircColors.NORMAL + MircColors.BOLD + " is " + MircColors.GREEN + temp + "F, " + MircColors.NORMAL + MircColors.BOLD
						+ MircColors.CYAN + "Feels" + MircColors.NORMAL + MircColors.BOLD + " like " + MircColors.GREEN + feels_like + "F" + MircColors.NORMAL + MircColors.BOLD + ", "
						+ MircColors.CYAN + "Min" + MircColors.NORMAL + MircColors.BOLD + " / " + MircColors.CYAN + "Max" + MircColors.NORMAL + MircColors.BOLD + " is "
						+ MircColors.GREEN + temp_min + "F"
						+ MircColors.NORMAL
						+ MircColors.BOLD
						+ " / "
						+ MircColors.GREEN + temp_max + "F" + MircColors.NORMAL + MircColors.BOLD + ", "
						+ MircColors.CYAN + "Pressure" + MircColors.NORMAL + MircColors.BOLD + " is " + MircColors.GREEN + pressure + "hPa" + MircColors.NORMAL + MircColors.BOLD + ", "
						+ MircColors.CYAN + "Humidity" + MircColors.NORMAL + MircColors.BOLD + " is " + MircColors.GREEN + humidity + "%"
						+ MircColors.NORMAL;

					return result;

				}

				public String toString() {

					return "temp: " + temp
						+ ", feels like: " + feels_like
						+ ", temp_min: " + temp_min
						+ ", temp_max: " + temp_max
						+ ", pressure: " + pressure
						+ ", humidity: " + humidity;

				}
			} // EOF WeatherMainJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			private class WeatherWindJsonItem {

				private float speed;
				private int deg;

				public String getColorString() {

					String result
						= MircColors.BOLD
						+ MircColors.CYAN + "Wind speed" + MircColors.NORMAL + MircColors.BOLD + " is " + MircColors.GREEN + speed + "MPH " + MircColors.NORMAL + MircColors.BOLD
						+ "at " + MircColors.GREEN + deg + "°"
						+ MircColors.NORMAL;

					return result;
				}

				public String toString() {
					return "speed: " + speed + ", deg: " + deg;
				}
			} // EOF WeatherWindJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			private class WeatherSysJsonItem {

				private String country;

				public String getColorString() {

					String result;
					if (country != null) {
						result
							= MircColors.BOLD
							+ MircColors.GREEN
							+ country
							+ MircColors.NORMAL;
					} else {
						result = "";
					}

					return result;
				}

				public String toString() {

					return "country: " + country;
				}
			} // EOF WeatherSysJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 */
			private class WeatherCloudsJsonItem {

				private int all;

				public String getColorString() {

					String result
						= MircColors.BOLD
						+ MircColors.CYAN + "Cloudiness " + MircColors.GREEN + all + "%"
						+ MircColors.NORMAL;

					return result;
				}

				public String toString() {
					return "all: " + all;
				}
			} // EOF WeatherCloudsJsonItem

			/**
			 *
			 * @author Chris Lemire <goodbye300@aim.com>
			 * @TODO Icons can be found here:
			 * https://openweathermap.org/weather-conditions
			 */
			private class WeatherWeatherJsonItem {

				private int id;
				private String main;
				private String description;
				private String icon;

				private String getColorString() {
					String iconUrl = "http://openweathermap.org/img/wn/" + icon + "@2x.png";
					String result
						= MircColors.BOLD
						+ "with (a) " + MircColors.GREEN + description + MircColors.NORMAL + MircColors.BOLD + ".\n"
						+ iconUrl
						+ MircColors.NORMAL;

					return result;
				}

				public String toString() {
					return "id: " + id + ", main: " + main + ", description: " + description + ", icon: " + icon;
				}

			} // EOF WeatherWeatherJsonItem

		} // EOF WeatherListJsonItem

	} // EOF WeatherJson

} // EOF Weather