package edu.kit.pse.beprepared.services.exceptions;

/**
 * Exception thrown by a service if a {@link edu.kit.pse.beprepared.simulation.Simulation} can not be found.
 */
public class SimulationNotFoundException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public SimulationNotFoundException(String message) {
        super(message);
    }
}
