package no.ntnu.idata2304.project.greenhouse;

/**
 * Runs the Greenhouse simulator, which again runs the greenhouse server.
 * 
 * <p>This class is used for test purposes only!</p>
 */
public class Greenhouse {
  /**
   * Runs the greenhouse simulator.
   *
   * @param args Command line arguments, not used.
   */
  public static void main(String[] args) {
    GreenhouseSimulator greenhouseSimulator = new GreenhouseSimulator(false);
    greenhouseSimulator.start();
  }
}
