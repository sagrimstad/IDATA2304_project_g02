package no.ntnu.idata2304.project.greenhouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.idata2304.project.ClientHandler;
import no.ntnu.idata2304.project.message.Message;

/**
 * Handles the TCP server socket(s).
 *
 * @author Group 2
 * @version v1.0 (2023.11.10)
 */
public class GreenhouseServer {

  public static final int PORT_NUMBER = 1337;
  boolean isServerRunning;
  private final List<ClientHandler> connectedClients = new ArrayList<>();

  /**
   * Constructs an instance of the GreenhouseServer class.
   */
  public GreenhouseServer() {
    // Intentionally left blank
  }

  /**
   * Starts the server and accept the next client.
   */
  public void startServer() {
    ServerSocket listeningSocket = openListeningSocket();
    if (listeningSocket != null) {
      System.out.println("Server listening of port " + PORT_NUMBER);
      isServerRunning = true;
      while (isServerRunning) {
        ClientHandler clientHandler = acceptNextClientConnection(listeningSocket);
        if (clientHandler != null) {
          connectedClients.add(clientHandler);
          clientHandler.start();
        }
      }
    }
  }

  /**
   * Returns a listening socket after it has been opened or null on error.
   *
   * @return A listening socket after it has been opened or null on error.
   */
  private ServerSocket openListeningSocket() {
    ServerSocket listeningSocket = null;
    try {
      listeningSocket = new ServerSocket(PORT_NUMBER);
    } catch (IOException e) {
      System.err.println("Could not open server socket: " + e.getMessage());
    }
    return listeningSocket;
  }

  /**
   * Returns the client handler for a client after the connection to the client has been accepted.
   *
   * @param listeningSocket A specified listening socket.
   * @return The client handler for a client after the connection to the client has been accepted.
   */
  private ClientHandler acceptNextClientConnection(ServerSocket listeningSocket) {
    ClientHandler clientHandler = null;
    try {
      Socket clientSocket = listeningSocket.accept();
      System.out.println("New client connected from " + clientSocket.getRemoteSocketAddress());
      clientHandler = new ClientHandler(clientSocket, this);
    } catch (IOException e) {
      System.err.println("Could not accept client connection: " + e.getMessage());
    }
    return clientHandler;
  }

  /**
   * Send a message to all currently connected clients.
   *
   * @param message the message to send.
   */
  public void sendResponseToAllClients(Message message) {
    for (ClientHandler clientHandler : this.connectedClients) {
      clientHandler.sendToClient(message);
    }
  }

  /**
   * Removes the client handler for a client after the client has been disconnected.
   *
   * @param clientHandler the desired client to disconnect.
   */
  public void clientDisconnected(ClientHandler clientHandler) {
    this.connectedClients.remove(clientHandler);
  }
}
