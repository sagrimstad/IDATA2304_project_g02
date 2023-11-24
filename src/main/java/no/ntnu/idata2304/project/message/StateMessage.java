package no.ntnu.idata2304.project.message;

/**
 * A message telling whether the tTV is ON or off.
 */
public class StateMessage implements Message {

  private final boolean isOn;

  /**
   * Create a state message
   *
   * @param isOn The Tv is ON if this is true, the TV is off if this is false-
   */
  public StateMessage(boolean isOn) {
    this.isOn = isOn;
  }

  /**
   * Check whether the TV is ON.
   *
   * @return ON if true, OFF if false.
   */
  public boolean isOn() {
    return this.isOn;
  }
}
