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

import java.util.Arrays;

/**
 *
 * @author Chris Lemire <goodbye300@aim.com>
 * @author BinaryStroke <binarystroke@null.net>
 * @since 2013-02-18
 *
 * @TODO Make this class, variables or methods static,
 * @TODO so the same data can be accessed from multiple classes
 *
 * <BullShark_> What is the best way to prevent a config file from being read
 * twice? If I have 15 java files, do I need to pass around the reference to
 * that config class to each java class file, so I won't be running new Config()
 * to get another reference to it, or is there a better way?
 * <Diablo-D3> That's... a really odd way of asking that question
 * <Diablo-D3> Typically you'd load and parse your config file, you'd have a
 * state object you'd pass to any method that needs it (either as an arg, or as
 * part of the class variables, passed in to that instance via constructor arg)
 */
public class Config {

	private String name;
	private String password;
	private String[] masters;
	private char command_symbol;
	private String network;
	private String channel;
	private String openweathermap_apikey;
	// Chan the bot originally was in before moving between channels
	private String baseChan;

	protected Config() {
		baseChan = channel;
	}

	/**
	 *
	 * @return The bot's name
	 * @since 2013-02-18
	 */
	protected String getName() {
		return name;
	}

	/**
	 *
	 * @return The bot's password
	 * @since 2013-02-18
	 */
	protected String getPass() {
		return password;
	}

	/**
	 *
	 * @return The bot's masters nicknames in an array
	 * @since 2013-02-18
	 */
	protected String[] getMasters() {
		return masters;
	}

	/**
	 *
	 * @return The bot's command symbol
	 * @since 2013-02-18
	 */
	protected char getCmdSymb() {
		return command_symbol;
	}

	/**
	 *
	 * @return The network to be connected
	 * @since 2013-02-18
	 */
	protected String getNetwork() {
		return network;
	}

	/**
	 * Represents the current channel
	 *
	 * @return The channel to be joined
	 * @since 2013-02-18
	 */
	protected String getChannel() {
		return channel;
	}

	/**
	 * Represents the current channel
	 *
	 * @param chan
	 * @since 2013-08-12
	 */
	protected void setChannel(String chan) {
		channel = chan;
	}

	/**
	 *
	 * @return The weatherUndergound API key
	 * @since 2013-02-18
	 */
	protected String getOpenWeatherMapKey() {
		return openweathermap_apikey;
	}

	/**
	 * Channel from where the bot began moving
	 *
	 * @return
	 */
	protected String getBaseChan() {
		return baseChan;
	}

	/**
	 * Channel from where the bot began moving
	 *
	 * @param baseChan
	 */
	protected void setBaseChan(String baseChan) {
		this.baseChan = baseChan;
	}

	/**
	 *
	 * @return The configured name
	 * @since 2013-02-18
	 * @FIXME The regex back reference is not working
	 */
	@Override
	public String toString() {
		return ("My name is " + name
			+ "\npassword is " + password.replaceAll(".", "*")
			+ "\nmasters are " + Arrays.toString(masters)
			+ "\ncommand_symbol is " + command_symbol
			+ "\nnetwork is " + network
			+ "\nchannel is " + channel
			+ "\nopenweathermap_apikey is " + openweathermap_apikey.replaceAll(".", "*")
			+ "\nbaseChan is " + baseChan).replaceAll("[\\n^]", "\\0[+++]\t\t");
	}
}
