package no.ntnu.idata2304.project.listeners.greenhouse;

import no.ntnu.idata2304.project.greenhouse.SensorActuatorNode;

/**
 * Listener for sensor update events. This will (probably) be usable only on the sensor/actuator
 * node (greenhouse) side, where the real sensor objects are available. The control panel side has
 * only sensor reading values available, not the sensors themselves.
 */
public interface SensorListener {

  /**
   * An event that is fired every time sensor values are updated.
   *
   * @param node The node having a list of sensors with new values (readings)
   */
  void sensorsUpdated(SensorActuatorNode node);
}
