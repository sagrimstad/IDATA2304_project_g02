package no.ntnu.idata2304.project.run;

import no.ntnu.idata2304.project.controlpanel.ControlPanelLogic;
import no.ntnu.idata2304.project.controlpanel.CommunicationChannel;
import no.ntnu.idata2304.project.controlpanel.RealCommunicationChannel;
import no.ntnu.idata2304.project.gui.controlpanel.ControlPanelApplication;
import no.ntnu.idata2304.project.tools.Logger;

/**
 * Starter class for the control panel. Note: we could launch the Application class directly, but
 * then we would have issues with the debugger (JavaFX modules not found)
 */
public class ControlPanelStarter {

  private final boolean fake;

  public ControlPanelStarter(boolean fake) {
    this.fake = fake;
  }

  /**
   * Entrypoint for the application.
   *
   * @param args Command line arguments, only the first one of them used: when it is "fake", emulate
   *             fake events, when it is either something else or not present, use real socket
   *             communication.
   */
  public static void main(String[] args) {
    boolean fake = false;
    if (args.length == 1 && "fake".equals(args[0])) {
      fake = true;
      Logger.info("Using FAKE events");
    }
    ControlPanelStarter starter = new ControlPanelStarter(fake);
    starter.start();
  }

  private void start() {
    ControlPanelLogic logic = new ControlPanelLogic();
    CommunicationChannel channel = initiateCommunication(logic);
    ControlPanelApplication.startApp(logic, channel);
    // This code is reached only after the GUI-window is closed
    Logger.info("Exiting the control panel application");
    stopCommunication(channel);
  }

  private CommunicationChannel initiateCommunication(ControlPanelLogic logic) {
    CommunicationChannel channel;
    channel = initiateSocketCommunication(logic);
    return channel;
  }

  private CommunicationChannel initiateSocketCommunication(ControlPanelLogic logic) {
    RealCommunicationChannel channel = new RealCommunicationChannel(logic);
    logic.setCommunicationChannel(channel);
    if (channel.open()) {
      channel.start();
    } else {
      Logger.error("Communication channel could not be opened");
    }
    return channel;
  }

  private void stopCommunication(CommunicationChannel channel) {
    RealCommunicationChannel realChannel = (RealCommunicationChannel) channel;
    realChannel.stopSensorReading();
    realChannel.stopCommunicationChannel();
  }
}
