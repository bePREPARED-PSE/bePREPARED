'use strict';

/**
 * Opens a prompt to manage phases.
 */
function clickPhaseManagement() {
    $("#middlePanel").removeClass("d-none");
    $('#middlePanel').load("frames/phasemanagement.html", () => {
        const cs = scenarioRepo.get(currentScenario);
        const ps = cs.phases;
        let options = "";
        ps.forEach(p => {
            options += `<option value=${p.phaseID}>${util.escape(p.name)} (${p.phaseID})</option>`;
        });
        $("#bp-phaseselect").html(options);
    });
}

/**
 * Discard the phase with the given ID from the current scenario.
 *
 * @param {Number} id - the ID of the phase to discard
 */
function discardPhase(id) {
    console.log(`Discard phase ${id}...`);
    if (!confirm(i18n("confirmDiscardPhase"))) {
        return;
    }
    const reply = controller.discardPhase(currentScenario, id);
    reply.then(result => {
        if (!result.ok) {
            console.error("Discarding of phase failed! See log of backend for more information.");
            throw new Error(i18n("couldNotDiscardPhase"));
        }
        util.refreshScenario();
        selectedPhases = selectedPhases.filter((val, idx, arr) => {
            return val !== id;
        });
        if (selectedPhases.length === 0) selectedPhases = [scenarioRepo.get(currentScenario).standardPhase];
        util.refreshSelectedPhases();
        util.clickClose();
    }).catch(err => util.error(err));
}

/**
 * Deletes the phase with the given ID from the current scenario.
 *
 * @param {Number} id - the ID of the phase to delete
 */
function deletePhase(id) {
    if (!confirm(i18n("confirmDeletePhase"))) {
        return;
    }
    console.log(`Delete phase ${id}...`);
    const reply = controller.deletePhase(currentScenario, id);
    reply.then(result => {
        if (!result.ok) {
            if (result.status === 403) {
                console.error("Tried to delete standard phase!");
                throw new Error(i18n("mustNotDeleteStandardPhase"));
            } else {
                console.error("Deletion of phase failed! See log of backend for more information.");
                throw new Error(i18n("couldNotDeletePhase"));
            }
        }
        scenarioRepo.removePhase(currentScenario, id);
        selectedPhases = selectedPhases.filter((val, idx, arr) => {
            return val !== id;
        });
        if (selectedPhases.length === 0) selectedPhases = [scenarioRepo.get(currentScenario).standardPhase];
        util.refreshSelectedPhases();
        util.clickClose();
    }).catch(err => util.error(err));
}

/**
 * Creates a new phase with the input data from the prompt.
 */
function createPhase() {
    console.log("Create new phase...")
    let name = $('#modal-phasename').val();
    name = name == "" ? "Phase" : name;
    const reply = controller.createPhase(currentScenario, name);
    reply.then(result => {
        if (!result.ok) {
            console.error("Creation of phase failed! See log of backend for more information.");
            throw new Error(i18n("couldNotCreatePhase"));
        }
        result.json().then(json => {
            console.log("Got response from backend:", json);
            scenarioRepo.addPhase(currentScenario, json);
            $("#modal-phase").modal('hide');
            refreshPhaseDropdown();
            $('#bp-phaseselect').val(json.phaseID);
        });
    });
}

/**
 * Shifts the phase with the given ID by the given time.
 *
 * @param {number} id - the ID of the phase to shift
 * @param {number} time - the time to shift by
 */
function shiftPhase(id, time) {
    console.log(`Shift phase ${id} by ${time} seconds..`);
    const reply = controller.shiftPhase(currentScenario, id, time * 1000);
    reply.then(result => {
        if (!result.ok) {
            if (result.status === 403) {
                console.error("Tried to shift before start time!");
                throw new Error(i18n("mustNotMoveBeforeStart"));
            } else {
                console.error("Shift of phase failed! See log of backend for more information.");
                throw new Error(i18n("couldNotShiftPhase"));
            }
        }
        util.refreshScenario();
    }).catch(err => util.error(err));
}

/**
 * Opens a prompt to create a new phase.
 */
function clickNewPhase() {
    $("#modal-phase").modal();
}

/**
 * Toggles the sign of the shift phase time.
 *
 * @param button - the button to toggle
 */
function togglePlusMinus(button) {
    $(button).find('i').toggleClass('fa-plus fa-minus');
    if ($(button).val() == 1) {
        $(button).val(-1);
    } else {
        $(button).val(1);
    }
}