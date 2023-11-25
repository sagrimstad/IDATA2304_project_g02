package no.ntnu.idata2304.project.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import no.ntnu.idata2304.project.greenhouse.GreenhouseServer;

public class NodeClientHandler extends Thread {
  private final GreenhouseServer server;
  private Socket clientSocket;
  private final PrintWriter socketWriter;
  private final BufferedReader socketReader;

  public NodeClientHandler(GreenhouseServer server, Socket clientSocket) throws IOException {
    this.server = server;
    this.clientSocket = clientSocket;
    this.socketWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
    this.socketReader = new BufferedReader(
        new InputStreamReader(this.clientSocket.getInputStream()));
  }

  @Override
  public void run() {
    try {
      String inputLine;
      while ((inputLine = socketReader.readLine()) != null) {
        socketWriter.println("Received: " + inputLine);
      }

      socketReader.close();
      socketWriter.close();
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Error");
    }
  }
}
