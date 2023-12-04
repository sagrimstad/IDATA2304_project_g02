package no.ntnu.idata2304.project.tools;

import no.ntnu.idata2304.project.greenhouse.GreenhouseServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClientHandlerTest {

  //TODO: NOT FINISHED!
  @Test
  public void testHandleValidSensorReading() {
    GreenhouseServer server = new GreenhouseServer();
    try {
      ClientHandler clientHandler = new ClientHandler(server, null);
      String validSensorReading = "1:temperature:25.5:Celsius";
      clientHandler.handleReceivedMessage(validSensorReading);
    } catch (IOException e) {
      fail("Exception occurred: " + e.getMessage());
    }
  }

}