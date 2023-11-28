package no.ntnu.idata2304.project.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NodeCommunicationChannel {
  private Socket clientSocket;
  private PrintWriter outputWriter;
  private BufferedReader inputReader;

  public NodeCommunicationChannel() {
    // Intentionally left blank
  }

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

  public void sendDataToServer(String data) {
    if (this.outputWriter != null) {
      this.outputWriter.println(data);
    }
  }

  public String receiveDataFromServer() {
    String message = null;
    try {
      if (this.inputReader != null) {
        message = inputReader.readLine();
      }
    } catch (IOException e) {
      System.err.println("Error while receiving message from server: " + e.getMessage());
    }
    return message;
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
}

