/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jrobo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Christopher Lemire <christopher.lemire@gmail.com>
 * TODO Change to class and move code here
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
