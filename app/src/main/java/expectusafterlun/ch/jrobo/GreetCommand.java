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
 * Helper method to the Greet Command.
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class GreetCommand implements Command {

	@Override
	public void execute(final CommandContext ctx) {

		try {
			if (!ctx.hasArgs()) {
				ctx.getConnection().msgChannel(ctx.getConfig().getChannel(), ctx.getJoke().getPhoneNumber(ctx.getRandChanUser()));
			} else {
				/*
				 * If temp is -1, that means there is no space
				 * We are getting the user for the joke
				 */
				int temp = ctx.getCmdArgs().indexOf(' ');
				if (temp != -1) {
					ctx.getConnection().msgChannel(ctx.getConfig().getChannel(), ctx.getJoke().getPhoneNumber(ctx.getCmdArgs().substring(0, temp)));
				} else {
					ctx.getConnection().msgChannel(ctx.getConfig().getChannel(), ctx.getJoke().getPhoneNumber(ctx.getCmdArgs()));
				}
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
			Logger.getLogger(GreetCommand.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
