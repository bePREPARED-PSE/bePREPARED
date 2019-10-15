package edu.kit.pse.beprepared.services.exceptions;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link Exception} thrown by the {@link edu.kit.pse.beprepared.services.EventManagementService} in case that one of
 * the supplied mathematical terms during event-series generation is invalid
 */
public class IllegalMathExpException extends Exception {

    /**
     * A list containing all the errors that have been detected in the expression.
     */
    private final List<String> errors;

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     * @param errors  a list containing all the errors that have been detected in the expression
     */
    public IllegalMathExpException(String message, List<String> errors) {
        super(message);
        this.errors = errors != null ? errors : new LinkedList<>();
    }
}
