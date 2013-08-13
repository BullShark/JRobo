/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jrobo;

/**
 *
 * @author Christopher Lemire <christopher.lemire@gmail.com>
 */
public class NotConfig extends Config {

  private String baseChan;
  
  public NotConfig() {
    baseChan = super.getChannel();
  }
  
  /**
   * Channel from where the bot began moving
   * @return 
   */
  public String getBaseChan() {
    return baseChan;
  }

  /**
   * Channel from where the bot began moving
   * @param baseChan 
   */
  public void setBaseChan(String baseChan) {
    this.baseChan = baseChan;
  }
}