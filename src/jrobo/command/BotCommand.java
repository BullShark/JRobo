/*
 * JRobo - An Advanced IRC Bot written in Java
 *
 * Copyright (C) <2013> <Christopher Lemire>
 * Copyright (C) <2013> <BinaryStroke>
 * Copyright (C) <2013> <Tyler Pollard>
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
//TODO javadoc all
// http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html
package jrobo.command;

import jrobo.Networking;
import jrobo.Config;
import jrobo.JRobo;

public abstract class BotCommand {
  private JRobo jRobo;

  public BotCommand() {
  }

  public void privmsg(String target, String msg) {
    jRobo.getConnection().sendln("PRIVMSG " + target + " :" + msg);
  }

  public void setJRobo(JRobo jRobo) {
    this.jRobo = jRobo;
  }

  public abstract void execute(String target, String[] args);
}
