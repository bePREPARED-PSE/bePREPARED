'use strict';

let _toEdit = -1;
let _toUpload = null;

function onFileBrowse(e, key) {
    _toUpload = e.files[0];
    $(`#label${key}`).html(_toUpload.name);
}

/**
 * Replaces the 'middlePanel's content with the menu to select an event type.
 */
function clickCreateEvent() {
    $("#middlePanel").removeClass("d-none").load("frames/eventtypes.html", () => {
        let buttons = "";
        eventTypeRepo.getAll().forEach(et => {
            //Show Buttons:
            buttons += util.eventTypeButton(et);
        });
        document.getElementById("eventTypeButtons").innerHTML = buttons;
    });
}

/**
 * Replaces the 'middlePanel's content with the menu to edit an event.
 */
function clickEditEvent(eventID) {
    if (currentSimulationState !== simulationState.RUNNING && currentSimulationState !== simulationState.PAUSED) {
        const phaseID = scenarioRepo.getPhaseIDForEventID(currentScenario, eventID);
        if (phaseID === -1) {
            util.error(new Error(i18n("eventBelongsToNoPhase")));
            return;
        }

        //Mark selected event in table, timeline and map
        timeline.timelineUnmarkEvents();
        timeline.timelineMarkEvent(eventID);
        table.tableMarkEvent(eventID);
        leafletMap.mapMarkEvent(eventID);
        leafletMap.leavePickingMode();

        const event = scenarioRepo.getEvent(currentScenario, phaseID, eventID);
        const eventType = event.type;
        selectedEt = eventType;
        console.log(`Edit event with ID: ${eventID}`);
        $('#middlePanel').removeClass("d-none").load("frames/editevent.html", () => {
            $("#bp-configevent").text(i18n("event %n edit", eventID) + ":");
            _toEdit = eventID;
            const et = eventTypeRepo.get(eventType);
            let fields = "";
            //Create input fields:
            et.inputDescriptor.forEach(inpd => {
                fields += util.inputDescriptorToHtml(inpd, false, eventType);
            });

            //Set point in time of event
            $('#datetime').val(moment(configRepo.get(currentConfig).scenarioStartTime + event.pointInTime).format("L LTS"));
            //Display input fields
            document.getElementById("bp-specevent").innerHTML = fields;

            //Fill fields with value from event
            const data = event.data;
            et.inputDescriptor.forEach(d => {
                switch (d.type) {
                    case 'GeolocationInputField' :
                        let location = data.locations[d.key].coordinates;
                        $(`#${d.key}lon`).val(location[0]);
                        $(`#${d.key}lat`).val(location[1]);
                        break;
                    case 'MultipleChoiceInputField' :
                        const arr = data[d.key];
                        arr.forEach(v => {
                            $(`#${d.key + v}`).prop('checked', true);
                        });
                        break;
                    case 'FileInputField' :
                        $(`#label${d.key}`).html(data.files[d.key]);
                        $(`#${d.key}`).removeAttr('required');
                        break;
                    default:
                        $(`#${d.key}`).val(data[d.key]);
                }
            });

            //Load and display scenario start time
            const m = moment(configRepo.get(currentConfig).scenarioStartTime);
            document.getElementById("bp-starttime").innerText = i18n("startPointInTime")
                + `: ${m.format('L')} ${m.format('LTS')}`;
            //Refresh phase dropdown
            refreshPhaseDropdown();
            $('#bp-phaseselect').val(phaseID);
        })
    }
}

/**
 * Replaces the 'middlePanel's content with menu to configure an event.
 * Therefore, the input descriptors of the given event type will be parsed.
 *
 * @param {string} name - name of the event type
 */
function specifyEvent(name) {
    console.log(`Selected event type: ${name}`);
    selectedEt = name;
    $("#middlePanel").removeClass("d-none").load("frames/specifyevent.html", () => {
        document.getElementById("bp-configevent").innerText = `${i18n(`${name}.type.name`)}-` + i18n("configEvent") + `:`;
        const et = eventTypeRepo.get(name);
        let fields = "";
        et.inputDescriptor.forEach(inpd => {
            fields += util.inputDescriptorToHtml(inpd, false, name);
        });
        document.getElementById("bp-specevent").innerHTML = fields;
        const m = moment(configRepo.get(currentConfig).scenarioStartTime);
        document.getElementById("bp-starttime").innerText = i18n("startPointInTime")
            + `: ${m.format('L')} ${m.format('LTS')}`;
        refreshPhaseDropdown();
    });
}

/**
 * Refreshes the data displayed of the phase select dropdown.
 */
function refreshPhaseDropdown() {
    const cs = scenarioRepo.get(currentScenario);
    const ps = cs.phases;
    let options = "";
    ps.forEach(p => {
        options += `<option value=${p.phaseID}>${util.escape(p.name)} (${p.phaseID})</option>`;
    });
    $("#bp-phaseselect").html(options);
}

/**
 * Gets and parses the input from all input fields, validates them, and finally (if the input was valid) sends a
 * request to create a new event to the
 *
 * @param {Boolean} edit - indicates if the event should be created or edited
 */
function submitEvent(edit) {
    console.log(`Event submitted, check for validity...`);
    //Check validity of the input
    const form = document.getElementById("form");
    const valid = form.checkValidity();
    form.classList.add('was-validated');

    if (!valid) {
        console.error(`Input data were not valid!`);
        return; //If not valid, return
    }

    const datetime = $('#datetimepicker1').datetimepicker('viewDate');
    let phase = parseInt($('#bp-phaseselect').val());

    const et = eventTypeRepo.get(selectedEt);
    let data = parseEventData(et, false);

    //--------------------------------------------

    console.log("Parsed data:", data);
    //Get phase and time
    console.log(`Selected phase: ${phase}`);
    const pointInTime = datetime.format('x') - configRepo.get(currentConfig).scenarioStartTime;
    console.log(`Event time: ${datetime.format('DD.MM.YYYY HH:mm:ss')} (${pointInTime})`);

    if (!edit) {
        //Send request to backend
        console.log("Create new event...");
        const reply = controller.createEvent(currentScenario, phase, selectedEt, pointInTime, data);
        reply.then(result => {
            if (!result.ok) {
                console.error("Event creation failed! See log of backend for more information.");
                throw new Error(i18n("couldNotCreateEvent"));
            }
            result.json().then(json => {
                console.log("Got response from backend:", json);
                scenarioRepo.addEvent(currentScenario, phase, json);
                util.clickClose();
            });
        }).catch(err => util.error(err));
    } else {
        //Edit event
        const oldPhase = scenarioRepo.getPhaseIDForEventID(currentScenario, _toEdit);
        const event = scenarioRepo.getEvent(currentScenario, oldPhase, _toEdit);
        event.data = data;
        event.pointInTime = pointInTime;

        const sendEditRequest = function () {
            //Send request to backend
            console.log("Edit event...");
            const reply = controller.editEvent(currentScenario, phase, event);
            reply.then(result => {
                if (!result.ok) {
                    console.error("Event creation failed! See log of backend for more information.");
                    throw new Error(i18n("couldNotCreateEvent"));
                }
                scenarioRepo.removeEvent(currentScenario, oldPhase, _toEdit);
                scenarioRepo.addEvent(currentScenario, phase, event);

                util.clickClose();
            }).catch(err => util.error(err));
        };

        if (oldPhase != phase) {
            console.log(`Reassign event #${event.eventID} of old phase ${oldPhase} to new phase ${phase}...`);
            const reply = controller.reassignToPhase(currentScenario, oldPhase, phase, event.eventID);
            reply.then(result => {
                if (!result.ok) {
                    console.error("Reassigning of phase failed! See log of backend for more information.");
                    throw new Error(i18n("couldNotChangePhase"));
                }
                sendEditRequest();
            }).catch(err => util.error(err));
        } else {
            sendEditRequest();
        }
    }
}

/**
 * Creates the events data-object with the values taken from the corresponding input form.
 *
 * @param et the event type the new event belongs to
 */
function parseEventData(et, forGenerator) {

    let data = {};
    let location = {};
    let files = {};

    et.inputDescriptor.forEach(d => {
        switch (d.type) {
            case 'GeolocationInputField' :
                if (forGenerator) {
                    location[d.key] = {
                        type: "Point",
                        coordinates: [$(`#${d.key}GenLon`).val(), $(`#${d.key}GenLat`).val()]
                    };
                } else {
                    location[d.key] = {
                        type: "Point",
                        coordinates: [parseFloat($(`#${d.key}lon`).val()), parseFloat($(`#${d.key}lat`).val())]
                    };
                }
                break;
            case 'MultipleChoiceInputField' :
                const boxes = document.getElementsByName(d.key);
                const sel = [];
                boxes.forEach(b => {
                    if (b.checked) sel.push(parseInt(b.value));
                });
                data[d.key] = sel;
                break;
            case 'NumericInputField' :
                if (forGenerator) {
                    data[d.key] = $(`#${d.key}`).val();
                } else {
                    data[d.key] = parseFloat($(`#${d.key}`).val());
                }
                break;
            case 'FileInputField' :
                if (_toUpload == null) {
                    files[d.key] = $(`#label${d.key}`).html();
                    return;
                }
                const name = `${moment.utc().format('x')}-${_toUpload.name}`;
                files[d.key] = name;
                controller.uploadFile(_toUpload, name);
                break;
            default:
                data[d.key] = $(`#${d.key}`).val();
        }
    });

    data['locations'] = location;
    data['files'] = files;

    return data;
}

/**
 * Called to delete the event which is currently getting edited.
 */
function deleteEvent() {
    console.log(`Delete Event ${_toEdit}`);
    const phaseID = scenarioRepo.getPhaseIDForEventID(currentScenario, _toEdit);
    const reply = controller.deleteEvent(currentScenario, phaseID, _toEdit);
    reply.then(result => {
        if (!result.ok) {
            console.error("Event deletion failed! See log of backend for more information.");
            throw new Error(i18n("couldNotDeleteEvent"));
        }
        scenarioRepo.removeEvent(currentScenario, phaseID, _toEdit);
        util.clickClose();
    }).catch(err => util.error(err))
}
