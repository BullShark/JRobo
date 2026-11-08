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
 * Passes the active bomb to another user. Delegates to the shared
 * {@link BombGame} instance so state is consistent with BombCommand and
 * DefuseCommand.
 *
 * @author Tyler Polard
 */
public class PassCommand implements Command {

	private final BombGame bombGame;

	public PassCommand(final BombGame bombGame) {
		this.bombGame = bombGame;
	}

	@Override
	public void execute(final CommandContext ctx) {
		bombGame.pass(ctx);
	}
}
