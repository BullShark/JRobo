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

/**
 * Gabe's quit message for this bot.
 *
 * @author Gabriele Zuddas {@literal <zer0.cam0@gmail.com>}
 */
public class QuitCommand implements Command {

	@Override
	public void execute(final CommandContext ctx) {

		ctx.getConnection().msgChannel(ctx.getConfig().getChannel(), "Detenation devices to nuclear reactors! (Zer0 is pressing the "
			+ "stupid BUTTOnN so GO OUT OF THIS FUCKING CHANNEL BITCHES!!!)");
	}
}
