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
 * Executed when the list bot command is received by JRobo. Lists the bot
 * commands syntax in a help message.
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class ListCommand implements Command {

	/**
	 * @param ctx The invocation context; always sent with colors, matching
	 * the original listHelper(true) call site.
	 */
	@Override
	public void execute(final CommandContext ctx) {
		execute(ctx, true);
	}

	/**
	 * @param ctx The invocation context
	 * @param hasColors Whether to send the mIRC-colored version of the list
	 * or a plain-text version with color/attribute codes stripped
	 */
	protected void execute(final CommandContext ctx, final boolean hasColors) {

		final ListColors LC = ctx.getListColors();

		/*
		 * GREEN = dark color
		 * CYAN = light color
		 *
		 * cmds|alias = dark
		 * flags = dark
		 * args = light
		 * special characters = no bold, no color
		 * such as [] <> , ...
		 *
		 * TODO: driveby db bomb pass defuse
		 */
		String colorStr = LC.attributesSynopsisLine(LC.colorToken("Available commands: ", MircColors.BOLD)
			+ LC.colorToken("google|lmgtfy|stfw ", MircColors.GREEN)
			+ LC.colorToken("<search query>, ", MircColors.CYAN)
			+ LC.colorToken("greet|g ", MircColors.GREEN)
			+ LC.colorToken("[user], ", MircColors.CYAN)
			+ LC.colorToken("wakeroom|wr, ", MircColors.GREEN)
			+ LC.colorToken("weather|w ", MircColors.GREEN)
			+ LC.colorToken("<location, zip, etc.>, ", MircColors.CYAN)
			+ LC.colorToken("urbandict|ud ", MircColors.GREEN)
			+ LC.colorToken("<search query>, ", MircColors.CYAN)
			+ LC.colorToken("list|ls, ", MircColors.GREEN)
			+ LC.colorToken("leet|leetx ", MircColors.GREEN)
			+ LC.colorToken("<catgegory> <search query>, " + MircColors.NORMAL, MircColors.CYAN)
			+ LC.colorToken(MircColors.BOLD + "(" + MircColors.CYAN + "Avaliable categories are:  Movies, TV, Games, " + MircColors.NORMAL + MircColors.BOLD + MircColors.CYAN + "Music, Apps, Documentaries, Anime, Other, XXX, All" + MircColors.WHITE + "), ", MircColors.WHITE)
			+ LC.colorToken("help|h ", MircColors.GREEN)
			+ LC.colorToken("[cmd], ", MircColors.CYAN)
			+ LC.colorToken("next|n, ", MircColors.GREEN)
			+ LC.colorToken("mum|m ", MircColors.GREEN)
			+ LC.colorToken("[user], ", MircColors.CYAN)
			+ LC.colorToken(MircColors.NORMAL + MircColors.BOLD + MircColors.GREEN + "invite-channel|ic ", "")
			+ LC.colorToken("<#channel>, ", MircColors.CYAN)
			+ LC.colorToken("goto|join ", MircColors.GREEN)
			+ LC.colorToken("<#channel>, ", MircColors.CYAN)
			+ LC.colorToken("pirate|pi ", MircColors.GREEN)
			+ LC.colorToken("[-s|-l|-d] ", MircColors.CYAN)
			+ LC.colorToken("<search query>, ", MircColors.CYAN)
			+ LC.colorToken("epic|e, ", MircColors.GREEN)
			+ LC.colorToken("isup ", MircColors.GREEN)
			+ LC.colorToken("<url>, ", MircColors.CYAN)
			+ LC.colorToken(MircColors.NORMAL + MircColors.BOLD + MircColors.GREEN + "version, ", MircColors.GREEN)
			+ LC.colorToken("quit|q", MircColors.GREEN));

		if (hasColors) {

			ctx.getConnection().msgChannel(ctx.getConfig().getChannel(), colorStr);
		} else {

			String noColorStr = colorStr.replaceAll("(\\P{Print}|[0-9]{2})", "");
			ctx.getConnection().msgChannel(ctx.getConfig().getChannel(), noColorStr);
		}
	}
}
