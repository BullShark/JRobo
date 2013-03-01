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
package jrobo;

/**
 * Helper class for coloring the output of the list command
 * @author Christopher Lemire <christopher.lemire@gmail.com>
 */
public class ListColors extends MircColors {
  /* Get all inherited members */
  public ListColors() {
    super();
  }

  /**
   * Colors a String in the form: cmd 
   * @param line
   * @return
   */
  public String colorSynopsisLine(String line) {
    /* Colors these lines and more:
     * 
     * Available commands: 
     * google|g|lmgtfy|stfw <search query>, 
     * wakeroom|wr, weather|w <location, zip, etc.>, 
     * urbandict|ud <search query, list|l, raw|r <raw irc line> help|h [cmd], 
     * next|n, mum|m [user], invite-channel|ic <channel>, 
     * invite-nick|in <nick> [# of times], 
     * pirate [-s|-l|-d] <search query>, 
     * isup <url>, 
     * version, 
     * quit|q
     */

    return "";
  }

}
