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
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 *
 * @author chris
 */
public class FileReader {
  /* Standard Java API Classes */
  private File config = null;
  private ArrayList<String> pickUpJokes = null, mommaJokes = null;
  
  /* User-defined Classes */
  private Networking connection;
  
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


    config = new File("Config.xml");

    fileToArrayList("MomJokes.txt", mommaJokes);

    fileToArrayList("FunnyJokes.txt", pickUpJokes);
  }
  
  //@TODO Use XML
  public boolean parseConf() {
    return false;
  }
  
  public String getNick() {
    return "JRobo";
  }
  
  public String getPass() {
    return "";
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
      err.println("FAILED TO READ CONFIG FOR JOIN CHAN!!!"
              + "\nIMPLEMENT JRobo TO READ AND PARSE XML CONFIG INSTEAD"); //TODO Delete when working
      return "#blackhats";
      //return "##blackhats-bots";
      //return "#theblackmatrix-bots";
//    }
  }
  
  public String getMaster() {
    return "BullShark";
//    return "iAmerikan";
  }
  
  public char getCmdSymb() {
    return '^';
  }
  
  private boolean fileToArrayList(String fileName, ArrayList<String> listArr) {
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
  
  /*
   * On success, the bot will receive phone numbers
   * From other IRC members
   * Pass null in as String to not precede message
   * With "User: "
   * Be sure to check for null return when calling this
   */
  public String getPhoneNumber(String user) {
    if(pickUpJokes.isEmpty()) {

      //Inform channel
      connection.noticeChan(getChan(), "[***]RELOADING AMMUNITION");

      //Reload the jokes
      fileToArrayList("pickup.txt", pickUpJokes);
    }
    if(user == null) {
  
      /* Random at 0 because the List was already shuffled */
      return pickUpJokes.remove(0);
    } else {

      return user + ", " + pickUpJokes.remove(0);
    }
  }
  
  /*
   * Replaces "Yo momma" with "User's momma"
   * Pass null in as String to not use replacement
   * Be sure to check for null return when calling this
   */
  public String getMommaJoke(String user) {
    if(mommaJokes.isEmpty()) {
  
      //Inform owner in PM
      connection.msgUser(getMaster(), "OUT OF PHONE NUMBERS!!!");
   
      //Inform channel
      connection.noticeChan(getChan(), "[***]RELOADING AMMUNITION");

      //Reload the jokes
      fileToArrayList("MomJokes.txt", mommaJokes);
    }
    if(user == null) {
  
      /* Random at 0 because the List was already shuffled */
      return mommaJokes.remove(0);
    } else {
  
      return mommaJokes.remove(0).replaceFirst("Yo", user + "'s");
    }
  } // EOF method
} // EOF class