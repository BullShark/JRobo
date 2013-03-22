/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
 * Copyright (C) <2013> <BinaryStroke>
 * Copyright (C) <2013> <Muhammad Sajid>
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

/*
 * This class will define constants and methods for sending colors to channels
 * That have /mode +c allowed in the channel
 *
 * Color codes
 * http://www.weechat.org/files/doc/stable/weechat_user.en.html#command_line_colors
 */
public class MircColors {
   /**
   * Removes all previously applied color and formatting attributes.
   */
  public static final String NORMAL = "\u000f";


  /**
   * Bold text.
   */
  public static final String BOLD = "\u0002";
  

  /**
   * Underlined text.
   */
  public static final String UNDERLINE = "\u001f";


  /**
   * Reversed text (may be rendered as italic text in some clients).
   */
  public static final String REVERSE = "\u0016";
  

  /**
   * White colored text.
   */
  public static final String WHITE = "\u000300";
  

  /**
   * Black colored text.
   */
  public static final String BLACK = "\u000301";
  

  /**
   * Dark blue colored text.
   */
  public static final String DARK_BLUE = "\u000302";
  

  /**
   * Dark green colored text.
   */
  public static final String DARK_GREEN = "\u000303";
  

  /**
   * Red colored text.
   */
  public static final String RED = "\u000304";
  

  /**
   * Brown colored text.
   */
  public static final String BROWN = "\u000305";
  

  /**
   * Purple colored text.
   */
  public static final String PURPLE = "\u000306";
  

  /**
   * Olive colored text.
   */
  public static final String OLIVE = "\u000307";
  

  /**
   * Yellow colored text.
   */
  public static final String YELLOW = "\u000308";
  

  /**
   * Green colored text.
   */
  public static final String GREEN = "\u000309";
  

  /**
   * Teal colored text.
   */
  public static final String TEAL = "\u000310";
  

  /**
   * Cyan colored text.
   */
  public static final String CYAN = "\u000311";
  

  /**
   * Blue colored text.
   */
  public static final String BLUE = "\u000312";
  

  /**
   * Magenta colored text.
   */
  public static final String MAGENTA = "\u000313";
  

  /**
   * Dark gray colored text.
   */
  public static final String DARK_GRAY = "\u000314";
  

  /**
   * Light gray colored text.
   */
  public static final String LIGHT_GRAY = "\u000315";
}