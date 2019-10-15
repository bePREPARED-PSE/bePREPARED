package edu.kit.pse.beprepared.services;

import edu.kit.pse.beprepared.model.Phase;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.model.ScenarioRepository;
import edu.kit.pse.beprepared.services.exceptions.IllegalPhaseOperationException;
import edu.kit.pse.beprepared.services.exceptions.PhaseNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class manages all {@link Phase}-related activities.
 */
@Service
public class PhaseManagementService {

    /**
     * The repository used to access all {@link Scenario}s.
     */
    private final ScenarioRepository scenarioRepository;


    /**
     * Constructor.
     * <p>
     * Constructs a new PhaseManagementService and initializes
     * {@link this#scenarioRepository} using the dependency injection provided by Spring Boot.
     *
     * @param scenarioRepository the repository used to access all scenarios
     */
    @Autowired
    public PhaseManagementService(final ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Creates a new {@link Phase} with the supplied typeName.
     *
     * @param name       the typeName the new {@link Phase} should have
     * @param scenarioId the scenario to add the phase to
     * @return the newly generated {@link Phase}
     * @throws ScenarioNotFoundException if no scenario with the supplied id can be found
     */
    public Phase createPhase(final int scenarioId, final String name) throws ScenarioNotFoundException {
        Scenario s = scenarioRepository.getScenario(scenarioId);
        if (s == null) {
            throw new ScenarioNotFoundException("No scenario found with the given id!");
        }
        Phase p = new Phase(name);
        s.assignPhase(p);
        return p;
    }

    /**
     * Delete a phase from a given scenario.
     *
     * @param scenarioId the scenario to remove the phase from
     * @param phaseId    the phase to remove
     * @return the phase that has been deleted
     * @throws ScenarioNotFoundException if no scenario with the supplied id can be found
     * @throws PhaseNotFoundException    if no phase with the supplied if can be found
     */
    public Phase deletePhase(final int scenarioId, final int phaseId) throws ScenarioNotFoundException,
            PhaseNotFoundException, IllegalPhaseOperationException {

        Scenario scenario = this.scenarioRepository.getScenario(scenarioId);
        if (scenario == null) {
            throw new ScenarioNotFoundException("can not find scenario with id: " + scenarioId);
        }
        Phase phase = scenario.getPhaseById(phaseId);
        if (phase == null) {
            throw new PhaseNotFoundException("can not find phase with id " + phaseId + " in scenario with id "
                    + scenarioId);
        }

        // this check is just there to create an error if necessary. deleting the standard phase is by design not possible
        if (scenario.getStandardPhase().getId() == phaseId) {
            throw new IllegalPhaseOperationException("phase " + phaseId + " is the standard phase of scenario "
                    + scenarioId);
        }

        scenario.unassignPhase(phase);

        return phase;
    }

    /**
     * Discard a phase from a given scenario.
     *
     * @param scenarioId the scenario to discard the phase from
     * @param phaseId    the phase to discard
     * @throws PhaseNotFoundException    if no phase with the supplied id can be found
     * @throws ScenarioNotFoundException if no scenario with the supplied id can be found
     */
    public void discardPhase(final int scenarioId, final int phaseId) throws PhaseNotFoundException,
            ScenarioNotFoundException {

        Scenario scenario = this.scenarioRepository.getScenario(scenarioId);
        if (scenario == null) {
            throw new ScenarioNotFoundException("can not find scenario with id: " + scenarioId);
        }
        Phase phase = scenario.getPhaseById(phaseId);
        if (phase == null) {
            throw new PhaseNotFoundException("can not find phase with id " + phaseId + " in scenario with id "
                    + scenarioId);
        }

        scenario.discardPhase(phase);
    }

    /**
     * Shifts all events in the given phase by the give time difference.
     *
     * @param phaseId the phase to shift
     * @param shift   the time to shift in milliseconds. Will be added to the relative event times
     * @return the phase with the shifted times
     * @throws PhaseNotFoundException         if no phase with the supplied id can be found
     * @throws ScenarioNotFoundException      if no scenario with the supplied id can be found
     * @throws IllegalPhaseOperationException if the shifted time of the first event is less than the scenario start
     *                                        time
     */
    public Phase shiftPhase(final int scenarioId, final int phaseId, final long shift) throws PhaseNotFoundException,
            ScenarioNotFoundException, IllegalPhaseOperationException {

        Scenario scenario = this.scenarioRepository.getScenario(scenarioId);
        if (scenario == null) {
            throw new ScenarioNotFoundException("can not find scenario with id: " + scenarioId);
        }
        Phase p = scenario.getPhaseById(phaseId);
        if (p == null) {
            throw new PhaseNotFoundException("No phase found with id: " + phaseId);
        }
        try {
            p.shiftEventTimes(shift);
            return p;
        } catch (IllegalArgumentException e) {
            throw new IllegalPhaseOperationException(e);
        }

    }
}
