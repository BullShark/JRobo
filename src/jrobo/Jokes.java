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

import java.util.ArrayList;

/**
 * This class is the new home for the joke functions from FileReader
 * @author BinaryStroke <binarystroke@null.net>
 * @since 2013-02-23
 */
public class Jokes {
    
  /* Standard Java API Classes */
  private ArrayList<String> pickUpJokes = null, mommaJokes = null;
  private String channel ;
    
   /* User-defined Classes */
  private Networking connection;
  private FileReader reader;
  
  public Jokes (Networking connection,String channel) {
    
    this.connection = connection;
    this.channel = channel;
    //@FIXME TODO This is causing the FileReader constructor to be called twice, then Config.json is read twice
    reader = new FileReader(); 
    
    mommaJokes = new ArrayList<>(50);
    pickUpJokes = new ArrayList<>(50);
    
    reader.fileToArrayList("MomJokes.txt", mommaJokes);
    reader.fileToArrayList("FunnyJokes.txt", pickUpJokes);
    
  }
  
  /**
   * On success, the bot will receive phone numbers
   * From other IRC members
   * Pass null in as String to not precede message
   * With "User: "
   * Be sure to check for null return when calling this
   * @param user direct the pickup joke at the user
   * @return a pickup Joke or null
   */
  public String getPhoneNumber(String user) {
    if(pickUpJokes.isEmpty()) {

      connection.msgMasters("OUT OF PHONE NUMBERS!!!");

      //Inform channel
      connection.noticeChan(this.channel, "[***]RELOADING AMMUNITION");

      //Reload the jokes
      reader.fileToArrayList("FunnyJokes.txt", pickUpJokes);
    }
    if(user == null) {
  
      /* Random at 0 because the List was already shuffled */
      return pickUpJokes.remove(0);
    } else {

      return user + ", " + pickUpJokes.remove(0);
    }
  }
  
  /**
   * Replaces "Yo momma" with "User's momma"
   * Pass null in as String to not use replacement
   * Be sure to check for null return when calling this
   * @param user  direct the momma joke at the user
   * @return a momma Joke or null
   */
  public String getMommaJoke(String user) {
    if(mommaJokes.isEmpty()) {
  
      connection.msgMasters("OUT OF MOM JOKES!!!");
   
      //Inform channel
      connection.noticeChan(this.channel, "[***]RELOADING AMMUNITION");

      //Reload the jokes
      reader.fileToArrayList("MomJokes.txt", mommaJokes);
    }
    if(user == null) {
  
      /* Random at 0 because the List was already shuffled */
      return mommaJokes.remove(0);
    } else {
  
      return mommaJokes.remove(0).replaceFirst("Yo", user + "'s");
    }
  } // EOF method
}
