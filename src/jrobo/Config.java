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
    public String name;
    public String password;
    public String[] masters;
    public String[] hostmasks; //TODO Remove when masters is working
    public char command_symbol;
    public String network;
    public String channel;
    public String wunderground_key;
    
    /**
     * 
     * @return The bot's name
     * @since 2013-02-18
     */
    public String getName () {
        return name;
    }
    
    /**
     * 
     * @return The bot's password
     * @since 2013-02-18
     */
    public String getPass () {
        return password;
    }
    
    /**
     * 
     * @return The bot's masters nicknames in an array
     * @since 2013-02-18
     */
    public String[] getMasters () {
        return masters;
    }
    
    /**
     * 
     * @return The hostmasks in an array
     */
    public String[] getHostmasks() {
        return hostmasks;
    }
    
    /**
     * 
     * @return The bot's command symbol
     * @since 2013-02-18
     */
    public char getCmdSymb () {
        return command_symbol;
    }
    
    /**
     * 
     * @return The network to be connected
     * @since 2013-02-18
     */
    public String getNetwork () {
        return network;
    }
    
    /**
     * 
     * @return The channel to be joined
     * @since 2013-02-18
     */
    public String getChannel () {
        return channel;
    }
    
    /**
     * 
     * @return The weatherUndergound API key
     * @since 2013-02-18
     */
    public String getWundergroundKey (){
        return wunderground_key;
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
