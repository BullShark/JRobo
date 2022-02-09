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

package expectusafterlun.ch.jrobo;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JRobo is an advanced IRC bot that uses its own IRC framework. It was written from scratch. This class is where execution begins.
 * 
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class JRobo {

	/* Defined Objects */
	private final Networking CONN;
	private final FileReader READER;
	private final Config CONFIG;
	private final Jokes JOKES;
	private final BotCommand BCMD;

	/* Networking */
	private String first = null;
	private String last = null;
	private String received = null;
	/* Miscallenous */
	private String user;

	/**
	 * Set the FileReader, Config, the Networking connection, Jokes and BotCommand
	 */
	public JRobo() {
		this.user = null;
		READER = new FileReader();
		CONFIG = READER.getConfig();
		READER.setDebug(CONFIG.getDebug());
		if(CONFIG.getProxyHost() != null && CONFIG.getProxyPort() != null) {

			//TODO Needs testing
			System.setProperty("http.proxyHost", CONFIG.getProxyHost());
			System.setProperty("http.proxyPort", CONFIG.getProxyPort());
		}
		CONN = new Networking(this);
		JOKES = new Jokes(this);
		BCMD = new BotCommand(this);
	}

	/**
	 * The beginning of JRobo's execution starts here
	 */
	private void initiate() {
		System.out.print(TermColors.info("Using configuration: \n" + CONFIG.getColorString()));

		System.out.println(TermColors.ANSI_BLUE_BACKGROUND + TermColors.ANSI_WHITE + "[+++]\t" + " *** INITIATED *** " + TermColors.ANSI_RESET);

		/* Identify to server */
		CONN.sendln("NICK " + CONFIG.getName());
		CONN.sendln("PASS " + CONFIG.getPass(), true);
		CONN.sendln("USER JRobo 0 * :Microsoft Exterminator!");
		/*
		 * Wait for server message:
		 * 001 JRobo :Welcome to the IRC Network
		 * Before attempting to join a channel
		 */
		while ((received = CONN.recieveln()) != null) {
			this.divideTwo();

			if (first.equals("PING")) {
				CONN.sendln("PONG " + last);
			}

			if (first.contains("001")) {
				break;
			}
		}
		CONN.sendln("JOIN " + CONFIG.getChannel());

		/*
   		 * Conditional checks happen in order
   		 * From Most likely to occur
   		 * To least likely to occur
   		 *
   		 * This is done for effiency
   		 * It will result in less conditional checks
   		 * Being made
		 */
		while ((received = CONN.recieveln()) != null) {
			this.divideTwo();

			/*
			 * A PING was received from the IRC server
			 */
			if (first.equals("PING")) {
				CONN.sendln("PONG " + last);
			/*
			 * A message was sent either to the channel
			 * Or to the bot; Could be a command
			 */
			} else if (first.contains("PRIVMSG")) {
				try {
					if (last.charAt(0) == CONFIG.getCmdSymb()) {
						String u = first.substring(1, first.indexOf('!'));
						String fullCmd = last;
						BCMD.bCommander(u, fullCmd);
					} else {

						/*
			                                                       * Match JRobo in any case
			                                                       * Typed by another user
						 */
						if (last.matches("(?i).*JR[0o]b[0o].*")) {
							try {
								user = first.substring(1, first.indexOf('!'));
								CONN.msgChannel(CONFIG.getChannel(), JOKES.getPhoneNumber(user));
							} catch (StringIndexOutOfBoundsException ex) {
								Logger.getLogger(JRobo.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					}
				} catch (StringIndexOutOfBoundsException ex) {
					Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
				}
			/*
		                   * A user has joined the channel
		                   * Excluding the bot joining
			 */
			} else if (first.contains("JOIN") && last.equals(CONFIG.getChannel()) && !first.contains(CONFIG.getName())) {
				user = first.substring(1, first.indexOf('!'));

				// Inform masters in PM
				CONN.msgMasters(user + " joined " + CONFIG.getChannel());
			} else if (received.matches("^:\\S+ KICK " + CONFIG.getChannel() + " " + CONFIG.getName() + " :.*")) {
				CONN.sendln("JOIN " + CONFIG.getChannel());
				user = first.substring(1, first.indexOf('!'));
			} else if (last.equals("Nickname is already in use")) {
				CONN.sendln("NICK " + CONFIG.getSecondName());
				CONN.sendln("PASS " + CONFIG.getPass(), true);
				CONN.sendln("USER JRobo 0 * :Microsoft Exterminator!");
				CONN.sendln("JOIN " + CONFIG.getChannel());
			} // EOF if-else-if-else...
		} // EOF while

		CONN.closeConnection();

		System.out.println(TermColors.ANSI_BLUE_BACKGROUND + TermColors.ANSI_WHITE + "[+++]\t" + " *** TERMINATED *** " + TermColors.ANSI_RESET);
	}

	/**
	 * Parse the input into two, first and last
	 */
	private void divideTwo() {
		try {
			first = received.split(" :", 2)[0];
			last = received.split(" :", 2)[1];
		} catch (ArrayIndexOutOfBoundsException ex) {
			first = "";
			last = "";
		}
	}

	/**
	 * @return the first half of the divided server input
	 */
	public String getFirst() {
		return first;
	}

	/**
	 * @return the last half of the divided server input
	 */
	public String getLast() {
		return last;
	}

	/**
	 * For Network I/O
	 * @return The Network Connection for this bot
	 */
	public Networking getCONN() {
		return CONN;
	}

	/**
	 * Handles all the File Reading I/O such as reading jokes and the Config file
	 * @return The FileReader for this bot
	 */
	public FileReader getREADER() {
		return READER;
	}

	/**
	 * Provides getters and setters for everything set in Config.json
	 * @return The Configuration for JRobo
	 */
	public Config getCONFIG() {
		return CONFIG;
	}

	/**
	 * Used for retrieving jokes about an IRC user
	 * @return The Jokes used by JRobo
	 */
	public Jokes getJOKES() {
		return JOKES;
	}

	/**
	 * Handles BotCommands given to JRobo
	 * @return The BotCommand for JRobo
	 */
	public BotCommand getBCMD() {
		return BCMD;
	}

	/**
	 * @param args The command line arguments, expects either -g|--debug for
	 * raw json output or none
	 */
	public static void main(String[] args) {
		System.out.println("""
				This software was created by Christopher Lemire <goodbye300@aim.com>
				Feedback is appreciated!

				To show raw JSON output and errors use "debug":"true" in Config.json.
				""");

		new JRobo().initiate();
	}
}
