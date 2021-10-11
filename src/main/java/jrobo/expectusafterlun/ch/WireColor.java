/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jrobo.expectusafterlun.ch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Chris Lemire {@literal <goodbye300@aim.com>}
 * TODO Change to class and move code here
 */
public enum WireColor {

  /**
   * RED, GREEN, and BLUE
   */
  RED, GREEN, BLUE;

  private static final List<WireColor> VALUES =
          Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  /**
   * @return A random wire color
   */
  public static WireColor randomColor() {
    return VALUES.get(RANDOM.nextInt(SIZE));
  }
}
