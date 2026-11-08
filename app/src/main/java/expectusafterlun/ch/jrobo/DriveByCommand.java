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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Does a drive-by: joins a channel, spams "lol" a few times, leaves, and
 * repeats -- with sleeps in the loop to avoid flooding.
 *
 * @author Tyler Polard
 */
public class DriveByCommand implements Command {

	@Override
	public void execute(final CommandContext ctx) {

		final Networking connection = ctx.getConnection();
		final Config config = ctx.getConfig();
		final String cmdArgs = ctx.getCmdArgs();
		final String user = ctx.getUser();

		if (cmdArgs.length() < 2 || !cmdArgs.startsWith("#")) {

			connection.msgChannel(config.getChannel(), "Invalid channel: " + cmdArgs);
			return;
		}
		config.setBaseChan(config.getChannel()); // The channel JRobo will return to
		connection.moveToChannel(config.getChannel(), cmdArgs);
		for (int i = 0; 25 >= i; i++) {

			connection.moveToChannel(config.getChannel(), cmdArgs);
			for (int z = 0; 5 >= z; z++) {

				connection.msgChannel(config.getChannel(), "lol");
				try {
					Thread.sleep(1000);

					/* Checking if the command was issued by a master */
					if (Arrays.asList(config.getMasters()).contains(user)) {
						break;
					}
				} catch (InterruptedException ex) {
					Logger.getLogger(DriveByCommand.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			connection.moveToChannel(cmdArgs, config.getBaseChan());
			try {
				Thread.sleep(2500);
				String serverLine = connection.receiveLn();
				if (serverLine == null || serverLine.contains("QUIT :Excess Flood")) {
					break;
				}
			} catch (InterruptedException ex) {
				Logger.getLogger(DriveByCommand.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		// If not in basechannel he will return to Basechannel
		if (config.getChannel() == null ? config.getBaseChan() != null : !config.getChannel().equals(config.getBaseChan())) {

			connection.moveToChannel(cmdArgs, config.getBaseChan());
		}
	}
}
