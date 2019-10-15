package edu.kit.pse.beprepared.restController;

import edu.kit.pse.beprepared.json.SimulationJson;
import edu.kit.pse.beprepared.restController.exceptions.InternalServerErrorException;
import edu.kit.pse.beprepared.restController.exceptions.NotFoundException;
import edu.kit.pse.beprepared.services.SimulationManagementService;
import edu.kit.pse.beprepared.services.exceptions.ConfigurationNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.PhaseNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.SimulationNotFoundException;
import edu.kit.pse.beprepared.simulation.Simulation;
import edu.kit.pse.beprepared.simulation.SimulationState;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This is the REST controller for the {@link Simulation} management.
 */
@RestController
public class SimulationManagementController {
    /**
     * The logger used by this class.
     */
    private final Logger log = Logger.getLogger(SimulationManagementController.class);

    /**
     * The corresponding {@link SimulationManagementService}.
     */
    private final SimulationManagementService simulationManagementService;

    /**
     * Constructor.
     *
     * @param simulationManagementService the corresponding {@link SimulationManagementService}.
     */
    @Autowired
    public SimulationManagementController(SimulationManagementService simulationManagementService) {
        this.simulationManagementService = simulationManagementService;
    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/simulations} (GET).
     *
     * @return a collection of {@code SimulationJson}
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/simulations", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Collection<SimulationJson> getSimulations(@PathVariable final int scenarioId) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/simulations\" Method: GET");

        return this.simulationManagementService.getSimulationsForScenario(scenarioId).stream()
                .map(SimulationJson::new).collect(Collectors.toList());

    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/simulations} (POST).
     *
     * @param scenarioId     the scenario to create a simulation for
     * @param simulationJson the requestBody
     * @return a Json representation of the newly generated {@link Simulation}
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/simulations", method = RequestMethod.POST,
            produces = "application/json", consumes = "application/json")
    public @ResponseBody
    SimulationJson createSimulation(@PathVariable final int scenarioId,
                                    @RequestBody final SimulationJson simulationJson) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/simulations\" Method: POST");
        log.debug("Received requestBody: \"" + simulationJson.toString() + "\"");

        try {
            return new SimulationJson(this.simulationManagementService.createSimulation(scenarioId,
                    simulationJson.getConfiguration().getConfigurationID(), simulationJson.getSelectedPhaseIDs(),
                    simulationJson.getSpeed()));
        } catch (ScenarioNotFoundException | ConfigurationNotFoundException | PhaseNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }
    }

    /**
     * Mapping for {@code /simulations/{simulationId}/play} (POST)
     *
     * @param simulationId the simulation to start
     * @return the simulation state
     */
    @RequestMapping(value = "/simulations/{simulationId}/play", method = RequestMethod.POST)
    public @ResponseBody
    SimulationState startSimulation(@PathVariable final int simulationId) {

        log.info("Received request: \"/simulations/" + simulationId + "/play\" Method: POST");

        try {
            return this.simulationManagementService.startSimulation(simulationId);
        } catch (SimulationNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }
    }

    /**
     * Mapping for {@code /simulations/{simulationId}/pause} (POST)
     *
     * @param simulationId the simulation to pause
     * @return the simulation state
     */
    @RequestMapping(value = "/simulations/{simulationId}/pause", method = RequestMethod.POST)
    public @ResponseBody
    SimulationState pauseSimulation(@PathVariable final int simulationId) {

        log.info("Received request: \"/simulations/" + simulationId + "/pause\" Method: POST");

        try {
            return this.simulationManagementService.pauseSimulation(simulationId);
        } catch (SimulationNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }
    }

    /**
     * Mapping for {@code /simulations/{simulationId}/stop} (POST)
     *
     * @param simulationId the simulation to stop
     * @return the simulation state
     */
    @RequestMapping(value = "/simulations/{simulationId}/stop", method = RequestMethod.POST)
    public @ResponseBody
    SimulationState stopSimulation(@PathVariable final int simulationId) {

        log.info("Received request: \"/simulations/" + simulationId + "/stop\" Method: POST");

        try {
            return this.simulationManagementService.stopSimulation(simulationId);
        } catch (SimulationNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }
    }

    /**
     * Mapping for {@code /simulations/{simulationId}/fastforward/{eventId}} (POST)
     *
     * @param simulationId the simulation to fast forward
     * @param eventId      the event to fast forward to
     * @return the simulation state
     */
    @RequestMapping(value = "/simulations/{simulationId}/fastforward/{eventId}", method = RequestMethod.POST)
    public @ResponseBody
    SimulationState fastForward(@PathVariable final int simulationId,
                                @PathVariable final long eventId) {

        log.info("Received request: \"/simulations/" + simulationId + "/fastforward/" + eventId + "\" Method: POST");

        try {
            return this.simulationManagementService.fastForwardSimulation(simulationId, eventId);
        } catch (SimulationNotFoundException | IllegalArgumentException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }
    }

    /**
     * Mapping for {@code /simulations/{simulationId}/collect} (GET).
     *
     * @param simulationId the id of the simulation
     * @return an appropriate {@link ResponseEntity}
     */
    @RequestMapping(value = "/simulations/{simulationId}/collect", method = RequestMethod.GET)
    public ResponseEntity<Resource> collectSimulation(@PathVariable final int simulationId) {

        log.info("Received request: \"/simulations/" + simulationId + "/collect\" Method: GET");

        try {

            File reportFile = this.simulationManagementService.collectSimulation(simulationId);
            Resource resource = new UrlResource(reportFile.toURI());

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (SimulationNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        } catch (IOException e) {
            log.warn(e);
            throw new InternalServerErrorException(e);
        }

    }

    /**
     * Mapping for {@code /simulations/{simulationId}/speed} (POST).
     *
     * @param simulationId the id of the simulation
     * @param speed        the new speed value for the simulation
     */
    @RequestMapping(value = "/simulations/{simulationId}/speed", method = RequestMethod.POST)
    public void setSpeed(@PathVariable int simulationId, @RequestParam("speed") double speed) {

        log.info("Received request: \"/simulations/" + simulationId + "/speed\" Method: POST");
        log.debug("Received request param speed=" + speed);

        try {
            this.simulationManagementService.changeSimulationSpeed(simulationId, speed);
        } catch (SimulationNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

    }

}
