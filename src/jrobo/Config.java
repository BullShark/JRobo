/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    public String[] hostmasks;
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
