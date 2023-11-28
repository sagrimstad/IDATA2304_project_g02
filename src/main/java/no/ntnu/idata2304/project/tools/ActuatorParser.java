package no.ntnu.idata2304.project.tools;

public class ActuatorParser {

  public static String parseControlPanelCommand(String command) {
    if (command == null || command.isEmpty()) {
      throw new IllegalArgumentException("Command cannot be empty");
    }
    String[] commandParts = command.split(";");


    return null;
  }
}
