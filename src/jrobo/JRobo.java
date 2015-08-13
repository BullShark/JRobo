/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
 * Copyright (C) <2013> <BinaryStroke>
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

package jrobo;

import jrobo.command.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author bullshark
 */
public class JRobo {

  /* Defined Objects */
  private final Networking connection;
  private final FileReader reader;
  private final Config config;
  private final Jokes jokes;
  private final CommandFactory factory;

  /* Networking */
  private String first = null;
  private String last = null;
  private String received = null;

  /* Miscallenous */
  private String user = null;
  private Map<String, Set<String>> channelUsers;

  public JRobo() {
    reader = new FileReader();
    config = reader.getConfig();
    connection = new Networking(config);
    jokes = new Jokes (connection, config.getChannel());
    factory = new CommandFactory();
    channelUsers = new HashMap<String, Set<String>>();
  }

  private void initiate() {
    //TODO: Use TermColors.java instead
    System.out.println("\u001b[1;44m *** INITIATED *** \u001b[m");

    /* Identify to server */
    connection.sendln("NICK " + config.getName());
    connection.sendln("PASS " + config.getPass());
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
    connection.sendln("JOIN " + config.getChannel());

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
        continue;
      }

      /*
       * Lets break the first half the message down a bit more
       */
      String ircMessage;
      try {
        ircMessage = first.split(" ")[1].toUpperCase();
      } catch(ArrayIndexOutOfBoundsException ex) {
        ircMessage = "";
      }

      /*
       * A message was sent either to the channel
       * Or to the bot; Could be a command
       */
      if(ircMessage.equals("PRIVMSG")) {
        try {
          if(last.charAt(0) == config.getCmdSymb()) {
            String user = first.substring(1, first.indexOf('!'));
            String cmd = getCommand(last);
            BotCommand botCmd = factory.newCommand(cmd);

            botCmd.setJRobo(this);
            botCmd.setInputCommand(cmd);
            botCmd.execute(getTarget(first), getParameters(last));
          }
        } catch(StringIndexOutOfBoundsException ex) {
          Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
        } catch(InstantiationException ex) {
          Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IllegalAccessException ex) {
          Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
        }
      }

      /*
       * A /NAMES list was received update the channelUsers list
       * for the channel in question.
       */

      else if(ircMessage.equals("353")) {
        String channel;
        Set<String> users = new HashSet<String>();

        try {
          channel = first.split(" ")[4];
        } catch(ArrayIndexOutOfBoundsException ex) {
          continue;
        }

        last = last.replaceAll("@|\\+|&|~|%", "");
        for(String nickname : last.split(" ")) {
          users.add(nickname);
        }

        if(channelUsers.get(channel) != null) {
          channelUsers.get(channel).addAll(users);
        } else {
          channelUsers.put(channel, users);
        }
      }

      /*
       * A JOIN message was received. Add that user to
       * the channel's user list.
       */
      else if(ircMessage.equals("JOIN")) {
        String nickname = getNickname();
        String channel  = last;

        if(nickname != null) {
          if(channelUsers.get(channel) != null) {
            channelUsers.get(channel).add(nickname);
          } else { 
            // TODO
          }
        }
      }

      /*
       * A PART message was received. Remove that user from
       * the channel's user list.
       */
      else if(ircMessage.equals("PART")) {
        try {
          String nickname = getNickname();
          String channel  = first.split(" ")[2];

          if(nickname != null) {
            if(nickname != myNickname()) {
              channelUsers.get(channel).remove(nickname);
            } else {
              channelUsers.remove(channel);
            }
          }
        } catch(ArrayIndexOutOfBoundsException ex) {
        }
      }
    }

    System.out.println("\u001b[1;44m *** TERMINATED *** \u001b[m");
  }

  /**
   * @author jotaki
   * @param line The full message line received after the `:'.
   * @return The command to be interpreted.
   */
  private String getCommand(String line) {
    try {
      return line.substring(1, line.indexOf(' '));
    } catch(StringIndexOutOfBoundsException ex) {
      return line.substring(1, line.length());
    }
  }

  /**
   * @author jotaki
   * @param line The message line before the `:'
   * @return The considered target
   * TODO: If target == Bot.nick return FromLine.nick
   */
  private String getTarget(String line) {
    String[] lineSplit = line.split(" ");
    try {
      return lineSplit[lineSplit.length-1];
    } catch(ArrayIndexOutOfBoundsException ex) {
      return "";
    }
  }

  /**
   * @author jotaki
   * @param line the full message line received after the `:'
   * @return the parameters to the bot command as a String array split by spaces.
   */
  private String[] getParameters(String line) {
    String[] fullArgs = line.split(" ");
    String[] args = new String[fullArgs.length - 1];
    int i;

    for(i = 1; i < fullArgs.length; i++) {
      args[i-1] = fullArgs[i];
    }

    return args;
  }

  public String getNickname(String line) {
    try {
      return line.substring(1, line.indexOf('!'));
    } catch(StringIndexOutOfBoundsException ex) {
      return null;
    }
  }

  public String getNickname() {
    return getNickname(first);
  }

  public String getUsername(String line) {
    try {
      return line.substring(line.indexOf('!')+1, line.indexOf('@'));
    } catch(StringIndexOutOfBoundsException ex) {
      return null;
    }
  }

  public String getUsername() {
    return getUsername(first);
  }

  public String getHostname(String line) {
    try {
      return line.substring(line.indexOf('@')+1, line.indexOf(' '));
    } catch(StringIndexOutOfBoundsException ex) {
      return null;
    }
  }

  public String getHostname() {
    return getHostname(first);
  }

  public String myNickname() {
    /* XXX: TODO: Make smarter / better */
    return config.getName();
  }


  private void divideTwo() {
    try {
      first = received.split(" :", 2)[0];
      last = received.split(" :", 2)[1];
    } catch(ArrayIndexOutOfBoundsException ex) {
      first = "";
      last = "";
    }
  }

  public String getFirst() {
    return first;
  }
  
  public String getLast() {
    return last;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new JRobo().initiate();
  }

  public Networking getConnection() {
    return connection;
  }

  public Set<String> getUsers(String channel) {
    return channelUsers.get(channel);
  }
}
