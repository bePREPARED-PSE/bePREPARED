package edu.kit.pse.beprepared.services;

import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEvent;
import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEventType;
import edu.kit.pse.beprepared.model.*;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import org.hamcrest.Matchers;
import org.junit.After;
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
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ScenarioManagementServiceTest {

    @Autowired
    private ScenarioManagementService scenarioManagementService;
    @MockBean
    private ScenarioRepository scenarioRepository;

    private HashMap<String, EventType> mockEventTypeMap;
    private HashMap<String, EventType> originalEventTypeMap;


    @Before
    public void setUp() throws Exception {

        this.mockEventTypeMap = new HashMap<>();
        this.originalEventTypeMap = EventTypeManager.getInstance().getEventTypes();

        Field etmField = EventTypeManager.class.getDeclaredField("eventTypes");
        etmField.setAccessible(true);
        etmField.set(EventTypeManager.getInstance(), this.mockEventTypeMap);

    }

    @After
    public void tearDown() throws Exception {

        Field etmField = EventTypeManager.class.getDeclaredField("eventTypes");
        etmField.setAccessible(true);
        etmField.set(EventTypeManager.getInstance(), this.originalEventTypeMap);

    }


    @Test
    public void testCreateScenario() {

        // preset

        final String scenarioName = "bouncyCats";

        // run method under test

        Scenario scenario = this.scenarioManagementService.createScenario(scenarioName);

        // check result

        assertThat(scenario.getAllPhases(), hasSize(1));
        assertThat(scenario.getStandardPhase().getName(), is("Standard"));

        verify(scenarioRepository).addScenario(scenario);

    }

    @Test
    public void testGetAllScenarios() {

        // run method under test

        this.scenarioManagementService.getAllScenarios();

        // check result

        verify(scenarioRepository).getAllScenarios();

    }

    @Test
    public void testGetScenarioValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);

        // run method under test

        Scenario scenario = this.scenarioManagementService.getScenario(scenarioId);

        // check result

        assertThat(scenario, is(mockScenario));

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testGetScenarioScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.scenarioManagementService.getScenario(scenarioId);

    }

    @Test
    public void testDeleteScenario() {

        // preset

        final int scenarioId = 42;

        // run method under test

        this.scenarioManagementService.deleteScenario(scenarioId);

        // check result

        verify(scenarioRepository).removeScenario(scenarioId);

    }

    @Test
    public void testImportExportScenarioValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final long pointInTime = 123456L;
        final String testValue = "bouncyCat";
        final Phase phase = new Phase("Standard");
        final Scenario mockScenario = new Scenario(phase);
        mockEventTypeMap.put(MockEventType.TYPE_NAME, new MockEventType());

        phase.addEvent(new MockEvent(mockEventTypeMap.get(MockEventType.TYPE_NAME), pointInTime, testValue));

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);

        // run methods under test

        File exportFile = this.scenarioManagementService.exportScenario(scenarioId);

        Scenario scenario = this.scenarioManagementService.importScenario(exportFile);

        // check results

        assertThat(exportFile.getName(), Matchers.startsWith("bePREPARED-scenario-"));
        assertThat(exportFile.getName(), Matchers.endsWith(".bp_scenario.zip"));
        assertThat(scenario.getAllPhases(), hasSize(1));
        assertThat(scenario.getStandardPhase().getName(), is("Standard"));
        assertThat(scenario.getStandardPhase().getAllEvents(), hasSize(1));
        assertThat(scenario.getStandardPhase().getAllEvents().get(0), instanceOf(MockEvent.class));

        MockEvent importedEvent = (MockEvent) scenario.getStandardPhase().getAllEvents().get(0);

        assertThat(importedEvent.getPointInTime(), is(pointInTime));
        assertThat(importedEvent.getaProperty(), is(testValue));

    }

    @Test(expected = IOException.class)
    public void testImportScenarioInvalidZip() throws Exception {

        // preset

        File file = Paths.get("src", "test", "resources", "testBadImport.bp_scenario.zip").toFile();

        // run method under test

        this.scenarioManagementService.importScenario(file);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testExportScenarioScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.scenarioManagementService.exportScenario(scenarioId);

    }


    @TestConfiguration
    static class ScenarioManagementServiceTestConfiguration {

        @Bean
        public ScenarioManagementService scenarioManagementService(ScenarioRepository scenarioRepository) {
            return new ScenarioManagementService(scenarioRepository);
        }

    }

}
