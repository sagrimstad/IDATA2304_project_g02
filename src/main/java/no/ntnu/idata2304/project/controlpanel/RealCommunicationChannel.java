package no.ntnu.idata2304.project.controlpanel;

import static no.ntnu.idata2304.project.tools.Parser.parseDoubleOrError;
import static no.ntnu.idata2304.project.tools.Parser.parseIntegerOrError;

import java.util.LinkedList;
import java.util.List;
import no.ntnu.idata2304.project.greenhouse.Actuator;
import no.ntnu.idata2304.project.greenhouse.Sensor;
import no.ntnu.idata2304.project.greenhouse.SensorReading;
import no.ntnu.idata2304.project.tools.Logger;

/**
 * Represents a real communication channel.
 */
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

  private void parseActuators(String actuatorSpecification, SensorActuatorNodeInfo info) {
    String[] parts = actuatorSpecification.split(" ");
    for (String part : parts) {
      parseActuatorInfo(part, info);
    }
  }

  private void parseActuatorInfo(String s, SensorActuatorNodeInfo info) {
    String[] actuatorInfo = s.split("_");
    if (actuatorInfo.length != 2) {
      throw new IllegalArgumentException("Invalid actuator info format: " + s);
    }
    int actuatorCount = parseIntegerOrError(actuatorInfo[0],
        "Invalid actuator count: " + actuatorInfo[0]);
    String actuatorType = actuatorInfo[1];
    for (int i = 0; i < actuatorCount; ++i) {
      Actuator actuator = new Actuator(actuatorType, info.getId());
      actuator.setListener(logic);
      info.addActuator(actuator);
    }
  }

  public void advertiseSensorData(String specification) {
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Sensor specification can't be null or empty");
    }
    String[] parts = specification.split(";");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Incorrect specification format: " + specification);
    }
    int nodeID = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
    List<SensorReading> sensors = parseSensors(parts[1]);
  }

  /**
   * Parses a sensor reading, seperated by ",", into a new reading.
   *
   * @param sensorInfo the information to parse.
   * @return the parsed reading.
   */
  private List<SensorReading> parseSensors(String sensorInfo) {
    List<SensorReading> readings = new LinkedList<>();
    String[] readingInfo = sensorInfo.split(",");
    for (String reading : readingInfo) {
      readings.add(parseReading(reading));
    }
    return readings;
  }

  /**
   * Parses a reading, seperated by a "=" sign, into a type, value and unit.
   *
   * @param reading the line of text wanted to parse.
   * @return a new {@link SensorReading} object with the new values and units.
   */
  private SensorReading parseReading(String reading) {
    String[] assignmentParts = reading.split("=");
    if (assignmentParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor reading specified: " + reading);
    }
    String[] valueParts = assignmentParts[1].split(" ");
    if (valueParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor value/unit: " + reading);
    }
    String sensorType = assignmentParts[0];
    double value = parseDoubleOrError(valueParts[0], "Invalid sensor value: " + valueParts[0]);
    String unit = valueParts[1];
    return new SensorReading(sensorType, value, unit);
  }

  /**
   * Sending an actuator change.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param isOn       When true, actuator must be turned on; off when false.
   */
  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    String state = isOn ? "ON" : "off";
    Logger.info("Sending command to greenhouse: turn " + state + " actuator"
        + "[" + actuatorId + "] on node " + nodeId);
  }

  @Override
  public boolean open() {
    return false;
  }
}
