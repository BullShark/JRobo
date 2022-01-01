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

package expectusafterlun.ch.jrobo;

import java.util.Arrays;

/**
 * Config provides getters and setters to all of the configuration settings.
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 * @author BinaryStroke {@literal <binarystroke@null.net>}
 * @since 2013-02-18
 *
 * BullShark: What is the best way to prevent a config file from being read
 * twice? If I have 15 java files, do I need to pass around the reference to
 * that config class to each java class file, so I won't be running new Config()
 * to get another reference to it, or is there a better way?
 * Diablo-D3: That's... a really odd way of asking that question
 * Diablo-D3: Typically you'd load and parse your config file, you'd have a
 * state object you'd pass to any method that needs it (either as an arg, or as
 * part of the class variables, passed in to that instance via constructor arg)
 */
public class Config {

	private String name;
	private String password;
	private String[] masters;
	private char command_symbol;
	private String network;
	private String proxy_host;
	private String proxy_port;
	private String channel;
	// Chan the bot originally was in before moving between channels
	private String base_chan;
	private String openweathermap_api_key;
	private String torrent_api_key;
	private String torrent_api_host;

	/**
	 * Just a placeholder for this object, doesn't execute any code
	 */
	protected Config() {
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
	 *
	 * @return The proxy address to connect to
	 * @since 2021-11-10
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	protected String getProxyHost() {
		return proxy_host;
	}

	/**
	 *
	 * @return The port that the proxy listens on
	 * @since 2021-11-10
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	protected String getProxyPort() {
		return proxy_port;
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
	 * Also sets the last channel as baseChan
	 *
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @param chan The channel JRobo currently is in or is joining
	 * @since 2013-08-12
	 */
	protected void setChannel(String chan) {
		base_chan = chan;
		channel = chan;
	}

	/**
	 * Channel from where the bot began moving
	 *
	 * @return baseChan is the previous channel
	 */
	protected String getBaseChan() {
		return base_chan;
	}

	/**
	 * Channel from where the bot began moving
	 *
	 * @param base_chan The first channel JRobo goes to before moving to any other channel
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	protected void setBaseChan(String base_chan) {
		this.base_chan = base_chan;
	}

	/**
	 *
	 * @return The weatherUndergound API key
	 * @since 2013-02-18
	 */
	protected String getOpenWeatherMapKey() {
		return openweathermap_api_key;
	}

	/**
	 *
	 * @return The Torrent API key
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @since 2021-10-21
	 */
	protected String getTorrentKey() {
		return torrent_api_key;
	}

    	/**
	 *
	 * @return The Torrent API Host
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 * @since 2021-12-30
	 */
	protected String getTorrentAPIHost() {
		return torrent_api_host;
	}

	/**
	 * A colored for IRC String representation of the Config
	 *
	 * @author Christopher Lemire {@literal <goodbye300@aim.com>}
	 * @return Colored for IRC String representing LeetJsonItem
	 */
	public String getColorString() {

		return (TermColors.colorInfo("\t\tMy name is " + name) 
			+ "\n" + TermColors.colorInfo("\t\tpassword is " + password.replaceAll(".", "*")) 
			+ "\n" + TermColors.colorInfo("\t\tmasters are " + Arrays.toString(masters))
			+ "\n" + TermColors.colorInfo("\t\tcommand_symbol is " + command_symbol)
			+ "\n" + TermColors.colorInfo("\t\tnetwork is " + network)
			+ "\n" + TermColors.colorInfo("\t\tproxy host/port is " + proxy_host + ":" + proxy_port)
			+ "\n" + TermColors.colorInfo("\t\tchannel is " + channel)
			+ "\n" + TermColors.colorInfo("\t\topenweathermap_api_key is " + openweathermap_api_key.replaceAll(".", "*"))
			+ "\n" + TermColors.colorInfo("\t\ttorrent_api_key is " + torrent_api_key.replaceAll(".", "*"))
			+ "\n" + TermColors.colorInfo("\t\ttorrent_api_host is " + torrent_api_host)
			+ "\n" + TermColors.colorInfo("\t\tbase_chan is " + base_chan)
			+ "\n");
	}

	/**
	 *
	 * @return The configured name
	 * @since 2013-02-18
	 * @author Chris Lemire {@literal <goodbye300@aim.com>}
	 */
	@Override
	public String toString() {
		return ("[+++]\t\tMy name is " + name
			+ "\n[+++]\t\tpassword is " + password.replaceAll(".", "*")
			+ "\n[+++]\t\tmasters are " + Arrays.toString(masters)
			+ "\n[+++]\t\tcommand_symbol is " + command_symbol
			+ "\n[+++]\t\tnetwork is " + network
			+ "\n[+++]\t\tproxy host/port is " + proxy_host + ":" + proxy_port
			+ "\n[+++]\t\tchannel is " + channel
			+ "\n[+++]\t\topenweathermap_api_key is " + openweathermap_api_key.replaceAll(".", "*")
			+ "\n[+++]\t\ttorrent_api_key is " + torrent_api_key.replaceAll(".", "*")
			+ "\n[+++]\t\ttorrent_api_host is " + torrent_api_host
			+ "\n[+++]\t\tbase_chan is " + base_chan
			+ "\n");
	}
}
