package no.ntnu.idata2304.project.greenhouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.idata2304.project.tools.ClientHandler;
import no.ntnu.idata2304.project.tools.Logger;
import no.ntnu.idata2304.project.tools.NodeClientHandler;

/**
 * Handles the TCP server socket/s.
 *
 * @author  Group 2
 * @version v1.0 (2023.11.25)
 */
public class GreenhouseServer {

  public static final int PORT_NUMBER = 1337;
  boolean isServerRunning;
  private final List<ClientHandler> connectedClients = new ArrayList<>();
  private final List<NodeClientHandler> connectedNodeClients = new ArrayList<>();
  private ServerSocket listeningSocket;

  /**
   * Constructs an instance of the GreenhouseServer class.
   */
  public GreenhouseServer() {
    // Intentionally left blank
  }

  /**
   * Starts the server and accepts the next client.
   */
  public void startServer() {
    this.listeningSocket = openListeningSocket();
    if (this.listeningSocket != null) {
      Logger.info("Server listening on port " + PORT_NUMBER);
      this.isServerRunning = true;
      while (this.isServerRunning) {
        ClientHandler clientHandler = acceptNextClientConnection(this.listeningSocket);
        NodeClientHandler nodeClientHandler = acceptNextNodeClientConnection(this.listeningSocket);
        if (clientHandler != null || nodeClientHandler != null) {
          this.connectedClients.add(clientHandler);
          clientHandler.start();
          this.connectedNodeClients.add(nodeClientHandler);
          nodeClientHandler.start();
        }
      }
    }
  }

  /**
   * Returns a listening socket after it has been opened or null on error.
   *
   * @return A listening socket after it has been opened or null on error
   */
  private ServerSocket openListeningSocket() {
    ServerSocket listeningSocket = null;
    try {
      listeningSocket = new ServerSocket(PORT_NUMBER);
    } catch (IOException e) {
      Logger.error("Could not open server socket (" + e.getMessage().toLowerCase() + ")");
    }
    return listeningSocket;
  }

  /**
   * Returns a client handler for a client after the connection to the client has been accepted.
   *
   * @param listeningSocket A specified listening socket
   * @return A client handler for a client after the connection to the client has been accepted
   */
  private ClientHandler acceptNextClientConnection(ServerSocket listeningSocket) {
    ClientHandler clientHandler = null;
    try {
      Socket clientSocket = listeningSocket.accept();
      Logger.info("New client connected from " + clientSocket.getRemoteSocketAddress());
      clientHandler = new ClientHandler(this, clientSocket);
    } catch (IOException e) {
      Logger.error("Could not accept client connection (" + e.getMessage().toLowerCase() + ")");
    }
    return clientHandler;
  }

  /**
   * Returns a client handler for a client after the connection to the client has been accepted.
   *
   * @param listeningSocket A specified listening socket
   * @return A client handler for a client after the connection to the client has been accepted
   */
  private NodeClientHandler acceptNextNodeClientConnection(ServerSocket listeningSocket) {
    NodeClientHandler nodeClientHandler = null;
    try {
      Socket clientSocket = listeningSocket.accept();
      Logger.info("New client connected from " + clientSocket.getRemoteSocketAddress());
      nodeClientHandler = new NodeClientHandler(this, clientSocket);
    } catch (IOException e) {
      Logger.error("Could not accept client connection (" + e.getMessage().toLowerCase() + ")");
    }
    return nodeClientHandler;
  }

  /**
   * Sends a specified message to all currently connected clients.
   *
   * @param message A specified message
   */
  public void sendResponseToAllClients(String message) {
    for (ClientHandler clientHandler : this.connectedClients) {
      clientHandler.sendToClient(message);
    }
  }

  /**
   * Removes a specifed client handler for a client after the client has been disconnected.
   *
   * @param clientHandler A specified client handler
   */
  public void clientDisconnected(ClientHandler clientHandler) {
    this.connectedClients.remove(clientHandler);
  }

  /**
   * Stos the server and disconnects every connected client and closes the server socket.
   */
  public void stopServer() {
    this.isServerRunning = false;
    try {
      for (ClientHandler clientHandler : this.connectedClients) {
        clientHandler.close();
      }
      this.connectedClients.clear();
      if (this.listeningSocket != null && !this.listeningSocket.isClosed()) {
        this.listeningSocket.close();
      }
    } catch (IOException e) {
      System.err.println("Error while stopping the server: " + e.getMessage());
    }
  }
}
