'use strict';

const simulationState = {
    NO_SIMULATION: "no_simulation",
    RUNNING: "running",
    PAUSED: "paused",
    STOPPED: "stopped"
};

var currentSimulationState = simulationState.NO_SIMULATION;
var currentSimulation = -1;
var simulationStateRefresh;
const scenarioPollingRate = 1000;
let eventsToPlay = [];

/**
 * Creates and starts a new simulation if currently no simulation is running.
 * Otherwise, the current simulation will be paused/continued.
 * In both cases, the GUI will be updated.
 */
function clickStartPause() {

    if (Object.keys(configRepo.get(currentConfig).additionalProperties).length === 0) {
        util.error(new Error(i18n("invalidConfig")));
        return;
    }

    if (currentSimulationState === simulationState.NO_SIMULATION || currentSimulationState === simulationState.STOPPED) { //If no simulation is running
        console.log("Create new simulation...");

        const reply = controller.createSimulation(currentScenario, configRepo.get(currentConfig), selectedPhases, currentSpeed);

        selectedPhases.forEach(pId => {
            scenarioRepo.getAllEventsFromPhase(currentScenario, pId).forEach(e => {
                eventsToPlay.push(e);
            });
        });
        eventsToPlay.sort((a, b) => {
            return a.pointInTime - b.pointInTime;
        });

        // hide elements from non selected phases
        for (let p of scenarioRepo.getAllPhases(currentScenario)) {

            let contained = false;
            for (let sp of selectedPhases) {
                if (sp === p.phaseID) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                console.error("remove phase p");
                scenarioRepo.removePhase(currentScenario, p.phaseID);
            }
        }
        util.refresh();

        reply.then(result => {
            if (!result.ok) {
                console.log("Creation of simulation failed! See log of backend for more information.");
                throw new Error(i18n("couldNotCreateSimulation"));
            }
            result.json().then(json => {
                console.log("Got response from backend:", json);
                currentSimulation = json.simulationID;
                simulationRepo.set(currentSimulation, json);

                console.log(`Start simulation ${json.simulationID}...`);
                const reply2 = controller.startSimulation(currentSimulation);
                reply2.then(result2 => {
                    if (!result2.ok) {
                        console.log("Start of simulation failed! See log of backend for more information.");
                        throw new Error(i18n("couldNotStartSimulation"));
                    }
                    currentSimulationState = simulationState.RUNNING;
                    timeline.updateSimulationState();
                    $('#bp-stopbtn').prop('disabled', false);
                    $('#bp-startpausebtn').html(`<span class="spinner-border spinner-border-sm"></span>` + i18n("pauseSimulation"));
                    util.clickClose();
                    util.setEditingMode(false);
                    simulationStateRefresh = setInterval(synchronizeCurrentSimulation, scenarioPollingRate);
                });
            }).catch(err => util.error(err));
        }).catch(err => util.error(err));
    } else { //If already a simulation runs
        console.log(`Toggle pause simulation ${currentSimulation}...`);
        controller.togglePauseSimulation(currentSimulation).then(result => {
            if (!result.ok) {
                console.log("Toggle pause failed! See log of backend for more information.");
                throw new Error(i18n("couldNotPauseContinueSimulation"));
            }
            if (currentSimulationState === simulationState.PAUSED) {
                currentSimulationState = simulationState.RUNNING;
                timeline.updateSimulationState();
                $('#bp-startpausebtn').html(`<span class="spinner-border spinner-border-sm"></span>` + i18n("pauseSimulation"));
            } else {
                currentSimulationState = simulationState.PAUSED;
                timeline.updateSimulationState();
                $('#bp-startpausebtn').html(`<span class="spinner-grow spinner-grow-sm"></span>` + i18n("continueSimulation"));
            }
        }).catch(err => util.error(err));
    }
}

/**
 * Stops the current simulation and updates the GUI.
 */
function clickStop() {
    console.log(`Stop simulation ${currentSimulation}...`);
    controller.stopSimulation(currentSimulation).then(result => {
        if (!result.ok) {
            console.log("Stop of simulation failed! See log of backend for more information.");
            throw new Error(i18n("couldNotStopSimulation"));
        }
        currentSimulationState = simulationState.STOPPED;
        timeline.updateSimulationState();

        $('#bp-startpausebtn').html(i18n("startSimulation"));
        $('#bp-stopbtn').prop('disabled', true);
        util.setEditingMode(true);
        simulationRepo.remove(currentSimulation);
        clearTimeout(simulationStateRefresh);
        if (confirm(i18n("confirmDownloadReport"))) {
            location.href = `/simulations/${currentSimulation}/collect`;
        }
        currentSimulation = -1;
        util.refreshScenario();
    }).catch(err => util.error(err))
}

/**
 * Function responsible for synchronising the currently running simulation between the back and frontend
 */
function synchronizeCurrentSimulation() {
    console.log("Synchronizing simulations.");
    controller.getSimulations(currentScenario).then(result => {
        if (!result.ok) {
            console.log("Getting of simulation failed! See log of backend for more information.");
            throw new Error(i18n("couldNotGetSimulation"));
        }
        result.json().then(json => {
            console.log("Got response from backend:", json);
            json.forEach(sim => {
                if (sim.simulationID == currentSimulation) {
                    //found the currentSimulation. Check its state
                    if (sim.simulationState == "FINISHED") {
                        //the simulation has terminated.
                        currentSimulationState = simulationState.STOPPED;
                        timeline.updateSimulationState();

                        $('#bp-startpausebtn').html(i18n("startSimulation"));
                        $('#bp-stopbtn').prop('disabled', true);
                        util.setEditingMode(true);
                        simulationRepo.remove(currentSimulation);
                        clearTimeout(simulationStateRefresh);
                        if (confirm(i18n("confirmDownloadReport"))) {
                            location.href = `/simulations/${currentSimulation}/collect`;
                        }
                        currentSimulation = -1;
                        util.refreshScenario();
                    } else if (sim.simulationState == "RUNNING" || sim.simulationState == "PAUSED") {
                        //set the correct time on the timeline
                        timeline.currentTime = configRepo.get(currentConfig).scenarioStartTime + sim.pointInTime;
                        timeline.line.setCustomTime(timeline.currentTime, 0);
                        while (eventsToPlay.length > 0 && eventsToPlay[0].pointInTime <= sim.pointInTime) {
                            leafletMap.getMapEntryForEvent(eventsToPlay[0].eventID).setMarkerOpacity(0.5);
                            eventsToPlay.shift();
                        }
                    }
                }
            });
        });
    }).catch(err => util.error(err))
}

/**
 * Fast forwards to the event with the given ID.
 *
 * @param {number} eventID - the ID of the event
 */
function fastForward(eventID) {
    if (currentSimulation === -1) return;
    console.info(`Fast forward to: ${eventID}`);
    controller.fastForwardSimulation(currentSimulation, eventID).then(result => {
        if (!result.ok) {
        }
    });
    synchronizeCurrentSimulation();
}