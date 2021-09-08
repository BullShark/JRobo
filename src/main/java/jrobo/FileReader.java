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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.err;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chris Lemire <goodbye300@aim.com>
 */
public class FileReader {

    /* Standard Java API Classes */
    private static String CONFIGFILE;

    /* User-defined Classes */
    private static Config config;

    /* Miscellaneous */
    private static boolean ranOnce = false;

    public FileReader() {
        CONFIGFILE = "Config.json";
        config = null;
    }

    /**
     * Opens a resource file inside the package and fills the passed ArrayList
     *
     * @param FILENAME The file name inside the package to be opened
     * @param LISTARR The array list to store the file lines to
     * @return A true on success and false on failure
     */
    protected boolean fileToArrayList(final String FILENAME, final ArrayList<String> LISTARR) {
        
        try ( BufferedReader br = new BufferedReader(new InputStreamReader(FileReader.class.getResourceAsStream(FILENAME)));) {
            Thread.dumpStack();

            if (FILENAME.equals(CONFIGFILE)) {
                ranOnce = true;
            }

            out.println("[+++]\tReading File (" + FILENAME + ")");

            String line;

            while ((line = br.readLine()) != null) {
                LISTARR.add(line);
            }

        } catch (IOException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);

        }

        Collections.shuffle(LISTARR);

        return true;
    }

    /**
     * Gets the data from the configuration file
     *
     * @return Returns a Config object, with the settings from Config.json
     * @since 2013-02-18
     */
    protected static Config getConfig() {

        /* 
		 * Should only be null if this method has not already been ran once 
		 * Or the constructor has never been called
         */
        if (config != null || ranOnce) {
            //Thread.dumpStack();
            out.println("[+++]\tReusing Config because it's != null or " + CONFIGFILE + " has already been read once");
            return config;
        }

        out.println("[+++]\tReading Configuration File (" + CONFIGFILE + ")");
        
        out.println("[+++]\t" + new File(CONFIGFILE).getAbsolutePath());
        out.println("[+++]\t" + System.getProperty("user.dir"));
        
        try (
                InputStream fileStream = FileReader.class.getResourceAsStream(CONFIGFILE);
                InputStreamReader fileStreamReader = new InputStreamReader(fileStream);
                BufferedReader br = new BufferedReader(fileStreamReader);
        ) {
            
            Thread.dumpStack();

            String line, json;

            json = "";

            while ((line = br.readLine()) != null) {
                json += line;
            }

            Gson gson = new Gson();
            config = gson.fromJson(json, Config.class);

            ranOnce = true;

        } catch (IOException | JsonSyntaxException | NullPointerException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        // Verifiying important settings for connection
        if (config.getName()
                == null) {
            err.println("[+++]\tError: Unable to find bot's Nickname");
            System.exit(1);
        }

        if (config.getPass()
                == null) {
            err.println("[+++]\tError: Unable to find bot's Password");
            System.exit(1);
        }

        if (config.getMasters()
                == null) {
            err.println("[+++]\tError: Unable to find bot's Masters");
            System.exit(1);
        }

        if (config.getCmdSymb()
                == '\u0000') {
            err.println("[+++]\tError: Unable to find bot's Command Symbol");
            System.exit(1);
        }

        if (config.getNetwork()
                == null) {
            err.println("[+++]\tError: Unable to find bot's Network");
            System.exit(1);
        }

        if (config.getChannel()
                == null) {
            err.println("[+++]\tError: Unable to find bot's Channel");
            System.exit(1);
        }

        if (config.getOpenWeatherMapKey()
                == null) {
            err.println("[+++]\tError: Unable to find bot's OpenWeatherMap API Key");
            //System.exit(1);
        }

        return config;
    } // EOF getConfig()
} // EOF class
