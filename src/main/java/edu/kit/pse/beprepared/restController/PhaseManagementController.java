package edu.kit.pse.beprepared.restController;

import edu.kit.pse.beprepared.json.PhaseJson;
import edu.kit.pse.beprepared.model.Phase;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.restController.exceptions.ForbiddenException;
import edu.kit.pse.beprepared.restController.exceptions.NotFoundException;
import edu.kit.pse.beprepared.services.PhaseManagementService;
import edu.kit.pse.beprepared.services.ScenarioManagementService;
import edu.kit.pse.beprepared.services.exceptions.IllegalPhaseOperationException;
import edu.kit.pse.beprepared.services.exceptions.PhaseNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This is the REST controller for the {@link Phase} management.
 */
@RestController
public class PhaseManagementController {
    /**
     * The logger used by this class.
     */
    private final Logger log = Logger.getLogger(PhaseManagementController.class);

    /**
     * The corresponding {@link PhaseManagementService}.
     */
    private final PhaseManagementService phaseManagementService;

    /**
     * The corresponding {@link ScenarioManagementService}.
     */
    private final ScenarioManagementService scenarioManagementService;


    /**
     * Constructor.
     *
     * @param phaseManagementService    the corresponding {@link PhaseManagementService}.
     * @param scenarioManagementService the corresponding {@link ScenarioManagementService}
     */
    @Autowired
    public PhaseManagementController(final PhaseManagementService phaseManagementService,
                                     final ScenarioManagementService scenarioManagementService) {
        this.phaseManagementService = phaseManagementService;
        this.scenarioManagementService = scenarioManagementService;
    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases} (POST)
     *
     * @param scenarioId the pathVariable {@code scenarioId}
     * @param phaseJson  the requestBody
     * @return a Json representation of the newly generated {@link Phase}
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public @ResponseBody
    PhaseJson createPhase(@PathVariable int scenarioId, @RequestBody PhaseJson phaseJson) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/phases/\" Method: POST");
        log.debug("Received requestBody: \"" + phaseJson.toString() + "\"");

        try {
            Phase phase = phaseManagementService.createPhase(scenarioId, phaseJson.getName());
            return new PhaseJson(phase);
        } catch (ScenarioNotFoundException e) {
            log.warn("Scenario with id " + scenarioId + " does not exist!", e);
            throw new NotFoundException(e);
        }
    }


    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases}.
     *
     * @param scenarioId the {@link Scenario} to get all phases from
     * @return a {@link Collection<PhaseJson>} containing all available {@link Phase}s in the selected {@link Scenario}.
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Collection<PhaseJson> getAllPhases(@PathVariable int scenarioId) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/phases\" Method: GET");

        Scenario s;
        try {
            s = this.scenarioManagementService.getScenario(scenarioId);
        } catch (ScenarioNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

        return s.getAllPhases().stream().map(PhaseJson::new).collect(Collectors.toList());
    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases/{phaseId}} (DELETE).
     *
     * @param scenarioId the id of the scenario the phase to delete belongs to
     * @param phaseId    the id of the phase that should be deleted
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases/{phaseId}", method = RequestMethod.DELETE)
    public void deletePhase(@PathVariable int scenarioId, @PathVariable int phaseId) {

        log.info("Received request: \"/scenario/" + scenarioId + "/phases/" + phaseId + "\" Method: DELETE");

        try {
            this.phaseManagementService.deletePhase(scenarioId, phaseId);
        } catch (ScenarioNotFoundException | PhaseNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        } catch (IllegalPhaseOperationException e) {
            log.warn(e);
            throw new ForbiddenException(e);
        }

    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases/{phaseId}/discard} (POST).
     *
     * @param scenarioId the id of the scenario the phase to discard belongs to
     * @param phaseId    the id of the phase to discard
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases/{phaseId}/discard", method = RequestMethod.POST)
    public void discardPhase(@PathVariable int scenarioId, @PathVariable int phaseId) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/phases/" + phaseId + "/discard\" Method: POST");

        try {
            this.phaseManagementService.discardPhase(scenarioId, phaseId);
        } catch (ScenarioNotFoundException | PhaseNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases/{phaseId}/shift/{time}} (PATCH).
     *
     * @param scenarioId the id of the scenario the phase that should be shifted belongs to
     * @param phaseId    the id of the phase that should be shifted
     * @param time       the time difference
     * @return the phase with shifted times
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases/{phaseId}/shift/{time}", method = RequestMethod.PATCH,
            produces = "application/json")
    public @ResponseBody
    PhaseJson shiftPhase(@PathVariable int scenarioId, @PathVariable int phaseId,
                         @PathVariable long time) {

        log.info("Received request: /scenarios/" + scenarioId + "/phases/" + phaseId + "/shift/" + time
                + "\" Method: PATCH");

        Phase phase;
        try {
            phase = this.phaseManagementService.shiftPhase(scenarioId, phaseId, time);
        } catch (ScenarioNotFoundException | PhaseNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        } catch (IllegalPhaseOperationException e) {
            log.warn(e);
            throw new ForbiddenException(e);
        }

        return new PhaseJson(phase);

    }


}
