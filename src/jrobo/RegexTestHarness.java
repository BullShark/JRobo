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

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.lang.System.out;

public class RegexTestHarness {
  public static void main(String[] args){
    String regex = "", search = "";
    Scanner scan = new Scanner(System.in);
    while(true) {

      out.print("\nEnter your regex: ");
      regex = scan.nextLine();

      out.print("Enter input string to search: ");
      search = scan.nextLine();

      Pattern pattern = Pattern.compile(regex);

      Matcher matcher = pattern.matcher(search);

      boolean found = false;
      while(matcher.find()) {
        out.printf("\nMatch found: \"%s\"\n\"%s\"\n",matcher.group(), search);
          found = true;
          int i;
          /* Move cursor under first char in match */
          for(i = -1; i < matcher.start(); i++) { out.print(" "); }
          /* Print carrot under first char in match */
          out.print('^');
          if(matcher.start() < matcher.end()) {
            /* Move cursor to end char in match */
            int matchLength = matcher.end() - matcher.start();
            for(i = 1; i < matchLength-1;i++) { out.print(" "); }
            /* Print carrot under end char in match */
            out.print('^');
          }

          out.println();
//          out.println("matcher.start: " + matcher.start() + ", matcher.end: " + matcher.end() + ", i: " + i);
      }
      if(!found) {
        out.println("\nNo match found.");
      }
    } // EOF while
  } // EOF main

  public static String re(String javaRegEx, String searchStr) {
    Pattern pattern = Pattern.compile(javaRegEx);
    Matcher matcher = pattern.matcher(searchStr);

    boolean found = false;
    String result = "";
    while(matcher.find()) {
      result.concat("I found the text \"" + matcher.group() +
        "\" starting at index " + matcher.start() +
        " and ending at index " + matcher.end() + ".\n");

      found = true;
    }

    if(found) {
      return "No match found.";
    } else {
      return result;
    }
  } // EOF function
} // EOF classdr    