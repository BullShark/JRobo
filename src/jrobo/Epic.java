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
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 *
 * @author Christopher Lemire
 * <goodbye300@aim.com>
 */
public class Epic {

	/* For the HTTP Connection */
	private URL url;
	private URLConnection conn;
	private OutputStreamWriter wr;
	private BufferedReader rd;

	/**
	 * Miscellanous
	 *
	 * @TODO Fix bug, if the url is not available, the bot will throw an exception and crash
	 * Example: https://store-site-backend-static.ak.epicgames.com/freeGamesPromotions?locale=en-US&country=TR&allowCountries=TR
	 */
	private static final String QUERY_URL = "https://invalid.not.a.real.domain";
	//private static final String QUERY_URL = "https://store-site-backend-static.ak.epicgames.com";
	private String json;
	private String locale;
	private String countrycode;

	/* For the Gson/Json */
	private Gson gson;
	private int defaultLimit;

	public Epic() {

		/* For the HTTP Connection */
		url = null;
		conn = null;

		/* Miscelanous */
		locale = "en-US";
		countrycode = "TR";
		json = "";
		defaultLimit = 5;

		/* For the Gson/Json */
		gson = new Gson();
	}

	/**
	 *
	 * @return
	 */
	private String getJson() {
		try {
			/* Create a URL obj from strings */

			url = new URL(
				(QUERY_URL
					+ "/freeGamesPromotions"
					+ "?locale=" + locale
					+ "&country=" + countrycode
					+ "&allowCountries=" + countrycode).replaceAll(" ", "%20")
			);

			System.out.println("[+++]\t" + url);

			conn = url.openConnection();

			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				json += line;
			}

			rd.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			//@TODO Close all i/o and streams here
		}

		System.out.println(json);

		return json;
	}

	public String getFormattedEpicSummary(final String locale, final boolean hasColors, final int limit) {

		if (locale != null && !locale.equals("")) {
			this.locale = locale;
		}

		/*
		 * TODO Add try/catch to handle
		 * TODO The exception that no JSON is received
		 */
		EpicJson epicJson;
		try {
			Type EpicJsonT = new TypeToken<ArrayList<EpicJson>>() {
			}.getType();
			System.out.println("[+++]\tEpicJson Type: " + EpicJsonT);

			gson = new GsonBuilder().setPrettyPrinting().create();
			epicJson = gson.fromJson(this.getJson(), EpicJson.class);

			return epicJson.toString();
//			return EpicJson.getColorString();

		} catch (JsonSyntaxException | IllegalStateException | NullPointerException ex) {
			ex.printStackTrace();
			return "Unable to retrieve the weather";
		}
	}

	/*
         * A main method for testing this class
	 */
	public static void main(String[] args) {
		if (args.length != 0) {
			System.err.println("Usage: java Epic");
			System.exit(-1);
		}

		System.out.println(new Epic().getFormattedEpicSummary(null, false, 10));

	} // EOF main

	/**
	 *
	 * @author Christopher Lemire
	 * <goodbye300@aim.com>
	 */
	private class EpicJson {

		/**
		 * Example:
		 *
		 * WeatherCloudsJsonItem clouds;
		 * ArrayList<WeatherWeatherJsonItem> weather;
		 *
		 * Class object { ... }; List<Class> object [ ... ];
		 */
		private EpicDataJsonItem data;
		private EpicExtensionsJsonItem extensions;

		private String getColorString() {
			String result = "";

			return result;
		}

		/**
		 *
		 * @override
		 */
		public String toString() {
			return "data: " + data + "extensions: " + extensions;
		}

		/**
		 *
		 * @author Christopher Lemire
		 * <goodbye300@aim.com>
		 */
		private class EpicDataJsonItem {

			private EpicCatalogJsonItem Catalog;

			public String toString() {
				return "Catalog: " + Catalog;

			}

			/**
			 *
			 * @author Christopher Lemire
			 * <goodbye300@aim.com>
			 */
			private class EpicCatalogJsonItem {

				private EpicSearchStoreJsonItem searchStore;

				public String toString() {
					return "searchStore: " + searchStore;

				}

				/**
				 *
				 * @author Christopher Lemire
				 * <goodbye300@aim.com>
				 */
				private class EpicSearchStoreJsonItem {

					private ArrayList<EpicElementsJsonItem> elements;

					public String toString() {
						return "elements: " + elements;

					}

					/**
					 *
					 * @author Christopher Lemire
					 * <goodbye300@aim.com>
					 */
					private class EpicElementsJsonItem {

						/**
						 * Example:
						 *
						 * private WeatherCloudsJsonItem
						 * clouds; private
						 * ArrayList<WeatherWeatherJsonItem>
						 * weather;
						 *
						 * Class object { ... }
						 * List<Class> object [ ... ]
						 */
						private String title;
						private String id;
						private String description;
						private String effectiveDate;
						//private EpicKeyImagesJsonItem keyImages;
						//private EpickeySellerJsonItem seller;
						private String productSlug;
						private String urlSlug;
						private String url;
						//private ArrayList<EpicItemsJsonItem> items;
						//private ArrayList<EpicCustomAttributesJsonItem> customAttributes;
						//private ArrayList<EpicCategoriesJsonItem> categories;
						//private ArrayList<EpicTagsJsonItem> tags;
						private EpicPriceJsonItem price;
						//private EpicPromotionsJsonItem promotions;

						private String getColorString() {
							String result = "";

							return result;
						}

						public String toString() {

							return "title: " + title + "\n"
								+ "id: " + id + "\n"
								+ "description: " + description + "\n"
								+ "effectiveDate" + effectiveDate + "\n"
								+ "productSlug: " + productSlug + "\n"
								+ "urlSlug: " + urlSlug + "\n"
								+ "url: " + url + "\n"
								+ "price: " + price + "\n"
								;
						}

						/**
						 *
						 * @author Christopher Lemire
						 * <goodbye300@aim.com>
						 */
						private class EpicPriceJsonItem {

							// Class object { ... }
							// List<Class> object [ ... ]
							private EpicTotalPriceJsonItem totalPrice;
							private int discountPrice;
							private int originalPrice;
							private int voucherDiscount;
							private int discount;
							private String currencyCode;
							//EpicCurrencyInfoJsonItem currencyInfo;
							//EpicfmtPriceJsonItem fmtPrice;

							public String toString() {
								return "totalPrice: " + totalPrice
									+ "discountPrice: " + discountPrice + "\n"
									+ "originalPrice: " + originalPrice + "\n"
									+ "voucherDiscount: " + voucherDiscount + "\n"
									+ "discount: " + discount + "\n"
									+ "currencyCode: " + currencyCode + "\n"
									;
							}

							/**
							 *
							 * @author Christopher Lemire
							 * <goodbye300@aim.com>
							 */
							private class EpicTotalPriceJsonItem {

								public String toString() {
									return null;

								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 *
	 * @author Christopher Lemire
	 * <goodbye300@aim.com>
	 */
	private class EpicExtensionsJsonItem {

		private EpicCacheControlJsonItem cacheControl;

		/**
		 *
		 * @override
		 */
		public String toString() {
			return "cacheControl " + cacheControl;
		}

		/**
		 *
		 * @author Christopher Lemire
		 * <goodbye300@aim.com>
		 */
		private class EpicCacheControlJsonItem {

			private String version;
			//private List<EpicHintsJsonItem> hints;

			/**
			 *
			 * @override
			 */
			public String toString() {
				return null;
			}
		}
	}
} // EOF class
