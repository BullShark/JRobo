/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Tyler Pollard>
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

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BombGame holds the state and logic shared by the bomb/pass/defuse
 * commands. Because that trio of commands needs to agree on who is
 * holding the bomb, which wire is live, and whether the bomb is
 * currently active, a single BombGame instance is created once by the
 * CommandFactory and handed to BombCommand, PassCommand, and
 * DefuseCommand so they all operate on the same state.
 *
 * @author Tyler Polard
 */
public class BombGame {

	private String bombHolder = "nobody";
	private boolean bombActive = false;
	private final boolean[] wire = new boolean[3];

	/**
	 * Starts the TIMER for the bomb, sets an active WIRE and prints
	 * explosion and kicks user holding at [20] seconds.
	 *
	 * @author Tyler Polard
	 */
	protected void bomb(final CommandContext ctx) {

		final Networking connection = ctx.getConnection();
		final Config config = ctx.getConfig();
		final String user = ctx.getUser();

		bombHolder = user;
		connection.msgChannel(config.getChannel(), MircColors.BOLD + bombHolder + MircColors.WHITE + " started the bomb!!!");
		connection.msgChannel(config.getChannel(), MircColors.WHITE + "You can pass it to another user with >pass [nick].");
		connection.msgChannel(config.getChannel(), MircColors.WHITE + "You can attempt to defuse with >defuse [" + MircColors.RED + "R" + MircColors.GREEN + "G" + MircColors.BLUE + "B" + MircColors.WHITE + "-color].");
		bombActive = true;
		wire[0] = false;
		wire[1] = false;
		wire[2] = false;
		wire[(int) (3.0 * Math.random())] = true;
		final Timer TIMER;

		TIMER = new Timer();

		class BombTask extends TimerTask {

			@Override
			public void run() {
				if (!bombActive) {
					TIMER.cancel();
				} else {
					connection.msgChannel(config.getChannel(), MircColors.BROWN + "          ,_=~~:-" + MircColors.YELLOW + ")" + MircColors.BROWN + ",,");
					connection.msgChannel(config.getChannel(), MircColors.YELLOW + "      (" + MircColors.BROWN + "==?,::,:::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "=:=" + MircColors.YELLOW + ")");
					connection.msgChannel(config.getChannel(), MircColors.BROWN + "     ?:=" + MircColors.YELLOW + "(" + MircColors.BROWN + ",~:::::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "~+=:I" + MircColors.YELLOW + ")");
					connection.msgChannel(config.getChannel(), MircColors.YELLOW + "   (" + MircColors.BROWN + "=:" + MircColors.YELLOW + "(" + MircColors.BROWN + ",=:~++" + MircColors.YELLOW + "=:" + MircColors.BROWN + "::~,:~:" + MircColors.YELLOW + "))" + MircColors.BROWN + "~~~." + MircColors.YELLOW + ")");
					connection.msgChannel(config.getChannel(), MircColors.YELLOW + "    (" + MircColors.BROWN + "+~" + MircColors.YELLOW + "(" + MircColors.BROWN + ",:" + MircColors.YELLOW + "(==:" + MircColors.BROWN + ":~~+~~" + MircColors.YELLOW + ")" + MircColors.BROWN + ",$,I?" + MircColors.YELLOW + "))");
					connection.msgChannel(config.getChannel(), MircColors.BROWN + "    ``  ```" + MircColors.YELLOW + "~~" + MircColors.BROWN + "?" + MircColors.YELLOW + "~=" + MircColors.BROWN + "$.~~~  ``");
					connection.msgChannel(config.getChannel(), MircColors.YELLOW + "             :" + MircColors.BROWN + "S" + MircColors.YELLOW + "Z=");
					connection.msgChannel(config.getChannel(), MircColors.YELLOW + "         .-~~" + MircColors.BROWN + "?=:=" + MircColors.YELLOW + "``~-_");
					connection.msgChannel(config.getChannel(), MircColors.YELLOW + "         `--=~=+~++=~`");
					connection.msgChannel(config.getChannel(), MircColors.YELLOW + "             ." + MircColors.BROWN + "~" + MircColors.YELLOW + ":" + MircColors.BROWN + "~");
					connection.msgChannel(config.getChannel(), MircColors.BROWN + "         ((.(\\.!/.):?)");
					connection.msgChannel(config.getChannel(), MircColors.DARK_GREEN + "   .?~:?.?7::,::::+,,~+~=:...");
					connection.kickFromChannel(config.getChannel(), user + " KABOOM!!!");
					bombActive = false;
					TIMER.cancel();
				}
			}
		}
		TIMER.schedule(new BombTask(), 20000);
	}

	/**
	 * Simply passes the bomb to another user and returns it if they attempt
	 * to pass to JRobo.
	 *
	 * @author Tyler Polard
	 */
	protected void pass(final CommandContext ctx) {

		final Networking connection = ctx.getConnection();
		final Config config = ctx.getConfig();
		final String user = ctx.getUser();
		final String cmdArgs = ctx.getCmdArgs();

		String users = ctx.getUsers();
		if (users.contains(cmdArgs) && !cmdArgs.equals("") && user.equals(bombHolder) && bombActive == true) {

			bombHolder = cmdArgs;
			connection.msgChannel(config.getChannel(), "The Bomb has been passed to " + bombHolder + "!!!");
			if (cmdArgs.equals(config.getName())) {

				try {
					Thread.sleep(2500);
				} catch (InterruptedException ex) {
					Logger.getLogger(BombGame.class.getName()).log(Level.SEVERE, null, ex);
				}
				connection.msgChannel(config.getChannel(), ">pass " + user);
				bombHolder = user;
				connection.msgChannel(config.getChannel(), "The Bomb has been passed to " + bombHolder + "!!!");
			}
		} else {

			connection.msgChannel(config.getChannel(), "Invalid.");
		}
	}

	/**
	 * This function will return the WIRE state for a given COLOR and is
	 * only to be used within defuse.
	 *
	 * @return True if wire COLOR is found, false otherwise
	 * @author Tyler Polard
	 */
	private boolean wire(final String COLOR) {

		switch (COLOR) {
			case "red" -> {
				return wire[0];
			}
			case "green" -> {
				return wire[1];
			}
			case "blue" -> {
				return wire[2];
			}
		}
		return false;
	}

	/**
	 * This is the defuse logic and refers to the wire array; the active
	 * wire is set to true in bomb().
	 *
	 * @author Tyler Polard
	 */
	protected void defuse(final CommandContext ctx) {

		final Networking connection = ctx.getConnection();
		final Config config = ctx.getConfig();
		final String user = ctx.getUser();
		final String cmdArgs = ctx.getCmdArgs();

		if (bombActive && user.equals(bombHolder)) {

			if (wire(cmdArgs) == true) {

				connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb defused.");
				bombActive = false;
			} else {

				connection.msgChannel(config.getChannel(),
					MircColors.BROWN + "          ,_=~~:-" + MircColors.YELLOW + ")" + MircColors.BROWN + ",,\n"
					+ MircColors.YELLOW + "      (" + MircColors.BROWN + "==?,::,:::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "=:=" + MircColors.YELLOW + ")\n"
					+ MircColors.BROWN + "     ?:=" + MircColors.YELLOW + "(" + MircColors.BROWN + ",~:::::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "~+=:I" + MircColors.YELLOW + ")\n"
					+ MircColors.YELLOW + "   (" + MircColors.BROWN + "=:" + MircColors.YELLOW + "(" + MircColors.BROWN + ",=:~++" + MircColors.YELLOW + "=:" + MircColors.BROWN + "::~,:~:" + MircColors.YELLOW + "))" + MircColors.BROWN + "~~~." + MircColors.YELLOW + ")\n"
					+ MircColors.YELLOW + "    (" + MircColors.BROWN + "+~" + MircColors.YELLOW + "(" + MircColors.BROWN + ",:" + MircColors.YELLOW + "(==:" + MircColors.BROWN + ":~~+~~" + MircColors.YELLOW + ")" + MircColors.BROWN + ",$,I?" + MircColors.YELLOW + "))\n"
					+ MircColors.BROWN + "    ``  ```" + MircColors.YELLOW + "~~" + MircColors.BROWN + "?" + MircColors.YELLOW + "~=" + MircColors.BROWN + "$.~~~  ``\n"
					+ MircColors.YELLOW + "             :" + MircColors.BROWN + "S" + MircColors.YELLOW + "Z=\n"
					+ MircColors.YELLOW + "         .-~~" + MircColors.BROWN + "?=:=" + MircColors.YELLOW + "``~-_\n"
					+ MircColors.YELLOW + "         `--=~=+~++=~`\n"
					+ MircColors.YELLOW + "             ." + MircColors.BROWN + "~" + MircColors.YELLOW + ":" + MircColors.BROWN + "~\n"
					+ MircColors.BROWN + "         ((.(\\.!/.):?)\n"
					+ MircColors.DARK_GREEN + "   .?~:?.?7::,::::+,,~+~=:... ");

				connection.kickFromChannel(config.getChannel(), user + " KABOOM!!!");
				bombActive = false;
			}
		} else {

			connection.msgChannel(config.getChannel(), "Invalid.");
		}
	}
}
