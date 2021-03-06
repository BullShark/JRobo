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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Lemire
 * <goodbye300@aim.com>
 */
public class Epic {

	/**
	 * Miscellaneous
	 *
	 * @TODO Fix bug, if the url is not available, the bot will throw an
	 * exception and crash Example:
	 * https://store-site-backend-static.ak.epicgames.com/freeGamesPromotions?locale=en-US&country=TR&allowCountries=TR
	 */
	private final String QUERY_URL = "https://invalid.not.a.real.domain";
	//private final String QUERY_URL = "https://store-site-backend-static.ak.epicgames.com";
	private final String LOCALE;
	private final String COUNTRYCODE;

	/**
	 *
	 * @author Chris Lemire <goodbye300@aim.com>
	 */
	public Epic() {

		/* Miscellaneous */
		LOCALE = "en-US";
		COUNTRYCODE = "TR";
//		defaultLimit = 5;
	}

	/**
	 *
	 * @return Json data from the Epic api
	 */
	private String getJson() {

		String json = "";
		final String URL = (QUERY_URL
			+ "/freeGamesPromotions"
			+ "?locale=" + LOCALE
			+ "&country=" + COUNTRYCODE
			+ "&allowCountries=" + COUNTRYCODE).replaceAll(" ", "%20");
		System.out.println("[+++]\t" + URL);

		/* Create a URL obj from strings */
		try ( BufferedReader br = new BufferedReader(new InputStreamReader(
			new URL(URL).openStream()))) {

			String line;

			while ((line = br.readLine()) != null) {
				json += line;
			}

		} catch (IOException ex) {
			Logger.getLogger(Epic.class.getName()).log(Level.SEVERE, null, ex);
			json = "{ \"data\": \"Unable to retrieve Epic json data\" }";

		} finally {
			System.err.println("[+++]\t" + json);
			return json;

		}
	}

	/**
	 *
	 * @param HASCOLORS
	 * @param LIMIT
	 * @return
	 */
	protected String getFormattedEpicSummary(final boolean HASCOLORS, final int LIMIT) {

		String result;
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			EpicJson epicJson = gson.fromJson(this.getJson(), EpicJson.class);
			Type epicJsonT = new TypeToken<ArrayList<EpicJson>>() {
			}.getType();
			System.out.println("[+++]\tepicJson Type: " + epicJsonT);

			result = (HASCOLORS) ? epicJson.getColorString() : epicJson.toString();

			return result;

		} catch (JsonSyntaxException | IllegalStateException | NullPointerException ex) {
			Logger.getLogger(Epic.class.getName()).log(Level.SEVERE, null, ex);
			result = "{ \"data\": \"Unable to retrieve Epic json data\" }";
			return result;

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

		System.out.println(new Epic().getFormattedEpicSummary(false, 5));

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
		@Override
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

			@Override
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

				@Override
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

					@Override
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

						@Override
						public String toString() {

							return "title: " + title + "\n"
								+ "id: " + id + "\n"
								+ "description: " + description + "\n"
								+ "effectiveDate" + effectiveDate + "\n"
								+ "productSlug: " + productSlug + "\n"
								+ "urlSlug: " + urlSlug + "\n"
								+ "url: " + url + "\n"
								+ "price: " + price + "\n";
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
							//private EpicCurrencyInfoJsonItem currencyInfo;
							//private EpicfmtPriceJsonItem fmtPrice;

							@Override
							public String toString() {
								return "totalPrice: " + totalPrice
									+ "discountPrice: " + discountPrice + "\n"
									+ "originalPrice: " + originalPrice + "\n"
									+ "voucherDiscount: " + voucherDiscount + "\n"
									+ "discount: " + discount + "\n"
									+ "currencyCode: " + currencyCode + "\n";
							}

							/**
							 *
							 * @author Christopher Lemire
							 * <goodbye300@aim.com>
							 */
							private class EpicTotalPriceJsonItem {

								@Override
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

		@Override
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

			@Override
			public String toString() {
				return null;
			}
		} // EOF EpicCacheControlJsonItem
	} // EOF EpicExtensionsJsonItem
} // EOF Epic

