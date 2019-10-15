'use strict';

function clickGenerator() {
    $('#modal-placeholder').load('frames/generator.html', () => {
        const cs = scenarioRepo.get(currentScenario);
        const ps = cs.phases;
        let options = "";
        ps.forEach(p => {
            options += `<option value=${p.phaseID}>${util.escape(p.name)} (${p.phaseID})</option>`;
        });
        $("#generator-phaseselect").html(options);
        $("#modal-generator").modal();
        refreshGeneratorDropdown();
        onGeneratorChanged();
    });
}

function refreshGeneratorDropdown() {
    const eventTypes = eventTypeRepo.getAll();
    let typeOptions = "";
    eventTypes.forEach(et => {
        typeOptions += `<option value="${et.name}">${i18n(`${et.name}.type.name`)}</option>`;
    });
    $("#bp-generatorselect").html(typeOptions);
}

function onGeneratorChanged() {
    let selectedType = $("#bp-generatorselect").val();
    let fields = "";
    let eventType = eventTypeRepo.get(selectedType);
    eventType.inputDescriptor.forEach(d => {
        fields += util.inputDescriptorToHtml(d, true, selectedType);
    });
    $("#bp-generatorInput").html(fields);
}

function generateEvents() {

    console.log(`Clicked generator confirm, check for validity...`);
    //Check validity of the input
    const form = document.getElementById("eventGeneratorForm");
    const valid = form.checkValidity();
    form.classList.add('was-validated');

    if (!valid) {
        console.error(`Input data were not valid!`);
        return; //If not valid, return
    }

    let eventType = eventTypeRepo.get($("#bp-generatorselect").val());
    let eventCount = $("#eventCount").val();
    let pitFunction = $("#eventTimeFunction").val();
    let phase = $("#generator-phaseselect").val();
    let data = parseEventData(eventType, true);

    let eventSeries = new EventSeries(eventCount, eventType.name, pitFunction, data);

    controller.createEventSeries(currentScenario, phase, eventSeries).then(res => {
        if (!res.ok) {
            throw new Error(i18n("anErrorOccurred"));
        }
        util.refreshScenario();
        $("#modal-generator").modal('hide');
    }).catch(err => {
        util.error(err);
    });

}