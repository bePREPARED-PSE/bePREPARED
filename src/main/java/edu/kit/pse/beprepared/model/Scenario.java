package edu.kit.pse.beprepared.model;

import edu.kit.pse.beprepared.json.PhaseJson;
import edu.kit.pse.beprepared.json.ScenarioJson;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * This class represents the Scenario, which contains {@link Phase phases}.
 */
public class Scenario {

    /**
     * Next ID.
     */
    private static int nextID = 0;


    /**
     * The logger for this class.
     */
    private final Logger log = Logger.getLogger(Scenario.class);

    /**
     * ID of the event.
     */
    private final int id;
    /**
     * The typeName of the Scenario.
     */
    private String name;
    /**
     * A list containing all {@link Phase phases}
     */
    private LinkedList<Phase> phases;
    /**
     * The ID of the standard phase.
     */
    private Phase standardPhase;

    /**
     * Constructor
     * <p>
     * Creates a new {@link Scenario}. Automatically assigns an id.
     *
     * @param standardPhase the standard phase
     */
    public Scenario(final Phase standardPhase) {
        this.standardPhase = standardPhase;
        this.id = Scenario.nextID++;
        phases = new LinkedList<>();
    }

    /**
     * Constructor
     * <p>
     * Creates a new {@link Scenario} with the given typeName. Automatically assigns an id.
     *
     * @param name          the typeName of the scenario
     * @param standardPhase the standard phase
     */
    public Scenario(final String name, final Phase standardPhase) {
        this(standardPhase);
        this.name = name;
    }

    /**
     * Constructor.
     * <p>
     * Constructs a new scenario from the supplied {@link ScenarioJson}. This constructor is especially useful for
     * importing scenarios from their JSON representation.
     *
     * @param scenarioJson the JSON representation of this scenario
     */
    public Scenario(final ScenarioJson scenarioJson) {

        this.id = Scenario.nextID++;
        this.name = scenarioJson.getName();

        this.phases = new LinkedList<>();
        for (PhaseJson phaseJson : scenarioJson.getPhases()) {
            if (phaseJson.getPhaseID() == scenarioJson.getStandardPhase()) {
                this.standardPhase = new Phase(phaseJson);
            } else {
                this.phases.add(new Phase(phaseJson));
            }
        }

    }

    /**
     * Gets the id of the scenario.
     *
     * @return id of the scenario
     */
    public int getId() {
        return id;
    }

    /**
     * Assigns the supplied {@link Phase} to this scenario.
     *
     * @param phase the phase to assign
     */
    public void assignPhase(final Phase phase) {
        if (!phases.contains(phase)) {
            phases.add(phase);
        }
    }

    /**
     * Unassigns the given {@link Phase} from this scenario.
     *
     * @param phase the phase to unassign
     */
    public void unassignPhase(final Phase phase) {
        phases.remove(phase);
    }

    /**
     * Discard the supplied {@link Phase} from this scenario.
     *
     * @param phaseToDiscard the phase that should be discarded
     */
    public void discardPhase(final Phase phaseToDiscard) {

        if (phaseToDiscard == null) {
            throw new NullPointerException("phase to discard must not be null!");
        }

        if (phaseToDiscard.getId() == this.standardPhase.getId()) {
            log.info("requested to discard standard phase");
            return;
        }

        if (!this.phases.contains(phaseToDiscard)) {
            log.warn("Tried to remove phase with id " + phaseToDiscard.getId() + " from scenario with id "
                    + this.id + ", but this phase does not exist in this scenario");
            return;
        }

        this.unassignPhase(phaseToDiscard);
        this.standardPhase.addEvents(phaseToDiscard.getAllEvents());

    }

    /**
     * Getter for the {@link Phase} with the supplied id.
     *
     * @param id the id of the requested phase
     * @return the phase with the supplied id or {@code null}, if no phase with the supplied id is assigned to this
     * scenario
     */
    public Phase getPhaseById(final int id) {

        Phase p = null;
        if (this.standardPhase.getId() == id) {
            return this.standardPhase;
        }
        for (Phase phase : this.phases) {
            if (phase.getId() == id) {
                p = phase;
                break;
            }
        }

        return p;
    }

    /**
     * Getter for all {@link Phase}s that are assigned to this scenario except {@link this#standardPhase}.
     *
     * @return a {@link Collection<Phase>} of all phases assigned to this scenario
     */
    public Collection<Phase> getAllPhases() {
        LinkedList<Phase> phases = new LinkedList<>(this.phases);
        phases.add(standardPhase);
        phases.sort(Comparator.comparingInt(Phase::getId));
        return phases;
    }

    /**
     * Returns the standard {@link Phase}.
     *
     * @return the standard phase
     */
    public Phase getStandardPhase() {
        return standardPhase;
    }

    /**
     * Sets the standard phase to the given {@link Phase}.
     *
     * @param phase the new standard phase
     */
    public void setStandardPhase(final Phase phase) {
        this.standardPhase = phase;
    }

    /**
     * Returns the typeName of this scenario.
     *
     * @return the typeName of the scenario
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the typeName of this scenario to the given one.
     *
     * @param name the new typeName of the scenario
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for a {@link String} representation of this scenario.
     *
     * @return a string representing this scenario
     */
    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", typeName='" + name + '\'' +
                ", phases=" + phases +
                ", standardPhase=" + standardPhase +
                '}';
    }
}
