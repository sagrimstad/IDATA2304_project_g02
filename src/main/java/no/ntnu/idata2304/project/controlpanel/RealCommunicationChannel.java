package no.ntnu.idata2304.project.controlpanel;

import static no.ntnu.idata2304.project.greenhouse.GreenhouseServer.PORT_NUMBER;
import static no.ntnu.idata2304.project.tools.Parser.parseIntegerOrError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import no.ntnu.idata2304.project.greenhouse.SensorReading;
import no.ntnu.idata2304.project.tools.Logger;
import no.ntnu.idata2304.project.tools.NodeParser;

/**
 * Represents a real communication channel.
 */
public class RealCommunicationChannel implements CommunicationChannel {

  private final ControlPanelLogic logic;
  private Socket socket;
  private PrintWriter socketWriter;
  private BufferedReader socketReader;
  private int delay;

  /**
   * Creates a communication channel.
   *
   * @param logic the application logic of the control panel node.
   */
  public RealCommunicationChannel(ControlPanelLogic logic) {
    this.logic = logic;
    this.delay = 2;
  }

  /**
   * Starts the communcation channel by receiving commands from the server, telling the control panel
   * what nodes to spawn.
   */
  public void start() {
    try {
      String serverCommand;
      do {
        serverCommand = this.socketReader.readLine();
        if (!serverCommand.equals("0")) {
          this.spawnNode(serverCommand);
        }
      } while (!serverCommand.equals("0"));
    } catch (IOException e) {
      Logger.error("Could not receive command from server");
    }
  }

  /**
   * Spawn a new sensor/actuator node information after a given delay.
   *
   * @param specification A (temporary) manual configuration of the node in the following format
   *                      [nodeId] semicolon [actuator_count_1] underscore [actuator_type_1] space
   *                      ... space [actuator_count_M] underscore [actuator_type_M]
   */
  private void spawnNode(String specification) {
    SensorActuatorNodeInfo nodeInfo = this.createSensorNodeInfoFrom(specification);
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onNodeAdded(nodeInfo);
      }
    }, this.delay * 1000L);
  }

  private SensorActuatorNodeInfo createSensorNodeInfoFrom(String specification) {
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Node specification can't be empty");
    }
    String[] parts = specification.split(";");
    if (parts.length > 3) {
      throw new IllegalArgumentException("Incorrect specification format");
    }
    int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
    SensorActuatorNodeInfo info = new SensorActuatorNodeInfo(nodeId);
    if (parts.length == 2) {
      NodeParser.parseActuators(parts[1], info, logic);
    }
    return info;
  }

  /**
   * Advertise that a node is removed.
   *
   * @param nodeId ID of the removed node
   */
  public void advertiseRemovedNode(int nodeId) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onNodeRemoved(nodeId);
      }
    }, this.delay * 1000L);
  }

  /**
   * Advertise new sensor readings.
   *
   * @param specification Specification of the readings in the following format: [nodeID] semicolon
   *                      [sensor_type_1] equals [sensor_value_1] space [unit_1] comma ... comma
   *                      [sensor_type_N] equals [sensor_value_N] space [unit_N]
   */
  public void advertiseSensorData(String specification) {
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Sensor specification can't be empty");
    }
    String[] parts = specification.split(";");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Incorrect specification format: " + specification);
    }
    int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
    List<SensorReading> sensors = NodeParser.parseSensors(parts[1]);
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onSensorData(nodeId, sensors);
      }
    }, this.delay * 1000L);
  }

  /**
   * Advertise that an actuator has changed it's state.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator.
   * @param on         When true, actuator is on; off when false.
   */
  public void advertiseActuatorState(int nodeId, int actuatorId, boolean on) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onActuatorStateChanged(nodeId, actuatorId, on);
      }
    }, this.delay * 1000L);
  }

  /**
   * Sends a specified command over the communication channel.
   * 
   * @param command A specified command
   */
  public void sendCommand(String command) {
    this.socketWriter.println(command);
    Logger.info("Sending " + command);
  }

  /**
   * Sends a sensor reading over the communication channel
   *
   * @param sensorReading A specified sensor reading
   */
  public void sendSensorReading(String sensorReading) {
    this.socketWriter.println(sensorReading);
    Logger.info("Sending " + sensorReading);
    //TODO: Actually send the message over the socket!
  }

  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    String state = isOn ? "ON" : "off";
    Logger.info("Sending command to greenhouse: turn " + state + " actuator"
        + "[" + actuatorId + "] on node " + nodeId);
  }

  @Override
  public boolean open() {
    boolean success = false;
    try {
      this.socket = new Socket("localhost", PORT_NUMBER);
      this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
      this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      success = true;
    } catch (IOException e) {
      Logger.error("Could not open communication channel");
    }
    return success;
  }
}
