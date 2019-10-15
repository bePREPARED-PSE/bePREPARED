package edu.kit.pse.beprepared.restController;

import edu.kit.pse.beprepared.json.ConfigurationJson;
import edu.kit.pse.beprepared.json.XInputFieldJson;
import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.EventTypeManager;
import edu.kit.pse.beprepared.restController.exceptions.InternalServerErrorException;
import edu.kit.pse.beprepared.restController.exceptions.NotFoundException;
import edu.kit.pse.beprepared.services.ConfigurationManagementService;
import edu.kit.pse.beprepared.services.exceptions.ConfigurationNotFoundException;
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
import java.nio.file.Files;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This is the REST controller for the configuration management.
 */
@RestController
public class ConfigurationController {

    /**
     * The logger used by this class.
     */
    private final Logger log = Logger.getLogger(ConfigurationController.class);

    /**
     * The corresponding {@link ConfigurationManagementService}.
     */
    private final ConfigurationManagementService service;


    /**
     * Constructor.
     *
     * @param service the corresponding {@link ConfigurationManagementService}
     */
    @Autowired
    public ConfigurationController(final ConfigurationManagementService service) {
        this.service = service;
    }


    /**
     * Mapping for {@code /configs} (GET).
     *
     * @return a collection of {@code ConfigurationJson}
     */
    @RequestMapping(value = "/configs", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Collection<ConfigurationJson> getConfigurations() {

        log.info("Received request: \"/configs\" Method: GET");

        return this.service.getAllConfigurations().stream().map(ConfigurationJson::new).collect(Collectors.toList());

    }

    /**
     * Mapping for {@code /configurationDescriptor} (GET).
     *
     * @return a corresponding {@link Collection<XInputFieldJson>}
     */
    @RequestMapping(value = "/configurationDescriptor", method = RequestMethod.GET)
    public Collection<XInputFieldJson> getConfigurationDescriptor() {

        log.info("Received request: \"/configurationDescriptor\" Method: GET");

        return EventTypeManager.getInstance().getConfigurationDescriptor().getFields().stream()
                .map(XInputFieldJson::new).collect(Collectors.toList());
    }

    /**
     * Mapping for {@code /configs} (POST).
     *
     * @param configurationJson the configuration that should be added with blank id field
     * @return the newly created configuration
     */
    @RequestMapping(value = "/configs", method = RequestMethod.POST, consumes = "application/json",
            produces = "application/json")
    public @ResponseBody
    ConfigurationJson addConfiguration(@RequestBody ConfigurationJson configurationJson) {

        log.info("Received request: \"/configs\" Method: POST");
        log.debug("Received requestBody: \"" + configurationJson.toString() + "\"");

        Configuration c;
        c = this.service.createConfiguration(configurationJson.getScenarioStartTime(),
                configurationJson.getAdditionalProperties());

        return new ConfigurationJson(c);
    }

    /**
     * Mapping for {@code /configs/{configId}} (GET).
     *
     * @param configId the id of the requested configuration
     * @return a json representation of the configuration with the supplied id
     */
    @RequestMapping(value = "/configs/{configId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ConfigurationJson getConfig(@PathVariable int configId) {

        log.info("Received request: \"/configs/" + configId + "\" Method: GET");

        try {
            return new ConfigurationJson(this.service.getConfigurationById(configId));
        } catch (ConfigurationNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

    }

    /**
     * Mapping for {@code /configs/{configId}} (PUT).
     *
     * @param configId the id of the configuration that should be updated
     */
    @RequestMapping(value = "/configs/{configId}", method = RequestMethod.PUT)
    public void updateConfig(@PathVariable int configId, @RequestBody ConfigurationJson configJson) {

        log.info("Received request: \"/configs/" + configId + "\" Method: PUT");
        log.debug("Received requestBody \"" + configJson.toString() + "\"");

        try {
            this.service.updateConfiguration(configId, configJson.getScenarioStartTime(),
                    configJson.getAdditionalProperties());
        } catch (ConfigurationNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

    }

    /**
     * Mapping for {@code /configs/{configurationId}} (DELETE).
     *
     * @param configId the id of the configuration that should be deleted
     */
    @RequestMapping(value = "/configs/{configId}", method = RequestMethod.DELETE)
    public void deleteConfiguration(@PathVariable int configId) {

        log.info("Received request: \"/configs" + configId + "\" Method: DELETE");

        this.service.deleteConfiguration(configId);

    }

    /**
     * Mapping for {@code /configs/import} (POST).
     *
     * @param multipartFile the uploaded file
     * @return the newly imported configuration
     */
    @RequestMapping(value = "/configs/import", method = RequestMethod.POST)
    public @ResponseBody
    ConfigurationJson uploadConfiguration(@RequestParam("file") MultipartFile multipartFile) {

        log.info("Received request: \"/configs/import\" Method: POST");

        try {

            File tmpFile = Files.createTempFile("bePREPARED-config-import-", ".beprepared").toFile();
            tmpFile.deleteOnExit();
            multipartFile.transferTo(tmpFile);

            return new ConfigurationJson(this.service.importConfiguration(tmpFile));

        } catch (IOException e) {
            log.error(e);
            throw new InternalServerErrorException(e);
        }

    }

    /**
     * Mapping for {@code /configs/{configId}/export} (GET).
     *
     * @param configId the id of the configuration that should be exported
     * @return an appropriate response entity
     */
    @RequestMapping(value = "/configs/{configId}/export", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadConfiguration(@PathVariable final int configId) {

        log.info("Received request: \"/configs/" + configId + "/export\" Method: GET");

        File file;
        try {
            file = this.service.exportConfiguration(configId);
        } catch (IOException e) {
            log.warn(e);
            throw new InternalServerErrorException(e);
        } catch (ConfigurationNotFoundException e) {
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

}
