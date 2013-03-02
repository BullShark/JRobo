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
   * Colors a SYNPOSIS line
   * @param line
   * A line in the form: command|cmdalias <required> 
   * @return
   */
  public String colorSynopsisLine(String line) {

    /* Debug */
    System.out.println("GREEN code length: " + super.GREEN.length());
    System.out.println("CYAN  code length: " + super.CYAN.length());

    System.out.println("GREEN code: " + super.GREEN.replaceAll(GREEN, "GREEN"));
    System.out.println("GREEN code: " + super.CYAN.replaceAll(CYAN, "CYAN"));

    /* http://i.imagebanana.com/img/n8316h4e/manman_001.png
     * Syntax:
     * command|cmdalias 
     */
    String colorStr = "", lastColorCode = null;
    int codePoint = -1;
    char ch;
    for(int x=0; x < line.length(); x++) {
      ch = line.charAt(x);

      if(ch == '\u0003') {
        lastColorCode = line.substring(x, (x+3));
        switch(lastColorCode) {
          case GREEN:
            System.out.println("code: GREEN");
            break;
          case CYAN:
            System.out.println("code: CYAN");
            break;
          default:
            System.out.println("Unknown code:" + lastColorCode);
        }
      }

      if(ch == '|' || ch == '<' || ch == '>' ||
         ch == '[' || ch == ']') {
        /* Debug */
//      colorStr += MAGENTA + line.substring(x, (x+1)) + NORMAL + BOLD;
        colorStr += NORMAL + line.substring(x, (x+1)) + BOLD;
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
   *
   * @param str
   * @return
   */
  public String attributeBold(String str) {
      return BOLD + str + NORMAL;
  }

  /**
   *
   * @param str
   * @param bold
   * @return
   */
  public String colorGreen(String str, boolean bold) {
    if(bold) {
      return BOLD + GREEN + str + NORMAL;
    } else {
      return GREEN + str + NORMAL;
    }
  }

  /**
   *
   * @param str
   * @param bold
   * @return
   */
  public String colorCyan(String str, boolean bold) {
    if(bold) {
      return BOLD + CYAN + str + NORMAL;
    } else {
      return CYAN + str + NORMAL;
    }
  }

  /**
   *
   * @param colorCode
   * @param bold
   * @return
   */
  public String color(String str, String colorCode, boolean bold) {
    if(bold) {
      return BOLD + colorCode + str + NORMAL;
    } else {
      return colorCode + str + NORMAL;
    }
  }

  /**
   *
   * @param str
   * @return
   */
  public String colorToken(String str, String colorCode) {
    // [-s|-l|-d]
    // NORMAL BOLD CYAN "[-s" NORMAL "|" BOLD CYAN "-l" NORMAL "|" BOLD CYAN "-d]
    // 
    String colorStr = "";
    if(str.contains("|")) {
      String[] strArr = str.split("|");

    // NORMAL BOLD GREEN  NORMAL | NORMAL | NORMAL | NORMAL | NORMAL | NORMAL | NORMAL | NORMAL | NORMAL | NORMAL | NORMAL BOLD GREEN  
      for(int x=0; x<strArr.length; x++) {
        if(x == 0 || x == strArr.length-1) {
          colorStr += " NORMAL " + "BOLD" + " GREEN " + strArr[x];
        } else {
          colorStr += " NORMAL " + "|";
        }
      }

    }
//    System.out.println(colorStr);
  //  return colorStr;
    return NORMAL + BOLD + colorCode + str;
  }
}
