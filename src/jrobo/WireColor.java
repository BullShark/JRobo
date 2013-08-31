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
 * http://stackoverflow.com/questions/1972392/java-pick-a-random-value-from-an-enum/1972399#1972399
 * TODO Why does the first private static line have an error?
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
