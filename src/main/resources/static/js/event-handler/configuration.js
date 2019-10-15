'use strict';

/**
 * Opens a prompt to create and edit configs.
 */
function clickConfig() {
    $('#modal-placeholder').load('frames/configuration.html', () => {
        $("#modal-config").modal();
        refreshConfigDropdown();
        $('#bp-configselect').val(currentConfig);
        onConfigChange();
    });
}

/**
 * Creates a new config with the input data from the config prompt.
 */
function createConfig() {
    console.log("Create/Edit config prompt was sumbitted, check for validity...");
    //Check validity of the input
    const form = document.getElementById("modal-config-form");
    const valid = form.checkValidity();
    form.classList.add('was-validated');

    if (!valid) {
        console.error("Input data were not valid!");
        return; //If not valid, return
    }

    let url = $('#modal-configURL').val();
    let bus = $('#modal-configBUS').val();

    let time = ((parseInt($('#datetimepicker2').datetimepicker('viewDate').format('x'))/1000) >> 0) * 1000;

    const selectedConfig = parseInt($('#bp-configselect').val());

    //Loads values: -------------------------
    let data = {};

    configPropertyDescriptor.forEach(d => {
        switch (d.type) {
            case 'GeolocationInputField' :
                throw new Error("GeolocationInputField not supported in config dialog!");
            case 'MultipleChoiceInputField' :
                const boxes = document.getElementsByName(d.key);
                const sel = [];
                boxes.forEach(b => {
                    if (b.checked) sel.push(parseInt(b.value));
                });
                data[d.key] = sel;
                break;
            case 'NumericInputField' :
                data[d.key] = parseFloat($(`#${d.key}`).val());
                break;
            case 'FileInputField' :
                throw new Error("FileInputField not supported in config dialog!");
            default:
                data[d.key] = $(`#${d.key}`).val();
        }
    });


    //---------------------------------------

    console.log("Input data were valid:");
    console.log(`URL: ${url} -- BUS: ${bus} -- time: ${time} -- selectedConfig: ${selectedConfig}`);

    if (selectedConfig === -1) {
        console.log("Create a new config...");
        const reply = controller.createConfiguration(time, data);
        reply.then(result => {
            if (!result.ok) {
                console.error("Creation of new config failed! See log of backend for more information.");
                throw new Error(i18n("couldNotCreateConfig"));
            }
            result.json().then(json => {
                console.log("Got response from backend:", json);
                configRepo.set(json.configurationID, json);
                $("#modal-config").modal('hide');
                changeConfig(json.configurationID);
            })
        }).catch(err => util.error(err));
    } else {
        console.log(`Update existing config...`);
        const config = new Configuration(selectedConfig, time, data);
        const reply = controller.updateConfiguration(config);
        reply.then(result => {
            if (!result.ok) {
                console.error("Updating config failed! See log of backend for more information!");
                throw new Error("couldNotUpdateConfig");
            }
            configRepo.set(config.configurationID, config);
            $("#modal-config").modal('hide');
            changeConfig(selectedConfig);
        }).catch(err => util.error(err));
    }
}

/**
 * Refreshes the data of the dropdown for config selection.
 */
function refreshConfigDropdown() {
    const configs = configRepo.getAll();
    let options = "";
    configs.forEach(c => {
        options += `<option value=${c.configurationID}>` + i18n("configuration") + ` ${c.configurationID}</option>`;
    });
    options += `<option value=-1>` + i18n("newConfiguration") + `...</option>`;
    $("#bp-configselect").html(options);
}

/**
 * Changes the values of the input fields when another config is selected.
 */
function onConfigChange() {
    $('#noConfig').hide();
    const selected = parseInt($('#bp-configselect').val());

    //Load input fields:
    let fields = "";
    //Create input fields:
    configPropertyDescriptor.forEach(inpd => {
        fields += util.inputDescriptorToHtml(inpd, false, "config");
    });
    $('#bp-config').html(fields);

    if (selected !== -1) {
        $('#datetime2').val(moment(configRepo.get(selected).scenarioStartTime).format("L LTS"));
        $('#modal-config-delete').prop('disabled', false);

        //Fill fields with value from event
        const data = configRepo.get(selected).additionalProperties;
        configPropertyDescriptor.forEach(d => {
            switch (d.type) {
                case 'GeolocationInputField' :
                    throw new Error("GeolocationInputField not supported in config dialog!");
                case 'MultipleChoiceInputField' :
                    const arr = data[d.key];
                    arr.forEach(v => {
                        $(`#${d.key + v}`).prop('checked', true);
                    });
                    break;
                case 'FileInputField' :
                    throw new Error("FileInputField not supported in config dialog");
                default:
                    $(`#${d.key}`).val(data[d.key]);
            }
        });

    } else {
        $('#datetime2').val("");
        $('#modal-config-delete').prop('disabled', true);
    }
}

/**
 * Changes the current config and updates the GUI.
 *
 * @param {Number} newConfig - ID of the new current config.
 */
function changeConfig(newConfig) {
    currentConfig = newConfig;
    console.log(`Config was changed to config with ID: ${currentConfig}`, configRepo.get(currentConfig));
    util.clickClose();
    timeline.shiftView();
}

/**
 * Deletes the selected config.
 */
function deleteConfig() {
    //Check if user tries to delete last config
    if (configRepo.repo.size - 1 === 0) {
        console.error("There needs to be at least one config!");
        $('#noConfig').show();
        return;
    }

    const toDelete = parseInt($('#bp-configselect').val());
    console.log(`Delete config ${toDelete}...`);
    controller.deleteConfiguration(toDelete).then(result => {
        if (!result.ok) {
            console.error("Deletion of config failed! See log of backend for more information.");
            throw new Error(i18n("couldNotDeleteConfig"));
        }
        configRepo.remove(toDelete);
        currentConfig = 0;
        refreshConfigDropdown();
        onConfigChange();

    }).catch(err => util.error(err));
}