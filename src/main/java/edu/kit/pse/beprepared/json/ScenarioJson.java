package edu.kit.pse.beprepared.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kit.pse.beprepared.model.Scenario;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a JSON object of type ScenarioJson
 */
public class ScenarioJson {

    /*
    Properties of the JSON object:
     */
    private int scenarioID;
    private List<PhaseJson> phases;
    private int standardPhase;
    private String name;

    /**
     * Instantiates a new {@link ScenarioJson}.
     *
     * @param scenarioID the scenario id
     * @param phases     the phases
     * @param name       the typeName
     */
    @JsonCreator
    public ScenarioJson(@JsonProperty(value = "scenarioID") final int scenarioID,
                        @JsonProperty(value = "phases") final List<PhaseJson> phases,
                        @JsonProperty(value = "standardPhase") final int standardPhase,
                        @JsonProperty(value = "name") final String name) {
        this.scenarioID = scenarioID;
        this.phases = phases;
        this.standardPhase = standardPhase;
        this.name = name;
    }

    /**
     * Constructor.
     * <p>
     * Constructs this PhaseJson from the corresponding {@link Scenario}.
     *
     * @param scenario the corresponding {@link Scenario}
     */
    public ScenarioJson(final Scenario scenario) {
        this.scenarioID = scenario.getId();
        this.phases = scenario.getAllPhases().stream().map(PhaseJson::new).collect(Collectors.toList());
        this.name = scenario.getName();
        this.standardPhase = scenario.getStandardPhase().getId();
    }

    /**
     * Getter for {@link this#standardPhase}.
     *
     * @return {@link this#standardPhase}
     */
    public int getStandardPhase() {
        return standardPhase;
    }

    /**
     * Setter for {@link this#standardPhase}.
     *
     * @param standardPhase the new value for {@link this#standardPhase}
     */
    public void setStandardPhase(int standardPhase) {
        this.standardPhase = standardPhase;
    }

    /**
     * Getter for {@link this#scenarioID}.
     *
     * @return the scenario id
     */
    public int getScenarioID() {
        return scenarioID;
    }

    /**
     * Setter for {@link this#scenarioID}.
     *
     * @param scenarioID the scenario id
     */
    public void setScenarioID(final int scenarioID) {
        this.scenarioID = scenarioID;
    }

    /**
     * Getter for {@link this#phases}.
     *
     * @return the phases
     */
    public List<PhaseJson> getPhases() {
        return phases;
    }

    /**
     * Setter for {@link this#phases}.
     *
     * @param phases the phases
     */
    public void setPhases(final List<PhaseJson> phases) {
        this.phases = phases;
    }

    /**
     * Getter for {@link this#name}.
     *
     * @return the typeName
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for {@link this#name}.
     *
     * @param name the typeName
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for a {@link String} representation of this scenarioJson.
     *
     * @return a string representing this scenarioJson
     */
    @Override
    public String toString() {
        return "ScenarioJson{" +
                "scenarioID=" + scenarioID +
                ", phases=" + phases +
                ", name='" + name + '\'' +
                '}';
    }
}
