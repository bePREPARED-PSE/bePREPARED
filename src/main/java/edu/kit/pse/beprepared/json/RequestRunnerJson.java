package edu.kit.pse.beprepared.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kit.pse.beprepared.simulation.RequestRunner;

/**
 * JSON representation of a {@link edu.kit.pse.beprepared.simulation.RequestRunner}
 */
public class RequestRunnerJson {

    private EventJson event;
    private ConfigurationJson configuration;

    /**
     * Constructor.
     *
     * @param event         the JSON representation of the event that belongs to this {@link RequestRunner}
     * @param configuration the JSON representation of the configuration that belongs to this {@link RequestRunner}
     */
    @JsonCreator
    public RequestRunnerJson(@JsonProperty(value = "event") EventJson event,
                             @JsonProperty(value = "configuration") ConfigurationJson configuration) {
        this.event = event;
        this.configuration = configuration;
    }

    /**
     * Constructor.
     * <p>
     * Constructs a new RequestRunnerJson from the supplied {@link RequestRunner}.
     *
     * @param runner the {@link RequestRunner} this JSON represents
     */
    public RequestRunnerJson(RequestRunner runner) {
        this.event = new EventJson(runner.getEvent());
        this.configuration = new ConfigurationJson(runner.getConfiguration());
    }

    /**
     * Getter for {@link this#event}.
     *
     * @return the value of {@link this#event}
     */
    public EventJson getEvent() {
        return event;
    }

    /**
     * Setter for {@link this#event}.
     *
     * @param event the new value for {@link this#event}
     */
    public void setEvent(EventJson event) {
        this.event = event;
    }

    /**
     * Getter for {@link this#configuration}.
     *
     * @return the value of {@link this#configuration}
     */
    public ConfigurationJson getConfiguration() {
        return configuration;
    }

    /**
     * Setter for {@link this#configuration}.
     *
     * @param configuration the new value for {@link this#configuration}
     */
    public void setConfiguration(ConfigurationJson configuration) {
        this.configuration = configuration;
    }
}
