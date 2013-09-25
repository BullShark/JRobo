package jrobo.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@interface JRoboCommand {
  String author();
  String date();
  String command();
}
