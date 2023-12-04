package no.ntnu.idata2304.project.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import no.ntnu.idata2304.project.greenhouse.GreenhouseServer;
import no.ntnu.idata2304.project.message.CurrentChannelMessage;
import no.ntnu.idata2304.project.message.Message;
import no.ntnu.idata2304.project.message.StateMessage;

/**
 * Handler for one specific client connection (TCP).
 *
 * @author  Group 2
 * @version v1.0 (2023.11.10)
 */
public class ClientHandler extends Thread {

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
        if (isSensorReading(receivedMessage)) {
          handleReceivedMessage(receivedMessage);
        } else {
          String response;
          do {
            String clientRequest = this.readClientRequest();
            if (clientRequest != null) {
              Logger.info("Received " + clientRequest);
              response = "OK";
              this.sendToClient(response);
            } else {
              response = null;
            }
          } while (response != null);
          Logger.info("Client " + this.socket.getRemoteSocketAddress() + " leaving");
          this.server.clientDisconnected(this);
        }
      }
    } while (receivedMessage != null);
    // Message response;
    // do {
    //   Command clientCommand = readClientRequest();
    //   if (clientCommand != null) {
    //     System.out.println("Received a " + clientCommand.getClass().getSimpleName());
    //     response = clientCommand.execute(server.getLogic());
    //     if (response != null) {
    //       if (isBroadcastMessage(response)) {
    //         this.server.sendResponseToAllClients(response);
    //       } else {
    //         sendToClient(response);
    //       }
    //     }
    //   } else {
    //     response = null;
    //   }
    // } while (response != null);
    // System.out.println("Client " + this.socket.getRemoteSocketAddress() + " leaving");
    // this.server.clientDisconnected(this);
  }

  /**
   * Initializes the client by converting all nodes to strings and sending them to the client.
   */
  private void initialize() {
    List<String> nodeStrings = NodeSerializer.toString(this.server.getNodes());
    for (String nodeString : nodeStrings) {
      this.sendToClient(nodeString);
    }
    this.sendToClient("0");
  }

  private boolean isSensorReading(String message) {
    return message.contains(":");
  }

  private boolean isBroadcastMessage(Message response) {
    return response instanceof StateMessage
        || response instanceof CurrentChannelMessage;
  }

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
    // Message clientCommand = null;
    // try {
    //   String rawClientRequest = this.socketReader.readLine();
    //   clientCommand = MessageSerializer.fromString(rawClientRequest);
    //   if (!(clientCommand instanceof Command)) {
    //     if (clientCommand != null) {
    //       System.err.println("Wrong message form the client: " + clientCommand);
    //     }
    //     clientCommand = null;
    //   }
    // } catch (IOException e) {
    //   System.err.println("Could not recieve client request: " + e.getMessage());
    // }
    // return (Command) clientCommand;
  }

  /**
   * Handles a message received from the server.
   *
   * @param message the message to handle.
   */
  public void handleReceivedMessage(String message) {
    //TODO: implement logic to parse sensor reading. perhaps move to a different class
    String[] parts = message.split(":");
    if (parts.length == 4) {
      int sensorId = Integer.parseInt(parts[1]);
      String type = parts[1];
      double value = Double.parseDouble(parts[2]);
      String unit = parts[3];

      System.out.println("Received sensor reading: " + "Sensor ID: " + sensorId
          + "Type: " + type + "Value: " + value + "Unit: " + unit);
    } else {
      // Handle incorrect message format or unexpected data
      System.out.println("Received invalid sensor reading message: " + message);
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
}