package no.ntnu.idata2304.project.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.ntnu.idata2304.project.greenhouse.Actuator;
import no.ntnu.idata2304.project.greenhouse.Sensor;
import no.ntnu.idata2304.project.greenhouse.SensorActuatorNode;

/**
 * The NodeSerializer class represents a tool that serializes nodes to a lists of strings.
 * 
 * @author  Group 2
 * @version v2.0 (2023.12.07)
 */
public class NodeSerializer {

  /**
   * Constructs an instance of the NodeSerializer class.
   *
   * <p>This class is not supposed to be instantiated.</p>
   */
  private NodeSerializer() {
    // Intentionally left blank
  }

  /**
   * Returns a list of strings containing all nodes along with their actuators on a string format
   * serialized from a specified map of nodes.
   * 
   * <p>It is possible for a node to have no actuators. In this case, only the id of the node is
   * used.</p>
   * 
   * @param nodes A specified map of nodes
   * @return A list of strings containing all nodes along with their actuators on a string format
   *         serialized from a specified map of nodes
   */
  public static List<String> toString(Map<Integer, SensorActuatorNode> nodes) {
    List<String> list = new ArrayList<>();
    for (SensorActuatorNode node : nodes.values()) {
      String s = Integer.toString(node.getId());
      Map<Integer, Actuator> actuators = node.getActuators().getAll();
      if (!actuators.values().isEmpty()) {
        s = s + ";";
        int count = 0;
        for (Actuator actuator : actuators.values()) {
          String n = " 1_" + actuator.getType();
          if (count < 1) {
            n = n.substring(1, n.length());
            count++;
          }
          s = s + n;
        }
      }
      list.add(s);
    }
    return list;
  }

  /**
   * Returns a list of strings containing all sensors for each node on a string format serialized
   * from a specified map of nodes.
   * 
   * @param nodes A specified map of nodes
   * @return A list of strings containing all sensors for each node on a string format serialized
   *         from a specified map of nodes
   */
  public static List<String> toSensorString(Map<Integer, SensorActuatorNode> nodes) {
    List<String> list = new ArrayList<>();
    for (SensorActuatorNode node : nodes.values()) {
      String s = Integer.toString(node.getId());
      List<Sensor> sensors = node.getSensors();
      if (!sensors.isEmpty()) {
        s = s + ";";
        int count = 0;
        for (Sensor sensor : sensors) {
          String n = "," + sensor.getType() + "=" + sensor.getReading().getValue() + " " +
                     sensor.getReading().getUnit();
          if (count < 1) {
            n = n.substring(1, n.length());
            count++;
          }
          s = s + n;
        }
        list.add(s);
      }
    }
    return list;
  }
}
