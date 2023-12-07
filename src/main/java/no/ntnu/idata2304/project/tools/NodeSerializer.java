package no.ntnu.idata2304.project.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.ntnu.idata2304.project.greenhouse.Actuator;
import no.ntnu.idata2304.project.greenhouse.SensorActuatorNode;

/**
 * The NodeSerializer class represents a tool that serializes nodes to a list of one or more
 * strings.
 *
 * @author Group 2
 * @version v1.0 (2023.12.04)
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
   * Returns a list of strings serialized from a specified map of nodes containing all nodes on a
   * string format.
   *
   * @param nodes A specified map of nodes
   * @return A list of strings serialized from a specified map of nodes containing all nodes on a
   * string format
   */
  public static List<String> toString(Map<Integer, SensorActuatorNode> nodes) {
    List<String> list = new ArrayList<String>();
    for (SensorActuatorNode node : nodes.values()) {
      String s = Integer.toString(node.getId());
      Map<Integer, Actuator> actuators = node.getActuators().getAll();
      if (!actuators.values().isEmpty()) {
        s = s + ";";
        int count = 0;
        for (Actuator actuator : actuators.values()) {
          String a = " 1_" + actuator.getType();
          if (count < 1) {
            a = a.substring(1, a.length());
            count++;
          }
          s = s + a;
        }
      }
      list.add(s);
    }
    return list;
  }
}
