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

package jrobo.expectusafterlun.ch;

import java.util.ArrayList;

/**
 * This class is the new home for the joke functions from FileReader
 * @author BinaryStroke {@literal <binarystroke@null.net>}
 * @since 2013-02-23
 */
public class Jokes {
    
  /* Standard Java API Classes */
  private ArrayList<String> pickUpJokes = null, mommaJokes = null;
  private final String CHANNEL ;
    
   /* User-defined Classes */
  private final Networking CONN;
  private final FileReader READER;
  
  /**
   * 
   * @param JROBO Provides getters for the Networking, Config/Channel, and FileReader
   * @seealso CONN The Networking connection used by JRobo
   * @seealso CHANNEL The IRC CHANNEL that JRobo resides
   * @seealso READER The FileReader used for Jokes and the Config file
   */
  public Jokes (final JRobo JROBO) {
    
    this.CONN = JROBO.getCONN();
    this.CHANNEL = JROBO.getCONFIG().getChannel();

    READER = JROBO.getREADER();
    
    mommaJokes = new ArrayList<>(50);
    pickUpJokes = new ArrayList<>(50);
    
    READER.fileToArrayList("MomJokes.txt", mommaJokes);
    READER.fileToArrayList("FunnyJokes.txt", pickUpJokes);
    
  }
  
  /**
   * On success, the bot will receive phone numbers
   * From other IRC members
   * Pass null in as String to not precede message
   * With "User: "
   * Be sure to check for null return when calling this
   * @param USER direct the pickup joke at the USER
   * @return a pickup Joke or null
   */
  public String getPhoneNumber(final String USER) {
    if(pickUpJokes.isEmpty()) {

      CONN.msgMasters("OUT OF PHONE NUMBERS!!!");

      //Inform CHANNEL
      CONN.noticeChan(this.CHANNEL, "[***]RELOADING AMMUNITION");

      //Reload the jokes
      READER.fileToArrayList("FunnyJokes.txt", pickUpJokes);
    }
    if(USER == null) {
  
      /* Random at 0 because the List was already shuffled */
      return pickUpJokes.remove(0);
    } else {

      return USER + ", " + pickUpJokes.remove(0);
    }
  }
  
  /**
   * Replaces "Yo momma" with "User's momma"
   * Pass null in as String to not use replacement
   * Be sure to check for null return when calling this
   * @param USER  direct the momma joke at the USER
   * @return a momma Joke or null
   */
  public String getMommaJoke(final String USER) {
    if(mommaJokes.isEmpty()) {
  
      CONN.msgMasters("OUT OF MOM JOKES!!!");
   
      //Inform CHANNEL
      CONN.noticeChan(this.CHANNEL, "[***]RELOADING AMMUNITION");

      //Reload the jokes
      READER.fileToArrayList("MomJokes.txt", mommaJokes);
    }
    if(USER == null) {
  
      /* Random at 0 because the List was already shuffled */
      return mommaJokes.remove(0);
    } else {
  
      return mommaJokes.remove(0).replaceFirst("Yo", USER + "'s");
    }
  } // EOF method
}
