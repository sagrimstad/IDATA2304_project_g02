package no.ntnu.idata2304.project.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import no.ntnu.idata2304.project.greenhouse.GreenhouseServer;
import no.ntnu.idata2304.project.message.Command;
import no.ntnu.idata2304.project.message.CurrentChannelMessage;
import no.ntnu.idata2304.project.message.Message;
import no.ntnu.idata2304.project.message.StateMessage;

/**
 * Handler for one specific client connection (TCP).
 *
 * @author Group 2
 * @version v1.0 (2023.11.10)
 */
public class ClientHandler extends Thread {

  private final Socket socket;
  private final BufferedReader socketReader;
  private final PrintWriter socketWriter;
  private final GreenhouseServer server;

  /**
   * Create a new client handler.
   *
   * @param socket Socket associated with this client.
   * @param server References to the main Server class
   * @throws IOException When something goes wrong with establishing the input or output streams.
   */
  public ClientHandler(Socket socket, GreenhouseServer server) throws IOException {
    this.server = server;
    this.socket = socket;
    this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
  }

  /**
   * Run the client handling logic.
   */
  @Override
  public void run() {
    Message response;
    do {
      Command clientCommand = readClientRequest();
      if (clientCommand != null) {
        System.out.println("Received a " + clientCommand.getClass().getSimpleName());
        response = clientCommand.execute(server.getLogic());
        if (response != null) {
          if (isBroadcastMessage(response)) {
            this.server.sendResponseToAllClients(response);
          } else {
            sendToClient(response);
          }
        }
      } else {
        response = null;
      }
    } while (response != null);
    System.out.println("Client " + this.socket.getRemoteSocketAddress() + " leaving");
    this.server.clientDisconnected(this);
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
  private Command readClientRequest() {
    Message clientCommand = null;
    try {
      String rawClientRequest = this.socketReader.readLine();
      clientCommand = MessageSerializer.fromString(rawClientRequest);
      if (!(clientCommand instanceof Command)) {
        if (clientCommand != null) {
          System.err.println("Wrong message form the client: " + clientCommand);
        }
        clientCommand = null;
      }
    } catch (IOException e) {
      System.err.println("Could not recieve client request: " + e.getMessage());
    }
    return (Command) clientCommand;
  }

  /**
   * Send a response from the server to the client, over the socket.
   *
   * @param message the message to send to the client.
   */
  public void sendToClient(Message message) {
    this.socketWriter.println(MessageSerializer.toString(message));
  }
}