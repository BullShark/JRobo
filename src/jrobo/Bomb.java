/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jrobo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Christopher Lemire <christopher.lemire@gmail.com>
 * TODO Change to class and move code here
 */
public class Bomb{
  
  private Networking connection;
  private Config config;  
  //public boolean bombActive = true;
  public BotCommand user;
 // public String bombHolder;
  public String liveWire;
 

/*
 * This is the bombs initial class containing timer.
 * Will likely suck to move all this code
 */
  public void Bomb(Networking connection, Config config, String user) {
       
    this.connection = connection;
    this.config = config;
    BotCommand.bombHolder = user;
    liveWire = Bomb.WireColor.randomColor().toString().toLowerCase();
    
    connection.msgChannel(config.getChannel(), MircColors.BOLD + BotCommand.bombHolder + MircColors.WHITE + " started the bomb!!!");
    connection.msgChannel(config.getChannel(), MircColors.WHITE + "You can pass it to another user with >pass [nick].");
    connection.msgChannel(config.getChannel(), MircColors.WHITE + "You can attempt to defuse with >defuse [" + MircColors.RED + "R" + MircColors.GREEN + "G" + MircColors.BLUE + "B" + MircColors.WHITE + "-color].");
    BotCommand.bombActive = true;
  
    final Timer timer;
    timer = new Timer();

    class BombTask extends TimerTask {
      public void run() {
        if (!BotCommand.bombActive) {
          timer.cancel();
        } else {
          explode(BotCommand.bombHolder);
          timer.cancel();
        }
      }
    }
    timer.schedule(new BombTask(), (int)(10000.0 * Math.random()) + 20000);
  } 
  
  
  /*
   * This function will define what happens when the timer goes off
   * or the wrong wire is cut
   */
  public void explode(String bombHolder) {
    connection.msgChannel(config.getChannel(), MircColors.BROWN + "          ,_=~~:-" + MircColors.YELLOW + ")" + MircColors.BROWN + ",,          ");
    connection.msgChannel(config.getChannel(), MircColors.YELLOW + "      (" + MircColors.BROWN + "==?,::,:::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "=:=" + MircColors.YELLOW + ")       ");
    connection.msgChannel(config.getChannel(), MircColors.BROWN + "     ?:=" + MircColors.YELLOW + "(" + MircColors.BROWN + ",~:::::::" + MircColors.YELLOW + ")" + MircColors.BROWN + "~+=:I" + MircColors.YELLOW + ")     ");
    connection.msgChannel(config.getChannel(), MircColors.YELLOW + "   (" + MircColors.BROWN + "=:" + MircColors.YELLOW + "(" + MircColors.BROWN + ",=:~++" + MircColors.YELLOW + "=:" + MircColors.BROWN + "::~,:~:" + MircColors.YELLOW + "))" + MircColors.BROWN + "~~~." + MircColors.YELLOW + ")    ");
    connection.msgChannel(config.getChannel(), MircColors.YELLOW + "    (" + MircColors.BROWN + "+~" + MircColors.YELLOW + "(" + MircColors.BROWN + ",:" + MircColors.YELLOW + "(==:" + MircColors.BROWN + ":~~+~~" + MircColors.YELLOW + ")" + MircColors.BROWN + ",$,I?" + MircColors.YELLOW + "))   ");
    connection.msgChannel(config.getChannel(), MircColors.BROWN + "    ``  ```" + MircColors.YELLOW + "~~" + MircColors.BROWN + "?" + MircColors.YELLOW + "~=" + MircColors.BROWN + "$.~~~  ``     ");
    connection.msgChannel(config.getChannel(), MircColors.YELLOW + "             :" + MircColors.BROWN + "S" + MircColors.YELLOW + "Z=             ");
    connection.msgChannel(config.getChannel(), MircColors.YELLOW + "         .-~~" + MircColors.BROWN + "?=:=" + MircColors.YELLOW + "``~-_        ");           connection.msgChannel(config.getChannel(), MircColors.YELLOW + "         `--=~=+~++=~`        ");
    connection.msgChannel(config.getChannel(), MircColors.YELLOW + "             ." + MircColors.BROWN + "~" + MircColors.YELLOW + ":" + MircColors.BROWN + "~             ");
    connection.msgChannel(config.getChannel(), MircColors.BROWN + "         ((.(\\.!/.):?)        ");
    connection.msgChannel(config.getChannel(), MircColors.DARK_GREEN + "   .?~:?.?7::,::::+,,~+~=:... ");
    connection.kickFromChannel(config.getChannel(), bombHolder + " KABOOM!!!");
    BotCommand.bombActive = false; 
  }
  
  
  public void defuse(String user, String color) {
    System.out.println("bombHolder= " + BotCommand.bombHolder + "\nuser= " + user + "\nliveWire= " + liveWire);
    if (BotCommand.bombActive && user.toString().equals(BotCommand.bombHolder)) {
      if (color.equals(liveWire)) {
        connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb defused.");
        BotCommand.bombActive = false;
      } else {
        explode(BotCommand.bombHolder);
        BotCommand.bombActive = false;
      }
    } else {
      connection.msgChannel(config.getChannel(), "Invalid.");
    }
  }
  
  public enum WireColor {

    RED, GREEN, BLUE;

    private static final List<WireColor> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static WireColor randomColor() {
      return VALUES.get(RANDOM.nextInt(SIZE));
    }
  }
}