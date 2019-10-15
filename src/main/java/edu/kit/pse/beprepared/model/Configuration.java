package edu.kit.pse.beprepared.model;

import edu.kit.pse.beprepared.json.ConfigurationJson;

import java.util.HashMap;

/**
 * This class represents a configuration
 */
public class Configuration {

    /**
     * Next, highest ID.
     */
    private static int nextID = 0;

    /**
     * The ID of this configuration.
     */
    private final int id;
    /**
     * A {@link HashMap} that stores additional params.
     */
    private final HashMap<String, Object> additionalProperties;
    /**
     * The configured start time of the {@link Scenario} in unix time.
     * Used to calculate the absolute time for {@link Event}s.
     */
    private long scenarioStartTime;

    /**
     * Create a new configuration.
     *
     * @param scenarioStartTime    the start time of the scenario
     * @param additionalProperties additional data that should be stored
     */
    public Configuration(final long scenarioStartTime, final HashMap<String, Object> additionalProperties) {

        this.id = nextID++;
        this.scenarioStartTime = scenarioStartTime;
        this.additionalProperties = additionalProperties == null ? new HashMap<>() : additionalProperties;
    }

    /**
     * Constructor.
     * <p>
     * Constructs a new configuration from the supplied JSON representation.
     *
     * @param configurationJson the json representation
     */
    public Configuration(final ConfigurationJson configurationJson) {

        this(configurationJson.getScenarioStartTime(), configurationJson.getAdditionalProperties());

    }

    /**
     * Get the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the start time of the {@link Scenario} in unix time.
     *
     * @return the start time of the {@link Scenario}
     */
    public long getScenarioStartTime() {
        return scenarioStartTime;
    }

    /**
     * Set the start time of the {@link Scenario} in unix time.
     *
     * @param scenarioStartTime the new start time of the {@link Scenario}
     */
    public void setScenarioStartTime(final long scenarioStartTime) {
        this.scenarioStartTime = scenarioStartTime;
    }

    /**
     * Getter for a specific property held by {@link this#additionalProperties}.
     *
     * @param key the key of the requested property
     * @return the property value or {@code null}, if this property does not exist
     */
    public Object getPropertyValue(String key) {
        return this.additionalProperties.get(key);
    }

    /**
     * Setter for a specific property held by {@link this#additionalProperties}.
     *
     * @param key   the key
     * @param value the value
     */
    public void setPropertyValue(String key, Object value) {
        this.additionalProperties.put(key, value);
    }

    /**
     * Getter for {@link this#additionalProperties}.
     *
     * @return the value of {@link this#additionalProperties}
     */
    public HashMap<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    /**
     * Getter for a {@link String}-representation of this object.
     *
     * @return a {@link String}-representation of this object
     */
    @Override
    public String toString() {
        return "Configuration{" +
                "id=" + id +
                ", additionalProperties=" + additionalProperties +
                ", scenarioStartTime=" + scenarioStartTime +
                '}';
    }
}
