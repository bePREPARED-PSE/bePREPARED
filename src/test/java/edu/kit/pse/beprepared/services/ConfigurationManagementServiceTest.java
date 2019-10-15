package edu.kit.pse.beprepared.services;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.ConfigurationRepository;
import edu.kit.pse.beprepared.services.exceptions.ConfigurationNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ConfigurationManagementServiceTest {


    @Autowired
    private ConfigurationManagementService configurationManagementService;
    @MockBean
    private ConfigurationRepository configurationRepository;

    @Before
    public void setUp() {
    }

    @Test
    public void testCreateConfiguration() {

        // sample data

        final String testKey = "bouncy";
        final String testVal = "cats";
        HashMap<String, Object> additionalProperties = new HashMap<>() {{
            put(testKey, testVal);
            put("fancinessOver", 9000);
        }};
        long pointInTime = System.currentTimeMillis();

        // run the method under test

        Configuration configuration = this.configurationManagementService.createConfiguration(pointInTime,
                additionalProperties);

        // check the result

        assertThat(configuration.getScenarioStartTime(), is(pointInTime));
        assertThat(configuration.getAdditionalProperties(), is(additionalProperties));
        assertThat(configuration.getPropertyValue(testKey), is(testVal));

        verify(configurationRepository).addConfiguration(configuration);

    }

    @Test
    public void testGetAllConfigurations() {

        // sample data

        final String testKey = "bouncy";
        final String testVal = "cats";
        HashMap<String, Object> additionalProperties = new HashMap<>() {{
            put(testKey, testVal);
            put("fancinessOver", 9000);
        }};
        long pointInTime = System.currentTimeMillis();
        Configuration sampleConfig = new Configuration(pointInTime, additionalProperties);

        // preset

        when(configurationRepository.getAllConfigurations()).thenReturn(new LinkedList<>() {{
            add(sampleConfig);
        }});

        // run method under test

        Collection<Configuration> configurations = this.configurationManagementService.getAllConfigurations();

        // check result

        assertThat(configurations, hasSize(1));
        assertThat(configurations, hasItem(sampleConfig));

    }

    @Test
    public void testGetConfigurationById() throws ConfigurationNotFoundException {

        // sample data

        final String testKey = "bouncy";
        final String testVal = "cats";
        HashMap<String, Object> additionalProperties = new HashMap<>() {{
            put(testKey, testVal);
            put("fancinessOver", 9000);
        }};
        long pointInTime = System.currentTimeMillis();
        Configuration sampleConfig = new Configuration(pointInTime, additionalProperties);

        // preset

        final int id = 42;

        when(configurationRepository.getConfigurationById(id)).thenReturn(sampleConfig);

        // run method

        Configuration c = this.configurationManagementService.getConfigurationById(id);

        // check result

        assertThat(c, is(sampleConfig));

    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void testGetConfigurationByIdInvalidParam() throws ConfigurationNotFoundException {

        // preset

        final int id = 42;
        when(configurationRepository.getConfigurationById(id)).thenReturn(null);

        // run method under test

        this.configurationManagementService.getConfigurationById(id);

    }

    @Test
    public void testDeleteConfiguration() {

        // preset

        final int id = 42;

        // run method under test

        this.configurationManagementService.deleteConfiguration(id);

        // check result

        verify(configurationRepository).removeConfiguration(id);

    }

    @Test
    public void testUpdateConfiguration() throws ConfigurationNotFoundException {

        // sample data

        final int id = 42;
        final String testKey = "bouncy";
        final String testVal = "cats";
        final String newTestVal = "castle";
        final String testKey2 = "fancinessOver";
        final int testVal2 = 9000;
        HashMap<String, Object> additionalProperties = new HashMap<>() {{
            put(testKey, testVal);
            put(testKey2, testVal2);
        }};
        HashMap<String, Object> newAdditionalProperties = new HashMap<>() {{
            put(testKey, newTestVal);
        }};
        long pointInTime = System.currentTimeMillis();
        long newPointInTime = pointInTime + 10;
        Configuration sampleConfig = new Configuration(pointInTime, additionalProperties);

        // preset

        when(configurationRepository.getConfigurationById(id)).thenReturn(sampleConfig);

        // run method under test

        this.configurationManagementService.updateConfiguration(id, newPointInTime, newAdditionalProperties);

        // check result

        assertThat(sampleConfig.getScenarioStartTime(), is(newPointInTime));
        assertThat(sampleConfig.getAdditionalProperties(), hasEntry(is(testKey), is(newTestVal)));
        assertThat(sampleConfig.getAdditionalProperties(), hasEntry(is(testKey2), is(testVal2)));
        assertThat(sampleConfig.getAdditionalProperties().size(), is(2));

    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void testUpdateConfigurationInvalidParam() throws ConfigurationNotFoundException {

        // preset

        final int id = 42;
        when(configurationRepository.getConfigurationById(id)).thenReturn(null);

        // run method under test

        this.configurationManagementService.updateConfiguration(id, 0, new HashMap<>());

    }

    @Test
    public void testExportConfiguration() throws IOException, ConfigurationNotFoundException, URISyntaxException {

        // sample data

        final int id = 42;
        final String testKey = "bouncy";
        final String testVal = "cats";
        final String testKey2 = "fancinessOver";
        final int testVal2 = 9000;
        HashMap<String, Object> additionalProperties = new HashMap<>() {{
            put(testKey, testVal);
            put(testKey2, testVal2);
        }};
        long pointInTime = System.currentTimeMillis();
        Configuration sampleConfig = new Configuration(pointInTime, additionalProperties);

        // preset

        when(configurationRepository.getConfigurationById(id)).thenReturn(sampleConfig);

        // run method under test

        File configFile = this.configurationManagementService.exportConfiguration(id);

        // import again

        Configuration configuration = this.configurationManagementService.importConfiguration(configFile);

        // check result

        assertThat(configuration.getAdditionalProperties(), hasEntry(is(testKey), is(testVal)));
        assertThat(configuration.getAdditionalProperties(), hasEntry(is(testKey2), is(testVal2)));
        assertThat(configuration.getAdditionalProperties().size(), is(sampleConfig.getAdditionalProperties().size()));
        assertThat(configuration.getScenarioStartTime(), is(pointInTime));

        verify(configurationRepository).addConfiguration(configuration);

    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void testExportNonExistingConfiguration() throws IOException, ConfigurationNotFoundException {

        // preset

        final int id = 42;
        when(configurationRepository.getConfigurationById(id)).thenReturn(null);

        // run method under test
        this.configurationManagementService.exportConfiguration(id);

    }


    @TestConfiguration
    static class ConfigurationManagementServiceTestConfiguration {

        @Bean
        public ConfigurationManagementService configurationManagementService(ConfigurationRepository configurationRepository) {
            return new ConfigurationManagementService(configurationRepository);
        }
    }

}