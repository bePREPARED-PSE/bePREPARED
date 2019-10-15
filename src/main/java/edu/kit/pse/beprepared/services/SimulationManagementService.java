package edu.kit.pse.beprepared.services;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.kit.pse.beprepared.json.ExecutionReportJson;
import edu.kit.pse.beprepared.model.*;
import edu.kit.pse.beprepared.services.exceptions.ConfigurationNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.PhaseNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import edu.kit.pse.beprepared.services.exceptions.SimulationNotFoundException;
import edu.kit.pse.beprepared.simulation.ExecutionReport;
import edu.kit.pse.beprepared.simulation.ExecutionStatus;
import edu.kit.pse.beprepared.simulation.Simulation;
import edu.kit.pse.beprepared.simulation.SimulationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * This class manages all {@link Simulation}-related activities.
 */
@Service
public class SimulationManagementService {
    /**
     * The repository used to access all {@link Simulation}s.
     */
    private final SimulationRepository simulationRepository;
    /**
     * The repository used to access all {@link Configuration}s.
     */
    private final ConfigurationRepository configurationRepository;
    /**
     * The repository used to access all {@link Scenario}s.
     */
    private final ScenarioRepository scenarioRepository;

    /**
     * Constructor.
     * <p>
     * Constructs a new SimulationManagementService and initializes {@link this#simulationRepository}
     * using the dependency injection provided by Spring Boot.
     *
     * @param simulationRepository the repository used to access all simulations
     */
    @Autowired
    public SimulationManagementService(final SimulationRepository simulationRepository,
                                       final ConfigurationRepository configurationRepository,
                                       final ScenarioRepository scenarioRepository) {
        this.simulationRepository = simulationRepository;
        this.configurationRepository = configurationRepository;
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Create a new {@link Simulation} and add it to the repository.
     *
     * @param scenarioID the {@link Scenario} that is simulated
     * @param phaseIds   the phases that should be simulated
     * @param configId   the {@link Configuration} of the simulation
     * @param speed      the initial speed of the simulation
     * @return the newly created simulation
     * @throws ScenarioNotFoundException      if no scenario with the supplied id can be found
     * @throws ConfigurationNotFoundException if no configuration with the supplied id can be found
     * @throws PhaseNotFoundException         if no phase with the supplied id can be found
     */
    public Simulation createSimulation(final int scenarioID, final int configId, final List<Integer> phaseIds,
                                       final double speed)
            throws ScenarioNotFoundException, ConfigurationNotFoundException, PhaseNotFoundException {
        Scenario scenario = scenarioRepository.getScenario(scenarioID);
        if (scenario == null) {
            throw new ScenarioNotFoundException("Invalid scenarioID: " + scenarioID);
        }
        Configuration configuration = configurationRepository.getConfigurationById(configId);
        if (configuration == null) {
            throw new ConfigurationNotFoundException("Invalid configID: " + configId);
        }

        if (phaseIds == null || phaseIds.size() == 0) {
            throw new PhaseNotFoundException("phaseIds can't be null and must contain at least one item!");
        }

        //get all selected phases
        HashSet<Phase> phases = new HashSet<>();
        for (Integer id : phaseIds) {
            Phase p = scenario.getPhaseById(id);
            if (p == null) {
                throw new PhaseNotFoundException("Invalid phaseID: " + id);
            }
            phases.add(p);
        }

        //now, create the simulation and return it
        return simulationRepository.createSimulation(scenario, phases, configuration, speed);
    }

    /**
     * Get all {@link Simulation}s in this repository
     *
     * @return all simulations in this repository
     */
    public Collection<Simulation> getAllSimulations() {
        return simulationRepository.getAllSimulations();
    }

    /**
     * Start a {@link Simulation}.
     *
     * @param simulationId the simulation to start
     * @return the simulation state
     * @throws SimulationNotFoundException if no simulation with the supplied id can be found
     */
    public SimulationState startSimulation(int simulationId) throws SimulationNotFoundException {
        return this.getSimulationById(simulationId).start();
    }

    /**
     * Toggle the {@link Simulation} between the states paused and running. Does nothing if the simulation
     * hasn't been started before of it is already finished or terminated.
     *
     * @param simulationId the simulation to pause/unpause
     * @return the state the simulation is in
     * @throws SimulationNotFoundException if no simulation with the supplied id can be found
     */
    public SimulationState pauseSimulation(int simulationId) throws SimulationNotFoundException {
        return this.getSimulationById(simulationId).togglePause();
    }

    /**
     * Stop a {@link Simulation}
     *
     * @param simulationId the simulation to stop
     * @return the simulation state
     * @throws SimulationNotFoundException if no simulation with the supplied id can be found
     */
    public SimulationState stopSimulation(int simulationId) throws SimulationNotFoundException {
        return this.getSimulationById(simulationId).stop();
    }

    /**
     * Fast forwards the supplied simulation to a specified event.
     *
     * @param simulationId the id of the simulation that should be fast forwarded
     * @param eventId      the id of the event the simulation should be fast forwarded to
     * @return the simulation state
     * @throws SimulationNotFoundException if no simulation with the supplied id exists
     * @throws IllegalArgumentException    if no event belonging to the supplied simulation has the supplied id
     */
    public SimulationState fastForwardSimulation(int simulationId, long eventId) throws SimulationNotFoundException {
        Simulation s = this.simulationRepository.getSimulationById(simulationId);
        if (s == null) {
            throw new SimulationNotFoundException("there is no simualtion with the id " + simulationId);
        }
        return s.fastForwardTo(eventId);
    }

    /**
     * Get a {@link Simulation} by its id.
     *
     * @param simulationId the simulation to get
     * @return the simulation
     * @throws SimulationNotFoundException if no simulation with the supplied id can be found
     */
    private Simulation getSimulationById(int simulationId) throws SimulationNotFoundException {
        Simulation s = simulationRepository.getSimulationById(simulationId);
        if (s == null) {
            throw new SimulationNotFoundException("Invalid simulationId");
        }
        return s;
    }

    /**
     * Getter for a {@link Collection<Simulation>} of all simulations that belong to the supplied {@link Scenario}.
     *
     * @param scenarioId the id of the scenario the simulations belong to
     * @return a {@link Collection<Simulation>} of all simulations that belong to the supplied scenario
     */
    public Collection<Simulation> getSimulationsForScenario(final int scenarioId) {

        return this.getAllSimulations().stream().filter(s -> s.getScenario().getId() == scenarioId)
                .collect(Collectors.toList());

    }

    /**
     * Generates a report for a specific simulation and deletes it from the repository.
     *
     * @param simulationId the id of the simulation
     * @return a temp file containing the report
     * @throws SimulationNotFoundException if the simulation with the supplied id can not be found
     * @throws IOException                 if something goes wrong while writing the report to the file
     */
    public File collectSimulation(final int simulationId) throws SimulationNotFoundException, IOException {

        Simulation simulation = this.simulationRepository.getSimulationById(simulationId);
        if (simulation == null) {
            throw new SimulationNotFoundException("can not find simulation with id " + simulationId);
        }

        Collection<Future<ExecutionReport>> futures = simulation.getFutures();

        File reportFile = Files.createTempFile("bePREPARED-simulation-report", ".beprepared-report").toFile();
        reportFile.deleteOnExit();

        FileOutputStream reportOutputStream = new FileOutputStream(reportFile);

        PrintWriter writer = new PrintWriter(reportOutputStream);
        String header = " Report for simulation with id " + simulationId + " ";
        writer.println(header);
        for (int i = 0; i < header.length(); i++) {
            writer.print('=');
        }
        writer.println();

        long normal = futures.stream().map(executionReportFuture -> {
            try {
                return executionReportFuture.get();
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).map(r -> r.getStatus() == ExecutionStatus.COMPLETED_NORMAL).count();
        int total = futures.size();
        writer.printf("Completed normal:\t%d\n", normal);
        writer.printf("Completed exceptionally:\t%d\n", total - normal);
        writer.printf("Total number of runners:\t%d\n", total);

        writer.flush();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        JsonGenerator generator = mapper.getFactory().createGenerator(reportOutputStream);

        mapper.writeValue(generator, futures.stream().map(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException ignored) {
                return null;
            }
        }).map(report -> report != null ? new ExecutionReportJson(report) : null).collect(Collectors.toList()));

        writer.close();

        return reportFile;

    }

    /**
     * Changes the current speed of a {@link Simulation}.
     *
     * @param simulationId the id of the simulation
     * @param speed        the speed
     * @throws SimulationNotFoundException if no simulation with the supplied id exists
     */
    public void changeSimulationSpeed(int simulationId, double speed) throws SimulationNotFoundException {

        Simulation s = this.simulationRepository.getSimulationById(simulationId);

        if (s == null) {
            throw new SimulationNotFoundException("can not find simulation with id " + simulationId);
        }

        s.changeSpeed(speed);
    }

}
