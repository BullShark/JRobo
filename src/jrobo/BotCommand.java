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
public class BotCommand {
  private final Networking connection;
  private final FileReader fReader;
  private final String botC;
  private UrbanDict urban;
  private JRobo jRobo;
  private final char SYMB;

  public BotCommand(Networking connection, FileReader fReader, JRobo jRobo) {
    /* Objects */
    this.connection = connection;
    this.fReader = fReader;
    this.jRobo = jRobo;

    /* Data-types */
    this.SYMB = fReader.getCmdSymb();
    this.botC = fReader.getChan();
  }

  /*
   * This is called when a bot command is received
   *
   * fullCmd includes the SYMB, command, and args
   */
  public void bCommander(String fullCmd) {
    String cmd = getCmd(fullCmd);
    String cmdArgs = getCmdArgs(fullCmd);

    boolean hasArgs = cmdArgs.isEmpty() ? false : true;

    /*
     * Java 7 Switch With Strings!!!!!
     */
    switch(cmd) {
      case "wakeroom": /* Requires no args */
      case "wr":
        wakeRoomHelper();
        break;
      case "re":
        connection.msgChannel(botC, "Almost implemented!");
//        connection.msgChannel(botC, RegexTestHarness.re(cmd, cmd));
        break;
      case "google":
      case "g":
      case "goog":
      case "lmgtfy":
      case "stfw": /* Show The Fucking World */
        /*
         * Puts together a String in the form
         * http://lmgtfy.com/?q=test+a+b+c
         */
        if(!hasArgs) {
          helpMsg(cmd);
        } else {
          String googleUrl = "http://lmgtfy.com/?q=".concat(getFormattedQuery(cmdArgs));
          connection.msgChannel(botC, googleUrl);
        }
        break;
      case "weather":
      case "w":
        /*
         * Put together a String in the form
         * http://www.google.com/ig/api?weather=Mountain+View
         */

//        connection.msgUser("BullShark", weatherUrl);
//        if(!hasArgs) {
//          helpMsg(cmd);
//        } else {
//          connection.msgChannel(botC, new Weather().getSummary(cmdArgs));
//          connection.msgChannel(botC, "^g weather " + cmdArgs);
//        }
        //TODO Parse and return the formatted XML instead
        connection.msgChannel(botC, new Weather().getXml(cmdArgs));
        break;
      case "mum":
      case "m":
        try {
        if(!hasArgs) {
          connection.msgChannel( botC, fReader.getMommaJoke( getRandChanUser() ) );
        } else {
          int temp = cmdArgs.indexOf(' ');
          if(temp != -1) {
            connection.msgChannel( botC, fReader.getMommaJoke( cmdArgs.substring(0, temp) ) );
          } else {
            connection.msgChannel( botC, fReader.getMommaJoke(cmdArgs) );
          }
        }
        } catch(NullPointerException ex) {
            Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
            connection.msgUser(this.fReader.getMaster(), "FIX ^mum; FileReader.java not reading input!!!");
        }
        break;
      case "next":
        connection.msgChannel(botC, "Another satisfied customer, NEXT!!!");
        break;
      case "wolframalpha":
      case "wa":
        connection.msgChannel(botC, "Do I look African to you?!");
        break;
      case "nicks":
      case "names":
      case "n":
        //@TODO This will cause the bot to leave the channel
        //execute NAMES #CHANNEL
        //PART THAT CHANNEL
        //AND COME BACK, UNLESS JOINING THE CHANNEL IS NOT REQUIRED
        //TO GET A LIST OF NICKS/NAMES
        //USE the getUsers() already implemented to help with this
        connection.msgChannel(botC, "You implement it!");
        break;
      case "invite-nick":
      case "in":
        if(!hasArgs || !(cmdArgs.split("\\s++").length > 0) ) { // Min 1 Arg
          //@TODO Replace code with getHelp(cmd); //Overloaded method
          connection.msgChannel(botC, "Usage: " + SYMB + "invite-nick {nick} [# of times]" );
        } else {
          connection.msgChannel(this.botC, "Roger that.");
          String cmdArgsArr[] = cmdArgs.split("\\s++");
          int numInvites = 50; // Default value
          if(cmdArgsArr.length < 1) {
            try{
              Thread.sleep(1500);
              numInvites = Integer.getInteger(cmdArgsArr[1]);
              //TODO Make the below string a variable that is mutable to be set by the XML configuration file
              if(connection.recieveln().contains(":JRobo!~Tux@unaffiliated/robotcow QUIT :Excess Flood")) {
//               this.jRobo. 
              }
            } catch(Exception ex) { //Find out exactly what exceptions are thrown
              Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          for(int x = 0; x < numInvites; x++) {
            connection.sendln("INVITE " + botC + " " + cmdArgsArr[0]);
          }
        }
        break;
      case "invite-channel":
      case "ic":
        if( jRobo.getFirst().startsWith( ":BullShark!~RTFM@gateway/tor-sasl/nanomachine" ) && hasArgs ) {
          String chan = cmdArgs.split(" ")[0], users;
          connection.sendln("PART " + botC + " :BSOD");
          connection.sendln("JOIN :" + chan);
          users = getUsers(chan);
          connection.sendln("PART " + chan + " :BSOD");
          connection.sendln("JOIN :" + botC);
          connection.msgChannel(botC, users);
        } else {
        ; // printHelp(ic);
        }
        connection.msgChannel(botC, "Still being implemented!");
        //@TODO arg nick, number of times to invite
        break;

      case "tinyurl":
      case "tiny":
      case "tu":
        connection.msgChannel(botC, "Still being implemented!");
        //@TODO arg nick, number of times to invite
        break;
      case "raw":
      case "r":
      /* We have received a message from the owner */
        //TODO Make the below string a variable that is mutable to be set by the XML configuration file
        if( jRobo.getFirst().startsWith( ":BullShark!debian-tor@gateway/tor-sasl/nanomachine" ) ) {
          connection.sendln("PRIVMSG " + botC + " :Yes Sir Chief!");
          String rawStr = jRobo.getLast();
          rawStr = rawStr.substring(rawStr.indexOf(' '));
          connection.sendln(rawStr);
          try {
           Thread.sleep(500);
            connection.msgChannel(botC, connection.recieveln());
          } catch (InterruptedException | NullPointerException ex) {
            Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
        break;
      case "urbandict":
      case "ud":
        connection.msgChannel(botC, new UrbanDict(cmdArgs).getFormattedUrbanDef());
        break;
      case "quit":
      case "q":
        connection.msgChannel(botC, "Detenation devices to nuclear reactors! (Zer0 is pressing the "
        + "stupid BUTTOnN so GO OUT OF THIS FUCKING CHANNEL BITCHES!!!)");
        break;
      case "^":
        connection.msgChannel(botC, "Hello iAmerikan");
        break;
      case "list":
      case "l":
        String str = "Available commands: google|g|goog|lmgtfy|stfw <search query>, " +
          "wakeroom|wr, weather|w <location, zip, etc.>, " +
          "urbandict|ud <search query, list|l, help|h [cmd], " +
          "next|n, mum|m [user], invite-channel|ic <channel>, invite-nick {nick} [# of times], " +
          "quit|q"; //@TODO update list for ALL commands
        connection.msgChannel(botC, str);
        break;
      case "help":
      case "h":
        //@TODO man page style usage for help blah
        connection.msgChannel(botC, "You implement it!");
        break;
      default:
        connection.msgChannel(botC, "Unknown command received: " + cmd);
    } // EOF switch

    //@TODO Java Regex TestHarness results!!!!!!!!!!!
    //@TODO Use blogger api to search my blog
    //@TODO Accept raw irc commands from bot owner to be sent by the bot
    //@TODO Search for bots on irc and watch their behavior for ideas such as WifiHelper in #aircrack-ng

  } // EOF function

  /*
   * Puts together a String in the form
   * test+a+b+c
   * From fullCmd
   *
   * Takes a string and manipulates it
   * By removing all starting and ending
   * Whitespace and then
   * Replacing all other whitespace
   * No matter the length of that whitespace
   * With one '+'
   */
  private String getFormattedQuery(String str) {
    return str.replaceAll("\\s++", "+");
  }

  private String getCmdArgs(String fullCmd) {
    //@TODO divded half of the work getFormattedQuery is doing to here
    try {
      return fullCmd.split("\\^\\w++", 2)[1].trim();
    } catch(ArrayIndexOutOfBoundsException ex) {
      Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
      return ""; /* Means no args!!! */
    } // EOF try-catch
  } // EOF function

  private String getCmd(String fullCmd) {
    return fullCmd.substring(1).replaceFirst("\\s.*+", "");
  }

  private String getRandChanUser() {
    /*
     * TODO: getUsers.split() and choose random index
     * rand fn returns number within 0 and len-1 of arr
     * 
     * TODO: Use with ^mum that's supplied no args
     */
    return "ChanServ";
  }

  /*
   * TODO: Return list of users so this code can be reused
   */
  private String getUsers() {
    String received = "", users = "";
    int tries = 2;

    connection.sendln("NAMES " + botC);
    do {
      received = connection.recieveln();
      tries--;
    } while(!received.matches("^:[a-z\\.]++ 353.*") || tries <= 0);
    if(received.matches("^:[a-z\\.]++ 353.*")) {
      try {
        users += received.split(" :")[1].replaceAll("@|\\+|&|~|%", "");
      } catch(ArrayIndexOutOfBoundsException ex) {
        Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
        connection.msgUser(fReader.getMaster(), "Could not get list of users!!!");
      }
    } else {
      connection.msgUser(fReader.getMaster(), "Could not get list of users!!!");
      return null;
    }
    return users;
  }

  private String getUsers(String chan) {
    String received = "", users = "";
    int tries = 2;

    connection.sendln("NAMES " + chan);
    do {
      received = connection.recieveln();
      tries--;
    } while(!received.matches("^:[a-z\\.]++ 366.*") || tries <= 0);
    if(received.matches("^:[a-z\\.]++ 353.*")) {
      try {
        users += received.split(" :")[1].replaceAll("@|\\+|&|~|%", "");
      } catch(ArrayIndexOutOfBoundsException ex) {
        Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
        connection.msgUser(fReader.getMaster(), "Could not get list of users!!!");
      }
    } else {
      connection.msgUser(fReader.getMaster(), "Could not get list of users!!!");
      return null;
    }
    return users;
  }

  private void wakeRoomHelper() {
    String users = getUsers();
    if(users != null) {
      connection.msgChannel(botC, users);
    } else {
      connection.msgChannel(botC, "Failed to get a list of users; Try again or notify the developer!");
    }
  }

  private void helpMsg(String cmd) {
    connection.msgChannel(botC, "Invalid usage of command: " + cmd);

    //TODO help string for each command
  }
} // EOF class