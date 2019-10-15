package edu.kit.pse.beprepared.json;

import edu.kit.pse.beprepared.model.Phase;
import edu.kit.pse.beprepared.simulation.Simulation;
import edu.kit.pse.beprepared.simulation.SimulationState;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a JSON object of type Simulation.
 */
public class SimulationJson {

    /*
    Properties of the JSON object:
     */
    private int simulationID;
    private SimulationState simulationState;
    private ConfigurationJson configuration;
    private List<Integer> selectedPhaseIDs;
    private long pointInTime;
    private double speed;

    /**
     * Instantiates a new {@link SimulationJson}.
     *
     * @param simulationID     the simulation id
     * @param simulationState  the simulation state
     * @param configuration    the configuration
     * @param selectedPhaseIDs the selected phase ids
     * @param pointInTime      the point in time the simulation is at
     * @param speed            the current simulation speed
     */
    public SimulationJson(int simulationID, SimulationState simulationState, double speed,
                          ConfigurationJson configuration, List<Integer> selectedPhaseIDs, long pointInTime) {
        this.simulationID = simulationID;
        this.simulationState = simulationState;
        this.configuration = configuration;
        this.selectedPhaseIDs = selectedPhaseIDs;
        this.pointInTime = pointInTime;
        this.speed = speed;
    }

    public SimulationJson(Simulation simulation) {
        this.simulationID = simulation.getId();
        this.simulationState = simulation.getSimulationState();
        this.configuration = new ConfigurationJson(simulation.getConfiguration());
        this.selectedPhaseIDs = simulation.getSelectedPhases().stream().map(Phase::getId)
                .collect(Collectors.toList());
        this.pointInTime = simulation.getPointInTime();
        this.speed = simulation.getCurrentSpeed();
    }

    /**
     * Getter for {@link this#pointInTime}
     *
     * @return {@link this#pointInTime}
     */
    public long getPointInTime() {
        return pointInTime;
    }

    /**
     * Setter for {@link this#pointInTime}
     *
     * @param pointInTime the new value for  {@link this#pointInTime}
     */
    public void setPointInTime(long pointInTime) {
        this.pointInTime = pointInTime;
    }

    /**
     * Getter for {@link this#simulationID}.
     *
     * @return the simulation id
     */
    public int getSimulationID() {
        return simulationID;
    }

    /**
     * Setter for {@link this#simulationID}.
     *
     * @param simulationID the simulation id
     */
    public void setSimulationID(int simulationID) {
        this.simulationID = simulationID;
    }

    /**
     * Returns the value of {@link this#getSimulationID()}
     *
     * @return the simulation state
     */
    public SimulationState getSimulationState() {
        return simulationState;
    }

    /**
     * Setter for {@link this#simulationState}.
     *
     * @param simulationState the simulation state
     */
    public void setSimulationState(SimulationState simulationState) {
        this.simulationState = simulationState;
    }

    /**
     * Getter for {@link this#configuration}.
     *
     * @return the configuration
     */
    public ConfigurationJson getConfiguration() {
        return configuration;
    }

    /**
     * Setter for {@link this#configuration}.
     *
     * @param configuration the configuration
     */
    public void setConfiguration(ConfigurationJson configuration) {
        this.configuration = configuration;
    }

    /**
     * Getter for {@link this#selectedPhaseIDs}.
     *
     * @return the selected phase IDs
     */
    public List<Integer> getSelectedPhaseIDs() {
        return selectedPhaseIDs;
    }

    /**
     * Setter for {@link this#selectedPhaseIDs}.
     *
     * @param selectedPhaseIDs the selected phase IDs
     */
    public void setSelectedPhaseIDs(List<Integer> selectedPhaseIDs) {
        this.selectedPhaseIDs = selectedPhaseIDs;
    }

    /**
     * Getter for {@link this#speed}.
     *
     * @return the value of {@link this#speed}
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Setter for {@link this#speed}.
     *
     * @param speed the new value for {@link this#speed}
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Getter for a {@link String} representation of this object.
     *
     * @return a {@link String} representation of this object
     */
    @Override
    public String toString() {
        return "SimulationJson{" +
                "simulationID=" + simulationID +
                ", simulationState=" + simulationState +
                ", configuration=" + configuration +
                ", selectedPhaseIDs=" + selectedPhaseIDs +
                ", pointInTime=" + pointInTime +
                '}';
    }
}
