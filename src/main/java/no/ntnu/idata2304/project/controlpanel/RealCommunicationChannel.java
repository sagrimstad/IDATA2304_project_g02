package no.ntnu.idata2304.project.controlpanel;


public class RealCommunicationChannel implements CommunicationChannel {

  private final ControlPanelLogic logic;

  /**
   * Creates a communication channel.
   *
   * @param logic the application logic of the control panel node.
   */
public RealCommunicationChannel(ControlPanelLogic logic) {
    this.logic = logic;
}



  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    //TODO implement method
  }

  @Override
  public boolean open() {
    return false;
  }
}
