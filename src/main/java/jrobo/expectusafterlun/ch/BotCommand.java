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
package jrobo.expectusafterlun.ch;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles all bot commands with a switch and case as well as helper methods for each command
 * 
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 */
public class BotCommand {

	private final Networking CONNECTION;
	private final Config CONFIG;
	private final JRobo JROBO;
	private String user;
	private String bombHolder;
	private String cmd;
	private String cmdArgs;
	private boolean hasArgs;
	private final ListColors LC;
	private boolean threadCreated;
	private boolean bombActive;
	private final boolean[] WIRE;
	private final Jokes JOKE;

	/**
	 * Constructor that initializes all globals of BotCommand and takes 3 arguments
	 * 
	 * @param CONNECTION Takes the CONNECTION created by JRobo.
	 * @param CONFIG A Config object represents the bots configuration read in from Config.json
	 * @param JROBO Class containing the main method for this bot
	 */
	public BotCommand(final Networking CONNECTION, final Config CONFIG, final JRobo JROBO) {

		/* Objects */
		this.CONNECTION = CONNECTION;
		this.CONFIG = CONFIG;
		this.JROBO = JROBO;
		JOKE = new Jokes(this.CONNECTION, this.CONFIG.getChannel());

		/* Cmds */
		cmd = "";
		cmdArgs = "";
		hasArgs = false;

		/* Misc */
		LC = new ListColors();
		threadCreated = false;

		/* Bomb game */
		WIRE = new boolean[3];
		bombActive = false;
		bombHolder = "nobody";

	}

	/**
	 * This is called when a bot command is received
	 *
	 * @param USER The user mentioned by a command given to JRobo
	 * @param FULLCMD The full command not split into segments
	 * TODO Accept raw irc commands from bot owner to be sent by the bot
	 * TODO Search for bots on irc and watch their behavior for ideas such as WifiHelper in #aircrack-ng
	 */
	protected void bCommander(final String USER, final String FULLCMD) {
		this.user = USER;
		cmd = getCmd(FULLCMD);
		cmdArgs = getCmdArgs(FULLCMD);
		hasArgs = cmdArgs.isEmpty() ? false : true;

		switch (cmd) {
			case "wakeroom":
			/* Requires no args */
			case "wr":
				wakeRoomHelper();
				break;
			case "google":
			case "lmgtfy":
			case "stfw":
				/* Show The Fucking World */
				googleHelper();
				break;
			case "goto":
			case "join":
				moveToChannelHelper();
			case "g":
			case "greet":
				greetHelper();
				break;
			case "weather":
			case "w":
				weatherHelper();
				break;
			case "mum":
			case "m":
				mumHelper();
				break;
			case "next":
				nextHelper();
				break;
			case "invite-nick":
			case "in":
				inviteNickHelper();
				break;
			case "invite-channel":
			case "ic":
				inviteChannelHelper();
				break;
			case "leet":
			case "leetx":
				leetHelper();
				break;
			case "raw":
			case "r":
				rawHelper();
				break;
			case "urbandict":
			case "ud":
				urbanDictionaryHelper();
				break;
			case "quit":
			case "q":
				quitHelper();
				break;
			case ".":
			case "..":
			case "-.":
				doNothingHelper();
				break;
			case "list":
			case "l":
			case "help":
			case "h":
				listHelper();
				break;
			case "pirate":
				pirateHelper();
				break;
			case "isup":
				isUpHelper();
				break;
			case "epic":
			case "e":
				epicHelper();
				break;
			case "version":
				versionHelper();
				break;
			case "driveby":
			case "db":
				driveBy();
				break;
			case "bomb":
				bomb();
				break;
			case "pass":
				pass();
				break;
			case "defuse":
				defuse();
				break;
			default:
				unknownCmdHelper();
		} // EOF switch
	} // EOF function

	/**
	 * Puts together a String in the form "test+a+b+c"
	 *
	 * @return Returns a new String from the String argument by removing all starting and ending whitespace, and then replacing all other whitespace, no matter the length of that whitespace, with one '+'.
	 */
	private String getFormattedQuery(final String STR) {
		return STR.trim().replaceAll("\\s++", "+");
	}

	/**
	 * Takes the full command "command arg1 arg2 arg3" and returns "arg1 arg2 arg3"
	 * 
	 * @param FULLCMD The full command given to the bot
	 * @return The arguments only excluding the command given to the bot
	 */
	private String getCmdArgs(final String FULLCMD) {
		
		try {
			return FULLCMD.split("\\" + CONFIG.getCmdSymb() + "[a-zA-Z_0-9\\-]++", 2)[1].trim();
		} catch (ArrayIndexOutOfBoundsException ex) {
			/* Means no args!!! */
			Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
			return "";
		} // EOF try-catch
	} // EOF function

	/**
	 * Takes the full command "cmd arg1 arg2 arg3" and returns "cmd"
	 *
	 * @param The full command including the command and all arguments given to the bot
	 * @return Only the command without the arguments given to the bot
	 */
	private String getCmd(final String FULLCMD) {

		try {
			return FULLCMD.substring(1).replaceFirst("\\s.*+", "");

		} catch (IndexOutOfBoundsException ex) {
			Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
			return "";
			
		}
	}

	/**
	 * @return Returns a random user in the chat. If that fails such as when the user list is empty, it returns ChanServ.
	 */
	private String getRandChanUser() {

		String[] usersList;

		usersList = getUsers().split("\\s++");

		try {
			// Random array index
			int randIndex = (int) (Math.random() * usersList.length);
			return usersList[randIndex];

		} catch(NullPointerException | ArrayIndexOutOfBoundsException ex) {
			Logger.getLogger( BotCommand.class.getName() ).log( Level.SEVERE, null, ex );
			return "ChanServ";

		}
	}

	/**
	 * Wrapper to getUsers(String chan) that uses channel defined by Config
	 *
	 * @return Returns the list of users in the channel defined by Config.getChannel()
	 */
	private String getUsers() {
		return getUsers(CONFIG.getChannel());
	}

	/**
	 * @param CHAN The channel to get a list of users
	 * @return all users that are in the channel CHAN
	 */
	private String getUsers(final String CHAN) {
		String received = "", users = "";
		String first = "", last = "";
		int tries = 8;

		CONNECTION.sendln("NAMES " + CHAN);
		do {
			received = CONNECTION.recieveln();
			try {
				first = received.split(" :", 2)[0];
				last = received.split(" :", 2)[1];
			} catch (ArrayIndexOutOfBoundsException ex) {
				//Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
				first = "";
				last = "";
			}
			if (first.contains("353")) {
				try {
					users += last.replaceAll("@|\\+|&|~|%", "");
				} catch (ArrayIndexOutOfBoundsException ex) {
					Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else if (first.equals("PING")) {
				CONNECTION.sendln("PONG " + last);
			}
			tries--;
		} while (!first.contains("366") && tries > 0);

			if (users.equals("")) {
				CONNECTION.msgMasters("Could not get list of users!!!");
			}

		return users;
	}

	/**
	 * Helper method to The WakeRoom Command
	 * Highlights all users
	 */
	private void wakeRoomHelper() {
		String users = getUsers();

		// Change nick[m] to nick for Matrix/Riot.im IRC Bridge
		users = users.replace("[m]", "");

		if (users != null) {
			CONNECTION.msgChannel(CONFIG.getChannel(), users);
		} else {
			CONNECTION.msgChannel(CONFIG.getChannel(), "Failed to get a list of users; Try again or notify the developer!");
		}
	}

	/**
	 * Puts together a String in the form http://lmgtfy.com/?q=test+a+b+c 
	 * And sends it to the channel
	 */
	private void googleHelper() {
		if (!hasArgs) {
			helpWrapper(cmd);
		} else {
			String googleUrl = "http://lmgtfy.com/?q=".concat(getFormattedQuery(cmdArgs));
			CONNECTION.msgChannel(CONFIG.getChannel(), googleUrl);
		}
	}

	/**
	 * Helper method to the isup {@literal <domain>} command
	 * Checks if the host at domain is up or not
	 */
	private void isUpHelper() {
		if (!hasArgs) {
			helpWrapper(cmd);
		} else {
			CONNECTION.msgChannel(CONFIG.getChannel(), new DownForEveryone().isUp(getFormattedQuery(cmdArgs), true));
		}
	}

	/**
	 * TODO Implement me
	 * TODO Still a work in progress
	 * TODO Needed JSON code and check only once a week code (Thursdays)
	 */
	private void epicHelper() {
		if(hasArgs) {
	        	helpWrapper(cmd);

        	} else {
        		//connection.msgChannel(CONFIG.getChannel(), CONFIG.getCmdSymb() + cmd + " " + cmdArgs); // Uncomment to see the command and args being used

			CONNECTION.msgChannel(CONFIG.getChannel(), new Epic().getFormattedEpicSummary(false, -1));
			
		}
	
	}

	/**
	 * Helper method to the weather {@literal <location>} command
	 * Sends a weather summary for the location to the channel
	 */
	private void weatherHelper() {

		if(!hasArgs) {
	        	helpWrapper(cmd);

        	} else {
        		Weather w = new Weather(this.CONFIG);

			try {
				CONNECTION.msgChannel(CONFIG.getChannel(), w.getFormattedWeatherSummary(cmdArgs, true, 5) );

			} catch (Weather.InvalidLocationException ex) {

				Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);

				helpWrapper(cmd); //TODO Show the correct syntax for the command and the location here
			}
		}
	}

	/**
	 * Helper method to the .mum {@literal <user>} command
	 * Sends a yo momma joke to the channel
	 */
	private void mumHelper() {
		try {
			if (!hasArgs) {
				CONNECTION.msgChannel(CONFIG.getChannel(),
					JOKE.getMommaJoke(getRandChanUser().replace("[m]", "")));
			} else {
				//TODO I don't remember why I wrote this. What does it do? Is it really needed?
				int temp = cmdArgs.indexOf(' ');
				if (temp != -1) {
					CONNECTION.msgChannel(CONFIG.getChannel(), JOKE.getMommaJoke(cmdArgs.substring(0, temp)));
				} else {
					CONNECTION.msgChannel(CONFIG.getChannel(), JOKE.getMommaJoke(cmdArgs));
				}
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
			Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Helper to next! Another satisified customer
	 */
	private void nextHelper() {
		CONNECTION.msgChannel(CONFIG.getChannel(), "Another satisfied customer, NEXT!!!");
	}

	/**
	 * TODO Fix this method
	 * TODO Replace code with getHelp(cmd); //Overloaded method
	 */
	private void inviteNickHelper() {
		if (true) {
			return;
		}

		if (!hasArgs || !(cmdArgs.split("\\s++").length > 0)) { // Min 1 Arg
			
			CONNECTION.msgChannel(CONFIG.getChannel(), "Usage: " + CONFIG.getCmdSymb() + "invite-nick {nick} [# of times]");
		} else {
			CONNECTION.msgChannel(CONFIG.getChannel(), "Roger that.");
			String cmdArgsArr[] = cmdArgs.split("\\s++");
			int numInvites = 50; // Default value
			if (cmdArgsArr.length < 1) {
				try {
					Thread.sleep(1500);
					//numInvites = Integer.getInteger(cmdArgsArr[1]);
					//TODO replace with CONFIG.getMaster()
					if (CONNECTION.recieveln().contains(":JRobo!~Tux@unaffiliated/robotcow QUIT :Excess Flood")) {
						//               this.JROBO. 
					}

				} catch (InterruptedException ex) {
					Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);

				}
			}
			for (int x = 0; numInvites >= x; x++) {
				CONNECTION.sendln("INVITE " + CONFIG.getChannel() + " " + cmdArgsArr[0]);
			}
		}
	}

	/**
	 * Helper to invite all members of a channel
	 */
	private void inviteChannelHelper() {
		String[] userArr;
		if(!hasArgs) {
		//TODO      helpWrapper(cmd);
			return;
		}

		if (threadCreated) {
			CONNECTION.msgChannel(CONFIG.getChannel(), "The invite thread is still active.");
			return;
		}

		//TODO Exclude duplicates from useray
		if (cmdArgs.contains(" ")) {
			final String[] CHANSARR = cmdArgs.split("\\s++");
			// Channel must begin with a # and be at least two characters long
			for (String chan : CHANSARR) {
				if (chan.length() < 2 || !chan.startsWith("#")) {
					CONNECTION.msgChannel(CONFIG.getChannel(), "Invalid channel: " + chan);
					return;
				}
			}

			CONFIG.setBaseChan(CONFIG.getChannel()); // The channel JRobo will return to
			String usersList = "";
			for (String chan : CHANSARR) {
				CONNECTION.moveToChannel(CONFIG.getChannel(), chan);
				if (CONFIG.getChannel().equals(CONFIG.getBaseChan())) {
					break;
				}
				usersList += getUsers();
			}
			userArr = usersList.split("\\s");
			CONNECTION.moveToChannel(CHANSARR[CHANSARR.length - 1], CONFIG.getBaseChan());
		} //The command does not contain more than one channel argument
		else {
			// Channel must begin with a # and be at least two characters long
			if (cmdArgs.length() < 2 || !cmdArgs.startsWith("#")) {
				CONNECTION.msgChannel(CONFIG.getChannel(), "Invalid channel: " + cmdArgs);
				return;
			}
			CONFIG.setBaseChan(CONFIG.getChannel()); // The channel JRobo will return to
			CONNECTION.moveToChannel(CONFIG.getChannel(), cmdArgs);
			userArr = getUsers().split("\\s");
			CONNECTION.moveToChannel(cmdArgs, CONFIG.getBaseChan());

		}
		//Statement required for Build (currently is workaround)
		final String[] user2Arr = userArr;

		for (String user : userArr) {
			System.out.println("userArr: " + user);
		}

		// Checking if ChanServ has opped JRobo
		String first, last, received;
		for (int tries = 4;;) {

			received = CONNECTION.recieveln();
			try {
				first = received.split(" :", 2)[0];
				last = received.split(" :", 2)[1];

			} catch (ArrayIndexOutOfBoundsException ex) {
				first = "";
				last = "";

			}
			if (first.equals("PING")) {
				CONNECTION.sendln("PONG " + last);

			} else if (received.equals(":ChanServ!ChanServ@services. MODE " + CONFIG.getChannel() + " +o " + CONFIG.getName())
				|| tries < 0) {
				break;

			}

			tries--;
		}

		Thread inviteT = new Thread() {
			@Override
			public void run() {

				// Prevent multiple threads from being created
				threadCreated = true;

				for (String user : user2Arr) {
					try {
						Thread.sleep(35000); //TODO Delay set by last command arg?
					} catch (InterruptedException ex) {
						Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
					}

					CONNECTION.sendln("INVITE " + user + " " + CONFIG.getChannel());
				}
				threadCreated = false;

			}

		};
		//Remove In a minute -projektile
		inviteT.start();

		/*
		 * TODO Implement and use FileReader.getNickAndHost() instead
		 * FIXME check all masters for-each loop    if(JROBO.getFirst().startsWith(CONFIG.getMasters()[0]) && hasArgs ) {
		 * Use for multiple channels, array    String[] channels = this.cmdArgs.split("\\s++");
		 *    if(channels.length )
		 */
	}

	/**
	 * Helper to send a raw IRC command by one of the masters only
	 * FIXME Use Config.getMasters()
	 */
	private void rawHelper() {
		if (JROBO.getFirst().startsWith(":BullShark!")) {
			CONNECTION.sendln("PRIVMSG " + CONFIG.getChannel() + " :Yes Sir Chief!");
			String rawStr = JROBO.getLast();
			rawStr = rawStr.substring(rawStr.indexOf(' '));
			CONNECTION.sendln(rawStr);
			try {
				Thread.sleep(500);
				CONNECTION.msgChannel(CONFIG.getChannel(), CONNECTION.recieveln());

			} catch (InterruptedException | NullPointerException ex) {
				Logger.getLogger(BotCommand.class
					.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Helper method to Urban Dictionary command
	 */
	private void urbanDictionaryHelper() {
		CONNECTION.msgChannel(CONFIG.getChannel(), new UrbanDict(cmdArgs).getFormattedUrbanDef(true), true, MircColors.BOLD);
	}

	/**
	 * Gabe's quit message for this bot
	 */
	private void quitHelper() {
		CONNECTION.msgChannel(CONFIG.getChannel(), "Detenation devices to nuclear reactors! (Zer0 is pressing the "
			+ "stupid BUTTOnN so GO OUT OF THIS FUCKING CHANNEL BITCHES!!!)");
	}

	/**
	 * Does nothing at all, just a placeholder
	 */
	private void doNothingHelper() {
	}

	/**
	 * Helper method called when a Pirate Bay Search Command is called
	 */
	private void pirateHelper() {
		CONNECTION.msgChannel(CONFIG.getChannel(), new PirateBay(cmdArgs).getFormattedResult(true));
	}

	/**
	 * Helper method called when a 1337x.to Search Command is called
	 */
	private void leetHelper() {
		CONNECTION.msgChannel(CONFIG.getChannel(), new Leet(CONFIG, cmdArgs).getFormattedResult(true));
	}

	/**
	 * A helper executed when the list bot command is received by JRobo
	 * Lists the bot commands syntax in a help message
	 */
	private void listHelper() {

		/*
		 * GREEN = dark color
		 * CYAN = light color
		 * 
		 * cmds|alias = dark
		 * flags = dark
		 * args = light
		 * special characters = no bold, no color
		 * such as [] <> , ...
		 */
		String colorStr1 = LC.attributesSynopsisLine(LC.colorToken("Available commands: ", MircColors.BOLD)
			+ LC.colorToken("google|lmgtfy|stfw ", MircColors.GREEN)
			+ LC.colorToken("<search query>, ", MircColors.CYAN)
			+ LC.colorToken("greet|g ", MircColors.GREEN)
			+ LC.colorToken("[user], ", MircColors.CYAN)
			+ LC.colorToken("wakeroom|wr, ", MircColors.GREEN)
			+ LC.colorToken("weather|w ", MircColors.GREEN)
			+ LC.colorToken("<location, zip, etc.>, ", MircColors.CYAN)
			+ LC.colorToken("urbandict|ud ", MircColors.GREEN)
			+ LC.colorToken("<search query>, ", MircColors.CYAN)
			+ LC.colorToken("list|l, ", MircColors.GREEN)
			+ LC.colorToken("leet|leetx ", MircColors.GREEN)
			+ LC.colorToken("<catgegory> <search query>, ", MircColors.CYAN));

		String colorStr2 = LC.attributesSynopsisLine(
			LC.colorToken("(Avaliable categories are:  Movies, TV, Games, Music, Apps, Documentaries, Anime, Other, XXX), ", MircColors.GREEN)
			+ LC.colorToken("raw|r ", MircColors.GREEN)
			+ LC.colorToken("<raw irc line>, ", MircColors.CYAN)
			+ LC.colorToken("help|h ", MircColors.GREEN)
			+ LC.colorToken("[cmd], ", MircColors.CYAN)
			+ LC.colorToken("next|n, ", MircColors.GREEN)
			+ LC.colorToken("mum|m ", MircColors.GREEN)
			+ LC.colorToken("[user], ", MircColors.CYAN)
			+ LC.colorToken("invite-channel|ic ", MircColors.BOLD + MircColors.GREEN)
			+ LC.colorToken("<channel>, ", MircColors.CYAN)
			+ LC.colorToken("invite-nick|in ", MircColors.GREEN)
			+ LC.colorToken("<nick> ", MircColors.CYAN)
			+ LC.colorToken("[# of times], ", MircColors.CYAN)
			+ LC.colorToken("pirate ", MircColors.GREEN)
			+ LC.colorToken("[-s|-l|-d] ", MircColors.CYAN)
			+ LC.colorToken("<search query>, ", MircColors.CYAN)
			+ LC.colorToken("isup ", MircColors.GREEN)
			+ LC.colorToken("<url>, ", MircColors.CYAN)
			+ LC.colorToken("epic|e ", MircColors.GREEN)
			+ LC.colorToken("[country code], ", MircColors.CYAN)
			+ LC.colorToken("version, ", MircColors.GREEN)
			+ LC.colorToken("quit|q", MircColors.GREEN));

		//String noColorStr = (colorStr1 +colorStr2).replaceAll("(\\P{Print}|[0-9]{2})", "");
		//System.out.println("String without colors: " + noColorStr);

		CONNECTION.msgChannel(CONFIG.getChannel(), colorStr1);
		CONNECTION.msgChannel(CONFIG.getChannel(), colorStr2);
	}

	/**
	 * Called when an Unknown Command is received
	 */
	private void unknownCmdHelper() {
		CONNECTION.msgChannel(CONFIG.getChannel(), "Unknown command received: " + cmd);
	}

	/**
	 * Wrapper Help command messages
	 */
	private void helpWrapper(final String CMD) {
		//TODO help string for each command
		CONNECTION.msgChannel(CONFIG.getChannel(), "Invalid usage of command: " + CMD);
	}

	/**
	 * Gives JRobo's version info with the Version Command is received
	 */
	private void versionHelper() {
		CONNECTION.msgChannel(CONFIG.getChannel(),
			MircColors.BOLD + MircColors.CYAN + "JRobo"
			+ MircColors.NORMAL + MircColors.BOLD + " - "
			+ MircColors.GREEN + "https://github.com/BullShark/JRobo");
	}

	/**
	 * Helper method to the Greet Command
	 */
	private void greetHelper() {
		try {
			if (!hasArgs) {
				CONNECTION.msgChannel(CONFIG.getChannel(), JOKE.getPhoneNumber(getRandChanUser()));
			} else {
				int temp = cmdArgs.indexOf(' ');
				if (temp != -1) {
					CONNECTION.msgChannel(CONFIG.getChannel(), JOKE.getPhoneNumber(cmdArgs.substring(0, temp)));
				} else {
					CONNECTION.msgChannel(CONFIG.getChannel(), JOKE.getPhoneNumber(cmdArgs));
				}
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
			Logger.getLogger(BotCommand.class
				.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Move from current channel to the channel specified by cmd args
	 */
	private void moveToChannelHelper() {
		//TODO Only Masters
		CONNECTION.moveToChannel(CONFIG.getChannel(), cmdArgs);
	}

	/**
	 * Starts TIMER for bomb, sets an active WIRE and prints explosion and kicks user holding at [20] seconds.
	 */
	public void bomb() {
		bombHolder = user;
		CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.BOLD + bombHolder + MircColors.WHITE + " started the bomb!!!");
		CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.WHITE + "You can pass it to another user with >pass [nick].");
		CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.WHITE + "You can attempt to defuse with >defuse [" + MircColors.RED + "R" + MircColors.GREEN + "G" + MircColors.BLUE + "B" + MircColors.WHITE + "-color].");
		bombActive = true;
		WIRE[0] = false;
		WIRE[1] = false;
		WIRE[2] = false;
		WIRE[(int) (3.0 * Math.random())] = true;
		final Timer TIMER;

		TIMER = new Timer();

		class BombTask extends TimerTask {

			@Override
			public void run() {
				if (!bombActive) {
					TIMER.cancel();
				} else {
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.BROWN + "          ,_=~~:-" + MircColors.YELLOW + ")" + MircColors.BROWN + ",,          ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.YELLOW + "      (" + MircColors.BROWN + "==?,::,:::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "=:=" + MircColors.YELLOW + ")       ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.BROWN + "     ?:=" + MircColors.YELLOW + "(" + MircColors.BROWN + ",~:::::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "~+=:I" + MircColors.YELLOW + ")     ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.YELLOW + "   (" + MircColors.BROWN + "=:" + MircColors.YELLOW + "(" + MircColors.BROWN + ",=:~++" + MircColors.YELLOW + "=:" + MircColors.BROWN + "::~,:~:" + MircColors.YELLOW + "))" + MircColors.BROWN + "~~~." + MircColors.YELLOW + ")    ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.YELLOW + "    (" + MircColors.BROWN + "+~" + MircColors.YELLOW + "(" + MircColors.BROWN + ",:" + MircColors.YELLOW + "(==:" + MircColors.BROWN + ":~~+~~" + MircColors.YELLOW + ")" + MircColors.BROWN + ",$,I?" + MircColors.YELLOW + "))   ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.BROWN + "    ``  ```" + MircColors.YELLOW + "~~" + MircColors.BROWN + "?" + MircColors.YELLOW + "~=" + MircColors.BROWN + "$.~~~  ``     ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.YELLOW + "             :" + MircColors.BROWN + "S" + MircColors.YELLOW + "Z=             ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.YELLOW + "         .-~~" + MircColors.BROWN + "?=:=" + MircColors.YELLOW + "``~-_        ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.YELLOW + "         `--=~=+~++=~`        ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.YELLOW + "             ." + MircColors.BROWN + "~" + MircColors.YELLOW + ":" + MircColors.BROWN + "~             ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.BROWN + "         ((.(\\.!/.):?)        ");
					CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.DARK_GREEN + "   .?~:?.?7::,::::+,,~+~=:... ");
					CONNECTION.kickFromChannel(CONFIG.getChannel(), user + " KABOOM!!!");
					bombActive = false;
					TIMER.cancel();
				}
			}
		}
		TIMER.schedule(new BombTask(), 20000);
	}

	/**
	 * Simply passes the bomb to another user and returns it if they attempt to pass to JRobo.
	 */
	private void pass() {
		String users = getUsers();
		if (users.contains(cmdArgs) && !cmdArgs.equals("") && user.equals(bombHolder) && bombActive == true) {
			bombHolder = cmdArgs;
			CONNECTION.msgChannel(CONFIG.getChannel(), "The Bomb has been passed to " + bombHolder + "!!!");
			if (cmdArgs.equals(CONFIG.getName())) {
				try {
					Thread.sleep(2500);
				} catch (InterruptedException ex) { //Find out exactly what exceptions are thrown
					Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
				}
				CONNECTION.msgChannel(CONFIG.getChannel(), ">pass " + user);
				bombHolder = user;
				CONNECTION.msgChannel(CONFIG.getChannel(), "The Bomb has been passed to " + bombHolder + "!!!");
			}
		} else {
			CONNECTION.msgChannel(CONFIG.getChannel(), "Invalid.");
		}
	}

	/**
	 * This function will return a WIRE for a given COLOR and is only to be used within defuse.
	 * 
	 * @return True if wire COLOR is found, false otherwise
	 */
	private boolean wire(final String COLOR) {
		switch (COLOR) {
			case "red":
				return WIRE[0];
			case "green":
				return WIRE[1];
			case "blue":
				return WIRE[2];
		}
		return false;
	}

	/**
	 * This is the defuse method and refers to a global boolean array of wires and the active WIRE is set to true in bomb() function.
	 */
	public void defuse() {
		if (bombActive && user.equals(bombHolder)) {
			if (wire(cmdArgs) == true) {
				CONNECTION.msgChannel(CONFIG.getChannel(), MircColors.WHITE + "Bomb defused.");
				bombActive = false;
			} else {
				//TODO Will removing leading spaces break things, or should we do it?
				CONNECTION.msgChannel(CONFIG.getChannel(),
					MircColors.BROWN + "          ,_=~~:-" + MircColors.YELLOW + ")" + MircColors.BROWN + ",,          \n"
					+ MircColors.YELLOW + "      (" + MircColors.BROWN + "==?,::,:::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "=:=" + MircColors.YELLOW + ")       \n"
					+ MircColors.BROWN + "     ?:=" + MircColors.YELLOW + "(" + MircColors.BROWN + ",~:::::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "~+=:I" + MircColors.YELLOW + ")     \n"
					+ MircColors.YELLOW + "   (" + MircColors.BROWN + "=:" + MircColors.YELLOW + "(" + MircColors.BROWN + ",=:~++" + MircColors.YELLOW + "=:" + MircColors.BROWN + "::~,:~:" + MircColors.YELLOW + "))" + MircColors.BROWN + "~~~." + MircColors.YELLOW + ")    \n"
					+ MircColors.YELLOW + "    (" + MircColors.BROWN + "+~" + MircColors.YELLOW + "(" + MircColors.BROWN + ",:" + MircColors.YELLOW + "(==:" + MircColors.BROWN + ":~~+~~" + MircColors.YELLOW + ")" + MircColors.BROWN + ",$,I?" + MircColors.YELLOW + "))   \n"
					+ MircColors.BROWN + "    ``  ```" + MircColors.YELLOW + "~~" + MircColors.BROWN + "?" + MircColors.YELLOW + "~=" + MircColors.BROWN + "$.~~~  ``     \n"
					+ MircColors.YELLOW + "             :" + MircColors.BROWN + "S" + MircColors.YELLOW + "Z=             \n"
					+ MircColors.YELLOW + "         .-~~" + MircColors.BROWN + "?=:=" + MircColors.YELLOW + "``~-_        \n"
					+ MircColors.YELLOW + "         `--=~=+~++=~`        \n"
					+ MircColors.YELLOW + "             ." + MircColors.BROWN + "~" + MircColors.YELLOW + ":" + MircColors.BROWN + "~             \n"
					+ MircColors.BROWN + "         ((.(\\.!/.):?)        \n"
					+ MircColors.DARK_GREEN + "   .?~:?.?7::,::::+,,~+~=:... ");

				CONNECTION.kickFromChannel(CONFIG.getChannel(), user + " KABOOM!!!");
				bombActive = false;
			}
		} else {
			CONNECTION.msgChannel(CONFIG.getChannel(), "Invalid.");
		}
	}

	/**
	 * Does a drive-by lol's parts and returns as well as try-sleeps in loop to avoid flooding.
	 */
	private void driveBy() {
		if (cmdArgs.length() < 2 || !cmdArgs.startsWith("#")) {
			CONNECTION.msgChannel(CONFIG.getChannel(), "Invalid channel: " + cmdArgs);
			return;
		}
		CONFIG.setBaseChan(CONFIG.getChannel()); // The channel JRobo will return to
		CONNECTION.moveToChannel(CONFIG.getChannel(), cmdArgs);
		for (int i = 0; 25 >= i; i++) {
			CONNECTION.moveToChannel(CONFIG.getChannel(), cmdArgs);
			for (int z = 0; 5 >= z; z++) {
				CONNECTION.msgChannel(CONFIG.getChannel(), "lol");
				try {
					Thread.sleep(1000);
					//FIXME Bad coding
					if (CONNECTION.recieveln().contains(":the_derp_knight!~JRobo@d-24-245-107-185.cpe.metrocast.net QUIT :Excess Flood")) {
						break;
					}
				} catch (InterruptedException ex) { //Find out exactly what exceptions are thrown
					Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			CONNECTION.moveToChannel(cmdArgs, CONFIG.getBaseChan());
			try {
				Thread.sleep(2500);
				//FIXME Bad coding
				if (CONNECTION.recieveln().contains(":the_derp_knight!~JRobo@d-24-245-107-185.cpe.metrocast.net QUIT :Excess Flood")) {
					break;
				}
			} catch (InterruptedException ex) { //Find out exactly what exceptions are thrown
				Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		// If not in basechannel he will return to Basechannel
		if (CONFIG.getChannel() == null ? CONFIG.getBaseChan() != null : !CONFIG.getChannel().equals(CONFIG.getBaseChan())) {
			CONNECTION.moveToChannel(cmdArgs, CONFIG.getBaseChan());
		}
	}
} // EOF class
