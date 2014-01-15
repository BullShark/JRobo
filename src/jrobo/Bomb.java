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
  public String bombHolder;
  public String liveWire;

/*
 * This method is the bombs initial constructor method.
 * contains and starts the timer, initializes the bombHolder, and sets liveWire  
 */
  public void Bomb(Networking connection, Config config, String user) {
       
    this.connection = connection;
    this.config = config;
    bombHolder = user;
    liveWire = Bomb.WireColor.randomColor().toString().toLowerCase();
    
    connection.msgChannel(config.getChannel(), MircColors.BOLD + bombHolder + MircColors.WHITE + " started the bomb!!!");
    connection.msgChannel(config.getChannel(), MircColors.WHITE + "You can pass it to another user with " + config.getCmdSymb() +  "pass [nick].");
    connection.msgChannel(config.getChannel(), MircColors.WHITE + "You can attempt to defuse it with " + config.getCmdSymb() + "defuse [" + MircColors.RED + "red" + MircColors.WHITE + "/" + MircColors.GREEN + "green" + MircColors.WHITE + "/" + MircColors.BLUE + "blue" + MircColors.WHITE + "]");
    BotCommand.bombActive = true;
  
    final Timer timer;
    timer = new Timer();

    class BombTask extends TimerTask {
      public void run() {
        if (!BotCommand.bombActive) {
          timer.cancel();
        } else {
          explode();
          timer.cancel();
        }
      }
    }
    timer.schedule(new BombTask(), (int)(10000.0 * Math.random()) + 20000);
  } 
  
  
  /*
   * This function/method will define what happens when the timer goes off
   * or the wrong wire is cut
   */
  public void explode() {
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
  
  /*
   * This method allows a user to defuse the bomb if the liveWire is argued.
   * It blows up otherwise.
   */
  public void defuse(String user, String color) {
    System.out.println("bombHolder= " + bombHolder + "\nuser= " + user + "\nliveWire= " + liveWire);
    if (BotCommand.bombActive && user.toString().equals(bombHolder)) {
      if (color.equals(liveWire)) {
        connection.msgChannel(config.getChannel(), MircColors.WHITE + "Bomb defused.");
        BotCommand.bombActive = false;
      } else {
        explode();
      }
    } else if (!user.toString().equals(bombHolder)){
      connection.msgChannel(config.getChannel(), "Invalid. " + user + " You're not holding the bomb, " + bombHolder + " is!!!");
    } else {
      connection.msgChannel(config.getChannel(), "Invalid. Bomb not active.");
    }
  }
  
  /*
   * This is the enumating class for wire function.
   * It contains all the options for WireColors used elsewhere.
   */
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
  
  /*
   * This Method allows the bombHolder to pass the bomb to another user;
   * Granted the user passed in the argument is present in the channel.
   */
  public void pass(String user, String toUser, String users) {
    if (users.contains(toUser) && !toUser.equals("") && user.equals(bombHolder) && BotCommand.bombActive == true) {
      bombHolder = toUser;
      connection.msgChannel(config.getChannel(), "The Bomb has been passed to " + bombHolder + "!!!");
      if (toUser.equals(config.getName())) {
        try {
          Thread.sleep(2500);
        } catch (Exception ex) { //Find out exactly what exceptions are thrown
          //Logger.getLogger(BotCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        connection.msgChannel(config.getChannel(), config.getCmdSymb() + "pass " + user);
        bombHolder = user;
        connection.msgChannel(config.getChannel(), "The Bomb has been passed to " + bombHolder + "!!!");
      }
    } else if (!user.equals(bombHolder)) {
      connection.msgChannel(config.getChannel(), "Invalid. " + user + " you're not holding the bomb, " + bombHolder + " is!!!");
    } else {
      connection.msgChannel(config.getChannel(), "Invalid. Bomb not active.");
    }
  }
}
