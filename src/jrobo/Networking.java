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
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLException;


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
  private Config config = null;
  private String received = null;
  private final int MAXCHARS = 450; /* Some RFC says 510 max chars */


  public Networking(Config config) {
    super(); // Gets rid of java.lang.VerifyError
    this.config = config;
    String network = config.getNetwork();
    String[] server = network.split(":");
    String hostname = server[0];
    boolean sslEnabled = config.getConnectionType();
    int port = Integer.parseInt(server[1]);

    if (sslEnabled) {
      try {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sock = (SSLSocket) factory.createSocket(hostname, port);
        sock.startHandshake(); //@TODO make this print the certificate upon connect.
   
        bwriter = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        breader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        
      } catch (SSLException ex) {
        Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
        err.println("I/O Error, Check ssl/networking");
        //@TODO close streams, connections, etc
      } catch (IOException ex) {
        Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
        err.println("I/O Error, Check networking");
        //@TODO close streams, connections, etc
        System.exit(-1);
      }
    } else {  //if not ssl than normal socket
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
   * The attribute codes can be lost some times on the messages
   * After the first one that were split. By giving a code(s),
   * They will be prepended to the split messages after the first one
   * @param chan Channel to send the message
   * @param msg Message to send to the channel
   * @param colorLines If true, use color and attribute codes
   * @param codes Attribute codes to use if the message is split. Use an empty string if none.
   * @return Whether this method was successful
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

  
  /*
   * The following function is only used to kick members from a channel.
   * It should probably be modified at some point to pass any desired irc command.
   * 
   */
  //TODO    msgArr commit moar than one word after " "
    public boolean kickFromChannel(String chan, String msg) {
    boolean success = true;
    msg = addNewLines(msg);
    String[] msgArr = msg.split("\n");
    char ch;

    for(int j=0;j<msgArr.length;j++) {
      /*
       * Meaning if one call to sendln returns false
       * This entire function will return false
       */
      if( !sendln("kick " + chan + " " + msgArr[j]) ) {
        success = false; 
      }
    }
    return success;
  }
  
  /**
   * Wrapper method using defaults, no colors added for split messages
   * @param chan Channel to send the message to
   * @param msg Message that gets send to the channel
   * @return Whether this method was successful
   */
  public boolean msgChannel(String chan, String msg) {
    return msgChannel(chan, msg, false, "");
  }
  
  /**
   * Overridden and wrapper method
   * @param chan
   * @param msgArr An array of messages, each message sent on its own line in IRC
   * @param colorLines If true, use color and attribute codes
   * @param codes Attribute codes to use if the message is split. Use an empty string if none.
   * @return Whether this method was successful
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
   * Sends a private message to the user
   * @param user User to message
   * @param msg Message to send to user
   * @return Whether this method was successful
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
   * @param fromChan
   * @param toChan
   * @return 
   */
  public boolean moveToChannel(String fromChan, String toChan) {
    boolean success = true;

    if(!sendln("PART " + fromChan) || !sendln("JOIN " + toChan)) {
      success = false;
    }

    config.setChannel(toChan);

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
   * 
   * TODO Is this really needed or does the wrapText make it obsolete?
   */
    private String addNewLines(String command) {
      String[] lines = wrapText(command, MAXCHARS);
      String tmp = "";
      for (int i = 0; i < lines.length; i++) {
          tmp += lines[i] + "\n";
      }
      return tmp;
      
    }


  public static String[] wrapText(String text, int len)
  {
    if (text == null) {
      return new String[0];
    }

    if (len <= 0) {
      return new String[] { text };
    }

    if (text.length() <= len) {
      return new String[] { text };
    }

    char[] chars = text.toCharArray();
    Vector lines = new Vector();
    StringBuffer line = new StringBuffer();
    StringBuffer word = new StringBuffer();

    for (int i = 0; i < chars.length; i++) {
      word.append(chars[i]);

      if (chars[i] == ' ') {
        if (line.length() + word.length() > len) {
          lines.add(line.toString());
          line.delete(0, line.length());
        }

        line.append(word);
        word.delete(0, word.length());
      }

    }

    if (word.length() > 0) {
      if (line.length() + word.length() > len) {
        lines.add(line.toString());
        line.delete(0, line.length());
      }
      line.append(word);
    }

    if (line.length() > 0) {
      lines.add(line.toString());
    }

    String[] ret = new String[lines.size()];
    int c = 0;
    for (Enumeration e = lines.elements(); e.hasMoreElements(); c++) {
      ret[c] = ((String)e.nextElement());
    }

    return ret;
  }

  /* Method below uses this */
  private static final byte[] HEXBYTES = { (byte) '0', (byte) '1', (byte) '2', (byte) '3',
      (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a',
      (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' };

  public static int getUTFSize(String s) {

      int len = (s == null) ? 0
                            : s.length();
      int l   = 0;

      for (int i = 0; i < len; i++) {
          int c = s.charAt(i);

          if ((c >= 0x0001) && (c <= 0x007F)) {
              l++;
          } else if (c > 0x07FF) {
              l += 3;
          } else {
              l += 2;
          }
      }

      return l;
  }
  
   /**
   * Inform masters in PM
   *
   * @param msg Message to send to all masters
   * @since 2013-03-22
   */
  public void msgMasters(String msg) {
      for(String master : config.getMasters()) {
          
        this.msgUser(master.split("@")[0], msg);
      }
  }
} //EOF class
