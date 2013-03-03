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
   * Removes colors and bold from special characters to have similar output
   * as the man pages viewed with most pager.
   * @param line A line in the form: command|cmdalias [option] <required>, ...
   * @return
   */
  public String attributesSynopsisLine(String line) {

    /* 
     * Color and attributes similar to man pages:
     * http://i.imagebanana.com/img/n8316h4e/manman_001.png
     */
    String colorStr = "", lastColorCode = null;
    char ch;
    for(int x=0; x < line.length(); x++) {
      ch = line.charAt(x);

      if(ch == '\u0003') {
        lastColorCode = line.substring(x, (x+3));
      }

      if(ch == '|' || ch == '<' || ch == '>' ||
         ch == '[' || ch == ']' || ch == ',') {
        /* Uncomment to get no bold for special chars, same as man pages */
//        colorStr += NORMAL + line.substring(x, (x+1)) + BOLD;
        //TODO BOLD should only be needed one time
//        colorStr += NORMAL + BOLD + line.substring(x, (x+1));
        /* Debug, testing */
        colorStr += NORMAL + line.substring(x, (x+1));
        if(lastColorCode != null) {
          colorStr += lastColorCode;
        }
      } else {
        colorStr += line.substring(x, (x+1));
      }
    }

    return colorStr;
  }

  /**
   * Colors a string for IRC and adds bold to it
   * 
   * Other color coded Strings can be concatenated with this because 
   * It does a reset on codes before adding new ones
   * 
   * @param str Takes one color code such as the constants in MircColors
   * @param colorCode The code or attribute code to use for this text
   * @return A color coded line with the bold attribute
   */
  public String colorToken(String str, String colorCode) {
    return colorCode + str;
//    return NORMAL + colorCode + str; //FIXME this NORMAL should not be needed
  }

  /**
   * Colors a string for IRC and adds bold to it
   * 
   * Other color coded Strings can be concatenated with this because 
   * It does a reset on codes before adding new ones
   * 
   * @param str Takes one color code such as the constants in MircColors
   * @param colorCode
   * @param noReset Whether or not to use place a color reset
   * @return
   */
  //TODO Delete me, probably not needed, NORMAL can be passed to the above method if needed anyway
  public String colorToken(String str, String colorCode, boolean noReset) {
    if(noReset) {
      return colorCode + str;
    } else {
      return NORMAL + colorCode + str;
    }
  }
}
