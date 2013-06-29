/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
 * Copyright (C) <2013> <BinaryStroke>
 * Copyright (C) <2013> <Muhammad Sajid>
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.w3c.dom.Document;

/**
 *
 * @author chris
 */


public class Weather {
  /* For the JSON/DOC */
  JSONObject jsonObject;
  
  /* For the HTTP Connection */
  private URL url;
  private URLConnection conn;
  private OutputStreamWriter wr;
  private BufferedReader rd;
  private String fullUrl;

  /* Miscelanous */
  private static final String QUERY_URL = "http://api.wunderground.com/api/92c71a10c8515070/conditions/lang:EN/q/%s/%s.json";
  private String weather;
  private String xml;

  public Weather() {


    /* For the HTTP Connection */
    url = null;
    conn = null;
    //TODO: Move BufferedReader declaration here
//    wr = null;
//    rd = null;
    fullUrl = "";

    /* Miscelanous */
    json= "";
    weather = "";
  }

  /**
   * 
   * @param location
   * @return
   */
  public String getJson(String location, String city) {
    try {
      /* Create the query url from template */
      city = city.replace(" ", "_");
      location = location.replace(" ", "_");
      url = String.format(QUERY_URL, location, city);

      System.out.println(fullUrl);

      conn = url.openConnection();

      // Get the response
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String line = "";
      while ((line = rd.readLine()) != null) {
        json = json.concat(line);
      }

      rd.close();

    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return json;
  }

  public String getFormattedWeatherSummary(String xml) {

    return xml;
  }

  /*
   *A main method for testing this class
   */
  public static void main(String[] args) {
    if(args.length == 0) {
      System.err.println("Usage: java Weather <location>");
      System.exit(-1);
    }
    //System.out.println(new Weather().getXML(args[0]) );
    Weather w = new Weather();
    System.out.println(w.getXml(args[0]));
  }

} // EOF class
