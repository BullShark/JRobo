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

/**
 * Moves JRobo from the current channel to the channel given as an argument.
 * Only masters (as defined in Config) are authorized to do this.
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class MoveToChannelCommand implements Command {

	@Override
	public void execute(final CommandContext ctx) {

		if (Arrays.asList(ctx.getConfig().getMasters()).contains(ctx.getUser())) {

			ctx.getConnection().moveToChannel(ctx.getConfig().getChannel(), ctx.getCmdArgs());
		} else {

			ctx.getConnection().msgChannel(ctx.getConfig().getChannel(), (ctx.getUser() + " is not authorized!"));
		}
	}
}
