package no.ntnu.idata2304.project.tools;

import static no.ntnu.idata2304.project.tools.Parser.parseDoubleOrError;
import static no.ntnu.idata2304.project.tools.Parser.parseIntegerOrError;

import java.util.LinkedList;
import java.util.List;

import no.ntnu.idata2304.project.controlpanel.ControlPanelLogic;
import no.ntnu.idata2304.project.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.idata2304.project.greenhouse.Actuator;
import no.ntnu.idata2304.project.greenhouse.SensorReading;

/**
 * The NodeParser class represents a tool that parses different strings into actuator and sensor
 * information that can be used by a control panel.
 * 
 * @author  Group 2
 * @version v1.0 (2023.12.04)
 */
public class NodeParser {
  /**
   * Constructs an instance of the NodeParser class.
   * 
   * <p>This class is not supposed to be instantiated.</p>
   */
  private NodeParser() {
    // Intentionally left blank
  }

  public static void parseActuators(String actuatorSpecification, SensorActuatorNodeInfo info, ControlPanelLogic logic) {
    String[] parts = actuatorSpecification.split(" ");
    for (String part : parts) {
      parseActuatorInfo(part, info, logic);
    }
  }

  private static void parseActuatorInfo(String s, SensorActuatorNodeInfo info, ControlPanelLogic logic) {
    String[] actuatorInfo = s.split("_");
    if (actuatorInfo.length != 2) {
      throw new IllegalArgumentException("Invalid actuator info format: " + s);
    }
    int actuatorId = parseIntegerOrError(actuatorInfo[0],
        "Invalid actuator count: " + actuatorInfo[0]);
    String actuatorType = actuatorInfo[1];
      Actuator actuator = new Actuator(actuatorId, actuatorType, info.getId());
      actuator.setListener(logic);
      info.addActuator(actuator);
  }

  public static List<SensorReading> parseSensors(String sensorInfo) {
    List<SensorReading> readings = new LinkedList<>();
    String[] readingInfo = sensorInfo.split(",");
    for (String reading : readingInfo) {
      readings.add(parseReading(reading));
    }
    return readings;
  }

  private static SensorReading parseReading(String reading) {
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
}
