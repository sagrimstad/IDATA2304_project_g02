package no.ntnu.idata2304.project.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.ntnu.idata2304.project.greenhouse.Actuator;
import no.ntnu.idata2304.project.greenhouse.GreenhouseServer;
import no.ntnu.idata2304.project.greenhouse.SensorActuatorNode;
import no.ntnu.idata2304.project.listeners.common.ActuatorListener;
import no.ntnu.idata2304.project.listeners.greenhouse.SensorListener;

/**
 * Handler for one specific client connection (TCP).
 *
 * @author Group 2
 * @version v2.0 (2023.12.07)
 */
public class ClientHandler extends Thread implements ActuatorListener, SensorListener {

  private final GreenhouseServer server;
  private final Socket socket;
  private final PrintWriter socketWriter;
  private final BufferedReader socketReader;

  /**
   * Create a new client handler.
   *
   * @param socket Socket associated with this client.
   * @param server References to the main Server class
   * @throws IOException When something goes wrong with establishing the input or output streams.
   */
  public ClientHandler(GreenhouseServer server, Socket socket) throws IOException {
    this.server = server;
    this.socket = socket;
    this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
    this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
  }

  /**
   * Run the client handling logic.
   */
  @Override
  public void run() {
    this.initialize();
    String receivedMessage;
    do {
      receivedMessage = readClientRequest();
      if (receivedMessage != null) {
        String response;
        do {
          String clientRequest = this.readClientRequest();
          if (clientRequest != null) {
            Logger.info("Received " + clientRequest);
            handleActuatorChangeCommand(receivedMessage);
            response = "OK";
            this.sendToClient(response);
          } else {
            response = null;
          }
        } while (response != null);
        Logger.info("Client " + this.socket.getRemoteSocketAddress() + " leaving");
        this.server.clientDisconnected(this);
      }
    } while (receivedMessage != null);
  }

  /**
   * Initializes the client by serializing all nodes along with their actuators to strings and
   * sending them to the client.
   */
  private void initialize() {
    this.sendNodesToClient(this.server.getNodes());
    this.sendToClient("0");
  }

  // private boolean isBroadcastMessage(Message response) {
  //   return response instanceof StateMessage
  //       || response instanceof CurrentChannelMessage;
  // }

  /**
   * Read one message form the socket - from the client.
   *
   * @return the received client message, or null on error.
   */
  private String readClientRequest() {
    String clientRequest;
    try {
      clientRequest = this.socketReader.readLine();
    } catch (IOException e) {
      clientRequest = null;
      Logger.error("Could not receive client request");
    }
    return clientRequest;
  }

  /**
   * Handles the incoming command and changes/updates the wanted actuator to the correct state. The
   * only type of command to come from the actuator is its change in state.
   */
  private void handleActuatorChangeCommand(String command) {
    try {
      if (command != null) {
        Logger.info(command); // temp line, only for debugging
        command = this.socketReader.readLine();
        String[] parts = command.split("[;:]");
        int nodeId = Integer.parseInt(parts[0]);
        int actuatorId = Integer.parseInt(parts[1]);
        String isOn = parts[2];

        SensorActuatorNode node = this.server.getNodes().get(nodeId);
        Actuator actuator = node.getActuator(actuatorId);

        switch (isOn) {
          case "true" -> actuator.turnOn();
          case "false" -> actuator.turnOff();
          default -> Logger.error("Error while changing state for actuator: " + actuator);
        }
      } else {
        System.err.println("Incorrect command: " + command);
      }
    } catch (IOException e) {
      System.err.println("Error while handling actuator change: " + e.getMessage());
    }
  }

  /**
   * Send a response from the server to the client, over the socket.
   *
   * @param message the message to send to the client.
   */
  public void sendToClient(String message) {
    this.socketWriter.println(message);
  }

  /**
   * Sends all nodes along with their actuators serialized from a specified map of nodes to the
   * client.
   * 
   * @param nodes A specified map of nodes
   */
  private void sendNodesToClient(Map<Integer, SensorActuatorNode> nodes) {
    List<String> nodeStrings = NodeSerializer.toString(nodes);
    for (String nodeString : nodeStrings) {
      this.sendToClient(nodeString);
    }
  }

  /**
   * Sends all sensors for each node serialized from a specified map of nodes to the client.
   * 
   * @param nodes A specified map of nodes
   */
  private void sendSensorsToClient(Map<Integer, SensorActuatorNode> nodes) {
    List<String> sensorStrings = NodeSerializer.toSensorString(nodes);
    for (String sensorString : sensorStrings) {
      this.sendToClient(sensorString);
    }
  }

  public void close() {
    try {
      if (this.socketWriter != null) {
        this.socketWriter.close();
      }
      if (this.socketReader != null) {
        this.socketReader.close();
      }
      if (this.socket != null) {
        this.socket.close();
      }
    } catch (IOException e) {
      System.err.println("Error while closing sockets: " + e.getMessage());
    }
  }

  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    //TODO: Send actuator state to control panel
  }

  @Override
  public void sensorsUpdated(SensorActuatorNode node) {
    Map<Integer, SensorActuatorNode> nodes = new HashMap<>();
    nodes.put(node.getId(), node);
    this.sendSensorsToClient(nodes);
  }
}
