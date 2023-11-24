package no.ntnu.idata2304.project;

import static no.ntnu.idata2304.project.tools.Parser.parseDoubleOrError;
import static no.ntnu.idata2304.project.tools.Parser.parseIntegerOrError;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import no.ntnu.idata2304.project.greenhouse.SensorReading;
import no.ntnu.idata2304.project.message.Message;

/**
 * Serializes messages to protocol-defined strings and vice versa.
 */
public class MessageSerializer {


  /**
   * Not allowed to instantiate this utility class.
   */
  private MessageSerializer() {
    // Intentionally left blank.
  }

  /**
   * Parses a reading (as plain text) into a {@link SensorReading} object.
   *
   * @param reading the wanted text to parse
   * @return the new object.
   */
  public SensorReading parseSensorReading(String reading) {
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

  private List<SensorReading> parseSensors(String sensorInfo) {
    List<SensorReading> readings = new LinkedList<>();
    String[] readingInfo = sensorInfo.split(",");
    for (String reading : readingInfo) {
      readings.add(parseSensorReading(reading));
    }
    return readings;
  }

  /**
   * Advertises a change in sensor data ?
   * TODO: fullføre metode / skal denne her?
   *
   * @param specification Specification of the readings in the following format: [nodeID] semicolon
   *                      [sensor_type_1] equals [sensor_value_1] space [unit_1] comma ... comma
   *                      [sensor_type_N] equals [sensor_value_N] space [unit_N]
   * @param delay         in seconds
   */
  public void advertiseSensorData(String specification, int delay) {
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Sensor specification can't be empty");
    }
    String[] parts = specification.split(";");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Incorrect specification format: " + specification);
    }
    int nodeId = parseIntegerOrError(parts[0], "Invalid node ID: " + parts[0]);
    List<SensorReading> sensors = parseSensors(parts[1]);
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        //logic.onSensorData(nodeId, sensors);
      }
    }, delay * 1000L);
  }

  /**
   * Create message from a string, according to the communication protocol.
   *
   * @param s The string sent over the communication channel
   * @return The logical message, as interpreted according to the protocol
   */
  public static Message fromString(String s) {
    Message m = null;
    if (s != null) {
      switch (s) {
        default -> {
          if (s.length() > 1) {
            m = parseParametrizedMessage(s);
          }
        }
      }
    }
    return m;
  }

  private static Message parseParametrizedMessage(String s) {
    return null;
  }

  private static Integer parseInteger(String s) {
    Integer i = null;
    try {
      i = Integer.valueOf(s);
    } catch (NumberFormatException e) {
      System.err.println("Could not parse integer <" + s + ">");
    }
    return i;
  }

  /**
   * Convert a message to a serialized string.
   *
   * @param m The message to translate
   * @return String representation of the message
   */
  public static String toString(Message m) {
    return null;
  }
}