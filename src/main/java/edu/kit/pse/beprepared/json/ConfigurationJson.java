package edu.kit.pse.beprepared.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kit.pse.beprepared.model.Configuration;

import java.util.HashMap;

/**
 * This class represents a JSON object of type Configuration.
 */
public class ConfigurationJson {

    /*
    Properties of the JSON object:
     */
    private int configurationID;
    private long scenarioStartTime;
    private HashMap<String, Object> additionalProperties;


    /**
     * Constructor.
     *
     * @param configuration the corresponding {@link Configuration}
     */
    public ConfigurationJson(final Configuration configuration) {

        this.configurationID = configuration.getId();
        this.scenarioStartTime = configuration.getScenarioStartTime();
        this.additionalProperties = configuration.getAdditionalProperties();

    }

    /**
     * Instantiates a new {@link ConfigurationJson}.
     *
     * @param configurationID   the configuration id
     * @param scenarioStartTime the scenario start time
     */
    @JsonCreator
    public ConfigurationJson(@JsonProperty(value = "configurationID") final int configurationID,
                             @JsonProperty(value = "scenarioStartTime") final long scenarioStartTime,
                             @JsonProperty(value = "additionalProperties") final HashMap<String, Object> additionalProperties) {

        this.configurationID = configurationID;
        this.scenarioStartTime = scenarioStartTime;
        this.additionalProperties = additionalProperties;
    }

    /**
     * Getter for {@link this#configurationID}.
     *
     * @return the configuration id
     */
    public int getConfigurationID() {
        return configurationID;
    }

    /**
     * Setter for {@link this#configurationID}.
     *
     * @param configurationID the configuration id
     */
    public void setConfigurationID(int configurationID) {
        this.configurationID = configurationID;
    }

    /**
     * Getter for {@link this#scenarioStartTime}.
     *
     * @return the scenario start time
     */
    public long getScenarioStartTime() {
        return scenarioStartTime;
    }

    /**
     * Setter for {@link this#scenarioStartTime}.
     *
     * @param scenarioStartTime the scenario start time
     */
    public void setScenarioStartTime(long scenarioStartTime) {
        this.scenarioStartTime = scenarioStartTime;
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
     * Setter for {@link this#additionalProperties}.
     *
     * @param additionalProperties the new value for {@link this#additionalProperties}
     */
    public void setAdditionalProperties(HashMap<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        return "ConfigurationJson{" +
                "configurationID=" + configurationID +
                ", scenarioStartTime=" + scenarioStartTime +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
