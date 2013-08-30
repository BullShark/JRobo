/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
 * Copyright (C) <2013> <BinaryStroke>
 * Copyright (C) <2013> <Tyler Pollard>
 * Copyright (C) <2013> <Muhammad Sajid>
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
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author bullshark
 */
public class BotCommand {

  private final Networking connection;
  private final Config config;
  private JRobo jRobo;
  private String user;
  private String bombHolder;
  private String cmd;
  private String cmdArgs;
  private boolean hasArgs;
  private ListColors lc;
  private boolean threadCreated;
  private boolean bombActive;
  private boolean[] wire = new boolean[3];


  /**
   *
   * @param connection
   * @param reader
   * @param jRobo
   */
  public BotCommand(Networking connection, Config config, JRobo jRobo) {
    /* Objects */
    this.connection = connection;
    this.config = config;
    this.jRobo = jRobo;

    /* Cmds */
    cmd = "";
    cmdArgs = "";
    hasArgs = false;

    /* Misc */
    lc = new ListColors();
    threadCreated = false;

  }

  /*
   * This is called when a bot command is received
   *
   * user is the user who sent the command
   * fullCmd includes the SYMB, command, and args
   */
  public void bCommander(String user, String fullCmd) {
    this.user = user;
    cmd = getCmd(fullCmd);
    cmdArgs = getCmdArgs(fullCmd);
    hasArgs = cmdArgs.isEmpty() ? false : true;

    switch (cmd) {
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
      case "goto":
      case "join":
        moveToChannelHelper();
      case "greet":
        greetHelper();
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
      case ".":
      case "..":
      case "-.":
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
        break;
      case "version":
        versionHelper();
        break;
      case "driveby":
      case "db":
        driveBy();
        break;
      case "bomb":
        bomb();
        break;
      case "pass":
        pass();
        break;
      case "defuse":
        defuse();
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
      return fullCmd.split("\\" + config.getCmdSymb() + "[a-zA-Z_0-9\\-]++", 2)[1].trim();
    } catch (ArrayIndexOutOfBoundsException ex) {
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
    String[] usersList;

    usersList = getUsers().split("\\s++");

    if (usersList != null) {
      //TODO Needs testing
      return usersList[(int) (Math.random() * usersList.length + 1)];
    } else {
      return "ChanServ";
    }
    /*
     public boolean getRandomBoolean() {
     return ((((int)(Math.random() * 10)) % 2) == 1);
     }
     */
  }

  /**
   * Wrapper
   *
   * @return
   */
  private String getUsers() {
    return getUsers(config.getChannel());
  }

  /**
   *
   * @param chan
   * @return
   */
  private String getUsers(String chan) {
    //TODO This method needs testing. It might be broke.
    String received = "", users = "";
    String first = "", last = "";
    int tries = 8;

    connection.sendln("NAMES " + chan);
    do {
      received = connection.recieveln();
      try {
        first = received.split(" :", 2)[0];
        last = received.split(" :", 2)[1];
      } catch (ArrayIndexOutOfBoundsException ex) {
        first = "";
        last = "";
      }
      if (first.contains("353")) {
        try {
          users += last.replaceAll("@|\\+|&|~|%", "");
        } catch (ArrayIndexOutOfBoundsException ex) {
          Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
      } else if (first.equals("PING")) {
        connection.sendln("PONG " + last);
      }
      tries--;
    } while (tries > 0 && !first.contains("366"));

    if (users.equals("")) {
      connection.msgMasters("Could not get list of users!!!");
    }

    return users;
  }
  
  /*
   * Helper methods
   */
  private void wakeRoomHelper() {
    String users = getUsers();
    if (users != null) {
      connection.msgChannel(config.getChannel(), users);
    } else {
      connection.msgChannel(config.getChannel(), "Failed to get a list of users; Try again or notify the developer!");
    }
  }
  
  /*
   * Starts timer for bomb.
   * Sets an active wire
   * Prints explosion and kicks user holding at [20] seconds
   */
  public void bomb() {
    bombHolder = user;
    connection.msgChannel(config.getChannel(), MircColors.BOLD + bombHolder + MircColors.WHITE + " started the bomb!!!");
    connection.msgChannel(config.getChannel(), MircColors.WHITE + "You can pass it to another user with >pass [nick].");
    connection.msgChannel(config.getChannel(), MircColors.WHITE + "You can attempt to defuse with >defuse [" + MircColors.RED + "R" + MircColors.GREEN + "G" + MircColors.BLUE + "B" + MircColors.WHITE + "-color].");
    bombActive = true;
    wire[0] = false;
    wire[1] = false;
    wire[2] = false;
    wire[(int)(3.0 * Math.random())] = true;
    final Timer timer;
    
    timer = new Timer();
    
    class BombTask extends TimerTask {
    public void run() {
      if (!bombActive) {
        timer.cancel();
        //connection.msgChannel(config.getChannel(), "Bomb defused!!!");
      } else {
      connection.msgChannel(config.getChannel(), MircColors.BROWN + "          ,_=~~:-" + MircColors.YELLOW + ")" + MircColors.BROWN + ",,          ");
      connection.msgChannel(config.getChannel(), MircColors.YELLOW + "      (" + MircColors.BROWN + "==?,::,:::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "=:=" + MircColors.YELLOW + ")       ");
      connection.msgChannel(config.getChannel(), MircColors.BROWN + "     ?:=" + MircColors.YELLOW + "(" + MircColors.BROWN + ",~:::::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "~+=:I" + MircColors.YELLOW + ")     ");
      connection.msgChannel(config.getChannel(), MircColors.YELLOW + "   (" + MircColors.BROWN + "=:" + MircColors.YELLOW + "(" + MircColors.BROWN + ",=:~++" + MircColors.YELLOW + "=:" + MircColors.BROWN + "::~,:~:" + MircColors.YELLOW + "))" + MircColors.BROWN + "~~~." + MircColors.YELLOW + ")    ");
      connection.msgChannel(config.getChannel(), MircColors.YELLOW +"    (" + MircColors.BROWN + "+~" + MircColors.YELLOW + "(" + MircColors.BROWN + ",:" + MircColors.YELLOW + "(==:" + MircColors.BROWN + ":~~+~~:,$,I?" + MircColors.YELLOW + "))   ");
      connection.msgChannel(config.getChannel(), MircColors.BROWN + "    ``  ```" + MircColors.YELLOW + "~~" + MircColors.BROWN + "?" + MircColors.YELLOW + "~=" + MircColors.BROWN + "$.~~~  ``     ");
      connection.msgChannel(config.getChannel(), MircColors.YELLOW + "             :" + MircColors.BROWN + "S" + MircColors.YELLOW + "Z=             ");
      connection.msgChannel(config.getChannel(), MircColors.YELLOW + "         .-~~" + MircColors.BROWN + "?=:=" + MircColors.YELLOW + "``~-_        ");
      connection.msgChannel(config.getChannel(), MircColors.YELLOW + "         `--=~=+~++=~`        ");
      connection.msgChannel(config.getChannel(), MircColors.YELLOW + "             ." + MircColors.BROWN + "~" + MircColors.YELLOW + ":" + MircColors.BROWN + "~             ");
      connection.msgChannel(config.getChannel(), MircColors.BROWN + "         ((.(\\.!/.):?)        ");
      connection.msgChannel(config.getChannel(), MircColors.DARK_GREEN + "   .?~:?.?7::,::::+,,~+~=:... ");
      connection.kickFromChannel(config.getChannel(), user + " KABOOM!!!");
      bombActive = false;
      timer.cancel();
      }
    }
    }
    timer.schedule(new BombTask(), 20000);
  }
  
  /*
   * Simply passes the bomb to another user
   */
  private void pass() {
    String users = getUsers();
    if (users.contains(cmdArgs) && user.equals(bombHolder) && bombActive == true) {
      bombHolder = cmdArgs;
      connection.msgChannel(config.getChannel(), "The Bomb has been passed to " + bombHolder + "!!!");
    } else {
      connection.msgChannel(config.getChannel(), "Invalid.");
    }
  }
  
  /*
   * This is the defuse method, refers to a global boolean array of wires.
   * Ative wire is set to true in bomb() function.
   */
  public void defuse() {
    if (bombActive && user.equals(bombHolder))  {
      switch (cmdArgs){
        case "red":
          if (wire[0]==true) {
            connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb defused.");
            bombActive = false;
          } else {
            connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb still active.");
          }
          break;
        case "green":
          if (wire[1]==true) {
            connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb defused.");
            bombActive = false;
          } else {
            connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb still active.");
          }
          break;
        case "blue":
          if (wire[2]==true) {
            connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb defused.");
            bombActive = false;
          } else {
            connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb still active.");
          }
          break;
      }
    } else {
      connection.msgChannel(config.getChannel(), "Invalid.");
    }
  }
  
  /**
   * Does driveby lol's
   * Parts and returns as well as try-sleeps in loop to avoid flooding.
   */
  private void driveBy() {
    if (cmdArgs.length() < 2 || !cmdArgs.startsWith("#")) {
        connection.msgChannel(config.getChannel(), "Invalid channel: " + cmdArgs);
        return;
    }
    config.setBaseChan(config.getChannel()); // The channel JRobo will return to
    connection.moveToChannel(config.getChannel(), cmdArgs);
    for (int i = 0; i < 25; i++) {
      connection.moveToChannel(config.getChannel(), cmdArgs);
      for (int z = 0; z < 5; z++) {
        connection.msgChannel(config.getChannel(), "lol");
        try {
        Thread.sleep(1000);
      if (connection.recieveln().contains(":the_derp_knight!~JRobo@d-24-245-107-185.cpe.metrocast.net QUIT :Excess Flood")) {
        break;
        }
      } catch (Exception ex) { //Find out exactly what exceptions are thrown
          //Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      connection.moveToChannel(cmdArgs, config.getBaseChan());
      try {
        Thread.sleep(2500);
      if (connection.recieveln().contains(":the_derp_knight!~JRobo@d-24-245-107-185.cpe.metrocast.net QUIT :Excess Flood")) {
        break;
        }
      } catch (Exception ex) { //Find out exactly what exceptions are thrown
          //Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // If not in basechannel he will return to Basechannel
    if (config.getChannel() != config.getBaseChan()) {
      connection.moveToChannel(cmdArgs, config.getBaseChan());
    }
  }
  
  
  /**
   * Puts together a String in the form http://lmgtfy.com/?q=test+a+b+c
   */
  private void googleHelper() {
    if (!hasArgs) {
      helpWrapper(cmd);
    } else {
      String googleUrl = "http://lmgtfy.com/?q=".concat(getFormattedQuery(cmdArgs));
      connection.msgChannel(config.getChannel(), googleUrl);
    }
  }

  private void isUpHelper() {
    if (!hasArgs) {
      helpWrapper(cmd);
    } else {
      connection.msgChannel(config.getChannel(), new DownForEveryone().isUp(getFormattedQuery(cmdArgs), true));
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

    Jokes joke = new Jokes(this.connection, config.getChannel());

    try {
      if (!hasArgs) {
        connection.msgChannel(config.getChannel(), joke.getMommaJoke(getRandChanUser()));
      } else {
        int temp = cmdArgs.indexOf(' ');
        if (temp != -1) {
          connection.msgChannel(config.getChannel(), joke.getMommaJoke(cmdArgs.substring(0, temp)));
        } else {
          connection.msgChannel(config.getChannel(), joke.getMommaJoke(cmdArgs));
        }
      }
    } catch (NullPointerException ex) {
      Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);

      //Inform masters in PM
      connection.msgMasters("FIX ^mum; FileReader.java not reading input!!!");
    }
  }

  private void nextHelper() {
    connection.msgChannel(config.getChannel(), "Another satisfied customer, NEXT!!!");
  }

  private void inviteNickHelper() {
    if (true) {
      return;
    } //TODO Fix this method

    if (!hasArgs || !(cmdArgs.split("\\s++").length > 0)) { // Min 1 Arg
      //@TODO Replace code with getHelp(cmd); //Overloaded method
      connection.msgChannel(config.getChannel(), "Usage: " + config.getCmdSymb() + "invite-nick {nick} [# of times]");
    } else {
      connection.msgChannel(config.getChannel(), "Roger that.");
      String cmdArgsArr[] = cmdArgs.split("\\s++");
      int numInvites = 50; // Default value
      if (cmdArgsArr.length < 1) {
        try {
          Thread.sleep(1500);
          numInvites = Integer.getInteger(cmdArgsArr[1]);
          //TODO replace with FileReader.getMaster()
          if (connection.recieveln().contains(":JRobo!~Tux@unaffiliated/robotcow QUIT :Excess Flood")) {
            //               this.jRobo. 
          }
        } catch (Exception ex) { //Find out exactly what exceptions are thrown
          Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      for (int x = 0; x < numInvites; x++) {
        connection.sendln("INVITE " + config.getChannel() + " " + cmdArgsArr[0]);
      }
    }
  }

  private void inviteChannelHelper() {
    String[] userArr;
    if (!hasArgs) {
//TODO      helpWrapper(cmd);
      return;
    }

    if (threadCreated) {
      connection.msgChannel(config.getChannel(), "The invite thread is still active.");
      return;
    }
    
    
    //TODO      exclude duplicates from useray
    if (cmdArgs.contains(" ")) {
      final String[] chansArr = cmdArgs.split("\\s++");
      // Channel must begin with a # and be at least two characters long
      for (int i = 0; i < chansArr.length; i++){
        if (chansArr[i].length() < 2 || !chansArr[i].startsWith("#")){
          connection.msgChannel(config.getChannel(), "Invalid channel: " + chansArr[i]);
          return;  
        }
      }
      
      config.setBaseChan(config.getChannel()); // The channel JRobo will return to
      String usersList = "";
      for (int i = 0; i < chansArr.length; i++){
        connection.moveToChannel(config.getChannel(), chansArr[i]);
        if (config.getChannel().equals(config.getBaseChan())) { 
          break; 
        }
        usersList += getUsers();
      }
      userArr = usersList.split("\\s");
      connection.moveToChannel(chansArr[chansArr.length-1], config.getBaseChan());
    }
    
    //The command does not contain more than one channel argument
    else {
          // Channel must begin with a # and be at least two characters long
      if (cmdArgs.length() < 2 || !cmdArgs.startsWith("#")) {
        connection.msgChannel(config.getChannel(), "Invalid channel: " + cmdArgs);
        return;
      }
      config.setBaseChan(config.getChannel()); // The channel JRobo will return to
      connection.moveToChannel(config.getChannel(), cmdArgs);
      userArr = getUsers().split("\\s");
      connection.moveToChannel(cmdArgs, config.getBaseChan());
     
    }
    //Statement required for Build (currently is workaround)
    final String[] user2Arr = userArr;
    
    for (int i=0; i<userArr.length; i++){
      System.out.println("userArray: " + userArr[i]);
    }
    
    // Checking if ChanServ has opped JRobo
    String first, last, received;
    for (int tries = 4;;) {
      received = connection.recieveln();
      try {
        first = received.split(" :", 2)[0];
        last = received.split(" :", 2)[1];
      } catch (ArrayIndexOutOfBoundsException ex) {
        first = "";
        last = "";
      }
      if (first.equals("PING")) {
        connection.sendln("PONG " + last);
      } else if (received.equals(":ChanServ!ChanServ@services. MODE " + config.getChannel() + " +o " + config.getName())
              || tries < 0) {
        break;
      }
      tries--;
    }

    Thread inviteT = new Thread() {
      public void run() {

        // Prevent multiple threads from being created
        threadCreated = true;

        for (String user : user2Arr) {
          try {
            Thread.sleep(35000); //TODO Delay set by last command arg?
          } catch (InterruptedException ex) {
            Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
          }
          connection.sendln("INVITE " + user + " " + config.getChannel());
        }
        threadCreated = false;
      }
    };
    //Remove In a minute -projektile
    inviteT.start();

    
//TODO Implement and use FileReader.getNickAndHost() instead
//FIXME check all masters for-each loop    if(jRobo.getFirst().startsWith(config.getMasters()[0]) && hasArgs ) {
//Use for multiple channels, array    String[] channels = this.cmdArgs.split("\\s++");
//    if(channels.length )
  }
  
  private void rawHelper() {
    /* We have received a message from the owner */
    //TODO Make the below string a variable that is mutable to be set by the XML configuration file
    if (jRobo.getFirst().startsWith(":BullShark!debian-tor@gateway/tor-sasl/nanomachine")) {
      connection.sendln("PRIVMSG " + config.getChannel() + " :Yes Sir Chief!");
      String rawStr = jRobo.getLast();
      rawStr = rawStr.substring(rawStr.indexOf(' '));
      connection.sendln(rawStr);
      try {
        Thread.sleep(500);
        connection.msgChannel(config.getChannel(), connection.recieveln());


      } catch (InterruptedException | NullPointerException ex) {
        Logger.getLogger(BotCommand.class
                .getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private void urbanDictionaryHelper() {
    connection.msgChannel(config.getChannel(), new UrbanDict(cmdArgs).getFormattedUrbanDef(true, 3), true, MircColors.BOLD);
  }

  private void quitHelper() {
    connection.msgChannel(config.getChannel(), "Detenation devices to nuclear reactors! (Zer0 is pressing the "
            + "stupid BUTTOnN so GO OUT OF THIS FUCKING CHANNEL BITCHES!!!)");
  }

  /*
   * Does nothing
   */
  private void doNothingHelper() {
  }

  private void pirateHelper() {
    connection.msgChannel(config.getChannel(), new PirateBay(cmdArgs).getFormattedResult(true));
  }

  /**
   * ***************************************************************************
   * Help messages
   */
  private void listHelper() {
    /*
     String str = "Available commands: google|g|lmgtfy|stfw <search query>, " +
     "wakeroom|wr, weather|w <location, zip, etc.>, " +
     "urbandict|ud <search query, list|l, raw|r <raw irc line> help|h [cmd], " +
     "next|n, mum|m [user], invite-channel|ic <channel>, " +
     "invite-nick|in <nick> [# of times], pirate [-s|-l|-d] <search query>, " +
     "isup <url>, version, quit|q"; //@TODO update list for ALL commands
     */

    String noColorStr = "Available commands: google|g|lmgtfy|stfw <search query>, "
            + "wakeroom|wr, weather|w <location, zip, etc.>, "
            + "urbandict|ud <search query, list|l, raw|r <raw irc line>, help|h [cmd], "
            + "next|n, mum|m [user], invite-channel|ic <channel>, "
            + "invite-nick|in <nick> [# of times], pirate [-s|-l|-d] <search query>, "
            + "isup <url>, version, quit|q"; //@TODO update list for ALL commands

    /*
     * GREEN = dark color
     * CYAN = light color
     * 
     * cmds|alias = dark
     * flags = dark
     * args = light
     * special characters = no bold, no color
     * such as [] <> , ...
     */
    String colorStr = lc.attributesSynopsisLine(
            lc.colorToken("Available commands: ", MircColors.BOLD)
            + lc.colorToken("google|g|lmgtfy|stfw ", MircColors.GREEN)
            + lc.colorToken("<search query>, ", MircColors.CYAN)
            + lc.colorToken("wakeroom|wr, ", MircColors.GREEN)
            + lc.colorToken("weather|w ", MircColors.GREEN)
            + lc.colorToken("<location, zip, etc.>, ", MircColors.CYAN)
            + lc.colorToken("urbandict|ud ", MircColors.GREEN)
            + lc.colorToken("<search query>, ", MircColors.CYAN)
            + lc.colorToken("list|l, ", MircColors.GREEN)
            + lc.colorToken("raw|r ", MircColors.GREEN)
            + lc.colorToken("<raw irc line>, ", MircColors.CYAN)
            + lc.colorToken("help|h ", MircColors.GREEN)
            + lc.colorToken("[cmd], ", MircColors.CYAN)
            + lc.colorToken("next|n, ", MircColors.GREEN)
            + lc.colorToken("mum|m ", MircColors.GREEN)
            + lc.colorToken("[user], ", MircColors.CYAN)
            + lc.colorToken("invite-channel|ic ", MircColors.GREEN)
            + lc.colorToken("<channel>, ", MircColors.CYAN)
            + lc.colorToken("invite-nick|in ", MircColors.GREEN)
            + lc.colorToken("<nick> ", MircColors.CYAN)
            + lc.colorToken("[# of times], ", MircColors.CYAN)
            + lc.colorToken("pirate ", MircColors.GREEN)
            + lc.colorToken("[-s|-l|-d] ", MircColors.CYAN)
            + lc.colorToken("<search query>, ", MircColors.CYAN)
            + lc.colorToken("isup ", MircColors.GREEN)
            + lc.colorToken("<url>, ", MircColors.CYAN)
            + lc.colorToken("version, ", MircColors.GREEN)
            + lc.colorToken("quit|q", MircColors.GREEN));

    connection.msgChannel(config.getChannel(), colorStr);
  }

  private void unknownCmdHelper() {
    connection.msgChannel(config.getChannel(), "Unknown command received: " + cmd);
  }

  /*
   * Wrapper Help command messages
   */
  private void helpWrapper(String cmd) {
    //TODO help string for each command
    connection.msgChannel(config.getChannel(), "Invalid usage of command: " + cmd);
  }

  private void helpHelper() {
    //@TODO man page style usage for help blah
    connection.msgChannel(config.getChannel(), "You implement it!");
  }

  private void versionHelper() {
    connection.msgChannel(config.getChannel(),
            MircColors.BOLD + MircColors.CYAN + "JRobo"
            + MircColors.NORMAL + MircColors.BOLD + " - "
            + MircColors.GREEN + "https://github.com/BullShark/JRobo");
  }

  private void greetHelper() {
    Jokes joke = new Jokes(this.connection, config.getChannel());

    try {
      if (!hasArgs) {
        connection.msgChannel(config.getChannel(), joke.getPhoneNumber(getRandChanUser()));
      } else {
        int temp = cmdArgs.indexOf(' ');
        if (temp != -1) {
          connection.msgChannel(config.getChannel(), joke.getPhoneNumber(cmdArgs.substring(0, temp)));
        } else {
          connection.msgChannel(config.getChannel(), joke.getPhoneNumber(cmdArgs));


        }
      }
    } catch (NullPointerException ex) {
      Logger.getLogger(BotCommand.class
              .getName()).log(Level.SEVERE, null, ex);

      //Inform masters in PM
      connection.msgMasters(
              "FIX ^mum; FileReader.java not reading input!!!");
    }
  }

  private void moveToChannelHelper() {
    //TODO Only Masters
    connection.moveToChannel(config.getChannel(), cmdArgs);
  }
} // EOF class
