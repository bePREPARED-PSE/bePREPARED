package edu.kit.pse.beprepared.simulation;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;

import java.util.concurrent.Callable;

/**
 * This is the interface for all request runners.
 */
public abstract class RequestRunner implements Callable<ExecutionReport> {

    /**
     * The event that should be simulated.
     */
    protected final Event event;
    /**
     * The configuration for the simulation.
     */
    protected final Configuration configuration;


    /**
     * Constructor.
     *
     * @param event         the event that should be simulated
     * @param configuration the configuration for the simulation
     */
    public RequestRunner(Event event, Configuration configuration) {
        this.event = event;
        this.configuration = configuration;
    }

    /**
     * Getter for {@link this#event}.
     *
     * @return the value of {@link this#event}
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Getter for {@link this#configuration}.
     *
     * @return the value of {@link this#configuration}
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Helper method that calculates the absolute point in time of an event.
     * <p>
     * The "absolute point in time" means the relative point in time of {@link this#event} plus the scenario start time
     * taken out of {@link this#configuration}.
     *
     * @return the absolute point of an event
     */
    public long getAbsoluteEventTime() {
        return this.event.getPointInTime() + this.getConfiguration().getScenarioStartTime();
    }

    /**
     * Getter for a {@link String} representation of this {@link RequestRunner}.
     *
     * @return a {@link String} representation of this {@link RequestRunner}
     */
    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "event=" + event +
                ", configuration=" + configuration +
                '}';
    }
}
