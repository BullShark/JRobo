package jrobo.command;
import jrobo.JRobo;

@JRoboCommand(author="jotaki",date="2013/09/27",command="join,goto")
public class OpCommands extends BotCommand {
  public OpCommands() {
    super();
  }

  public void execute(String target, String[] args) {
    if(args.length == 0) {
      help(target);
      return;
    }

    switch(inputCommand) {
      case "goto":
        part(target);
      case "join":
        join(args[0]);
    }
  }

  public void help(String target) {
    privmsg(target, "Usage: " + inputCommand + " <channel>");
  }
}
