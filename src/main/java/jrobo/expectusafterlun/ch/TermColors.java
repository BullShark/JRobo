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

package jrobo.expectusafterlun.ch;

/**
 * Makes color formatting the console output more user-friendly
 *
 * TODO Missing: BRIGHT, DIM, UNDERLINE, BLINK, REVERSE, and HIDDEN
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class TermColors {

	/*******************************************************************
	 * Reset
	 *******************************************************************/

	 /**
	  * Removes all previously applied color and formatting attributes.
	  */
	public static final String ANSI_RESET = "\u001B[0m";

	/*******************************************************************
	 * Regular Colors
	 *******************************************************************/

	/**
	 * Black colored text.
	 */
	public static final String ANSI_BLACK = "\u001B[30m";

	/**
	 * Red colored text.
	 */
	public static final String ANSI_RED = "\u001B[31m";

	/**
	 * Green colored text.
	 */
	public static final String ANSI_GREEN = "\u001B[32m";

	/**
	 * Yellow colored text.
	 */
	public static final String ANSI_YELLOW = "\u001B[33m";

	/**
	 * Blue colored text.
	 */
	public static final String ANSI_BLUE = "\u001B[34m";
		
	/**
	 * Purple colored text.
	 */
	public static final String ANSI_PURPLE = "\u001B[35m";

	/**
	 * Cyan colored text.
	 */
	public static final String ANSI_CYAN = "\u001B[36m";

	/**
	 * White colored text.
	 */
	public static final String ANSI_WHITE = "\u001B[37m";

	/*******************************************************************
	 * Bold
	 *******************************************************************/

	public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
	public static final String RED_BOLD = "\033[1;31m";    // RED
	public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
	public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
	public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
	public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
	public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
	public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

	/*******************************************************************
	 * Underline
	 *******************************************************************/

	public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
	public static final String RED_UNDERLINED = "\033[4;31m";    // RED
	public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
	public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
	public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
	public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
	public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
	public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

	/*******************************************************************
	 * Background
	 *******************************************************************/

	/**
	 * Black colored background.
	 */
	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";

	/**
	 * Red colored background.
	 */
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

	/**
	 * Green colored background.
	 */
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";

	/**
	 * Yellow colored background.
	 */
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";

	/**
	 * Blue colored background.
	 */
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";

	/**
	 * Purple colored background.
	 */
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";

	/**
	 * Cyan colored background.
	 */
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";

	/**
	 * White colored background.
	 */
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	/*******************************************************************
	 * High Intensity
	 *******************************************************************/

	public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
	public static final String RED_BRIGHT = "\033[0;91m";    // RED
	public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
	public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
	public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
	public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
	public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
	public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

	/*******************************************************************
	 * Bold High Intensity
	 *******************************************************************/

	public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
	public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
	public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
	public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
	public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
	public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
	public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
	public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

	/*******************************************************************
	 * High Intensity backgrounds
	 *******************************************************************/

	public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
	public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
	public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
	public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
	public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
	public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
	public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
	public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE



	


	/* TODO Use me, 3 methods below */

	/**
	 * This method means to change the color, it only needs to be done here, not in 150 different places, change once
	 * <p>
	 * [***] means output
	 * [---] means input
	 * [+++] means info, neither output or input
	 * 
	 * @param MSG The console server info message to color
	 * @return The colored info line
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	public static String colorInfo(final String MSG) {

		return TermColors.ANSI_GREEN + "[+++]" + TermColors.ANSI_RESET + "\t" + MSG;
	}

	/**
	 * This method means to change the color, it only needs to be done here, not in 150 different places, change only once
	 *
	 * @param MSG The console server output message to color
	 * @return The colored server output line
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	public static String colorOut(final String MSG) {

		return TermColors.ANSI_RED + "[***]" + TermColors.ANSI_RESET + "\t" + MSG;
	}

	/**
	 * This method means to change the color, it only needs to be done here,
	 * not in 150 different places, change only once
	 *
	 * @param MSG The console server input message to color
	 * @return The colored server input line
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	public static String colorIn(final String MSG) {

		return String.format(TermColors.ANSI_BLUE + "[---]" + TermColors.ANSI_RESET + "\t%s", MSG);
	}

	/**
	 * Main method for testing this class
	 *
	 * @param args Is ignored
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	public static void main(String[] args) {
		System.out.println(TermColors.ANSI_BLUE + TermColors.ANSI_RED_BACKGROUND + "Testing testing 123" + TermColors.ANSI_RESET);
	}
}
