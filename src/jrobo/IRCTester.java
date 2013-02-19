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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chris
 */
public class IRCTester {

  /* Defined Objects */
  private Networking connection = null;
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

  private int randomBoolean = 0;
  private final FileReader fReader;

  public IRCTester() {
    connection = new Networking();

    fReader = new FileReader(connection);

    /* Set Attributes/State for this JRobo Object */
//    botN = "JavaBeans";
//    botP = "haX0rzMe";
    botN = "ProtoAnazlyer";
    botP = "";
    botC = "#blackhats";

  }

  public void initiate() {
    System.out.println("\u001b[1;44m *** INITIATED *** \u001b[m");

    /* Identify to server */
    connection.sendln("PASS " + botP);
    connection.sendln("NICK " + botN);
    connection.sendln("USER RTFM 0 * :Microsoft Exterminator!");

    /*
     * Give the server 4 seconds to identify JRobo
     * Before attempting to join a channel
     */
    try {
      Thread.sleep(10000);
    } catch (InterruptedException ex) {
      Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
    }

    connection.sendln("JOIN " + botC);

    while( (received = connection.recieveln()) != null ) {

      this.divideTwo();

      /* Code to Test JRobo */


      /* We have received a message from the owner */
      if( first.startsWith( ":BullShark!lulz@windfyre.net" ) &&
          ( last.charAt(0) == SYMB ) ) {

        connection.sendln("PRIVMSG " + botC + " :Yes Sir Chief!");
        connection.sendln(last.substring(1));
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

  public void divideTwo() {
    try {
      first = received.split(" :", 2)[0];
      last = received.split(" :", 2)[1];
    } catch(ArrayIndexOutOfBoundsException ex) {
      Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
      first = "";
      last = "";
    }

  } //@TODO Get List of Users
  public void getSmartAss() {
    /* Code to Test JRobo */
    try {
      Thread.sleep(30000);
    } catch (InterruptedException ex) {
      Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
    }

    randomBoolean = (int) (Math.random() * 10 % 2);

    if(randomBoolean == 1) {
      connection.sendln("PRIVMSG " + botC + " :" + fReader.getMommaJoke(null));
    } else {
      connection.sendln("PRIVMSG " + botC + " :" + fReader.getPhoneNumber(null));
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new IRCTester().initiate();
  }
}