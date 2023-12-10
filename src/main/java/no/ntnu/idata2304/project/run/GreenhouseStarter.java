package no.ntnu.idata2304.project.run;

import no.ntnu.idata2304.project.greenhouse.GreenhouseSimulator;
import no.ntnu.idata2304.project.tools.Logger;

/**
 * Run a greenhouse simulation using command-line interface (no GUI).
 */
public class GreenhouseStarter {

  /**
   * Application entrypoint for the command-line version of the simulator.
   *
   * @param args Command line arguments, only the first one of them used: when it is "fake", emulate
   *             fake events, when it is either something else or not present, use real socket
   *             communication.
   */
  public static void main(String[] args) {
    Logger.info("Running greenhouse simulator in command line (without GUI)...");
    boolean fake = false;
    if (args.length == 1 && "fake".equals(args[0])) {
      fake = true;
      Logger.info("Using FAKE events");
    }
    GreenhouseSimulator simulator = new GreenhouseSimulator(fake);
    simulator.initialize();
    simulator.start();
  }
}
