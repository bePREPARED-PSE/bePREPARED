package edu.kit.pse.beprepared.services.exceptions;

import java.security.PrivilegedActionException;

/**
 * Exception thrown by the {@link edu.kit.pse.beprepared.services.PhaseManagementService}, if somebody tries to
 * perform an illegal operation (e.g. deleting the standard phase of a scenario).
 */
public class IllegalPhaseOperationException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IllegalPhaseOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public IllegalPhaseOperationException(Throwable cause) {
        super(cause);
    }
}
