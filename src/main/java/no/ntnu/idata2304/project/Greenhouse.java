package no.ntnu.idata2304.project;

import no.ntnu.idata2304.project.controlpanel.ControlPanelLogic;
import no.ntnu.idata2304.project.greenhouse.GreenhouseServer;

/**
 * Runs the Greenhouse logic and server.
 */
public class Greenhouse {

  /**
   * Runs the Control panel logic and server.
   *
   * @param args Command line arguments, not used.
   */
  public static void main(String[] args) {
    ControlPanelLogic logic = new ControlPanelLogic();
    GreenhouseServer greenhouseServer = new GreenhouseServer(logic);
    greenhouseServer.startServer();
  }


}
