package edu.kit.pse.beprepared.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kit.pse.beprepared.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a JSON object of type Phase.
 */
public class PhaseJson {

    /*
    Properties of the JSON object:
     */
    private int phaseID;
    private List<EventJson> events;
    private String name;

    /**
     * Instantiates a new {@link PhaseJson}.
     *
     * @param phaseID the phase id
     * @param events  the events
     * @param name    the typeName
     */
    @JsonCreator
    public PhaseJson(@JsonProperty(value = "phaseID") final int phaseID,
                     @JsonProperty(value = "events") final List<EventJson> events,
                     @JsonProperty(value = "name") final String name) {
        this.phaseID = phaseID;
        this.events = events;
        this.name = name;
    }

    /**
     * Constructor.
     * <p>
     * Constructs this PhaseJson from the corresponding {@link Phase}.
     *
     * @param phase the corresponding {@link Phase}
     */
    public PhaseJson(final Phase phase) {
        this.phaseID = phase.getId();
        this.events = phase.getAllEvents().stream().map(EventJson::new).collect(Collectors.toList());
        this.name = phase.getName();
    }

    /**
     * Getter for {@link this#phaseID}.
     *
     * @return the phase id
     */
    public int getPhaseID() {
        return phaseID;
    }

    /**
     * Setter for {@link this#phaseID}.
     *
     * @param phaseID the phase id
     */
    public void setPhaseID(final int phaseID) {
        this.phaseID = phaseID;
    }

    /**
     * Getter for {@link this#events}.
     *
     * @return the events
     */
    public List<EventJson> getEvents() {
        return events;
    }

    /**
     * Setter for {@link this#events}.
     *
     * @param events the events
     */
    public void setEvents(final List<EventJson> events) {
        this.events = events;
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
     * Getter for a {@link String} representation of this object.
     *
     * @return a {@link String} representation of this object
     */
    @Override
    public String toString() {
        return "PhaseJson{" +
                "phaseID=" + phaseID +
                ", events=" + events +
                ", typeName='" + name + '\'' +
                '}';
    }
}
