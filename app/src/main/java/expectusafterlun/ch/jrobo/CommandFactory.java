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

import java.util.HashMap;
import java.util.Map;

/**
 * CommandFactory is responsible for producing the {@link Command}
 * implementation that corresponds to a given command name or alias.
 *
 * This replaces what used to be one large switch statement in BotCommand
 * with a single lookup table built once at startup, so that adding,
 * removing, or re-aliasing a command means touching this one place instead
 * of the dispatch logic itself.
 *
 * Most commands are stateless, so one shared instance per command class is
 * registered under all of its aliases. A few commands need to remember
 * state between invocations (the invite-thread guard, the bomb game), so
 * those instances are constructed once here and reused across calls,
 * exactly as the fields on the old BotCommand were.
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class CommandFactory {

	private final Map<String, Command> commands = new HashMap<>();
	private final Command unknownCommand = new UnknownCommand();

	public CommandFactory() {

		/* Shared state that must be the same across bomb/pass/defuse */
		final BombGame bombGame = new BombGame();

		register(new WakeRoomCommand(), "wakeroom", "wr");
		register(new GoogleCommand(), "google", "lmgtfy", "stfw");
		register(new MoveToChannelCommand(), "goto", "join");
		register(new GreetCommand(), "g", "greet");
		register(new WeatherCommand(), "weather", "w");
		register(new MumCommand(), "mum", "m");
		register(new NextCommand(), "next", "n");
		register(new InviteChannelCommand(), "invite-channel", "ic");
		register(new LeetCommand(), "leet", "leetx");
		register(new UrbanDictCommand(), "urbandict", "ud");
		register(new QuitCommand(), "quit", "q");
		register(new DoNothingCommand(), ".", "..", "-.");
		register(new ListCommand(), "list", "ls", "help", "h");
		register(new PirateCommand(), "pirate", "pi");
		register(new IsUpCommand(), "isup");
		register(new EpicCommand(), "epic", "e");
		register(new VersionCommand(), "version");
		register(new DriveByCommand(), "driveby", "db");
		register(new BombCommand(bombGame), "bomb");
		register(new PassCommand(bombGame), "pass");
		register(new DefuseCommand(bombGame), "defuse");
	}

	private void register(final Command command, final String... aliases) {
		for (String alias : aliases) {
			commands.put(alias, command);
		}
	}

	/**
	 * @param name The command name/alias as typed by the user (e.g. "w", "weather")
	 * @return The Command registered for NAME, or a fallback UnknownCommand
	 * if no command is registered under that name.
	 */
	public Command getCommand(final String name) {
		return commands.getOrDefault(name, unknownCommand);
	}
}
