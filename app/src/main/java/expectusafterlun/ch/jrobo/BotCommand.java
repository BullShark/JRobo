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

package expectusafterlun.ch.jrobo;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses incoming bot commands and dispatches them. Command lookup and
 * instantiation is delegated to {@link CommandFactory} (Factory design
 * pattern), which decouples this class from the ~20 concrete Command
 * implementations under expectusafterlun.ch.jrobo. Adding a new bot command
 * no longer requires touching this class at all -- just add a new Command
 * implementation and register it in CommandFactory.
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class BotCommand {

	private final JRobo JROBO;
	private final ListColors LC;
	private final CommandFactory FACTORY;

	/**
	 * Constructor that initializes BotCommand's dependencies.
	 *
	 * @param JROBO Provides getters for the Networking, Config, and Jokes
	 * used by individual commands via CommandContext
	 */
	public BotCommand(final JRobo JROBO) {

		this.JROBO = JROBO;
		this.LC = new ListColors();
		this.FACTORY = new CommandFactory();
	}

	/**
	 * This is called when a bot command is received. Parses the command
	 * name and arguments, builds a CommandContext, and asks the
	 * CommandFactory for the Command to run.
	 *
	 * @param USER The user mentioned by a command given to JRobo
	 * @param FULLCMD The full command not split into segments
	 */
	protected void bCommander(final String USER, final String FULLCMD) {
	//TODO Search for bots on irc and watch their behavior for ideas such as WifiHelper in #aircrack-ng

		final String cmd = getCmd(FULLCMD);
		final String cmdArgs = getCmdArgs(FULLCMD);

		final CommandContext ctx = new CommandContext(JROBO, LC, USER, cmd, cmdArgs);
		final Command command = FACTORY.getCommand(cmd);

		command.execute(ctx);
	}

	/**
	 * Takes the full command "command arg1 arg2 arg3" and returns "arg1 arg2 arg3"
	 *
	 * @param FULLCMD The full command given to the bot
	 * @return The arguments only excluding the command given to the bot
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	private String getCmdArgs(final String FULLCMD) {

		try {

			return FULLCMD.split("\\" + JROBO.getCONFIG().getCmdSymb() + "[a-zA-Z_0-9\\-]++", 2)[1].trim();
		} catch (ArrayIndexOutOfBoundsException ex) {

			/* Means no args!!! */
			Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
			return "";
		} // EOF try-catch
	} // EOF function

	/**
	 * Takes the full command "cmd arg1 arg2 arg3" and returns "cmd"
	 *
	 * @param FULLCMD The full command including the command and all arguments given to the bot
	 * @return Only the command without the arguments given to the bot
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	private String getCmd(final String FULLCMD) {

		try {
			return FULLCMD.substring(1).replaceFirst("\\s.*+", "");

		} catch (IndexOutOfBoundsException ex) {

			Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
			return "";
		}
	}
} // EOF class
