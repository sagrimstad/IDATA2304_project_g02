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
  private boolean read;
  private int delay;

  /**
   * Creates a communication channel.
   *
   * @param logic the application logic of the control panel node.
   */
  public RealCommunicationChannel(ControlPanelLogic logic) {
    this.logic = logic;
    this.read = true;
    this.delay = 2;
  }

  /**
   * Starts the communication channel by receiving commands from the server, telling the control
   * panel what nodes along with actuators to spawn, and then telling the channel to start reading
   * sensor updates from the server.
   */
  public void start() {
    try {
      String serverCommand;
      do {
        serverCommand = this.socketReader.readLine();
        if (!serverCommand.equals("0")) {
          this.spawnNode(serverCommand, this.delay);
          this.delay++;
        }
      } while (!serverCommand.equals("0"));
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          startReading();
        }
      }, this.delay * 1000L);
    } catch (IOException e) {
      Logger.error("Could not receive command from server");
    }
  }

  /**
   * Starts the reading of commands from the server.
   */
  public void startReading() {
    try {
      String serverCommand;
      while (this.read) {
        serverCommand = this.socketReader.readLine();
        if (serverCommand.contains(":")) {
          String[] parts = serverCommand.split("[;:]");
          int nodeId = Integer.parseInt(parts[0]);
          int actuatorId = Integer.parseInt(parts[1]);
          boolean isOn = Boolean.parseBoolean(parts[2]);
          this.advertiseActuatorState(nodeId, actuatorId, isOn, this.delay);
        } else {
          try {
            this.advertiseSensorData(serverCommand, this.delay);
          } catch (IllegalArgumentException e) {
            Logger.error("Invalid sensor reading, continuing to read...");
          }
        }
      }
    } catch (IOException e) {
      Logger.error("Could not receive command from server");
    }
  }

  /**
   * Stops the reading of sensor updates from the server.
   */
  public void stopSensorReading() {
    this.read = false;
  }

  /**
   * Spawn a new sensor/actuator node information after a given delay.
   *
   * @param specification A (temporary) manual configuration of the node in the following format
   *                      [nodeId] semicolon [actuator_count_1] underscore [actuator_type_1] space
   *                      ... space [actuator_count_M] underscore [actuator_type_M]
   */
  private void spawnNode(String specification, int delay) {
    SensorActuatorNodeInfo nodeInfo = this.createSensorNodeInfoFrom(specification);
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onNodeAdded(nodeInfo);
      }
    }, delay * 1000L);
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

  // TODO Change JavaDoc specifiation according to protocol

  /**
   * Advertise new sensor readings.
   *
   * @param specification Specification of the readings in the following format: [nodeID] semicolon
   *                      [sensor_type_1] equals [sensor_value_1] space [unit_1] comma ... comma
   *                      [sensor_type_N] equals [sensor_value_N] space [unit_N]
   */
  public void advertiseSensorData(String specification, int delay) {
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
    }, delay * 1000L);
  }

  /**
   * Advertise that an actuator has changed it's state.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator.
   * @param on         When true, actuator is on; off when false.
   */
  public void advertiseActuatorState(int nodeId, int actuatorId, boolean on, int delay) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onActuatorStateChanged(nodeId, actuatorId, on);
      }
    }, delay * 1000L);
  }

  /**
   * Sends a notification of an actuator change to the connected device through the communication
   * socket.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param isOn       When true, actuator must be turned on; off when false.
   */
  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    this.socketWriter.println(nodeId + ";" + actuatorId + ":" + isOn);
  }

  /**
   * Opens a communication channel by establishing a socket connection to the specified host and
   * port.
   *
   * @return true if the communication channel is successfully opened, false otherwise.
   */
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

  /**
   * Stops the communication channel.
   */
  public void stopCommunicationChannel() {
    if (this.socket != null) {
      this.delay++;
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            socket.close();
            socket = null;
            socketReader = null;
            socketWriter = null;
          } catch (IOException e) {
            Logger.error("Could not close the socket: " + e.getMessage());
          }
        }
      }, delay * 1000L);
    }
  }
}