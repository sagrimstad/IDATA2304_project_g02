package no.ntnu.idata2304.project.controlpanel;

import static no.ntnu.idata2304.project.greenhouse.GreenhouseServer.PORT_NUMBER;
import static no.ntnu.idata2304.project.tools.Parser.parseDoubleOrError;
import static no.ntnu.idata2304.project.tools.Parser.parseIntegerOrError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import no.ntnu.idata2304.project.CommunicationChannel;
import no.ntnu.idata2304.project.greenhouse.Actuator;
import no.ntnu.idata2304.project.greenhouse.SensorReading;
import no.ntnu.idata2304.project.tools.Logger;

/**
 * Represents a real communication channel.
 */
public class ControlPanelCommunicationChannel implements CommunicationChannel {

  // private final ControlPanelLogic logic;
  private Socket socket;
  private PrintWriter socketWriter;
  private BufferedReader socketReader;

  // /**
  //  * Creates a communication channel.
  //  *
  //  * @param logic the application logic of the control panel node.
  //  */
  // public ControlPanelCommunicationChannel(ControlPanelLogic logic) {
  //   this.logic = logic;
  // }

  /**
   * Sends a specified command over the communcation channel.
   * 
   * @param command A specified command
   */
  public void sendCommand(String command) {
    this.socketWriter.println(command);
    Logger.info("Sending " + command);
  }

  // /**
  //  * Spawn a new sensor/actuator node information after a given delay.
  //  *
  //  * @param specification A (temporary) manual configuration of the node in the following format
  //  *                      [nodeId] semicolon [actuator_count_1] underscore [actuator_type_1] space
  //  *                      ... space [actuator_count_M] underscore [actuator_type_M]
  //  * @param delay         Delay in seconds
  //  */
  // public void spawnNode(String specification, int delay) {
  //   SensorActuatorNodeInfo nodeInfo = createSensorNodeInfoFrom(specification);
  //   Timer timer = new Timer();
  //   timer.schedule(new TimerTask() {
  //     @Override
  //     public void run() {
  //       logic.onNodeAdded(nodeInfo);
  //     }
  //   }, delay * 1000L);
  // }

  // private SensorActuatorNodeInfo createSensorNodeInfoFrom(String specification) {
  //   if (specification == null || specification.isEmpty()) {
  //     throw new IllegalArgumentException("Node specification can't be empty");
  //   }
  //   String[] parts = specification.split(";");
  //   if (parts.length > 3) {
  //     throw new IllegalArgumentException("Incorrect specification format");
  //   }
  //   int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
  //   SensorActuatorNodeInfo info = new SensorActuatorNodeInfo(nodeId);
  //   if (parts.length == 2) {
  //     parseActuators(parts[1], info);
  //   }
  //   return info;
  // }

  // private void parseActuators(String actuatorSpecification, SensorActuatorNodeInfo info) {
  //   String[] parts = actuatorSpecification.split(" ");
  //   for (String part : parts) {
  //     parseActuatorInfo(part, info);
  //   }
  // }

  // private void parseActuatorInfo(String s, SensorActuatorNodeInfo info) {
  //   String[] actuatorInfo = s.split("_");
  //   if (actuatorInfo.length != 2) {
  //     throw new IllegalArgumentException("Invalid actuator info format: " + s);
  //   }
  //   int actuatorCount = parseIntegerOrError(actuatorInfo[0],
  //       "Invalid actuator count: " + actuatorInfo[0]);
  //   String actuatorType = actuatorInfo[1];
  //   for (int i = 0; i < actuatorCount; ++i) {
  //     Actuator actuator = new Actuator(actuatorType, info.getId());
  //     actuator.setListener(logic);
  //     info.addActuator(actuator);
  //   }
  // }

  // /**
  //  * Advertise that a node is removed.
  //  *
  //  * @param nodeId ID of the removed node
  //  * @param delay  Delay in seconds
  //  */
  // public void advertiseRemovedNode(int nodeId, int delay) {
  //   Timer timer = new Timer();
  //   timer.schedule(new TimerTask() {
  //     @Override
  //     public void run() {
  //       logic.onNodeRemoved(nodeId);
  //     }
  //   }, delay * 1000L);
  // }

  // /**
  //  * Advertise that an actuator has changed it's state.
  //  *
  //  * @param nodeId     ID of the node to which the actuator is attached
  //  * @param actuatorId ID of the actuator.
  //  * @param on         When true, actuator is on; off when false.
  //  * @param delay      The delay in seconds after which the advertisement will be generated
  //  */
  // public void advertiseActuatorState(int nodeId, int actuatorId, boolean on, int delay) {
  //   Timer timer = new Timer();
  //   timer.schedule(new TimerTask() {
  //     @Override
  //     public void run() {
  //       logic.onActuatorStateChanged(nodeId, actuatorId, on);
  //     }
  //   }, delay * 1000L);
  // }

  // /**
  //  * Advertise new sensor readings.
  //  *
  //  * @param specification Specification of the readings in the following format: [nodeID] semicolon
  //  *                      [sensor_type_1] equals [sensor_value_1] space [unit_1] comma ... comma
  //  *                      [sensor_type_N] equals [sensor_value_N] space [unit_N]
  //  * @param delay         Delay in seconds
  //  */
  // public void advertiseSensorData(String specification, int delay) {
  //   if (specification == null || specification.isEmpty()) {
  //     throw new IllegalArgumentException("Sensor specification can't be empty");
  //   }
  //   String[] parts = specification.split(";");
  //   if (parts.length != 2) {
  //     throw new IllegalArgumentException("Incorrect specification format: " + specification);
  //   }
  //   int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
  //   List<SensorReading> sensors = parseSensors(parts[1]);
  //   Timer timer = new Timer();
  //   timer.schedule(new TimerTask() {
  //     @Override
  //     public void run() {
  //       logic.onSensorData(nodeId, sensors);
  //     }
  //   }, delay * 1000L);
  // }

  // private List<SensorReading> parseSensors(String sensorInfo) {
  //   List<SensorReading> readings = new LinkedList<>();
  //   String[] readingInfo = sensorInfo.split(",");
  //   for (String reading : readingInfo) {
  //     readings.add(parseReading(reading));
  //   }
  //   return readings;
  // }

  // private SensorReading parseReading(String reading) {
  //   String[] assignmentParts = reading.split("=");
  //   if (assignmentParts.length != 2) {
  //     throw new IllegalArgumentException("Invalid sensor reading specified: " + reading);
  //   }
  //   String[] valueParts = assignmentParts[1].split(" ");
  //   if (valueParts.length != 2) {
  //     throw new IllegalArgumentException("Invalid sensor value/unit: " + reading);
  //   }
  //   String sensorType = assignmentParts[0];
  //   double value = parseDoubleOrError(valueParts[0], "Invalid sensor value: " + valueParts[0]);
  //   String unit = valueParts[1];
  //   return new SensorReading(sensorType, value, unit);
  // }

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