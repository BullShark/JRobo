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

import java.util.List;

/**
 *
 * @author Christopher Lemire <christopher.lemire@gmail.com>
 */
public class UrbanJson {
  public int total;
  public String result_type;
  public List<UrbanJsonItem> list;

/**
 *
 * @override
 */
  public String toString() {
    return "Total: " + Integer.toString(total) + " has result_type: " + result_type;
  }
}
