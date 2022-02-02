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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Weather obtains weather information for a specific area, and this class colors, formats, and summarizes that info from the API.
 * 
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class Weather {

	/**
	 * OpenWeatherMap API URL and URL Parameters
	 *
	 * Guide for OpenWeatherMap API: https://web.stanford.edu/group/csp/cs22/using-an-api.pdf
	 * Example: https://api.openweathermap.org/data/2.5/find?q=Palo+Alto&units=imperial&type=accurate&mode=xml&APPID=api-key
	 */
	private final String BASE_URL = "https://api.openweathermap.org";
	private final Config CONFIG;
	private final String APIKEY;

	/**
	 * Constructor that takes a Config object for retrieving the API Key
	 *
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @param CONFIG Used to retrieve the API Key
	 */
	public Weather(final Config CONFIG) throws NullPointerException {

		if(CONFIG == null) { 
			APIKEY = "";
			throw new NullPointerException("Config is not set and cannot retrieve The OpenWeatherMap API Key");
		} else {
			this.CONFIG = CONFIG;
			APIKEY = getApiKey();
		}
	}
        
	/**
	 * Called by its wrapper method
	 * 
	 * @param LOCATION the location for the weather query
	 * @return String The JSON weather results for the LOCATION
	 */
	protected String getJson(final String LOCATION) {

		/* Create a URL obj from strings */
		final String URL = (BASE_URL
				+ "/data/2.5/" + "find" // Possible values: find, weather, forecast
				+ "?q=" + LOCATION.replaceAll(" ", "%20")
				+ "&units=" + "imperial"
				+ "&type=" + "accurate"
				+ "&mode=" + "json"
				+ "&lang=" + "en"
				+ "&appid=" + APIKEY).replaceAll(" ", "%20");

		System.out.println(TermColors.info(URL));

		String json = "";

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
			new URL(URL).openStream()))) {

			String line;

			while ((line = br.readLine()) != null) {
				json += line;
			}

		} catch (IOException ex) {
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
			json = "{ \"data\": \"Unable to retrieve Weather json data\" }";

		} finally {
			System.out.println(TermColors.info(json));
			return json;

		}
	}

	/**
	 * Retrieve the data as a summary with IRC color codes and formatting or just return the names and values from the json
	 * 
	 * @param LOCATION The LOCATION for the weather summary
	 * @param HASCOLORS Whether there is IRC colors and formatting
	 * @return Formatted and colored JSON if hasColor is true, else just the JSON information
	 */
	protected String getFormattedWeatherSummary(final String LOCATION, final boolean HASCOLORS) {

		String result = "";

		try {
			Type WeatherJsonT = new TypeToken<ArrayList<WeatherJson>>() {}.getType();
			System.out.println(TermColors.info("WeatherJson Type: " + WeatherJsonT));

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			WeatherJson weatherJson = gson.fromJson(this.getJson(LOCATION), WeatherJson.class);

			result = (HASCOLORS) ? weatherJson.getColorString() : weatherJson.toString();

		} catch (JsonSyntaxException ex) {
			Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println(TermColors.info("Invalid Json: Does not match the Json code or wrong type"));
			result = "Unable to retrieve the weather";

		} finally {
			return result;

		}
	}

	/**
	 *
	 * @return API Key for OpenWeatherMap retrieved from Config.json
	 */
	private String getApiKey() {
		if(CONFIG != null) {
			return CONFIG.getOpenWeatherMapKey();
		} else {
			return "";
		}
	}

	/**
	 * A main method for testing this class
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {

		System.out.println(new Weather(null).getFormattedWeatherSummary("San Antonio,TX,US", false));
	}

	/**
	 *
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * TODO Retrieve sunrise and sunset info
	 */
	private class WeatherJson {

		private String message;
		private String cod;
		private int count;
		private ArrayList<WeatherListJsonItem> list;

		private String getColorString() {

			String result
				= MircColors.BOLD
				+ "Result " + MircColors.GREEN + "1" + MircColors.NORMAL + MircColors.BOLD + " / "
				+ MircColors.GREEN + count + MircColors.NORMAL + MircColors.BOLD + ": "
				+ MircColors.NORMAL;

			try {
				result += list.get(0).getColorString();

			} catch (IndexOutOfBoundsException ex) {
				Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
				System.err.println(TermColors.info("Could not get json for list at index 0"));

			} finally {
				return result;
			}
		}

		/**
		 *
	 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
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
	 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
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

			private String getColorString() {

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

//				@TODO Figure out how to embed this into Matrix
//				result = result + "\n" + 
//					"https://s.w-x.co/staticmaps/wu/radsum/county_loc/sat/20200930/0500z.gif";

				return result;
			}

			@Override
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
		 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
			 */
			private class WeatherCoordJsonItem {

				private float lat;
				private float lon;

				private String getColorString() {

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

				@Override
				public String toString() {
					return String.format("lat: %s lon: %s", lat, lon);
				}
			} // EOF WeatherCoordJsonItem

			/**
			 *
		 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
			 */
			private class WeatherMainJsonItem {

				private float temp;
				private float feels_like;
				private float temp_min;
				private float temp_max;
				private int pressure;
				private String humidity;

				private String getColorString() {

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

				@Override
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
		 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
			 */
			private class WeatherWindJsonItem {

				private float speed;
				private int deg;

				private String getColorString() {

					String result
						= MircColors.BOLD
						+ MircColors.CYAN + "Wind speed" + MircColors.NORMAL + MircColors.BOLD + " is " + MircColors.GREEN + speed + "MPH " + MircColors.NORMAL + MircColors.BOLD
						+ "at " + MircColors.GREEN + deg + "°"
						+ MircColors.NORMAL;

					return result;
				}

				@Override
				public String toString() {
					return "speed: " + speed + ", deg: " + deg;
				}
			} // EOF WeatherWindJsonItem

			/**
			 *
		 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
			 */
			private class WeatherSysJsonItem {

				private String country;

				private String getColorString() {

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

				@Override
				public String toString() {
					return "country: " + country;
				}
			} // EOF WeatherSysJsonItem

			/**
			 *
		 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
			 */
			private class WeatherCloudsJsonItem {

				private int all;

				private String getColorString() {

					String result
						= MircColors.BOLD
						+ MircColors.CYAN + "Cloudiness " + MircColors.GREEN + all + "%"
						+ MircColors.NORMAL;

					return result;
				}

				@Override
				public String toString() {
					return "all: " + all;
				}
			} // EOF WeatherCloudsJsonItem

			/**
			 *
		 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
			 * TODO Icons can be found here:
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

				@Override
				public String toString() {
					return "id: " + id + ", main: " + main + ", description: " + description + ", icon: " + icon;
				}

			} // EOF WeatherWeatherJsonItem

		} // EOF WeatherListJsonItem

	} // EOF WeatherJson

} // EOF Weather
