package no.ntnu.idata2304.project.node;

import static no.ntnu.idata2304.project.greenhouse.GreenhouseServer.PORT_NUMBER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import no.ntnu.idata2304.project.controlpanel.CommunicationChannel;
import no.ntnu.idata2304.project.tools.Logger;

/**
 * Represents a communication channel between a node (client) and a server.
 */
public class NodeCommunicationChannel implements CommunicationChannel {

  private Socket clientSocket;
  private PrintWriter outputWriter;
  private BufferedReader inputReader;

  public NodeCommunicationChannel() {
    // Intentionally left blank
  }

  /**
   * Connect to a Server.
   *
   * @param serverAddress the address of the server
   * @param serverPort    the port number of the server
   * @return True if successfully connected to the server, false otherwise.
   */
  public boolean connectToServer(String serverAddress, int serverPort) {
    boolean success = false;
    try {
      this.clientSocket = new Socket(serverAddress, serverPort);
      this.outputWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
      this.inputReader = new BufferedReader(
          new InputStreamReader(this.clientSocket.getInputStream()));
      success = true;
    } catch (IOException e) {
      System.err.println("Could not connect to server: " + e.getMessage());
    }
    return success;
  }

  @Override
  public boolean open() {
    boolean success = false;
    try {
      this.clientSocket = new Socket("localhost", PORT_NUMBER);
      this.outputWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
      this.inputReader = new BufferedReader(
          new InputStreamReader(this.clientSocket.getInputStream()));
      success = true;
    } catch (IOException e) {
      Logger.error("Could not open communication channel");
    }
    return success;
  }

  /**
   * Sending data to the server. (data is represented by a string of text)
   *
   * @param data the wanted information to send.
   */
  public void sendDataToServer(String data) {
    if (data.isBlank() && data == null) {
      throw new IllegalArgumentException();
    }
    if (this.outputWriter != null) {
      this.outputWriter.println(data);
    }
  }

  /**
   * Receives data, in the form of a string, from the server.
   *
   * @return the received data.
   */
  public String receiveDataFromServer() {
    String data = null;
    try {
      if (this.inputReader != null) {
        data = inputReader.readLine();
      }
    } catch (IOException e) {
      System.err.println("Error while receiving message from server: " + e.getMessage());
    }
    return data;
  }

  /**
   * Closes the connection.
   */
  public void closeConnection() {
    try {
      if (outputWriter != null) {
        outputWriter.close();
      }
      if (inputReader != null) {
        inputReader.close();
      }
      if (clientSocket != null && !clientSocket.isClosed()) {
        clientSocket.close();
      }
    } catch (IOException e) {
      System.err.println("Error while closing connection to server: " + e.getMessage());
    }
  }

  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    String state = isOn ? "ON" : "off";
    Logger.info("Sending command to greenhouse: turn " + state + " actuator"
        + "[" + actuatorId + "] on node " + nodeId);
  }
}

