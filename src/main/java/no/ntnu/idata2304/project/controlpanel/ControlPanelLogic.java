package no.ntnu.idata2304.project.controlpanel;

import java.util.LinkedList;
import java.util.List;
import no.ntnu.idata2304.project.greenhouse.Actuator;
import no.ntnu.idata2304.project.greenhouse.SensorReading;
import no.ntnu.idata2304.project.listeners.common.ActuatorListener;
import no.ntnu.idata2304.project.listeners.common.CommunicationChannelListener;
import no.ntnu.idata2304.project.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.idata2304.project.tools.Logger;

/**
 * The central logic of a control panel node. It uses a communication channel to send commands and
 * receive events. It supports listeners who will be notified on changes (for example, a new node is
 * added to the network, or a new sensor reading is received). Note: this class may look like
 * unnecessary forwarding of events to the GUI. In real projects (read: "big projects") this logic
 * class may do some "real processing" - such as storing events in a database, doing some checks,
 * sending emails, notifications, etc. Such things should never be placed inside a GUI class (JavaFX
 * classes). Therefore, we use proper structure here, even though you may have no real control-panel
 * logic in your projects.
 */
public class ControlPanelLogic implements GreenhouseEventListener, ActuatorListener,
    CommunicationChannelListener {

  private final List<GreenhouseEventListener> listeners = new LinkedList<>();

  private CommunicationChannel communicationChannel;
  private CommunicationChannelListener communicationChannelListener;

  /**
   * Set the channel over which control commands will be sent to sensor/actuator nodes.
   *
   * @param communicationChannel The communication channel, the event sender
   */
  public void setCommunicationChannel(CommunicationChannel communicationChannel) {
    this.communicationChannel = communicationChannel;
  }

  /**
   * Set listener which will get notified when communication channel is closed.
   *
   * @param listener The listener
   */
  public void setCommunicationChannelListener(CommunicationChannelListener listener) {
    this.communicationChannelListener = listener;
  }

  /**
   * Add an event listener.
   *
   * @param listener The listener who will be notified on all events
   */
  public void addListener(GreenhouseEventListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Notifies listeners about the addition of a new sensor-actuator node.
   *
   * @param nodeInfo Information about the added node
   */
  @Override
  public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
    listeners.forEach(listener -> listener.onNodeAdded(nodeInfo));
  }

  /**
   * Notifies listeners about the removal of a sensor-actuator node.
   *
   * @param nodeId ID of the node which has disappeared (removed)
   */
  @Override
  public void onNodeRemoved(int nodeId) {
    listeners.forEach(listener -> listener.onNodeRemoved(nodeId));
  }

  /**
   * Notifies listeners about the availability of sensor data from a sensor-actuator node.
   *
   * @param nodeId  ID of the node
   * @param sensors List of all current sensor values
   */
  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {
    listeners.forEach(listener -> listener.onSensorData(nodeId, sensors));
  }

  /**
   * Notifies listeners about a change in the state of an actuator associated with a
   * sensor-actuator node.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   * @param isOn       When true, actuator is on; off when false.
   */
  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
    listeners.forEach(listener -> listener.onActuatorStateChanged(nodeId, actuatorId, isOn));
  }

  /**
   * Updates the state of an actuator associated with a sensor-actuator
   * node and notifies listeners.
   *
   * @param nodeId   ID of the node on which this actuator is placed
   * @param actuator The actuator that has changed its state
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (communicationChannel != null) {
      communicationChannel.sendActuatorChange(nodeId, actuator.getId(), actuator.isOn());
    }
    listeners.forEach(listener ->
        listener.onActuatorStateChanged(nodeId, actuator.getId(), actuator.isOn())
    );
  }

  /**
   * Notifies the system about the closure of the communication channel
   * and updates associated logic.
   */
  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, updating logic...");
    if (communicationChannelListener != null) {
      communicationChannelListener.onCommunicationChannelClosed();
    }
  }
}
