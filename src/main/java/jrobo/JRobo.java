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
 * @author Christopher Lemire <goodbye300@aim.com>
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
	private String user = null;

	/**
	 * Set the Reader, Config, Connection, Jokes and BotCommand
	 */
	public JRobo() {
		READER = new FileReader();
		CONFIG = FileReader.getConfig();
		CONN = new Networking(CONFIG);
		JOKES = new Jokes(CONN, CONFIG.getChannel());
		BCMD = new BotCommand(CONN, CONFIG, this);
	}

	/**
	 * @TODO Write me
	 * @param proxy
	 * @param port
	 */
	public JRobo(final String proxy, final int port) {
		READER = new FileReader();
		CONFIG = FileReader.getConfig();
		CONN = new Networking(CONFIG);
		JOKES = new Jokes(CONN, CONFIG.getChannel());
		BCMD = new BotCommand(CONN, CONFIG, this);
	}

	/**
	 * @TODO Write me
	 */
	private void initiate() {
		System.out.print("[+++]\tUsing configuration: \n" + CONFIG.toString());

		//TODO: Use TermColors.java instead
		System.out.println("\u001b[1;44m *** INITIATED *** \u001b[m");

		/* Identify to server */
		CONN.sendln("NICK " + CONFIG.getName());
		CONN.sendln("PASS " + CONFIG.getPass().replaceAll(".", "*"));
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
						String user = first.substring(1, first.indexOf('!'));
						String fullCmd = last;
						BCMD.bCommander(user, fullCmd);
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
			} // EOF if-else-if-else...
		} // EOF while

		//@TODO Implement a Networking.killConnection() and call it here
		System.out.println("\u001b[1;44m *** TERMINATED *** \u001b[m");
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
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		new JRobo().initiate();
	}
}
