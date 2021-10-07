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
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

public class Leet {

    /* For the HTTP Connection */
    private URL url;
    private URLConnection conn;
    private OutputStreamWriter wr;
    private BufferedReader rd;
    private String fullUrl;

    /*
     * Miscelanous
     *@TODO Fix bug, if the url is not available, the bot will throw an exception and crash
     */
    private static final String QUERY_URL = "http://odin.root.sx/thepiratebay.php";
    private String def;
    private String json;
    private String s_name="blackhats";
    private String s_switch="s";
    private final int MAX_RESULTS = 5;	


	/*
		We can also filter by categories:
			<option value="/category-search/test/Movies/1/">
			<option value="/category-search/test/TV/1/">
			<option value="/category-search/test/Games/1/">
			<option value="/category-search/test/Music/1/">
			<option value="/category-search/test/Apps/1/">
			<option value="/category-search/test/Documentaries/1/">
			<option value="/category-search/test/Anime/1/">
			<option value="/category-search/test/Other/1/">
			<option value="/category-search/test/XXX/1/">

		@app.route('/1337x/<query>/<page>/<category>', methods=['GET'])
		def api_1337x(query, page=1, category=None):
		if request.headers.get("API_KEY") != app.config["API_KEY"]:

	Search example: https://1337x.to/search/example+search/1/

	http://152.89.107.76:5000/thepiratebay/movies/1
    
	http://152.89.107.76:5000/1337x/query/page/category
	*/
	private final String BASE_URL="https://1337x.to/search/"

	/* For the Gson/Json */
	private Gson gson;

	public Leet() {
		/* For the HTTP Connection */
		url = null;
		conn = null;
		fullUrl = "";
		/* Miscelanous */
		json = "";
		/* For the Gson/Json */
		gson = new Gson();
	}

	public String getJson() {
		try {
			/* Create a URL obj from strings */
			fullUrl
				= (QUERY_URL
					+ "?name=" + s_name
					+ "&orderby=" + s_switch
					+ "&limit=" + MAX_RESULTS).replaceAll(" ", "%20");

			url = new URL(fullUrl);

			/* Debug */
			System.out.println(fullUrl);

			conn = url.openConnection();
			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				json = json.concat(line);
			}
			rd.close();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (ConnectException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return json;
	}

	public String getFormattedResult(boolean hasColors) {

		PirateBayJsonItem[] results;

		try {

			gson = new GsonBuilder().setPrettyPrinting().create();
			results = gson.fromJson(this.getJson(), PirateBayJsonItem[].class);

		} catch (IllegalStateException | NullPointerException ex) {
			ex.printStackTrace();
			return "";
		}

		String output = "";

		if (hasColors) {
			for (PirateBayJsonItem result : results) {
				output += result.getColorString();
			}
		} else {
			for (PirateBayJsonItem result : results) {
				output += result.toString();
			}
		}
		return output;
	}

	public String toString() {
		String mystring= name + " <" + tinyurl + "> (" + Size + " S:" + seeders + " L:" + leechers + ") \n";
		return mystring;
	}

	/*
 	 * A main method for testing this class
	 */
	public static void main(String[] args) {
		System.out.println(new Leet("-s matrix reloaded").getFormattedResult(false));
	}
}







package jrobo;



public class PirateBay {



    public PirateBay(String command) {
        String[] splitArray = command.split("\\s+");
        String argument="";
        if (splitArray.length > 0) {
            switch (splitArray[0]) {
                case "-s":
                    argument=splitArray[0];
                    s_switch = "s";
                    break;
                case "-l":
                    argument=splitArray[0];
                    s_switch = "l";
                    break;
                case "-d":
                    argument=splitArray[0];
                    s_switch = "d";
                    break;
                case "-n":
                    argument=splitArray[0];
                    s_switch = "n";
                default:
                    s_switch = "s";
                    break;
            }
            command=command.replace(argument, "");
        }
        if(command.length()>0){
        s_name=command;}

    }



 



    public class PirateBayJsonItem {

        public String type;
        public String name;
        public String url;
        public String tinyurl;
        public String Uploaded;
        public String Size;
        public String ULed;
        public String seeders;
        public String leechers;

        public String getColorString() {
            String mystring=
              MircColors.BOLD + name + " " +
              MircColors.GREEN + "<" + tinyurl + ">" +
              MircColors.NORMAL + MircColors.BOLD + " (" + Size + 
              MircColors.GREEN + " S:" + seeders +
              MircColors.CYAN + " L:" + leechers + 
              MircColors.NORMAL + MircColors.BOLD + ")\n";
             
            return mystring;
        }


    }
}