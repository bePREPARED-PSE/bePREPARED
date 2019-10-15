package edu.kit.pse.beprepared.services.exceptions;

/**
 * Exception thrown by the services if an {@link edu.kit.pse.beprepared.model.EventType}'s typeName can not be found.
 */
public class IllegalEventTypeException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IllegalEventTypeException(String message) {
        super(message);
    }
}
