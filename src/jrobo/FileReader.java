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
  private String config_file = null;
  private ArrayList<String> pickUpJokes = null, mommaJokes = null;
  
  /* User-defined Classes */
  private Networking connection;
  private Config config;
  
  public FileReader () {
      
    config_file = "Config.json";
    getConfig();
  }
  
  public FileReader(Networking connection) {
    /*
     * @TODO ...
     * Why is Netbeans not complaining about Exceptions
     * Exceptions needed to determine which file was not able to be read
     * See if exception will get thrown when file missing
     * If config fails, catch exception and set defaults
     * 
     * If first condition is met, JRobo is being ran from Netbeans
     * Else if second contidition, JRobo is being ran from CLI
     */
    
    this.connection = connection;

    mommaJokes = new ArrayList<>(50);
    pickUpJokes = new ArrayList<>(50);


    config_file = "Config.json";

    getConfig();
    
    fileToArrayList("MomJokes.txt", mommaJokes);

    fileToArrayList("FunnyJokes.txt", pickUpJokes);
  }
  
  //@TODO Use XML
  public boolean parseConf() {
    return false;
  }
  
  public String getNick() {
    return config.getName();
  }
  
  public String getPass() {
    return config.getPass();
  }
  
  /**
   * 
   * @return
   */
  public String getChan() {
//    try {
//      return new Scanner(new File(pathToFile + "config.xml")).nextLine();
//    } catch (FileNotFoundException ex) {
//      Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
      //err.println("FAILED TO READ CONFIG FOR JOIN CHAN!!!"
      //        + "\nIMPLEMENT JRobo TO READ AND PARSE XML CONFIG INSTEAD"); //TODO Delete when working
      //return "#blackhats";
      //return "##blackhats-bots";
      //return "#theblackmatrix-bots";
//    }
      return config.getChannel();
  }
  
  public String getMaster() {  
    //return "BullShark";
    //temp workaround for master
    String[] masters = config.getMasters();
    return masters[0];
  }
  
  public char getCmdSymb() {
    return config.getCmdSymb();
  }
  
  public boolean fileToArrayList(String fileName, ArrayList<String> listArr) {
   out.println("[+++]\tReading: " + fileName);

   BufferedReader br = new BufferedReader(new InputStreamReader(FileReader.class.getResourceAsStream(fileName)));

   String line = "";

    try {
      while( (line = br.readLine()) != null) {
        listArr.add(line);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  
    Collections.shuffle(listArr);
  
    return true;
  }
  

  
  /**
   * 
   * @return Network address from the configuration
   * @since 2013-02-19
   */
  public String getNetwork () {
      return config.getNetwork();
  }
  
  /**
   * 
   * @return the weather underground key
   * @since 2013-02-19
   */
  public String getWundergroundKey (){
      return config.getWundergroundKey();
  }
  
  /**
   * gets the data from the configuration file
   * 
   * @since 2013-02-18
   */
  private void getConfig () {
      
      out.println("[+++]\tReading Configuration file (Config.json)");
      
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
          while ((line= br.readLine()) != null){
          json = json.concat(line);
      
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
      
      if (config.getName()==null){
          err.println("[+++]\tError: Unable to find bot's nickname");
          System.exit(1);
      }
      
      if (config.getPass()==null){
          err.println("[+++]\tError: Unable to find bot's password");
          System.exit(1);
      }
      
      if (config.getMasters()==null){
          err.println("[+++]\tError: Unable to find bot's Masters");
          System.exit(1);
      }
      
      if (config.getHostmasks()==null){
          err.println("[+++]\tError: Unable to find bot's Hostmasks");
          System.exit(1);
      }
      
      if (config.getCmdSymb()=='\u0000'){
          err.println("[+++]\tError: Unable to find bot's Command Symbol");
          System.exit(1);
      }
      
      if (config.getNetwork()==null){
          err.println("[+++]\tError: Unable to find bot's Network");
          System.exit(1);
      }
      
      if (config.getChannel()==null){
          err.println("[+++]\tError: Unable to find bot's Channel");
          System.exit(1);
      }
  }
} // EOF class