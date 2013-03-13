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
 * @author bullshark
 */
public class JRobo {

  /* Defined Objects */
  private final Networking connection;
  private final FileReader fReader;
  private final Config config;
  private final Jokes jokes;
  private final BotCommand bCmd;

  /* Networking */
  private String first = null;
  private String last = null;
  private String received = null;

  /* JRobo Attributes */
  private final String botN;
  private final String botP;
  private final String botC;
  private boolean isRunning;
  private final char SYMB;

  /* Miscallenous */
  private String user = null;

  public JRobo() {
    fReader = new FileReader();
    config = fReader.getConfig();
    connection = new Networking(config.getNetwork());
    jokes = new Jokes (connection);
    bCmd = new BotCommand(connection, config, this);

    /* Set Attributes/State for this JRobo Object */
    botN = config.getName();
    botP = config.getPass();
    botC = config.getChannel();
    isRunning = false;
    SYMB = config.getCmdSymb();
    //TODO Set identd to JRobo
  }

  private void initiate() {
    //TODO: Use TermColors.java instead
    System.out.println("\u001b[1;44m *** INITIATED *** \u001b[m");

    /*
     * Give the server 4 seconds to identify JRobo
     * Before attempting to join a channel
     */
    try {
      Thread.sleep(4000);
    } catch (InterruptedException ex) {
      Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
    }
    /* Identify to server */
    
    connection.sendln("NICK " + botN);
//    connection.sendln("PASS " + botP);
    connection.sendln("USER JRobo 0 * :Microsoft Exterminator!");
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

  /*
   * Conditional checks happen in order
   * From Most likely to occur
   * To least likely to occur
   *
   * This is done for effiency
   * It will result in less conditional checks
   * Being made
   */
    while( ( received = connection.recieveln()) != null ) {
      this.divideTwo();

      /*
       * A PING was received from the IRC server
       */
      if(first.equals("PING")) { //@TODO Implement with regex
        connection.sendln("PONG " + last);
      }

      /*
       * A message was sent either to the channel
       * Or to the bot; Could be a command
       */
      else if(first.contains("PRIVMSG")) {
        try {
          if(last.charAt(0) == SYMB) {
            bCmd.bCommander(last);
          } else {

            /*
             * Match JRobo in any case typed by another user
             * TODO Should we change this to last.contains(botN) with ignore case
             * TODO Because the bot's name might be something other than JRobo
             */
            if(last.matches("(?i).*JR[0o]b[0o].*")) {
              try {
                user = first.substring(1, first.indexOf('!'));
                connection.msgChannel(botC, jokes.getPhoneNumber(user));
              } catch(StringIndexOutOfBoundsException ex) {
                Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
              }
            }


          }
        } catch(StringIndexOutOfBoundsException ex) {
          Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
        }
      }

      /*
       * A user has joined the channel
       * Excluding the bot joining
       */
      else if(first.contains("JOIN") && last.equals(botC) && !first.contains(botN)) { //@TODO Implement with regex
        //@TODO Decide how to handle this and implement it

        /* Debug Code */
        user = first.substring(1, first.indexOf('!'));
        connection.msgUser(fReader.getMaster(), user + " joined " + botC); //@TODO Implement w/ bot owner or bot owners arr instead
      }

      else if(received.matches("^:\\S+ KICK " + botC + " " + botN + " :.*")) {
        connection.sendln("JOIN " + botC);
        user = first.substring(1, first.indexOf('!'));
        //connection.msgChannel(botC, user + " >>> I'll rip your head off and shit down your neck!");
      } // EOF if-else-if-else...

    } // EOF while

    //@TODO Implement a Networking.killConnection() and call it here

    //@TODO onUserJoin, ctcp version whois user
    System.out.println("\u001b[1;44m *** TERMINATED *** \u001b[m");
  }

  private void divideTwo() {
    try {
      first = received.split(" :", 2)[0];
      last = received.split(" :", 2)[1];
    } catch(ArrayIndexOutOfBoundsException ex) {
      first = "";
      last = "";
    }


  } //@TODO Get List of Users

  public String getFirst() {
    return first;
  }
  
  public String getLast() {
    return last;
  }

  /*
   * One of the few public methods
   * From this class that is public
   * For use from other classes
   *
   * Returns a random boolean
   * 
   * TODO: Use this somewhere
   */
  public boolean getRandomBoolean() {
    return ((((int)(Math.random() * 10)) % 2) == 1);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new JRobo().initiate();
  }
}
