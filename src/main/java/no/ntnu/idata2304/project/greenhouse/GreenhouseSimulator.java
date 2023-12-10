package no.ntnu.idata2304.project.greenhouse;

import java.util.HashMap;
import java.util.Map;
import no.ntnu.idata2304.project.listeners.greenhouse.NodeStateListener;
import no.ntnu.idata2304.project.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {

  private final Map<Integer, SensorActuatorNode> nodes = new HashMap<>();
  private final boolean fake;
  private GreenhouseServer server;

  /**
   * Create a greenhouse simulator.
   *
   * @param fake When true, simulate a fake periodic events instead of creating socket
   *             communication
   */
  public GreenhouseSimulator(boolean fake) {
    this.fake = fake;
  }

  /**
   * Initialise the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    createNode(1, 2, 1, 0, 0);
    createNode(1, 0, 0, 2, 1);
    createNode(2, 0, 0, 0, 0);
    Logger.info("Greenhouse initialized");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    SensorActuatorNode node = DeviceFactory.createNode(
        temperature, humidity, windows, fans, heaters);
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    for (SensorActuatorNode node : nodes.values()) {
      node.start();
    }
    Logger.info("Simulator started");
    initiateCommunication();
  }

  private void initiateCommunication() {
      initiateRealCommunication();
  }

  /**
   * Initiates real communication by creating and starting a GreenhouseServer instance.
   */
  private void initiateRealCommunication() {
    this.server = new GreenhouseServer(this.nodes);
    this.server.startServer();
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  public void stop() {
    stopCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
      this.server.stopServer();
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (SensorActuatorNode node : nodes.values()) {
      node.addStateListener(listener);
    }
  }
}
