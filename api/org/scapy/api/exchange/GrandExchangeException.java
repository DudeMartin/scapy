package org.scapy.api.exchange;

/**
 * Signals that a problem has occurred while querying the Grand Exchange catalog.
 *
 * @author Martin Tuskevicius
 */
public class GrandExchangeException extends RuntimeException {

    GrandExchangeException(String s) {
        super(s);
    }

    GrandExchangeException(String message, Throwable cause) {
        super(message, cause);
    }
}