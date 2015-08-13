package jrobo.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface JRoboCommand {
  String author();
  String date();
  String command();
}
