package jrobo;

import jrobo.command.*;

import java.util.Set;
import org.reflections.Reflections;

public class CommandFactory {
  public BotCommand newCommand(String command) throws InstantiationException, IllegalAccessException {
    BotCommand botCommand = null;
    Reflections reflections = new Reflections("jrobo.command");
    Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(JRoboCommand.class);

    for(Class<?> klass : annotated) {
      JRoboCommand jRoboCommand = klass.getAnnotation(JRoboCommand.class);
      for(String cmd : jRoboCommand.command().split(",")) {
        if(command.equals(cmd)) {
          botCommand = (BotCommand) klass.newInstance();
          break;
        }
      }
    }

    return botCommand;
  }
}
