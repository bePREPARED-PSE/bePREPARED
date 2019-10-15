package edu.kit.pse.beprepared.restController;

import edu.kit.pse.beprepared.json.ScenarioJson;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.restController.exceptions.InternalServerErrorException;
import edu.kit.pse.beprepared.restController.exceptions.NotFoundException;
import edu.kit.pse.beprepared.services.FileManagementService;
import edu.kit.pse.beprepared.services.ScenarioManagementService;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * This is the REST controller for the {@link Scenario} management.
 */
@RestController
public class ScenarioManagementController {
    /**
     * The logger used by this class.
     */
    private final Logger log = Logger.getLogger(ScenarioManagementController.class);

    /**
     * The corresponding {@link ScenarioManagementService}.
     */
    private final ScenarioManagementService scenarioManagementService;

    /**
     * Constructor.
     *
     * @param scenarioManagementService the corresponding {@link ScenarioManagementService}.
     */
    @Autowired
    public ScenarioManagementController(ScenarioManagementService scenarioManagementService) {
        this.scenarioManagementService = scenarioManagementService;
    }

    /**
     * Mapping for {@code /scenarios} (POST)
     *
     * @param scenarioJson the requestBody
     * @return a Json representation of the newly generated {@link Scenario}
     */
    @RequestMapping(value = "/scenarios", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public @ResponseBody
    ScenarioJson createScenario(@RequestBody ScenarioJson scenarioJson) {

        log.info("Received request: \"/scenarios\" Method: POST");
        log.debug("Received requestBody: \"" + scenarioJson.toString() + "\"");

        Scenario scenario = scenarioManagementService.createScenario(scenarioJson.getName());
        return new ScenarioJson(scenario);
    }

    /**
     * Mapping for {@code /scenarios} (GET).
     *
     * @return a collection of {@code ScenarioJson}
     */
    @RequestMapping(value = "/scenarios", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Collection<ScenarioJson> getScenarios() {

        log.info("Received request: \"/scenarios\" Method: GET");

        return this.scenarioManagementService.getAllScenarios().stream()
                .map(ScenarioJson::new).collect(Collectors.toList());
    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}} (GET).
     *
     * @param scenarioId the id of the requested scenario
     * @return a json representation of the scenario with the supplied id
     */
    @RequestMapping(value = "/scenarios/{scenarioId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ScenarioJson getScenario(@PathVariable int scenarioId) {

        log.info("Received request: \"/scenarios/" + scenarioId + "\" Method: GET");

        try {
            return new ScenarioJson(this.scenarioManagementService.getScenario(scenarioId));
        } catch (ScenarioNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }
    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}} (DELETE).
     *
     * @param scenarioId the id of the scenario that should be deleted
     */
    @RequestMapping(value = "/scenarios/{scenarioId}", method = RequestMethod.DELETE)
    public void deleteScenario(@PathVariable int scenarioId) {

        log.info("Received request: \"/scenarios" + scenarioId + "\" Method: DELETE");

        this.scenarioManagementService.deleteScenario(scenarioId);
    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/export} (GET).
     *
     * @param scenarioId the id of the scenario that should be exported
     * @return an appropriate response entity
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/export", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadScenario(@PathVariable final int scenarioId) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/export\" Method: GET");

        File file;
        try {
            file = this.scenarioManagementService.exportScenario(scenarioId);
        } catch (IOException e) {
            log.warn(e);
            throw new InternalServerErrorException(e);
        } catch (ScenarioNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

        Resource resource;
        try {
            resource = new UrlResource(file.toURI());
        } catch (MalformedURLException e) {
            log.warn(e);
            throw new InternalServerErrorException(e);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Mapping for {@code /scenarios/import} (POST).
     */
    @RequestMapping(value = "/scenarios/import", method = RequestMethod.POST)
    public @ResponseBody
    ScenarioJson uploadScenario(@RequestParam("file") MultipartFile multipartFile) {

        log.info("Received request: \"/scenarios/import\" Method: POST");

        try {

            File tmpFile = FileManagementService.getInstance().createFile("beprepared-import", true);
            tmpFile.deleteOnExit();
            multipartFile.transferTo(tmpFile);

            return new ScenarioJson(this.scenarioManagementService.importScenario(tmpFile));

        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }

    }

}
