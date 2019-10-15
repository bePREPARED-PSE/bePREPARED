package edu.kit.pse.beprepared.services;

import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEvent;
import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEventRunner;
import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEventType;
import edu.kit.pse.beprepared.model.*;
import edu.kit.pse.beprepared.services.exceptions.ConfigurationNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.PhaseNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.SimulationNotFoundException;
import edu.kit.pse.beprepared.simulation.ExecutionReport;
import edu.kit.pse.beprepared.simulation.ExecutionStatus;
import edu.kit.pse.beprepared.simulation.Simulation;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class SimulationManagementServiceTest {

    @Autowired
    private SimulationManagementService simulationManagementService;
    @MockBean
    private SimulationRepository simulationRepository;
    @MockBean
    private ConfigurationRepository configurationRepository;
    @MockBean
    private ScenarioRepository scenarioRepository;


    @Test
    @SuppressWarnings("unchecked")
    public void testCreateSimulationValidParams() throws Exception {

        // preset

        final int scenarioId = 42;
        final int configId = 42;
        final int phaseId1 = 47;
        final int phaseId2 = 11;
        final double initialSpeed = 4.2;
        final Scenario mockScenario = mock(Scenario.class);
        final Phase mockPhase1 = mock(Phase.class);
        final Phase mockPhase2 = mock(Phase.class);
        final Configuration mockConfiguration = mock(Configuration.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(configurationRepository.getConfigurationById(configId)).thenReturn(mockConfiguration);
        when(mockScenario.getPhaseById(phaseId1)).thenReturn(mockPhase1);
        when(mockScenario.getPhaseById(phaseId2)).thenReturn(mockPhase2);

        // run method under test

        this.simulationManagementService.createSimulation(scenarioId, configId, new LinkedList<>() {{
            add(phaseId1);
            add(phaseId2);
        }}, initialSpeed);

        // check result

        ArgumentCaptor<Collection<Phase>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(simulationRepository).createSimulation(eq(mockScenario), captor.capture(), eq(mockConfiguration),
                eq(initialSpeed));

        Collection<Phase> phases = captor.getValue();
        assertThat(phases, hasItems(mockPhase1, mockPhase2));

    }

    @Test(expected = ScenarioNotFoundException.class)
    public void testCreateSimulationScenarioNotFound() throws Exception {

        // preset

        final int scenarioId = 42;

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(null);

        // run method under test

        this.simulationManagementService.createSimulation(scenarioId, 0, null, 0);

    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void testCreateSimulationConfigurationNotFound() throws Exception {

        // preset

        final int scenarioId = 42;
        final int configurationId = 42;
        final Scenario mockScenario = mock(Scenario.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(configurationRepository.getConfigurationById(configurationId)).thenReturn(null);

        // run method under test

        this.simulationManagementService.createSimulation(scenarioId, configurationId, null, 0);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testCreateSimulationPhasesNull() throws Exception {

        // preset

        final int scenarioId = 42;
        final int configurationId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Configuration mockConfiguration = mock(Configuration.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(configurationRepository.getConfigurationById(configurationId)).thenReturn(mockConfiguration);

        // run method under test

        this.simulationManagementService.createSimulation(scenarioId, configurationId, null, 0);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testCreateSimulationPhasesEmpty() throws Exception {

        // preset

        final int scenarioId = 42;
        final int configurationId = 42;
        final Scenario mockScenario = mock(Scenario.class);
        final Configuration mockConfiguration = mock(Configuration.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(configurationRepository.getConfigurationById(configurationId)).thenReturn(mockConfiguration);

        // run method under test

        this.simulationManagementService.createSimulation(scenarioId, configurationId, new LinkedList<>(), 0);

    }

    @Test(expected = PhaseNotFoundException.class)
    public void testCreateSimulationPhaseNotFoundInScenario() throws Exception {

        // preset

        final int scenarioId = 42;
        final int configId = 42;
        final int validPhaseId = 47;
        final int invalidPhaseId = 11;
        final Scenario mockScenario = mock(Scenario.class);
        final Configuration mockConfiguration = mock(Configuration.class);
        final Phase mockPhase = mock(Phase.class);

        when(scenarioRepository.getScenario(scenarioId)).thenReturn(mockScenario);
        when(configurationRepository.getConfigurationById(configId)).thenReturn(mockConfiguration);
        when(mockScenario.getPhaseById(validPhaseId)).thenReturn(mockPhase);
        when(mockScenario.getPhaseById(invalidPhaseId)).thenReturn(null);

        List<Integer> phaseIds = Arrays.asList(validPhaseId, invalidPhaseId);

        // run method under test

        this.simulationManagementService.createSimulation(scenarioId, configId, phaseIds, 0);

    }

    @Test
    public void testGetAllSimulations() {

        // preset

        final int numberOfSimulations = 42;
        final List<Simulation> simulations = new LinkedList<>();
        for (int i = 0; i < numberOfSimulations; i++) {
            simulations.add(mock(Simulation.class));
        }

        when(simulationRepository.getAllSimulations()).thenReturn(simulations);

        // run method under test

        Collection<Simulation> result = this.simulationManagementService.getAllSimulations();

        // check result

        assertThat(result, hasSize(numberOfSimulations));
        assertThat(result, everyItem(isIn(simulations)));

    }

    @Test
    public void testStartSimulation() throws SimulationNotFoundException {

        // preset

        final int simulationId = 42;
        final Simulation mockSimulation = mock(Simulation.class);

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(mockSimulation);

        // run method under test

        this.simulationManagementService.startSimulation(simulationId);

        // check result

        verify(mockSimulation).start();

    }

    @Test
    public void testPauseSimulation() throws SimulationNotFoundException {

        // preset

        final int simulationId = 42;
        final Simulation mockSimulation = mock(Simulation.class);

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(mockSimulation);

        // run method under test

        this.simulationManagementService.pauseSimulation(simulationId);

        // check result

        verify(mockSimulation).togglePause();

    }

    @Test
    public void testStopSimulation() throws SimulationNotFoundException {

        // preset

        final int simulationId = 42;
        final Simulation mockSimulation = mock(Simulation.class);

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(mockSimulation);

        // run method under test

        this.simulationManagementService.stopSimulation(simulationId);

        // check result

        verify(mockSimulation).stop();

    }

    @Test
    public void testFastForwardSimulationValidInput() throws Exception {

        // preset

        final int simulationId = 42;
        final int eventId = 42;
        final Simulation mockSimulation = mock(Simulation.class);

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(mockSimulation);

        // run method under test

        this.simulationManagementService.fastForwardSimulation(simulationId, eventId);

        // check result

        verify(mockSimulation).fastForwardTo(eventId);

    }

    @Test(expected = SimulationNotFoundException.class)
    public void testFastForwardSimulationSimulationNotFound() throws Exception {

        // preset

        final int simulationId = 42;

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(null);

        // run method under test

        this.simulationManagementService.fastForwardSimulation(simulationId, 0);

    }

    @Test(expected = SimulationNotFoundException.class)
    public void testGetSimulationByIdSimulationNotFound() throws Exception {

        // preset

        final int simulationId = 42;

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(null);

        // run method under test

        // the method under test is private. In order to not use reflection, we call this method that accesses the
        // method under test
        this.simulationManagementService.startSimulation(simulationId);

    }

    @Test
    public void testGetSimulationsForScenario() {

        // preset

        final int scenarioId = 42;
        final int numberOfSimulationsForScenario = 11;
        final int numberOfOtherSimulations = 47;
        final Scenario mockWantedScenario = mock(Scenario.class);
        final Scenario mockOtherScenario = mock(Scenario.class);

        when(mockWantedScenario.getId()).thenReturn(scenarioId);
        when(mockOtherScenario.getId()).thenReturn(scenarioId - 1);

        Collection<Simulation> simulationsForScenario = new LinkedList<>();
        Collection<Simulation> simulationsNotForScenario = new LinkedList<>();

        for (int i = 0; i < numberOfSimulationsForScenario; i++) {
            Simulation mockSimulation = mock(Simulation.class);
            when(mockSimulation.getScenario()).thenReturn(mockWantedScenario);
            simulationsForScenario.add(mockSimulation);
        }
        for (int i = 0; i < numberOfOtherSimulations; i++) {
            Simulation mockSimulation = mock(Simulation.class);
            when(mockSimulation.getScenario()).thenReturn(mockOtherScenario);
            simulationsNotForScenario.add(mockSimulation);
        }

        List<Simulation> allSimulations = new LinkedList<>() {{
            addAll(simulationsNotForScenario);
            addAll(simulationsForScenario);
        }};

        when(simulationRepository.getAllSimulations()).thenReturn(allSimulations);

        // run method under test

        Collection<Simulation> result = this.simulationManagementService.getSimulationsForScenario(scenarioId);

        // check result

        assertThat(result, hasSize(numberOfSimulationsForScenario));
        assertThat(result, everyItem(isIn(simulationsForScenario)));
        assertThat(result, everyItem(not(isIn(simulationsNotForScenario))));

    }

    @Test
    public void testCollectSimulation() throws Exception {

        // preset

        final int simulationId = 42;
        final Simulation mockSimulation = mock(Simulation.class);
        final ExecutionReport mockExecutionReport = mock(ExecutionReport.class);
        final Collection<Future<ExecutionReport>> futures = new LinkedList<>();
        futures.add(new Future<ExecutionReport>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public ExecutionReport get() throws InterruptedException, ExecutionException {
                return mockExecutionReport;
            }

            @Override
            public ExecutionReport get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return mockExecutionReport;
            }
        });
        final long pointInTime = System.currentTimeMillis();

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(mockSimulation);
        when(mockSimulation.getFutures()).thenReturn(futures);
        when(mockExecutionReport.getStatus()).thenReturn(ExecutionStatus.COMPLETED_NORMAL);
        when(mockExecutionReport.getRunner()).thenReturn(new MockEventRunner(new MockEvent(new MockEventType(),
                pointInTime, "bouncyCat"), new Configuration(pointInTime, null)));
        when(mockExecutionReport.getThrowable()).thenReturn(null);
        when(mockExecutionReport.getPointInTime()).thenReturn(pointInTime);

        // run method under test

        File reportFile = this.simulationManagementService.collectSimulation(simulationId);

        // check result

        assertThat(reportFile.getName(), Matchers.startsWith("bePREPARED-simulation-report"));
        assertThat(reportFile.getName(), Matchers.endsWith(".beprepared-report"));

        BufferedReader reader = Files.newBufferedReader(reportFile.toPath());
        String line;
        LinkedList<String> lines = new LinkedList<>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        assertThat(lines.get(0).matches(" Report for simulation with id [0-9]+ "), is(true));
        assertThat(lines.get(2), Matchers.endsWith("1"));
        assertThat(lines.get(3), Matchers.endsWith("0"));
        assertThat(lines.get(4), Matchers.endsWith("1"));

    }

    @Test(expected = SimulationNotFoundException.class)
    public void testCollectSimulationSimulationNotFound() throws Exception {

        // preset

        final int simulationId = 42;

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(null);

        // run method under test

        this.simulationManagementService.collectSimulation(simulationId);

    }

    @Test
    public void testChangeSpeedValidInput() throws Exception {

        // preset

        final int simulationId = 42;
        final double newSpeed = 47.11;
        final Simulation mockSimulation = mock(Simulation.class);

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(mockSimulation);

        // run method under test

        this.simulationManagementService.changeSimulationSpeed(simulationId, newSpeed);

        // check result

        verify(mockSimulation).changeSpeed(newSpeed);

    }

    @Test(expected = SimulationNotFoundException.class)
    public void testChangeSpeedSimulationNotFound() throws Exception {

        // preset

        final int simulationId = 42;

        when(simulationRepository.getSimulationById(simulationId)).thenReturn(null);

        // run method under test

        this.simulationManagementService.changeSimulationSpeed(simulationId, 0);

    }


    @TestConfiguration
    static class SimulationManagementServiceTestConfiguration {

        @Bean
        public SimulationManagementService simulationManagementService(SimulationRepository simulationRepository,
                                                                       ConfigurationRepository configurationRepository,
                                                                       ScenarioRepository scenarioRepository) {

            return new SimulationManagementService(simulationRepository, configurationRepository, scenarioRepository);
        }

    }

}
