package no.ntnu.idata2304.project;

import no.ntnu.idata2304.project.message.Message;

/**
 * Serializes messages to protocol-defined strings and vice versa.
 */
public class MessageSerializer {

  public static final String CHANNEL_COUNT_COMMAND = "c";
  public static final String TURN_ON_COMMAND = "1";
  public static final String TURN_OFF_COMMAND = "0";
  public static final String GET_CHANNEL_COMMAND = "g";
  public static final String SET_CHANNEL_COMMAND = "s";
  public static final String CHANNEL_COUNT_MESSAGE = "N";
  public static final String ERROR_MESSAGE = "e";
  public static final String CURRENT_CHANNEL_MESSAGE = "C";
  public static final String TV_STATE_ON_MESSAGE = "TVON";
  public static final String TV_STATE_OFF_MESSAGE = "TVoff";

  /**
   * Not allowed to instantiate this utility class.
   */
  private MessageSerializer() {
    // Intentionally left blank.
  }

  /**
   * Create message from a string, according to the communication protocol.
   *
   * @param s The string sent over the communication channel
   * @return The logical message, as interpreted according to the protocol
   */
  public static Message fromString(String s) {
    Message m = null;
    if (s != null) {
      switch (s) {
        default -> {
          if (s.length() > 1) {
            m = parseParametrizedMessage(s);
          }
        }
      }
    }
    return m;
  }

  private static Message parseParametrizedMessage(String s) {
    return null;
  }

  private static Integer parseInteger(String s) {
    Integer i = null;
    try {
      i = Integer.valueOf(s);
    } catch (NumberFormatException e) {
      System.err.println("Could not parse integer <" + s + ">");
    }
    return i;
  }

  /**
   * Convert a message to a serialized string.
   *
   * @param m The message to translate
   * @return String representation of the message
   */
  public static String toString(Message m) {
    return null;
  }
}