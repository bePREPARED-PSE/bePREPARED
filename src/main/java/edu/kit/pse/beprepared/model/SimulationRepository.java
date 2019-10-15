package edu.kit.pse.beprepared.model;

import edu.kit.pse.beprepared.simulation.Simulation;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to access all {@link Simulation}s.
 */
@Repository
public class SimulationRepository {

    /**
     * Container for all {@link Simulation}s.
     */
    private final LinkedList<Simulation> simulations;


    /**
     * Constructor.
     * <p>
     * Initializes {@link this#simulations} with an empty {@link LinkedList<Simulation>}.
     */
    public SimulationRepository() {
        this.simulations = new LinkedList<>();
    }

    /**
     * Create a new {@link Simulation} and add it to the repository.
     *
     * @param scenario       the scenario that is simulated
     * @param selectedPhases the phases that should be simulated
     * @param configuration  the {@link Configuration} of the simulation
     * @param speed          the initial speed of the simulation
     * @return the newly created simulation
     */
    public Simulation createSimulation(final Scenario scenario, final Collection<Phase> selectedPhases,
                                       final Configuration configuration, final double speed) {
        Simulation s = new Simulation(scenario, selectedPhases, configuration, speed);
        simulations.add(s);
        return s;
    }

    /**
     * Get a {@link Simulation} by its ID.
     *
     * @param id the id to look for
     * @return the simulation with given id or {@code null} if no simulation with the given id exists
     */
    public Simulation getSimulationById(final int id) {
        for (Simulation s : this.simulations) {
            if (s.getId() == id) {
                return s;
            }
        }

        return null;
    }

    /**
     * Get all {@link Simulation}s in this repository
     *
     * @return all simulations in this repository
     */
    public List<Simulation> getAllSimulations() {
        return new LinkedList<>(simulations);
    }

    /**
     * Removes the {@link Simulation} with the supplied id from this repository.
     *
     * @param id the id of the simulation that should be removed
     * @return the removed simulation or {@code null}, if no simulation with the supplied id could be found
     */
    public Simulation removeSimulation(final int id) {
        Simulation s = this.getSimulationById(id);
        simulations.remove(s);
        return s;
    }

}
