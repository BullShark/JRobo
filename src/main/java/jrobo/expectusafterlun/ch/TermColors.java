/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
 * Copyright (C) <2013> <Travis Mosley>
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
 */
public class TermColors {

	 /**
	  * Removes all previously applied color and formatting attributes.
	  */
	public static final String ANSI_RESET = "\u001B[0m";

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

	/**
	 * Main method for testing this class
	 *
	 * @param args Is ignored
	 */
	public static void main(String[] args) {
		System.out.println(TermColors.ANSI_BLUE + TermColors.ANSI_RED_BACKGROUND + "Testing testing 123" + TermColors.ANSI_RESET);
	}
}
