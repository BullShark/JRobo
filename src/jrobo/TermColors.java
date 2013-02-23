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

package jrobo;

/**
 *
 * @author bullshark
 * @author DrChaos
 */
public class TermColors {
    private enum BGColors {
      BLACK = 40, RED, GREEN,
      YELLOW, BLUE, MAGENTA,
      CYAN, WHITE
    };
    private enum FGColors {
      BLACK = 30, RED, GREEN,
      YELLOW, BLUE, MAGENTA,
      CYAN, WHITE
    };
    public enum Colors {
      BLACK = 0, RED, GREEN,
      YELLOW, BLUE, MAGENTA,
      CYAN, WHITE
    };
}
