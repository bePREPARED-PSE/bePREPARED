'use strict';

//Add event listener for file dialogs
const input = $('#bp-import-scenario');
input.change(e => {
    console.log("Send imported scenario to backend...");
    controller.importScenario(e.target.files[0]).then(result => {
        if (!result.ok) {
            console.error("Import failed! See log of backend for more information.");
            throw new Error(i18n("invalidScenarioUpload"));
        }
        console.info("Import successful");
        result.json().then(json => {
            scenarioRepo.set(json);
            currentScenario = json.scenarioID;
            selectedPhases = [json.standardPhase];
            util.refresh();
            util.refreshSelectedPhases();
            timeline.shiftView();
            leafletMap.fitView();
        });
    }).catch(err => util.error(err));
});

const input2 = $('#bp-import-config');
input2.change(e => {
    console.log("Send imported config to backend...");
    controller.importConfiguration(e.target.files[0]).then(result => {
        if (!result.ok) {
            console.error("Import failed! See log of backend for more information.");
            throw new Error(i18n("invalidConfigurationUpload"));
        }
        console.info("Import successful");
        result.json().then(json => {
            configRepo.set(json.configurationID, json);
            currentConfig = json.configurationID;
            util.refresh();
            timeline.line.moveTo(configRepo.get(currentConfig).scenarioStartTime);
        });
    }).catch(err => util.error(err));
});

/**
 * Uploads a scenario to the backend.
 */
function importScenarioFile() {
    console.log("Opened file dialog to import scenario.");
    input.click();
}

/**
 * Uploads a configuration to the backend.
 */
function importConfigFile() {
    console.log("Opened file dialog to import configuration.");
    input2.click();
}

/**
 * Deletes the current scenario and creates a blanco scenario as new one.
 */
function discardScenario() {
    const ok = confirm(i18n("confirmDiscardScenario"));
    if (!ok) {
        return;
    }

    console.log(`Delete scenario ${currentScenario}`);
    controller.deleteScenario(currentScenario).then(result => {
        if (!result.ok) {
            console.error("Deletion of scenario failed! See log of backend for more information");
            throw new Error(i18n("couldNotDeleteScenario"));
        }

        scenarioRepo.remove(currentScenario);
        controller.createScenario("_blancoScenario").then(result2 => {
            if (!result2.ok) {
                console.error("Creation of blanco scenario! See log of backend for more information");
                throw new Error(i18n("couldNotCreateEmptyScenario"));
            }
            console.log("Create blanco scenario...");
            result2.json().then(json => {
                scenarioRepo.set(json);
                currentScenario = json.scenarioID;
                selectedPhases = [json.standardPhase];
                util.refresh();
                util.refreshSelectedPhases();
                timeline.line.moveTo(configRepo.get(currentConfig).scenarioStartTime);
            })
        }).catch(err => util.error(err));
    }).catch(err => util.error(err));
}

/**
 * Downloads the current scenario from the backend.
 */
function exportScenarioFile() {
    console.log(`Export scenario ${currentScenario}...`);
    controller.exportScenario(currentScenario);
}

/**
 * Downloads the current configuration from the backend.
 */
function exportConfigFile() {
    console.log(`Export config ${currentConfig}...`);
    controller.exportConfiguration(currentConfig);
}

/**
 * Opens a prompt where the user can select phases to simulate.
 */
function clickSelectPhases() {
    $('#modal-placeholder').load("frames/selectphases.html", () => {
        $('#modal-select-phase').modal();
        let tbody = "";
        scenarioRepo.getAllPhases(currentScenario).forEach(p => tbody += util.createTableLine(p));
        document.getElementById("bp-table-body").innerHTML = tbody;
        //Check all current selected phases:
        selectedPhases.forEach(p => $(`#${p}`).prop('checked', true));
    });
}

/**
 * Validates the selected checkboxes and sets the new ones.
 */
function phasesSelected() {
    let selected = [];

    //Iterate over all checkboxes and save the checked ones
    document.getElementsByName("bp-p").forEach(c => {
        if (c.checked) {
            console.log(`Phase ${c.value} is checked`)
            selected.push(parseInt(c.value));
        }
    });

    //Check if at least one phase is selected
    if (selected.length === 0) {
        $('#noPhaseSelected').show();
        return;
    }

    //Save new selected phases and update GUI
    console.log("Setting selected phases:", selected);
    selectedPhases = selected;
    $('#modal-select-phase').modal('hide');
    util.refreshSelectedPhases();
    util.refresh();
}

/**
 * selects or deselects all checkboxes with the ID bp-p
 * @param source - the source checkbox (used to select or deselect all)
 */
function toggleCheckBoxes(source) {
    const checkboxes = document.getElementsByName("bp-p");
    checkboxes.forEach(c => c.checked = source.checked);
}