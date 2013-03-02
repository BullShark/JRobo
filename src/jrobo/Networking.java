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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import static java.lang.System.err;
import static java.lang.System.out;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all the networking done by the IRC protocol
 * Including many helpful methods to make coding JRobo easier
 *
 * @author Christopher Lemire <christopher.lemire@gmail.com>
 */
public class Networking {
  private Socket sock = null;
  private BufferedWriter bwriter = null;
  private BufferedReader breader = null;
  private String received = null;
  private final int MAXCHARS = 450; /* Some RFC says 510 max chars */

  public Networking(String network) {
    super(); // Gets rid of java.lang.VerifyError
    
    String[] server = network.split(":");
    String hostname = server[0];
    int port = Integer.parseInt(server[1]);
    
    try {
      sock = new Socket(hostname, port); //TODO SSL conn on port 6697
    } catch (UnknownHostException ex) {
      Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
      err.println("Possible DNS resolution failed");
      //@TODO close streams, connections, etc
      System.exit(-1);
    } catch (IOException ex) {
      Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
      err.println("I/O Error, Check networking");
      //@TODO close streams, connections, etc
      System.exit(-1);
    }
    try {
      bwriter = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
      breader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    } catch (IOException ex) {
      Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
      err.println("Error getting streams with server");
      //@TODO close streams, connections, etc
      System.exit(-1);
    }
  }

  /**
   * For sending in raw IRC protocol
   * @param command The raw IRC line to send
   * @return Successful sending
   * @throws IOException Network related issues
   */
  public boolean sendln(String command) {
    try {
      bwriter.write(command);
      bwriter.newLine();
      bwriter.flush();
      out.println("[***]\t" + command); //@TODO Color-code me
      return true;

    } catch (IOException ex) {
      err.printf("Failed to send \"%s\"\n", command);
      Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
      return false;

    }
  }

  /**
   * For receiving in raw IRC protocol
   * @return Successful sending
   */
  public String recieveln() {
    try {
      received = breader.readLine();
      out.printf("[---]\t%s\n", received); //@TODO Color-code this opposite of colorcode for sent, "[-]" should be blue
      return received;
    } catch (IOException ex) {
      Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }

  /**
   * TODO write me
   * @param chan Channel to send the message
   * @param msg Message to send to the channel
   * @param colorLines If true, use color and attribute codes
   * @param codes
   * @return
   */
  public boolean msgChannel(String chan, String msg, boolean colorLines, String codes) {
    boolean success = true;
    msg = addNewLines(msg);
    String[] msgArr = msg.split("\n");
    char ch;

    if(colorLines) {
      for(int j=0;j<msgArr.length;j++) {
        /*
         * Meaning if one call to sendln returns false
         * This entire function will return false
         */
        ch = msgArr[j].charAt(0);
        if(j > 0 && !(ch == '\u0003' || ch == '\u000f' || ch == '\u0002' || ch == '\u001f' || ch == '\u0016')) {
          if( !sendln("PRIVMSG " + chan + " :" + codes + msgArr[j]) ) {
            success = false; 
          } 
        } else {
          if( !sendln("PRIVMSG " + chan + " :" + msgArr[j]) ) {
              success = false; 
          }
        }
      }
    } else {
      for(int j=0;j<msgArr.length;j++) {
        /*
         * Meaning if one call to sendln returns false
         * This entire function will return false
         */
        if( !sendln("PRIVMSG " + chan + " :" + msgArr[j]) ) {
          success = false; 
        }
      }
    }
    return success;
  }

  /**
   * Wrapper method
   * @param chan
   * @param msg
   * @return
   */
  public boolean msgChannel(String chan, String msg) {
    return msgChannel(chan, msg, false, null);
  }
  
  /**
   * Overridden and wrapper method
   * 
   * @param chan
   * 
   * @param msgArr
   * 
   * @param colorLines
   * 
   * @param codes
   * 
   * @return
   * 
   */
  public boolean msgChannel(String chan, String[] msgArr, boolean colorLines, String codes) {
    boolean success = true;
    for(String s : msgArr) {
      if(!msgChannel(chan, s, colorLines, codes)) {
        success = false;
      }
    }
    
    return success;
  }

//  public boolean msgChannel(String chan, String msg) {

  /**
   *
   * @param user
   * @param msg
   * @return
   */
  public boolean msgUser(String user, String msg) {
    boolean success = true;
    String[] msgArr = msg.split("\n");
    for(int j=0;j<msgArr.length;j++) {
      /*
       * Meaning if one call to sendln returns false
       * This entire function will return false
       */
      if( !sendln("PRIVMSG " + user + " :" + msgArr[j]) ) {
        success = false;
      }
    }
    return success;
  } // EOF method
  
  /**
   *
   * @param chan
   * @param msg
   * @return
   */
  public boolean noticeChan(String chan, String msg){
      boolean success = true;
      
      String[] msgSplit = msg.split("\n");
      
      for(int i=0;i<msgSplit.length;i++) {
        if(!sendln("NOTICE " + chan + " :" + msgSplit[i]) ) {
          success = false;
        }
     }
      return success;
  }
  
  /**
   *
   * @param user
   * @param msg
   * @return
   */
  public boolean noticeUser(String user, String msg) {
    boolean success = true;
      
    String[] msgSplit = msg.split("\n");
      
    for(int i=0;i<msgSplit.length;i++){
      if(!sendln("NOTICE " + user + " :" + msgSplit[i]) ) {
        success = false;
      }
    }

    return success;
  }

  /*
   * Useful for the other methods that split messages into several where
   * Newlines occure
   * This is used to prevent a message being truncated by IRC because
   * It exceeds MAXCHARS
   */
    private String addNewLines(String command) {
      String[] lines = wrapText(command, MAXCHARS);
      String tmp = "";
      for (int i = 0; i < lines.length; i++) {
          tmp += lines[i] + "\n";
      }
      return tmp;
    }

    static String[] wrapText(String text, int len) {
      // return empty array for null text
      if (text == null) {
          return new String[]{};
      }

      // return text if len is zero or less
      if (len <= 0) {
        return new String[]{text};
      }

      // return text if less than length
      if (text.length() <= len) {
        return new String[]{text};
      }

      char[] chars = text.toCharArray();
      Vector lines = new Vector();
      StringBuffer line = new StringBuffer();
      StringBuffer word = new StringBuffer();

      for (int i = 0; i < chars.length; i++) {
        word.append(chars[i]);

        if (chars[i] == ' ') {
          if ((line.length() + word.length()) > len) {
            lines.add(line.toString());
            line.delete(0, line.length());
          }

          line.append(word);
          word.delete(0, word.length());
        }
      }

      // handle any extra chars in current word
      if (word.length() > 0) {
        if ((line.length() + word.length()) > len) {
          lines.add(line.toString());
          line.delete(0, line.length());
        }
          line.append(word);
        }

        // handle extra line
        if (line.length() > 0) {
          lines.add(line.toString());
        }

        String[] ret = new String[lines.size()];
        int c = 0; // counter
        for (Enumeration e = lines.elements(); e.hasMoreElements(); c++) {
          ret[c] = (String) e.nextElement();
        }

        return ret;
    }
} //EOF class