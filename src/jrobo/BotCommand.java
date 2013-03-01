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

//TODO javadoc all
// http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html
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
  private String cmd;
  private String cmdArgs;
  private boolean hasArgs;

  public BotCommand(Networking connection, FileReader fReader, JRobo jRobo) {
    /* Objects */
    this.connection = connection;
    this.fReader = fReader;
    this.jRobo = jRobo;

    /* Data-types */
    this.SYMB = fReader.getCmdSymb();
    this.botC = fReader.getChan();

    /* Cmds */
    cmd = "";
    cmdArgs = "";
    hasArgs = false;
  }

  /*
   * This is called when a bot command is received
   *
   * fullCmd includes the SYMB, command, and args
   */
  public void bCommander(String fullCmd) {
    cmd = getCmd(fullCmd);
    cmdArgs = getCmdArgs(fullCmd);
    hasArgs = cmdArgs.isEmpty() ? false : true;

    /*
     * Java 7 Switch With Strings!!!!!
     */
    switch(cmd) {
      case "wakeroom": /* Requires no args */
      case "wr":
        wakeRoomHelper();
        break;
      case "google":
      case "g":
      case "lmgtfy":
      case "stfw": /* Show The Fucking World */
        googleHelper();
        break;
      case "weather":
      case "w":
        weatherHelper();
        break;
      case "mum":
      case "m":
        mumHelper();
        break;
      case "next":
        nextHelper();
        break;
      case "invite-nick":
      case "in":
        inviteNickHelper();
        break;
      case "invite-channel":
      case "ic":
        inviteChannelHelper();
        break;
      case "raw":
      case "r":
        rawHelper();
        break;
      case "urbandict":
      case "ud":
        urbanDictionaryHelper();
        break;
      case "quit":
      case "q":
        quitHelper();
        break;
      case "^":
      case "^^":
      case "^.^":
        doNothingHelper();
        break;
      case "list":
      case "l":
        listHelper();
        break;
      case "help":
      case "h":
        helpHelper();
        break;
      case "pirate":
        pirateHelper();
        break;
      case "isup":
        isUpHelper();
      case "version":
        versionHelper();
        break;
      default:
        unknownCmdHelper();
    //@TODO Accept raw irc commands from bot owner to be sent by the bot
    //@TODO Search for bots on irc and watch their behavior for ideas such as WifiHelper in #aircrack-ng
    } // EOF switch
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
   * 
   * TODO JavaDocs
   */
  private String getFormattedQuery(String str) {
    return str.replaceAll("\\s++", "+");
  }

  /*
   * TODO Javadocs
   */
  private String getCmdArgs(String fullCmd) {
    //@TODO divded half of the work getFormattedQuery is doing to here
    try {
      return fullCmd.split("\\" + this.SYMB +"\\w++", 2)[1].trim();
    } catch(ArrayIndexOutOfBoundsException ex) {
      Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
      return ""; /* Means no args!!! */
    } // EOF try-catch
  } // EOF function

  /*
   * TODO JavaDocs
   */
  private String getCmd(String fullCmd) {
    return fullCmd.substring(1).replaceFirst("\\s.*+", "");
  }

  /*
   * TODO JavaDocs
   */
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
   * TODO JavaDocs
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

  /*
   * TODO Comment me
   */
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

  /*****************************************************************************
   * Helper methods
   */
  private void wakeRoomHelper() {
    String users = getUsers();
    if(users != null) {
      connection.msgChannel(botC, users);
    } else {
      connection.msgChannel(botC, "Failed to get a list of users; Try again or notify the developer!");
    }
  }

  /*
   * Puts together a String in the form
   * http://lmgtfy.com/?q=test+a+b+c
   */
  private void googleHelper() {
    if(!hasArgs) {
      helpWrapper(cmd);
    } else {
      String googleUrl = "http://lmgtfy.com/?q=".concat(getFormattedQuery(cmdArgs));
      connection.msgChannel(botC, googleUrl);
    }
  }

  private void isUpHelper() {
    if(!hasArgs) {
      helpWrapper(cmd);
    } else {
      String testUrl = "http://www.downforeveryoneorjustme.com/".concat(getFormattedQuery(cmdArgs));
//      connection.msg
    }
  }

  private void weatherHelper() {
  /*
   * Put together a String in the form
   * http://www.google.com/ig/api?weather=Mountain+View
   */

//        connection.msgUser("BullShark", weatherUrl);
//        if(!hasArgs) {
//          helpWrapper(cmd);
//        } else {
//          connection.msgChannel(botC, new Weather().getSummary(cmdArgs));
//          connection.msgChannel(botC, "^g weather " + cmdArgs);
//        }
  //TODO Parse and return the formatted JSON or XML instead
//        connection.msgChannel(botC, new Weather().getXml(cmdArgs));
//TODO Re-implement all the use wunderground.net
}

  private void mumHelper() {
      
    Jokes joke =new Jokes(this.connection);
      
    try {
      if(!hasArgs) {
        connection.msgChannel(botC, joke.getMommaJoke(getRandChanUser()));
      } else {
        int temp = cmdArgs.indexOf(' ');
        if(temp != -1) {
          connection.msgChannel(botC, joke.getMommaJoke(cmdArgs.substring(0, temp)));
        } else {
          connection.msgChannel(botC , joke.getMommaJoke(cmdArgs) );
        }
      }
    } catch(NullPointerException ex) {
      Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
      connection.msgUser(fReader.getMaster(), "FIX ^mum; FileReader.java not reading input!!!");
    }
  }

  private void nextHelper() {
    connection.msgChannel(botC, "Another satisfied customer, NEXT!!!");
  }

  private void inviteNickHelper() {
    if(true) { return; } //TODO Fix this method

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
  }

  private void inviteChannelHelper() {
    if(true) { return; } //TODO Fix this method

    //TODO Implement and use FileReader.getNickAndHost() instead
    if(jRobo.getFirst().startsWith(fReader.getMaster()) && hasArgs ) {
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
  }

  private void rawHelper() {
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
  }

  private void urbanDictionaryHelper() {
    connection.msgChannel(botC, new UrbanDict(cmdArgs).getFormattedUrbanDef(true, 3), true, MircColors.BOLD);
  }

  private void quitHelper() {
    connection.msgChannel(botC, "Detenation devices to nuclear reactors! (Zer0 is pressing the "
    + "stupid BUTTOnN so GO OUT OF THIS FUCKING CHANNEL BITCHES!!!)");
  }

  /*
   * Does nothing
   */
  private void doNothingHelper() {
  }

  private void pirateHelper() {
    connection.msgChannel(botC, new PirateBay(cmdArgs).getFormattedResult(true));
  }

  /*****************************************************************************
   * Help messages
   */
  private void listHelper() {
    //TODO Color me with MircColors
    String str = "Available commands: google|g|lmgtfy|stfw <search query>, " +
      "wakeroom|wr, weather|w <location, zip, etc.>, " +
      "urbandict|ud <search query, list|l, raw|r <raw irc line> help|h [cmd], " +
      "next|n, mum|m [user], invite-channel|ic <channel>, " +
      "invite-nick|in <nick> [# of times], pirate [-s|-l|-d] <search query>, " +
      "version, quit|q"; //@TODO update list for ALL commands
    connection.msgChannel(botC, str);
  }

  private void unknownCmdHelper() {
    connection.msgChannel(botC, "Unknown command received: " + cmd);
  }

  /*
   * Wrapper Help command messages
   */
  private void helpWrapper(String cmd) {
    //TODO help string for each command
    connection.msgChannel(botC, "Invalid usage of command: " + cmd);
  }

  private void helpHelper() {
    //@TODO man page style usage for help blah
    connection.msgChannel(botC, "You implement it!");
  }

  private void versionHelper() {
    connection.msgChannel(botC,
      MircColors.BOLD + MircColors.CYAN + "JRobo" +
      MircColors.NORMAL + MircColors.BOLD + MircColors.WHITE + " - " +
      MircColors.NORMAL + MircColors.BOLD + MircColors.GREEN + "https://github.com/BullShark/JRobo");
  }
} // EOF class
