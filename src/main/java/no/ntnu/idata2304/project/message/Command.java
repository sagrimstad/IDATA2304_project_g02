package no.ntnu.idata2304.project.message;

import no.ntnu.idata2304.project.controlpanel.ControlPanelLogic;
import no.ntnu.idata2304.project.message.Message;

/**
 * A command sent from the client to the server.
 */
public abstract class Command implements Message {

  /**
   * Executes the command.
   */
  public abstract Message execute(ControlPanelLogic logic);
}
