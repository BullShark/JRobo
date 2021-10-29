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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A tester bot to help learn the IRC protocol and test commands sent to the server
 * 
 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
 */
public class IRCTester {

  /* Defined Objects */
  private Networking connection = null;
  private Config config = null;
  private Jokes jokes = null;
  private BotCommand bCmd = null;

  /* Networking */
  private String first = null;
  private String last = null;
  private String received = null;

  /* JRobo Attributes */
  private String botN = null;
  private String botP = null;
  private String botC = null;
  private final char SYMB = '$';

  private int randomBoolean;
  private final FileReader FR;

  /**
   * Initializes the FileReasder, Config, connection, jokes, bot name, bot pass, bot channel, and a random boolean used to make the jokes random
   */
  public IRCTester() {
    this.FR = new FileReader();
    this.config = this.FR.getConfig();
    this.connection = new Networking(config);
    this.jokes = new Jokes(connection,config.getChannel(), FR);

    /* Set Attributes/State for this JRobo Object */
    this.botN = "ProtoAnazlyer";
    this.botP = "";
    this.botC = "#blackhats";

    /* Misc */
    randomBoolean = 0;
  }

  /**
   * The beginning of JRobo's execution starts here
   */
  public void initiate() {
    System.out.println("\u001b[1;44m *** INITIATED *** \u001b[m");

    /* Identify to server */
    connection.sendln("NICK " + botN);
//    connection.sendln("PASS " + botP);
    connection.sendln("USER RTFM 0 * :Microsoft Exterminator!");

//    /*
//     * Give the server 4 seconds to identify JRobo
//     * Before attempting to join a channel
//     */
//    try {
//      Thread.sleep(10000);
//    } catch (InterruptedException ex) {
//      Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
//    }
    /*
     * Wait for server message:
     * 001 JRobo :Welcome to the IRC Network
     * Before attempting to join a channel
     */
    while(( received = connection.recieveln()) != null ) {
      this.divideTwo();

      if(first.equals("PING")) {
        connection.sendln("PONG " + last);
      }
      
      if(first.contains("001")) {
        break;
      }
    }

    connection.sendln("JOIN " + botC);

    while( (received = connection.recieveln()) != null ) {

      this.divideTwo();

      /* Code to Test JRobo */


      /* We have received a message from the owner */
      if( first.startsWith( ":BullShark" ) &&
          ( last.charAt(0) == SYMB ) ) {
        connection.msgChannel(botC, MircColors.RED + "Yes Sir Chief!" + MircColors.NORMAL);
      }
/* Handle lines like these
 *
 * Received:       PING :hubbard.freenode.net
 * Received:       :RobotCow!~User@unaffiliated/robotcow PRIVMSG ##sushi :^command
 * Received:       :RobotCow!~User@unaffiliated/robotcow PRIVMSG ProtoAnalyzer :cmd
 * Received:       :hubbard.freenode.net 366 ProtoAnalyzer ##sushi :End of /NAMES list.
 */
    if(first.equals("PING")) {
      connection.sendln("PONG :" + last);
    }

  }

    System.out.println("\u001b[1;44m *** TERMINATED *** \u001b[m");
  }

  /**
   * Parse the input into two, first and last
   */
  public void divideTwo() {
    try {
      first = received.split(" :", 2)[0];
      last = received.split(" :", 2)[1];
    } catch(ArrayIndexOutOfBoundsException ex) {
      Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
      first = "";
      last = "";
    }

  }

  /**
   * Retrieves a random Yo momma joke and sends it to the channel
   */
  public void getMumJoke() {
    /* Code to Test JRobo */
    try {
      Thread.sleep(30000);
    } catch (InterruptedException ex) {
      Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
    }

    randomBoolean = (int) (Math.random() * 10 % 2);

    if(randomBoolean == 1) {
      connection.sendln("PRIVMSG " + botC + " :" + jokes.getMommaJoke(null));
    } else {
      connection.sendln("PRIVMSG " + botC + " :" + jokes.getPhoneNumber(null));
    }
  }

  /**
   * @param args the command line arguments and none are expected
   */
  public static void main(String[] args) {
    new IRCTester().initiate();
  }
}
