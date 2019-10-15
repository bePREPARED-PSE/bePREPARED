package edu.kit.pse.beprepared.services.exceptions;

import edu.kit.pse.beprepared.model.Event;

/**
 * Exception thrown by service if an {@link Event} can not be found.
 */
public class EventNotFoundException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public EventNotFoundException(String message) {
        super(message);
    }
}
