package edu.kit.pse.beprepared.services;

import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEvent;
import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEventType;
import edu.kit.pse.beprepared.model.*;
import edu.kit.pse.beprepared.services.exceptions.EventNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.IllegalEventTypeException;
import edu.kit.pse.beprepared.services.exceptions.PhaseNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class EventManagementServiceTest {

    @Autowired
    private EventManagementService eventManagementService;
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
    public void testGetEventTypes() {

        // preset

        MockEventType type = new MockEventType();
        this.mockEventTypeMap.put(MockEventType.TYPE_NAME, type);

        // run method under test

        Collection<EventType> types = this.eventManagementService.getEventTypes();

        // check result

        assertThat(types, hasSize(1));
        assertThat(types, contains(is(type)));

    }

    @Test
    public void testCreateEventValidInput() throws Exception {

        //preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final long pointInTime = System.currentTimeMillis();
        final String propertyValue = "bouncyCat";
        final HashMap<String, Object> sampleData = new HashMap<>() {{
            put(MockEvent.PROPERTY_KEY, propertyValue);
        }};

        Scenario scenario = mock(Scenario.class);
        Phase phase = mock(Phase.class);
        MockEventType mockEventType = mock(MockEventType.class);
        MockEvent mockEvent = mock(MockEvent.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(scenario);
        when(scenario.getPhaseById(phaseId)).thenReturn(phase);
        when(mockEventType.createEvent(pointInTime, sampleData)).thenReturn(mockEvent);

        this.mockEventTypeMap.put(MockEventType.TYPE_NAME, mockEventType);

        // run method under test

        Event event = this.eventManagementService.createEvent(scenarioId, phaseId, MockEventType.TYPE_NAME, sampleData,
                pointInTime);

        // check result

        verify(mockEventType).createEvent(pointInTime, sampleData);
        verify(phase).addEvent(event);

    }

    @Test(expected = IllegalEventTypeException.class)
    public void testCreateEventIllegalEventType() throws Exception {

        // run method under test
        this.eventManagementService.createEvent(0, 0, "Blablablubb", null, 0L);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testCreateEventScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        this.mockEventTypeMap.put(MockEventType.TYPE_NAME, new MockEventType());
        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.eventManagementService.createEvent(scenarioId, 0, MockEventType.TYPE_NAME, null, 0L);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testCreateEventPhaseNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);

        this.mockEventTypeMap.put(MockEventType.TYPE_NAME, new MockEventType());
        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(null);

        // run method under test

        this.eventManagementService.createEvent(scenarioId, phaseId, MockEventType.TYPE_NAME, null, 0L);

    }

    @Test
    public void testEditEventValidInput() throws Exception {

        //preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final int eventId = 42;
        final long pointInTime = System.currentTimeMillis();
        final String propertyValue = "bouncyCat";
        final HashMap<String, Object> sampleData = new HashMap<>() {{
            put(MockEvent.PROPERTY_KEY, propertyValue);
        }};

        Scenario scenario = mock(Scenario.class);
        Phase phase = mock(Phase.class);
        MockEvent mockEvent = mock(MockEvent.class);
        MockEventType mockEventType = mock(MockEventType.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(scenario);
        when(scenario.getPhaseById(phaseId)).thenReturn(phase);
        when(phase.getEventById(eventId)).thenReturn(mockEvent);
        when(mockEvent.getEventTypeInstance()).thenReturn(mockEventType);

        this.mockEventTypeMap.put(MockEventType.TYPE_NAME, new MockEventType());

        // run method under test

        this.eventManagementService.editEvent(scenarioId, phaseId, eventId, sampleData, pointInTime);

        // check result

        verify(mockEventType).editEvent(pointInTime, sampleData, mockEvent);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testEditEventScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.eventManagementService.editEvent(scenarioId, 0, 0L, null, 0L);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testEditEventPhaseNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(null);

        // run method under test

        this.eventManagementService.editEvent(scenarioId, phaseId, 0L, null, 0L);

    }

    @Test(expected = EventNotFoundException.class)
    public void testEditEventEventNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final int eventId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(mockPhase);
        when(mockPhase.getEventById(eventId)).thenReturn(null);

        // run method under test

        this.eventManagementService.editEvent(scenarioId, phaseId, eventId, null, 0L);

    }

    @Test
    public void testReassignEventValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final int oldPhaseId = 47;
        final int newPhaseId = 11;
        final int eventId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockOldPhase = mock(Phase.class);
        final Phase mockNewPhase = mock(Phase.class);
        final MockEvent mockEvent = mock(MockEvent.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(oldPhaseId)).thenReturn(mockOldPhase);
        when(mockScenario.getPhaseById(newPhaseId)).thenReturn(mockNewPhase);
        when(mockOldPhase.removeEvent(eventId)).thenReturn(mockEvent);

        // run method under test

        this.eventManagementService.reassignEvent(scenarioId, oldPhaseId, newPhaseId, eventId);

        // check result

        verify(mockOldPhase).removeEvent(eventId);
        verify(mockNewPhase).addEvent(mockEvent);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testReassignEventScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.eventManagementService.reassignEvent(scenarioId, 0, 0, 0);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testReassignEventOldPhaseNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int oldPhaseId = 47;
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(oldPhaseId)).thenReturn(null);

        // run method under test

        this.eventManagementService.reassignEvent(scenarioId, oldPhaseId, 0, 0);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testReassignEventNewPhaseNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int oldPhaseId = 47;
        final int newPhaseId = 11;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockOldPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(oldPhaseId)).thenReturn(mockOldPhase);
        when(mockScenario.getPhaseById(newPhaseId)).thenReturn(null);

        // run method under test

        this.eventManagementService.reassignEvent(scenarioId, oldPhaseId, newPhaseId, 0);

    }

    @Test(expected = EventNotFoundException.class)
    public void testReassignEventEventNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int oldPhaseId = 47;
        final int newPhaseId = 11;
        final int eventId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockOldPhase = mock(Phase.class);
        final Phase mockNewPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(oldPhaseId)).thenReturn(mockOldPhase);
        when(mockScenario.getPhaseById(newPhaseId)).thenReturn(mockNewPhase);
        when(mockOldPhase.removeEvent(eventId)).thenReturn(null);

        // run method under test

        this.eventManagementService.reassignEvent(scenarioId, oldPhaseId, newPhaseId, eventId);

    }

    @Test
    public void testDeleteEventValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final int eventId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(mockPhase);

        // run method under test

        this.eventManagementService.deleteEvent(scenarioId, phaseId, eventId);

        // check result

        verify(mockPhase).removeEvent(eventId);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testDeleteEventScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.eventManagementService.deleteEvent(scenarioId, 0, 0);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testDeleteEventPhaseNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(null);

        // run method under test

        this.eventManagementService.deleteEvent(scenarioId, phaseId, 0);

    }

    @Ignore
    @Test
    public void testCreateEventSeriesValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final int numOfEvents = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockPhase = new Phase();
        final String pointInTimeFunction = "(x^2)*1000";
        final String prefix = "the \\$ x-value is: ";
        final HashMap<String, Object> mockData = new HashMap<>() {{
            put(MockEvent.PROPERTY_KEY, prefix + "$x$");
        }};

        this.mockEventTypeMap.put(MockEventType.TYPE_NAME, new MockEventType());
        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(mockPhase);

        // run method under test

        this.eventManagementService.createEventSeries(scenarioId, phaseId, numOfEvents, MockEventType.TYPE_NAME,
                pointInTimeFunction, mockData);

        // verify result

        assertThat(mockPhase.getAllEvents(), hasSize(numOfEvents));

        String cleanedPrefix = new StringBuilder(prefix).deleteCharAt(4).toString();
        mockPhase.getAllEvents().forEach(e -> {

            assertThat(e.getPointInTime(), is(e.getId() * e.getId() * 1000));
            assertThat(e, instanceOf(MockEvent.class));

            MockEvent me = (MockEvent) e;

            assertThat(me.getaProperty(), equalTo(cleanedPrefix + e.getId()));

        });

    }

    @TestConfiguration
    static class EventManagementServiceTestConfiguration {

        @Bean
        public EventManagementService eventManagementService(ScenarioRepository scenarioRepository) {
            return new EventManagementService(scenarioRepository);
        }

    }

}
