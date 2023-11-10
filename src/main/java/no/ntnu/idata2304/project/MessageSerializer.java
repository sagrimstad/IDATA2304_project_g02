package no.ntnu.idata2304.project;

import no.ntnu.idata2304.project.message.Message;

/**
 * Serializers message to protocol-defined strings and vice versa.
 */
public class MessageSerializer {
  //TODO: add messages when protocol is defined.

  private MessageSerializer() {
    // Intentionally left blank.
  }

  public static Message fromString(String s) {
    Message message = null;
    if (s != null) {
      switch (s) {
        //TODO: finish switch case when protocol is defined.
        default -> {
          if (s.length() > 1) {
            message = parseParametrizedMessage(s);
          }
        }
      }
    }
  }

  private static Message parseParametrizedMessage(String s) {
    Message m = null;
    //TODO: finish method when protocol is defined.
    return m;
  }

  /**
   * Returns an Integer from a wanted String.
   *
   * @param s the string wanted to be converted.
   * @return the desired Integer.
   */
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
   * @param message the message to translate.
   * @return String representation of the message.
   */
  public static String toString(Message message) {
    String s = null;
    //TODO: finish method when protocol is defined.
    return s;
  }
}
