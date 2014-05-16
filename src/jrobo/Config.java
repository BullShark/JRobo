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

/**
 *
 * @author BinaryStroke <binarystroke@null.net>
 * @since 2013-02-18
 */
public class Config {

  private String name;
  private String password;
  private String[] masters;
  private boolean ssl_enabled;
  private char command_symbol;
  private String network;
  private String channel;
  private String wunderground_key;
  // Chan the bot originally was in before moving between channels
  private String baseChan;

  public Config() {
    baseChan = channel;
  }

  /**
   *
   * @return The bot's name
   * @since 2013-02-18
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @return The bot's password
   * @since 2013-02-18
   */
  public String getPass() {
    return password;
  }

  /**
   *
   * @return The bot's masters nicknames in an array
   * @since 2013-02-18
   */
  public String[] getMasters() {
    return masters;
  }
  
    /**
   * 
   * @return the bot's connection type (ssl: yes/no)
   * @since 2014-03-09
   */
  public boolean getConnectionType() {
    return ssl_enabled;
  }

  /**
   *
   * @return The bot's command symbol
   * @since 2013-02-18
   */
  public char getCmdSymb() {
    return command_symbol;
  }

  /**
   *
   * @return The network to be connected
   * @since 2013-02-18
   */
  public String getNetwork() {
    return network;
  }

  /**
   * Represents the current channel
   *
   * @return The channel to be joined
   * @since 2013-02-18
   */
  public String getChannel() {
    return channel;
  }

  /**
   * Represents the current channel
   *
   * @param chan
   * @since 2013-08-12
   */
  public void setChannel(String chan) {
    channel = chan;
  }

  /**
   *
   * @return The weatherUndergound API key
   * @since 2013-02-18
   */
  public String getWundergroundKey() {
    return wunderground_key;
  }

  /**
   * Channel from where the bot began moving
   *
   * @return
   */
  public String getBaseChan() {
    return baseChan;
  }

  /**
   * Channel from where the bot began moving
   *
   * @param baseChan
   */
  public void setBaseChan(String baseChan) {
    this.baseChan = baseChan;
  }

  /**
   *
   * @return The configured name
   * @since 2013-02-18
   */
  @Override
  public String toString() {
    return "My name is " + name;
  }
}
