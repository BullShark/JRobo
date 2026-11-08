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
 * CommandContext bundles everything a {@link Command} needs to run: the
 * bot's shared services (Networking, Config, Jokes, ...) plus the specific
 * invocation details (which user issued the command, and with what
 * arguments). A new CommandContext is built for every incoming bot command
 * and handed to whichever Command the {@link CommandFactory} produces for it.
 *
 * It also carries a handful of small utility methods that used to live as
 * private helpers on BotCommand, since several commands need them.
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class CommandContext {

	private final Networking connection;
	private final Config config;
	private final Jokes joke;
	private final JRobo jRobo;
	private final ListColors listColors;

	private final String user;
	private final String cmd;
	private final String cmdArgs;
	private final boolean hasArgs;

	/**
	 * @param jRobo Gives access to the bot's shared Networking/Config/Jokes
	 * @param listColors Shared color-formatting helper, used by ListCommand
	 * @param user The IRC nick that issued the command
	 * @param cmd The command name itself, e.g. "weather"
	 * @param cmdArgs Everything after the command name, e.g. "London"
	 */
	public CommandContext(final JRobo jRobo, final ListColors listColors, final String user, final String cmd, final String cmdArgs) {
		this.jRobo = jRobo;
		this.connection = jRobo.getCONN();
		this.config = jRobo.getCONFIG();
		this.joke = jRobo.getJOKES();
		this.listColors = listColors;
		this.user = user;
		this.cmd = cmd;
		this.cmdArgs = cmdArgs;
		this.hasArgs = !cmdArgs.isEmpty();
	}

	protected Networking getConnection() {
		return connection;
	}

	protected Config getConfig() {
		return config;
	}

	protected Jokes getJoke() {
		return joke;
	}

	protected JRobo getJRobo() {
		return jRobo;
	}

	protected ListColors getListColors() {
		return listColors;
	}

	protected String getUser() {
		return user;
	}

	protected String getCmd() {
		return cmd;
	}

	protected String getCmdArgs() {
		return cmdArgs;
	}

	protected boolean hasArgs() {
		return hasArgs;
	}

	/**
	 * Puts together a String in the form "test+a+b+c"
	 *
	 * @return Returns a new String from the String argument by removing all
	 * starting and ending white-space, and then replacing all other
	 * white-space, no matter the length of that white-space, with one '+'.
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	protected String getFormattedQuery(final String STR) {

		return STR.trim().replaceAll("\\s++", "+");
	}

	/**
	 * @return Returns a random user in the chat. If that fails such as when
	 * the user list is empty, it returns ChanServ.
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	protected String getRandChanUser() {

		String[] usersList;

		usersList = getUsers().split("\\s++");

		try {

			// Random array index
			int randIndex = (int) (Math.random() * usersList.length);
			return usersList[randIndex];
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {

			Logger.getLogger(CommandContext.class.getName()).log(Level.SEVERE, null, ex);
			return "ChanServ";
		}
	}

	/**
	 * Wrapper to getUsers(String chan) that uses channel defined by Config
	 *
	 * @return Returns the list of users in the channel defined by Config.getChannel()
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	protected String getUsers() {

		return getUsers(config.getChannel());
	}

	/**
	 * @param CHAN The channel to get a list of users
	 * @return all users that are in the channel CHAN
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	protected String getUsers(final String CHAN) {

		String received, users = "";
		String first, last;
		int tries = 8;

		connection.sendLn("NAMES " + CHAN);
		do {

			received = connection.receiveLn();
			if (received == null) {
				first = "";
				last = "";
				break;
			}
			try {

				first = received.split(" :", 2)[0];
				last = received.split(" :", 2)[1];
			} catch (ArrayIndexOutOfBoundsException ex) {

				Logger.getLogger(CommandContext.class.getName()).log(Level.INFO, null, ex);
				first = "";
				last = "";
			}
			if (first.contains("353")) {

				try {
					users += last.replaceAll("@|\\+|&|~|%", "");
				} catch (ArrayIndexOutOfBoundsException ex) {
					Logger.getLogger(CommandContext.class.getName()).log(Level.WARNING, null, ex);
				}
			} else if (first.equals("PING")) {

				connection.sendLn("PONG " + last);
			}
			tries--;
		} while (!first.contains("366") && tries > 0);

		if (users.equals("")) {

			connection.msgMasters("Could not get list of users!!!");
		}

		return users;
	}

	/**
	 * Sends the channel a generic invalid-usage message for CMD
	 *
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	protected void helpWrapper(final String CMD) {

		connection.msgChannel(config.getChannel(), "Invalid usage of command: " + CMD);
	}
}
