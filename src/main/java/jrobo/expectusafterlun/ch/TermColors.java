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
 */
public class TermColors {
    static StringBuilder sbuilder;
   
    public static enum Color {
        BLACK(0), RED(1), GREEN(2),
        YELLOW(3), BLUE(4), MAGENTA(5),
        CYAN(6), WHITE(7);

        private int value;    

        private Color(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    } 

    public static enum Attribute {
        RESET(0), BRIGHT(1), DIM(2),
        UNDERLINE(3), BLINK(4),
        REVERSE(7), HIDDEN(8);
        private int value;    

        private Attribute(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    public static String getString(String str, Color fg, Color bg, Attribute attrib) {
        sbuilder = new StringBuilder();
        sbuilder.append(0x1B);
        sbuilder.append('[');

        sbuilder.append(attrib.getValue());
        sbuilder.append(';');

        sbuilder.append(fg.getValue() + 30);
        sbuilder.append(';');

        sbuilder.append(bg.getValue() + 40);
        sbuilder.append(';');

        sbuilder.append('m');
        
        sbuilder.append(str);

        // Reset attributes and colors
        sbuilder.append(0x1B);
        sbuilder.append('[');
        sbuilder.append(Attribute.RESET.getValue());
        sbuilder.append('m');

        return sbuilder.toString();
    }

    public static void main(String[] args) {
        System.out.println(getString("Testing testing 123", Color.RED, Color.YELLOW, Attribute.UNDERLINE));
    }
}
