package edu.kit.pse.beprepared.simulation;

/**
 * This enum contains the different states a {@link Simulation} can be in.
 */
public enum SimulationState {
    /**
     * The {@link Simulation} has been initialized, but has not been started.
     */
    INITIALIZED,
    /**
     * The {@link Simulation} is currently running.
     */
    RUNNING,
    /**
     * The {@link Simulation} is currently paused.
     */
    PAUSED,
    /**
     * The {@link Simulation} is finished. All its {@link RequestRunner}s have been executed.
     */
    FINISHED,
    /**
     * The {@link Simulation} has been terminated externally.
     */
    TERMINATED
}
