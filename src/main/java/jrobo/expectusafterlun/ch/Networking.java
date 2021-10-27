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
 * Handles all the networking done by the IRC protocol including many helpful
 * methods to make coding JRobo easier
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class Networking {

	private Socket sock = null;
	private BufferedWriter bwriter;
	private BufferedReader breader;
	private final Config CONFIG;
	private String received = null;
	private final int MAXCHARS = 450;

	/**
	 * Networking takes care of the connection, reads from the Config file, and handles the MAXCHARS a message can have
	 * Some RFC says 510 max chars
 	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @param CONFIG The configuration to be read such as which irc network to join
	 */
	public Networking(final Config CONFIG) {
		super(); // Gets rid of java.lang.VerifyError
		this.CONFIG = CONFIG;
		String network = CONFIG.getNetwork();
		String[] server = network.split(":");
		String hostname = server[0];
		int port = Integer.parseInt(server[1]);

		try {
			sock = new Socket(hostname, port); //TODO SSL conn on port 6697

			bwriter = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			breader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

		} catch (UnknownHostException ex) {
			Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
			err.println("Possible DNS resolution failure");
			System.exit(-1);

		} catch (IOException ex) {
			Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
			err.println("I/O Error, Check networking");
			System.exit(-1);
		}
	}

	/**
	 * For sending in raw IRC protocol
	 *
	 * @param COMMAND The raw IRC line to send
	 * @return Successful sending
	 */
	protected boolean sendln(final String COMMAND) {
		try {
			bwriter.write(COMMAND);
			bwriter.newLine();
			bwriter.flush();
			out.println("[***]\t" + COMMAND); //@TODO Color-code me
			return true;

		} catch (IOException ex) {
			Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
			err.printf("Failed to send \"%s\"\n", COMMAND);
			return false;

		} 
	}

	/**
	 * For receiving in raw IRC protocol
	 *
	 * @return Successful sending
	 */
	protected String recieveln() {
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
	 * The attribute codes can be lost some times on the messages After the
	 * first one that were split.By giving a code(s), They will be
	 * prepended to the split messages after the first one
	 *
	 * @param CHAN Channel to send the message
	 * @param msg Message to send to the channel
	 * @param COLORLINES If true, use color and attribute CODES
	 * @param CODES Attribute CODES to use if the message is split. Use an
	 * empty string if none.
	 * @return Whether this method was successful
	 */
	protected boolean msgChannel(final String CHAN, String msg, final boolean COLORLINES, final String CODES) {
		boolean success = true;
		msg = addNewLines(msg);
		final String[] MSGARR = msg.split("\n");
		char ch;

		if (COLORLINES) {
			for (int j = 0; j < MSGARR.length; j++) {
				/*
				 * Meaning if one call to sendln returns false
				 * This entire function will return false
				 */
				ch = MSGARR[j].charAt(0);
				if (j > 0 && !(ch == '\u0003' || ch == '\u000f' || ch == '\u0002' || ch == '\u001f' || ch == '\u0016')) {
					if (!sendln("PRIVMSG " + CHAN + " :" + CODES + MSGARR[j])) {
						success = false;
					}
				} else {
					if (!sendln("PRIVMSG " + CHAN + " :" + MSGARR[j])) {
						success = false;
					}
				}
			}
		} else {
			for (String message : MSGARR) {
				/*
				 * Meaning if one call to sendln returns false
				 * This entire function will return false
				 */
				if (!sendln("PRIVMSG " + CHAN + " :" + message)) {
					success = false;
				}
			}
		}
		return success;
	}

	/**
	 * The following function is only used to kick members from a channel.It should probably be modified at some point to pass any desired irc command.
	 * TODO MSGARR commit more than one word after " "
	 * @param CHAN The channel JRobo is in and the user to be kicked
	 * @param msg The kick message, reason for kicking
	 * @return Whether the kicking succeeded or not
	 */
	protected boolean kickFromChannel(final String CHAN, String msg) {
		boolean success = true;
		msg = addNewLines(msg);
		final String[] MSGARR = msg.split("\n");
		char ch;

		for (String token : MSGARR) {
			/*
		   * Meaning if one call to sendln returns false
		   * This entire function will return false
			 */
			if (!sendln("kick " + CHAN + " " + token)) {
				success = false;
			}
		}
		return success;
	}

	/**
	 * Wrapper method using defaults, no colors added for split messages
	 *
	 * @param CHAN Channel to send the message to
	 * @param MSG Message that gets send to the channel
	 * @return Whether this method was successful
	 */
	protected boolean msgChannel(final String CHAN, final String MSG) {
		return msgChannel(CHAN, MSG, false, "");
	}

	/**
	 * Overridden and wrapper method
	 *
	 * @param CHAN The IRC channel for the message to be sent to
	 * @param MSGARR An array of messages, each message sent on its own line
	 * in IRC
	 * @param COLORLINES If true, use color and attribute codes
	 * @param CODES Attribute codes to use if the message is split. Use an
	 * empty string if none.
	 * @return Whether this method was successful
	 *
	 */
	protected boolean msgChannel(final String CHAN, final String[] MSGARR, final boolean COLORLINES, final String CODES) {
		boolean success = true;
		for (String s : MSGARR) {
			if (!msgChannel(CHAN, s, COLORLINES, CODES)) {
				success = false;
			}
		}

		return success;
	}

//	protected boolean msgChannel(String chan, String msg) { }
	/**
	 * Sends a private message to the user
	 *
	 * @param USER User to message
	 * @param MSG Message to send to user
	 * @return Whether this method was successful
	 */
	protected boolean msgUser(final String USER, final String MSG) {
		boolean success = true;
		final String[] MSGARR = MSG.split("\n");
		for (String msg : MSGARR) {
			/*
			 * Meaning if one call to sendln returns false
			 * This entire function will return false
			 */
			if (!sendln("PRIVMSG " + USER + " :" + msg)) {
				success = false;
			}
		}
		return success;
	} // EOF method

	/**
	 * Sends a notice message to the channel
	 * @param CHAN The IRC channel for the notice to be sent to
	 * @param MSG The message as a notice to be sent to the channel
	 * @return If succeeded or not
	 */
	protected boolean noticeChan(final String CHAN, final String MSG) {
		boolean success = true;

		final String[] SPLIT = MSG.split("\n");

		for (String msg : SPLIT) {
			if (!sendln("NOTICE " + CHAN + " :" + msg)) {
				success = false;
			}
		}
		return success;
	}

	/**
	 * Moves JRobo from one channel to another
	 * @param FROMCHAN The channel to move from
	 * @param TOCHAN The channel to move to
	 * @return If succeeded or not
	 */
	protected boolean moveToChannel(final String FROMCHAN, final String TOCHAN) {
		boolean success = true;

		if (!sendln("PART " + FROMCHAN) || !sendln("JOIN " + TOCHAN)) {
			success = false;
		}

		CONFIG.setChannel(TOCHAN);

		return success;
	}

	/**
	 * Sends a notice message to the user
	 * @param USER The user that receives the notice message
	 * @param MSG The message to be sent as a notice to the user
	 * @return Whether it succeeded or not
	 */
	protected boolean noticeUser(final String USER, final String MSG) {
		boolean success = true;

		final String[] SPLIT = MSG.split("\n");

		for (String msg : SPLIT) {
			if (!sendln("NOTICE " + USER + " :" + msg)) {
				success = false;
			}
		}

		return success;
	}

	/**
	 * Useful for the other methods that split messages into several where
	 * Newlines occur
	 * This is used to prevent a message being truncated by IRC because
	 * It exceeds MAXCHARS
	 * 
	 * TODO Is this really needed or does the wrapText make it obsolete?
	 */
	private String addNewLines(final String COMMAND) {
		final String[] LINES = wrapText(COMMAND, MAXCHARS);
		String result = "";
		for (String line : LINES) {
			result += line + "\n";
		}

		return result;
	}

	/**
	 * Splits a message into multiple messages that is too long
	 * @param TEXT The message TEXT to be split
	 * @param LEN The length each new split message should be
	 * @return The split message as an Array of String
	 */
	protected static String[] wrapText(final String TEXT, final int LEN) {
		if (TEXT == null) {
			return new String[0];
		}

		if (LEN <= 0) {
			return new String[]{TEXT};
		}

		if (TEXT.length() <= LEN) {
			return new String[]{TEXT};
		}

		char[] chars = TEXT.toCharArray();
		Vector lines = new Vector();
		StringBuilder line = new StringBuilder();
		StringBuffer word = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {
			word.append(chars[i]);

			if (chars[i] == ' ') {
				if (line.length() + word.length() > LEN) {
					lines.add(line.toString());
					line.delete(0, line.length());
				}

				line.append(word);
				word.delete(0, word.length());
			}

		}

		if (word.length() > 0) {
			if (line.length() + word.length() > LEN) {
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
			ret[c] = ((String) e.nextElement());
		}

		return ret;
	}

	/**
	 * Inform masters in PM
	 *
	 * @param MSG Message to send to all masters
	 * @since 2013-03-22
	 */
	protected void msgMasters(final String MSG) {
		for (String master : CONFIG.getMasters()) {

			this.msgUser(master, MSG);
		}
	}
} //EOF class
