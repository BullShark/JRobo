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
      JRoboCommand cmd = klass.getAnnotation(JRoboCommand.class);
      if(command.equals(cmd.command())) {
        botCommand = (BotCommand) klass.newInstance();
      }
    }

    return botCommand;
  }
}
