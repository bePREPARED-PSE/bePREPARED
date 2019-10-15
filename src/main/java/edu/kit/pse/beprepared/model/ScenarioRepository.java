package edu.kit.pse.beprepared.model;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedList;

/**
 * This is used to access all {@link Scenario scenarios}.
 */
@Repository
public class ScenarioRepository {

    /**
     * A list containing all {@link Scenario scenarios}.
     */
    private LinkedList<Scenario> scenarios;

    /**
     * Constructor
     * <p>
     * Creates a new {@link ScenarioRepository}.
     */
    public ScenarioRepository() {
        this.scenarios = new LinkedList<>();
    }

    /**
     * Adds a new {@link Scenario} to this repository.
     *
     * @param scenario the scenario to add
     */
    public void addScenario(final Scenario scenario) {
        scenarios.add(scenario);
    }

    /**
     * Removes a {@link Scenario}  with the given ID from the repository.
     *
     * @param scenarioID the ID of the scenario to remove
     * @return the removed {@link Scenario} if it exists, returns {@code null} otherwise
     */
    public Scenario removeScenario(final int scenarioID) {
        Scenario temp = null;
        for (Scenario s : scenarios) {
            if (s.getId() == scenarioID) {
                temp = s;
                scenarios.remove(s);
                break;
            }
        }
        return temp;
    }

    /**
     * Returns the scenario with the given ID.
     *
     * @param scenarioID the ID of the scenario
     * @return the {@link Scenario} with the given ID if it exists, return {@code null} otherwise
     */
    public Scenario getScenario(final int scenarioID) {
        for (Scenario s : scenarios) {
            if (s.getId() == scenarioID) {
                return s;
            }
        }
        return null;
    }

    /**
     * Getter for all the scenarios.
     *
     * @return a collection of all the scenarios
     */
    public Collection<Scenario> getAllScenarios() {

        return new LinkedList<>(this.scenarios);

    }

}
