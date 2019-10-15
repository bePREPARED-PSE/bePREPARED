'use strict';

function initialize() {
//Create slider
    $('#slider').slider({
        min: 0.1,
        max: 10,
        step: 0.1,
        value: currentSpeed,
        scale: 'logarithmic',
    }).on('change', function (val) {
        currentSpeed = parseFloat(val.value.newValue);
        $('#bouncy-cat').html(currentSpeed);
        if (currentSimulationState === simulationState.RUNNING) {
            controller.updateSpeed(currentSimulation, currentSpeed);
        }
    });

    //Set Language
    moment.locale(navigator.language);

    //Load config descriptor ------------------------------------------------------------------------------------------
    console.log("Load config descriptors...");
    controller.getConfigDescriptors().then(resp => {
        return resp.json();
    }).then(json => {
        console.log("Got response from backend:", json);
        configPropertyDescriptor = json;
        //Load Event Types --------------------------------------------------------------------------------------------
        console.log("Load event types...");
        return controller.getEventTypes();
    }).then(result => {
        if (!result.ok) {
            throw new Error("Could not fetch event types!");
        }
        return result.json();
    }).then(json => {
        console.log("Got response from backend:", json);
        json.forEach(et => {
            //Add to database:
            et.name = util.escape(et.name);
            eventTypeRepo.set(et.name, et);
        });
        //Load Scenarios ----------------------------------------------------------------------------------------------
        console.log("Load scenarios...");
        return controller.getScenarios();
    }).then(result => {
        if (!result.ok) {
            throw new Error("Could not fetch scenarios!");
        }
        return result.json();
    }).then(async json => {
        console.log("Got response from backend:", json);
        if (json.length === 0) {
            await controller.createScenario("Scenario" + moment().format('X'))
                .then(async resp => await resp.json().then(s => {
                    scenarioRepo.set(s);
                    currentScenario = s.scenarioID;
                    selectedPhases = [s.standardPhase];
                }));
        } else {
            json.forEach(s => {
                scenarioRepo.set(s);
                currentScenario = s.scenarioID;
            });
            selectedPhases = [scenarioRepo.get(currentScenario).standardPhase];
        }
        //Load Configurations -----------------------------------------------------------------------------------------
        console.log("Load configurations...");
        return controller.getConfigurations();
    }).then(result => {
        if (!result.ok) {
            throw new Error("Could not fetch configurations!");
        }
        return result.json();
    }).then(async json => {
        console.log("Got response from backend:", json);
        if (json.length === 0) {
            await controller.createConfiguration(moment().format('X') * 1000, {})
                .then(async resp => await resp.json().then(c => {
                    currentConfig = c.configurationID;
                    configRepo.set(currentConfig, c);
                }))
        } else {
            json.forEach(c => {
                configRepo.set(c.configurationID, c);
                currentConfig = c.configurationID;
            });
        }
        //Load Simulations --------------------------------------------------------------------------------------------
        console.log("Load simulations...");
        return controller.getSimulations(currentScenario);
    }).then(result => {
        if (!result.ok) {
            throw new Error("Could not fetch simulations!");
        }
        return result.json();
    }).then(json => {
        console.log("Got response from backend:", json);
        json.forEach(sim => {
            if ((sim.simulationState == "RUNNING" || sim.simulationState == "PAUSED") && currentSimulationState === simulationState.NO_SIMULATION) {
                //found a running simulation
                currentSimulation = sim.simulationID;
                simulationRepo.set(currentSimulation, sim);
                currentSpeed = sim.speed;
                console.log(`Running simulation ${sim.simulationID}...`);
                $('#bp-stopbtn').prop('disabled', false);
                if (sim.simulationState == "RUNNING") {
                    currentSimulationState = simulationState.RUNNING;
                    timeline.updateSimulationState();
                    $('#bp-startpausebtn').html(`<span class="spinner-border spinner-border-sm"></span>`
                        + i18n("pauseSimulation"));
                } else {
                    currentSimulationState = simulationState.PAUSED;
                    timeline.updateSimulationState();
                    $('#bp-startpausebtn').html(`<span class="spinner-grow spinner-grow-sm"></span>`
                        + i18n("continueSimulation"));
                }
                simulationStateRefresh = setInterval(synchronizeCurrentSimulation, scenarioPollingRate);
                util.setEditingMode(false);
            }
        });
        //Update GUI --------------------------------------------------------------------------------------------------
    }).then(() => {
        timeline.line.moveTo(configRepo.get(currentConfig).scenarioStartTime);
        timeline.shiftView();
        leafletMap.fitView();
        util.refreshSelectedPhases();
        util.clickClose();
    }).catch(err => {
        console.error(err.message);
        util.error(new Error(i18n("couldNotInit")));
    });

}