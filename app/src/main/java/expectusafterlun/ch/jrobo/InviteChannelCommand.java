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
 * Helper to invite all members of a channel. A single instance of this
 * Command is reused by the CommandFactory so that the "one invite thread
 * at a time" guard (threadCreated) persists between invocations, matching
 * the original behavior.
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 * @author Tyler Polard
 */
public class InviteChannelCommand implements Command {

	private boolean threadCreated = false;

	@Override
	public void execute(final CommandContext ctx) {

		final Networking connection = ctx.getConnection();
		final Config config = ctx.getConfig();
		final String cmdArgs = ctx.getCmdArgs();
		final String user = ctx.getUser();

		String[] userArr;
		if (!ctx.hasArgs()) {
			ctx.helpWrapper(ctx.getCmd());
			return;
		}

		if (threadCreated) {
			connection.msgChannel(config.getChannel(), "The invite thread is still active.");
			return;
		}

		if (cmdArgs.contains(" ")) {
			final String[] CHANSARR = cmdArgs.split("\\s++");
			// Channel must begin with a # and be at least two characters long
			for (String chan : CHANSARR) {
				if (chan.length() < 2 || !chan.startsWith("#")) {
					connection.msgChannel(config.getChannel(), "Invalid channel: " + chan);
					return;
				}
			}

			config.setBaseChan(config.getChannel()); // The channel JRobo will return to
			String usersList = "";
			for (String chan : CHANSARR) {
				connection.moveToChannel(config.getChannel(), chan);
				if (config.getChannel().equals(config.getBaseChan())) {
					break;
				}
				usersList += ctx.getUsers();
			}
			userArr = usersList.split("\\s");
			connection.moveToChannel(CHANSARR[CHANSARR.length - 1], config.getBaseChan());
		} else {
			// Channel must begin with a # and be at least two characters long
			if (cmdArgs.length() < 2 || !cmdArgs.startsWith("#")) {
				connection.msgChannel(config.getChannel(), "Invalid channel: " + cmdArgs);
				return;
			}
			config.setBaseChan(config.getChannel()); // The channel JRobo will return to
			connection.moveToChannel(config.getChannel(), cmdArgs);
			userArr = ctx.getUsers().split("\\s");
			connection.moveToChannel(cmdArgs, config.getBaseChan());
		}
		final String[] user2Arr = userArr;

		for (String u : userArr) {
			System.out.println("userArr: " + u);
		}

		// Checking if ChanServ has opped JRobo
		String first, last, received;
		for (int tries = 4;;) {

			received = connection.receiveLn();
			if (received == null) {
				break;
			}
			try {
				first = received.split(" :", 2)[0];
				last = received.split(" :", 2)[1];

			} catch (ArrayIndexOutOfBoundsException ex) {
				first = "";
				last = "";
			}
			if (first.equals("PING")) {
				connection.sendLn("PONG " + last);

			} else if (received.equals(":ChanServ!ChanServ@services. MODE " + config.getChannel() + " +o " + config.getName())
				|| tries < 0) {
				break;
			}
			tries--;
		}

		Thread inviteT = new Thread() {
			@Override
			public void run() {

				// Prevent multiple threads from being created
				threadCreated = true;

				for (String invitee : user2Arr) {
					try {
						Thread.sleep(35000);
					} catch (InterruptedException ex) {
						Logger.getLogger(InviteChannelCommand.class.getName()).log(Level.SEVERE, null, ex);
					}

					connection.sendLn("INVITE " + invitee + " " + config.getChannel());
				}
				threadCreated = false;
			}
		};
		inviteT.start();
	}
}
