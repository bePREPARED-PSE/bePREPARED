package edu.kit.pse.beprepared.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.kit.pse.beprepared.json.ScenarioJson;
import edu.kit.pse.beprepared.model.Phase;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.model.ScenarioRepository;
import edu.kit.pse.beprepared.restController.exceptions.InternalServerErrorException;
import edu.kit.pse.beprepared.services.exceptions.ScenarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This class manages all scenario-related activities.
 */
@Service
public class ScenarioManagementService {

    /**
     * The repository used to access all scenarios.
     */
    private final ScenarioRepository scenarioRepository;


    /**
     * Constructor.
     * <p>
     * Constructs a new ScenarioManagementService and initializes {@link this#scenarioRepository} using the dependency
     * injection provided by Spring Boot.
     *
     * @param scenarioRepository the repository used to access all scenarios
     */
    @Autowired
    public ScenarioManagementService(final ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }


    /**
     * Creates a new {@link Scenario} with the supplied typeName.
     * <p>
     * The newly generated {@link Scenario} is added to the {@link ScenarioRepository} referenced by
     * {@link this#scenarioRepository}.
     *
     * @param name the typeName the new scenario should have
     * @return the newly generated scenario
     */
    public Scenario createScenario(final String name) {
        Phase phase = new Phase("Standard");
        Scenario scenario = new Scenario(name, phase);
        scenarioRepository.addScenario(scenario);
        return scenario;
    }

    /**
     * Get a {@link LinkedList<Scenario>} of all scenarios contained by the {@link ScenarioRepository} referenced by
     * {@link this#scenarioRepository}.
     *
     * @return a {@link Collection<Scenario>} of all scenarios
     */
    public Collection<Scenario> getAllScenarios() {
        return this.scenarioRepository.getAllScenarios();
    }

    /**
     * Get the {@link Scenario} with the supplied id from the {@link ScenarioRepository} referenced by
     * {@link this#scenarioRepository}.
     *
     * @param id the id of the requested scenario
     * @return the requested scenario or {@code null}, if there is no scenario with the supplied id
     * @throws ScenarioNotFoundException if no scenario with the supplied id can be found
     */
    public Scenario getScenario(final int id) throws ScenarioNotFoundException {
        Scenario scenario = this.scenarioRepository.getScenario(id);
        if (scenario == null) {
            throw new ScenarioNotFoundException("the scenario with the id " + id + " can not be found");
        }
        return scenario;
    }

    /**
     * Delete the {@link Scenario} with the supplied id from the {@link ScenarioRepository} referenced by
     * {@link this#scenarioRepository}.
     *
     * @param id the id of the scenario that should be deleted
     * @return the removed scenario or {@code null}, if it could not be found
     */
    public Scenario deleteScenario(final int id) {
        return this.scenarioRepository.removeScenario(id);
    }

    /**
     * Exports the scenario with the supplied id.
     * <p>
     * Creates a file with the JSON-representation of the {@link Scenario} with the supplied id.
     *
     * @param id the id of the {@link Scenario} that should be exported
     * @return a file containing the scenario
     * @throws ScenarioNotFoundException if no scenario with the supplied id can be found
     * @throws IOException               if something goes wrong while creating the scenario file
     */
    public File exportScenario(final int id) throws IOException, ScenarioNotFoundException {

        Scenario scenario = this.scenarioRepository.getScenario(id);
        if (scenario == null) {
            throw new ScenarioNotFoundException("scenario with id " + id + " can not be found!");
        }

        ScenarioJson scenarioJson = new ScenarioJson(scenario);

        // create the scenario file

        String exportName = "bePREPARED-scenario-" + scenario.getName() + "_" + scenario.getId();
        File scenarioFile = FileManagementService.getInstance().createFile(exportName + ".beprepared-scenario", true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(scenarioFile, scenarioJson);

        // fetch the attached files

        LinkedList<String> allFileKeys = new LinkedList<>();
        scenario.getAllPhases().forEach(p -> p.getAllEvents().forEach(e -> allFileKeys.addAll(e.getAttachedFileKeys())));

        Collection<File> allAttachedFiles = FileManagementService.getInstance().collectFiles(allFileKeys);

        File attachedFileZip = FileManagementService.getInstance().createZipFile("attachedFiles.zip", allAttachedFiles,
                true);

        return FileManagementService.getInstance().createZipFile(exportName + ".bp_scenario.zip", new LinkedList<>() {{
            add(scenarioFile);
            add(attachedFileZip);
        }}, true);
    }

    /**
     * Import a scenario from a file.
     *
     * @param importFile the scenario file
     * @throws IOException if something does not work while reading the input file
     */
    public Scenario importScenario(final File importFile) throws IOException {

        LinkedList<File> imports = FileManagementService.getInstance().unzipFile(importFile);
        if (imports.size() != 2) {
            throw new IOException("unknown import file format");
        }

        File scenarioFile = imports.getFirst();

        LinkedList<File> attachedFiles = FileManagementService.getInstance().unzipFile(imports.get(1));
        attachedFiles.forEach(f -> {
            try {
                FileManagementService.getInstance().storeFile(f, f.getName(), true);
            } catch (IOException e) {
                throw new InternalServerErrorException(e);
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        ScenarioJson scenarioJson = mapper.readValue(scenarioFile, ScenarioJson.class);

        Scenario scenario = new Scenario(scenarioJson);
        this.scenarioRepository.addScenario(scenario);

        return scenario;
    }

}
