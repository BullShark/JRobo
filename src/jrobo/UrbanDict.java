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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author chris
 */


public class UrbanDict {

  /* For the HTTP Connection */
  private URL url;
  private URLConnection conn;
  private OutputStreamWriter wr;
  private BufferedReader rd;
  private String fullUrl;

  /* Miscelanous */
  private static final String QUERY_URL = "http://www.urbandictionary.com/iphone/search/define?term=";
  private String def;
  private String json;
  private String word;

  /* For the Gson/Json */
  private Gson gson;

  public UrbanDict(String word) {

    /* For the HTTP Connection */
    url = null;
    conn = null;
    fullUrl = "";
    this.word = word;

    /* Miscelanous */
    json = "";

    /* For the Gson/Json */
    gson = new Gson();

  }

  /**
   * 
   * @return
   */
  public String getJson() {
    try {
      /* Create a URL obj from strings */
      url =  new URL((QUERY_URL.concat(word)).replace(" ", "%20"));

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

  public String getFormattedUrbanDef(boolean hasColors, int limit) {
    
    gson = new GsonBuilder().setPrettyPrinting().create();
    UrbanJson uj = gson.fromJson(this.getJson(), UrbanJson.class);

    String output = "";
    
    if(hasColors) {
      for(UrbanJsonItem uji : uj.list) {
        if(limit > 0) {
          output += uji.getColorString();
          limit--;
        } else {
          break;
        }
      }
    } else {
      for(UrbanJsonItem uji : uj.list) {
        if(limit > 0) {
          output += uji.toString();
          limit--;
        } else {
          break;
        }
      }
    }
    
    return output;
  }

  /*
   *A main method for testing this class
   */
  public static void main(String[] args) {
    if(args.length == 0) {
      System.err.println("Usage: java UrbanDict <word>");
      System.exit(-1);
    }
    System.out.println(new UrbanDict(args[0]).getFormattedUrbanDef(false, -1));

  } // EOF main

} // EOF class