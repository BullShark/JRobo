package jrobo.command;
import jrobo.JRobo;

@JRoboCommand(author="jotaki",date="2013/09/25",command="test")
public class TestCommand extends BotCommand {

  public TestCommand(JRobo jRobo) {
    super(jRobo);
  }

  public void execute(String target, String[] args) {
    privmsg(target, "Hello!");
  }
}
