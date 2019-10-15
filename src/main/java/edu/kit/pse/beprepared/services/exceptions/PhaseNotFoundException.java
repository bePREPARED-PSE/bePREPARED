package edu.kit.pse.beprepared.services.exceptions;

/**
 * This exception is thrown by a service in case a selected phase can not be found.
 */
public class PhaseNotFoundException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public PhaseNotFoundException(String message) {
        super(message);
    }
}
