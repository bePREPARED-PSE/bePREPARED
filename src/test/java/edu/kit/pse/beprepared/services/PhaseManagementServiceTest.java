package edu.kit.pse.beprepared.services;

import edu.kit.pse.beprepared.model.Phase;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.model.ScenarioRepository;
import edu.kit.pse.beprepared.services.exceptions.IllegalPhaseOperationException;
import edu.kit.pse.beprepared.services.exceptions.PhaseNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class PhaseManagementServiceTest {

    @Autowired
    private PhaseManagementService phaseManagementService;
    @MockBean
    private ScenarioRepository scenarioRepository;


    @Test
    public void testCreatePhaseValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final String phaseName = "bouncyCat";
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);

        // run method under test

        Phase phase = this.phaseManagementService.createPhase(scenarioId, phaseName);

        // check result

        assertThat(phase.getName(), is(phaseName));
        assertThat(phase.getAllEvents(), is(empty()));

        verify(mockScenario).assignPhase(phase);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testCreatePhaseScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.phaseManagementService.createPhase(scenarioId, null);

    }

    @Test
    public void testDeletePhaseValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockPhase = mock(Phase.class);
        final Phase mockStandardPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(mockPhase);
        when(mockScenario.getStandardPhase()).thenReturn(mockStandardPhase);
        when(mockStandardPhase.getId()).thenReturn(phaseId + 1);
        when(mockPhase.getId()).thenReturn(phaseId);

        // run method under test

        Phase phase = this.phaseManagementService.deletePhase(scenarioId, phaseId);

        // check result

        assertThat(phase, is(mockPhase));

        verify(mockScenario).unassignPhase(mockPhase);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testDeletePhaseScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.phaseManagementService.deletePhase(scenarioId, 0);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testDeletePhasePhaseNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(null);

        // run method under test

        this.phaseManagementService.deletePhase(scenarioId, phaseId);

    }

    @Test(expected = IllegalPhaseOperationException.class)
    public void testDeletePhaseStandardPhase() throws Exception {

        // preset
        final int scenarioId = 42;
        final int phaseToDeleteId = 42;
        final int standardPhaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockStandardPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseToDeleteId)).thenReturn(mockStandardPhase);
        when(mockScenario.getStandardPhase()).thenReturn(mockStandardPhase);
        when(mockStandardPhase.getId()).thenReturn(standardPhaseId);

        // run method under test

        this.phaseManagementService.deletePhase(scenarioId, phaseToDeleteId);

    }

    @Test
    public void testDiscardPhaseValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(mockPhase);

        // run method under test

        this.phaseManagementService.discardPhase(scenarioId, phaseId);

        // check result

        verify(mockScenario).discardPhase(mockPhase);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testDiscardPhaseScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.phaseManagementService.discardPhase(scenarioId, 0);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testDiscardPhasePhaseNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(null);

        // run method under test

        this.phaseManagementService.discardPhase(scenarioId, phaseId);

    }

    @Test
    public void testShiftPhaseValidInput() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final long shift = 4711;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(mockPhase);

        // run method under test

        Phase phase = this.phaseManagementService.shiftPhase(scenarioId, phaseId, shift);

        // check result

        assertThat(phase, is(mockPhase));

        verify(mockPhase).shiftEventTimes(shift);

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testShiftPhaseScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.phaseManagementService.shiftPhase(scenarioId, 0, 0L);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testShiftPhasePhaseNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(null);

        // run method under test

        this.phaseManagementService.shiftPhase(scenarioId, phaseId, 0L);

    }

    @Test(expected = IllegalPhaseOperationException.class)
    public void testShiftPhaseIllegalShift() throws Exception {

        // preset

        final int scenarioId = 42;
        final int phaseId = 42;
        final long shift = 4711L;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(mockScenario.getPhaseById(phaseId)).thenReturn(mockPhase);
        doThrow(new IllegalArgumentException()).when(mockPhase).shiftEventTimes(shift);

        // run method under test

        this.phaseManagementService.shiftPhase(scenarioId, phaseId, shift);

    }

    @TestConfiguration
    static class PhaseManagementServiceTestConfiguration {

        @Bean
        public PhaseManagementService phaseManagementService(ScenarioRepository repository) {
            return new PhaseManagementService(repository);
        }

    }

}
