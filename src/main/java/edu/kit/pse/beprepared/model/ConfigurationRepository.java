package edu.kit.pse.beprepared.model;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

/**
 * This repository holds {@link Configuration}s.
 */
@Repository
public class ConfigurationRepository {
    /**
     * List of all available {@link Configuration}s.
     */
    private final LinkedList<Configuration> configs;

    /**
     * Constructs a new ConfigurationRepository.
     */
    public ConfigurationRepository() {
        this.configs = new LinkedList<>();
    }

    /**
     * Adds a new {@link Configuration} to this repository.
     *
     * @param config the {@link Configuration} that should be added
     */

    public void addConfiguration(final Configuration config) {
        this.configs.add(config);
    }

    /**
     * Getter for a specific {@link Configuration}.
     *
     * @param id the id of the requested {@link Configuration}
     * @return the requested {@link Configuration} or {@code null}, if the {@link Configuration} with the supplied id is not found
     */
    public Configuration getConfigurationById(final int id) {
        for (Configuration config : configs) {
            if (config.getId() == id) {
                return config;
            }
        }
        return null;
    }

    /**
     * Get all {@link Configuration}s in this repository.
     *
     * @return a list with all Configurations in this repository.
     */
    public List<Configuration> getAllConfigurations() {
        return new LinkedList<>(configs);
    }

    /**
     * Removes the {@link Configuration} with the supplied id from this repository.
     *
     * @param id the id of the {@link Configuration} that should be removed
     * @return the removed {@link Configuration} or {@code null}, if no {@link Configuration} with the supplied id could be found
     */
    public Configuration removeConfiguration(final int id) {

        Configuration c = this.getConfigurationById(id);
        this.configs.remove(c);

        return c;
    }

}
