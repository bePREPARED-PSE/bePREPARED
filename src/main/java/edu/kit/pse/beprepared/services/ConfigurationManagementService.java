package edu.kit.pse.beprepared.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.kit.pse.beprepared.json.ConfigurationJson;
import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.ConfigurationRepository;
import edu.kit.pse.beprepared.services.exceptions.ConfigurationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class manages all {@link Configuration}-related activities.
 */
@Service
public class ConfigurationManagementService {
    /**
     * The repository used to access all {@link Configuration}s.
     */
    private final ConfigurationRepository configRepository;

    /**
     * Constructor.
     * <p>
     * <p>
     * Constructs a new ConfigurationManagementService and initializes {@link this#configRepository}
     * using the dependency injection provided by Spring Boot.
     *
     * @param configRepository the repository used to access all configurations
     */
    @Autowired
    public ConfigurationManagementService(final ConfigurationRepository configRepository) {
        this.configRepository = configRepository;
    }

    /**
     * Create a new {@link Configuration} and add it to the {@link ConfigurationRepository}.
     *
     * @param scenarioStartTime    the start time of the scenario
     * @param additionalProperties the additional properties
     * @return the new configuration
     */
    public Configuration createConfiguration(final long scenarioStartTime,
                                             final HashMap<String, Object> additionalProperties) {
        Configuration c = new Configuration(scenarioStartTime, additionalProperties);
        configRepository.addConfiguration(c);
        return c;
    }

    /**
     * Get all {@link Configuration}s in the {@link ConfigurationRepository}.
     *
     * @return a collection containing all Configurations in the ConfigurationRepository
     */
    public Collection<Configuration> getAllConfigurations() {
        return configRepository.getAllConfigurations();
    }

    /**
     * Getter for a specific {@link Configuration}.
     *
     * @param id the id of the requested {@link Configuration}
     * @return the requested {@link Configuration} or {@code null}, if the {@link Configuration} with the supplied id is not found
     * @throws ConfigurationNotFoundException if no configuration with the supplied id can be found
     */
    public Configuration getConfigurationById(final int id) throws ConfigurationNotFoundException {
        Configuration configuration = configRepository.getConfigurationById(id);
        if (configuration == null) {
            throw new ConfigurationNotFoundException("can not find configuration with id " + id);
        }
        return configuration;
    }

    /**
     * Removes the {@link Configuration} with the supplied id from this repository.
     *
     * @param id the id of the {@link Configuration} that should be removed
     */
    public void deleteConfiguration(final int id) {
        this.configRepository.removeConfiguration(id);
    }

    /**
     * Update an existing {@link Configuration}.
     *
     * @param id                   the id of the configuration to update
     * @param scenarioStartTime    the new scenarioStartTime in unix time to set
     * @param additionalProperties the additional properties
     * @throws ConfigurationNotFoundException if no configuration with the supplied id can be found
     */
    public void updateConfiguration(final int id, final long scenarioStartTime,
                                    final HashMap<String, Object> additionalProperties)
            throws ConfigurationNotFoundException {

        Configuration c = this.getConfigurationById(id);
        //a config with the given id has been found. update it
        c.setScenarioStartTime(scenarioStartTime);
        additionalProperties.forEach(c::setPropertyValue);

    }

    /**
     * Exports the {@link Configuration} with the supplied id.
     * <p>
     * Creates a file with the JSON representation of {@link Configuration} with the supplied id.
     *
     * @param configId the id of the configuration that should be exported
     * @return the file containing the config
     * @throws IOException                    if something goes wrong while writing/creating the file
     * @throws ConfigurationNotFoundException if no configuration with the supplied id can be found
     */
    public File exportConfiguration(final int configId) throws IOException, ConfigurationNotFoundException {

        Configuration config = this.configRepository.getConfigurationById(configId);
        if (config == null) {
            throw new ConfigurationNotFoundException("could not find config with id " + configId);
        }

        ConfigurationJson configJson = new ConfigurationJson(config);

        File tmpFile = Files.createTempFile("bePREPARED-config-export-", ".beprepared-configuration").toFile();
        tmpFile.deleteOnExit();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(tmpFile, configJson);

        return tmpFile;
    }

    /**
     * Import a {@link Configuration} from a file.
     *
     * @param configFile the file containing the JSON  representation of the configuration to import
     * @return the newly imported configuration
     * @throws IOException if something goes wrong while reading the input file
     */
    public Configuration importConfiguration(final File configFile) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ConfigurationJson configJson = mapper.readValue(configFile, ConfigurationJson.class);

        Configuration config = new Configuration(configJson);
        this.configRepository.addConfiguration(config);

        return config;
    }

}
