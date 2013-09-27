package jrobo.command;
import jrobo.JRobo;

@JRoboCommand(author="jotaki",date="2013/09/27",command="google,g,lmgtfy,stfw")
public class GoogleCommand extends BotCommand {
  public GoogleCommand() {
    super();
  }

  public void execute(String target, String[] args) {
    if(args.length > 0) {
      privmsg(target, "http://lmgtfy.com/?q=" + strJoin(args, " ").replaceAll("\\s++", "+"));
    }
  }
}
