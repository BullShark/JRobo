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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 *
 * @author chris
 */


public class Weather {
  /* For the XML/DOC */
  private DocumentBuilderFactory dbf;
  private DocumentBuilder db;
  private Document doc;

  /* For the HTTP Connection */
  private URL url;
  private URLConnection conn;
  private OutputStreamWriter wr;
  private BufferedReader rd;
  private String fullUrl;

  /* Miscelanous */
  private static final String QUERY_URL = "http://www.google.com/ig/api?weather=";
  private String weather;
  private String xml;

  public Weather() {
    /* For the XML/DOC */
    dbf = null;
    db = null;
    doc = null;

    /* For the HTTP Connection */
    url = null;
    conn = null;
    //TODO: Move BufferedReader declaration here
//    wr = null;
//    rd = null;
    fullUrl = "";

    /* Miscelanous */
    xml = "";
    weather = "";
  }

  /**
   * 
   * @param location
   * @return
   */
  public String getXml(String location) {
    try {
      /* Create a URL obj from strings */
      url =  new URL((QUERY_URL.concat(location)).replace(" ", "%20"));

      System.out.println(fullUrl);

      conn = url.openConnection();

      // Get the response
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String line = "";
      while ((line = rd.readLine()) != null) {
        xml = xml.concat(line);
      }

      rd.close();

    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return xml;
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