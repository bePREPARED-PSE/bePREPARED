'use strict';

var util = {

    /**
     * Creates HTML code for a button.
     *
     * @param {EventType} eventType - the event type object
     * @returns {string} HTML code
     */
    eventTypeButton: function (eventType) {
        return `<div class="col-3">
                    <div id="${eventType.name}" class="card" type="button" onclick="specifyEvent('${eventType.name}')">
                        <div class="card-container">
                            <img class="card-img-top" src="${eventType.icon}" alt="Eventtyp:">
                        </div>
                        <div class="card-body">
                            <div class="card-title my-center">${i18n(`${eventType.name}.type.name`)}</div>
                        </div>
                    </div>
                </div>`;
    },

    /**
     * Converts an input descriptor to HTML code.
     *
     * @param inpdescr - the input descriptor object to convert
     * @param forGenerator - whether that should be used in a generator or not
     * @param typeName - the name of the eventtype
     * @returns {string} HTML code
     */
    inputDescriptorToHtml: function (inpdescr, forGenerator, typeName) {

        const key = inpdescr.key;
        const description = this.escape(i18n(`${typeName}.${key}.label`));
        const tooltip = util.escape(i18n(`${typeName}.${key}.tooltip`));
        const defaultVal = util.escape(inpdescr.defaultValue);
        const required = inpdescr.required ? "required" : "";

        // ensure this is set to a correct boolean
        if (forGenerator !== true) {
            forGenerator = false;
        }

        //Determine which kind of Input Field the descriptor describes
        if (inpdescr.type == 'NumericInputField' && !forGenerator) { //NumericInputField
            let [max, min, step] = [inpdescr.maxValue, inpdescr.minValue, inpdescr.stepSize];
            step = step == -1 ? "any" : step;

            return `<div class="form-group row">
                    <div class="col-3">
                    <div class="col-form-label">${description}</div>
                    </div>
                    <div class="col-9">
                    <input class="form-control" id="${key}" name="${key}" type="number" value="${defaultVal}" max="${max}" min="${min}" step="${step}" value="0" title="${tooltip}" ${required}>
                    <div class="invalid-tooltip">` + i18n("num between %{min} and %{max}", {
                min: min,
                max: max
            }) + `</div>
                    </div></div>`;
        } else if (inpdescr.type == 'NumericInputField' && forGenerator) { //NumericInputField
            let [max, min, step] = [inpdescr.maxValue, inpdescr.minValue, inpdescr.stepSize];

            return `<div class="form-group row">
                    <div class="col-3">
                    <div class="col-form-label">${description}</div>
                    </div>
                    <div class="col-9">
                    <input class="form-control" id="${key}" name="${key}" type="text" value="${defaultVal}" title="${tooltip}" pattern="(?:[0-9-+,.*\/^()x]|abs|e^x|ln|log|a?(?:sin|cos|tan|rand|randInt)h?)+" ${required}>
                    <div class="invalid-tooltip">` + i18n("num between %{min} and %{max} or function", {
                min: min,
                max: max
            }) + `</div>
                    </div></div>`;
        } else if (inpdescr.type == 'FileInputField') { //FileInputField
            let accept = "";
            inpdescr.allowedFileTypes.forEach(s => accept += `.${s},`);
            accept = accept.slice(0, -1);

            return `<div class="form-grougit p row">
                    <div class="col-3">
                    <div class="col-form-label">${description}</div>
                    </div>
                    <div class="col-9">
                    <div class="custom-file">
                    <input onchange="onFileBrowse(this, '${key}')" type="file" class="custom-file-input" id="${key}" name="${key}" accept="${accept}" title="${tooltip}" ${required}>
                    <label id="label${key}" class="custom-file-label" for="${key}">` + `</label>   
                    </div></div></div>`;
        } else if (inpdescr.type == 'MultipleChoiceInputField') { //MultipleChoiceInputField
            const type = inpdescr.singleChoice === true ? "radio" : "checkbox";
            let options = "";
            let i = 0;
            inpdescr.options.forEach(o => {
                options += `<div class="form-check">
                            <input class="form-check-input" type="${type}" id="${key}${i}" name="${key}" value="${i}" ${inpdescr.singleChoice ? "required" : ""}>
                            <label class="form-check-label" for="${key}${i}">${o}</label>
                            </div>`;
                i++;
            });
            return `<div class="form-group row">
                    <div class="col-3">
                    <div class="col-form-label" title="${tooltip}">${description}</div>
                    </div>
                    <div class="col-9" >${options}</div>
                    </div>`;
        } else if (inpdescr.type == 'GeolocationInputField' && !forGenerator) { //GeoLocationInputField
            return `<div class="form-group row">
                <div class="col-3">
                    <div class="col-form-label" title="${tooltip}">${description}</div>
                </div>
                <div class="input-group col-9">
                    <input class="form-control" id="${key}lat" name="${key}lat" type="text" title="` + i18n("latitude") + `" placeholder="` + i18n("latitude") + `"
                           pattern="-?\\d+([.]\\d+)?" ${required}>
                    <input class="form-control" id="${key}lon" name="${key}lon" type="text" title="` + i18n("longitude") + `" placeholder="` + i18n("longitude") + `"
                           pattern="-?\\d+([.]\\d+)?" ${required}>
                           <div class="invalid-tooltip">` + i18n("enterValidCoordinates(eg %{eg})", {eg: "49.01509091147133;8.425612449645998"}) + `</div>
                    <div class="input-group-append">
                        <button type="button" class="row-2 btn btn-secondary" id="${key}pickLocation" onclick="leafletMap.pickLocation('${key}')">
                            <i class="fa fa-map-pin"></i>
                        </button>
                    </div>
                </div>
             </div>`;
        } else if (inpdescr.type == 'GeolocationInputField' && forGenerator) { //GeoLocationInputField
            return `<div class="form-group row">
                <div class="col-3">
                    <div class="col-form-label" title="${tooltip}">${description}</div>
                </div>
                <div class="input-group col-9">
                    <input class="form-control" id="${key}GenLat" name="${key}lat" type="text" title="` + i18n("latitude") + `" placeholder="` + i18n("latitude") + `"
                           pattern="(?:[0-9-+,.*\/^()x]|abs|e^x|ln|log|a?(?:sin|cos|tan|rand|randInt)h?)+" ${required}>
                    <input class="form-control" id="${key}GenLon" name="${key}lon" type="text" title="` + i18n("longitude") + `" placeholder="` + i18n("longitude") + `"
                           pattern="(?:[0-9-+,.*\/^()x]|abs|e^x|ln|log|a?(?:sin|cos|tan|rand|randInt)h?)+" ${required}>
                           <div class="invalid-tooltip">` + i18n("enterValidCoordinates(eg %{eg})", {eg: "49.01509091147133;8.425612449645998"}) + `</div>
                </div>
             </div>`;
        } else if (inpdescr.type == "UrlInputField") { //UrlInputField
            return `<div class="form-group row">
                    <div class="col-3">
                    <label class="col-form-label" for="${key}">${description}</label>
                    </div>
                    <div class="col-9">
                    <input class="form-control" name="${key}" id="${key}" type="url" value="${defaultVal}" 
                    placeholder="https://example.com" title="${tooltip}" ${required}>
                    <div class="invalid-tooltip">${i18n("enterValidURL")}</div>
                    </div></div>`;
        } else if (inpdescr.type == "PasswordInputField") { //PasswordInputField
            return `<div class="form-group row">
                        <div class="col-3">
                            <label class="col-form-label" for="${key}">${description}</label>
                        </div>
                        <div class="input-group col-9">
                            <input class="form-control" name="${key}" id="${key}" type="password" title="${tooltip}" ${required}>
                            <div class="invalid-tooltip">${i18n("plsFillOutThisField")}</div>
                            <div class="input-group-append">
                                <script type="text/javascript">
                                    function togglePwd(key) {
                                      let textInput = $("#"+key);
                                      if (textInput.prop('type') === "password") {
                                          textInput.prop('type', 'text');
                                          $("#"+key+"togglePwdIco").prop('class', 'fa fa-eye-slash');
                                      } else {
                                          textInput.prop('type', 'password');
                                          $("#"+key+"togglePwdIco").prop('class', 'fa fa-eye');
                                      }
                                    }
                                </script>
                                <button type="button" class="btn btn-secondary" id="${key}togglePwd" onclick="togglePwd('${key}')">
                                    <i class="fa fa-eye" id="${key}togglePwdIco"></i>
                                </button>
                            </div>
                        </div>
                    </div>`;
        } else if (forGenerator) { //InputField
            return `<div class="form-group row">
                    <div class="col-3">
                    <label class="col-form-label" for="${key}">${description}</label>
                    </div>
                    <div class="col-9">
                    <input class="form-control" name="${key}" id="${key}" type="text" value="${defaultVal}" title="${tooltip}" 
                    placeholder="` + i18n("generatorInputFieldPlaceholder") + `" ${required}>
                    <small class="form-text text-muted">` + i18n("generatorInputFieldHint") + `</small>
                    <div class="invalid-tooltip">${i18n("plsFillOutThisField")}</div>
                    </div></div>`;
        } else { //InputField
            return `<div class="form-group row">
                    <div class="col-3">
                    <label class="col-form-label" for="${key}">${description}</label>
                    </div>
                    <div class="col-9">
                    <input class="form-control" name="${key}" id="${key}" type="text" value="${defaultVal}" title="${tooltip}" ${required}>
                    <div class="invalid-tooltip">${i18n("plsFillOutThisField")}</div>
                    </div></div>`;
        }
    },

    /**
     * Shows an error prompt.
     *
     * @param {Error} error - Object of the error
     */
    error: function (error) {
        document.getElementById("modal-title").innerText = i18n("anErrorOccurred");
        document.getElementById("modal-content").innerText = error.message;
        $('#modal').modal();
    },

    /**
     * Collapes the middle-panel.
     */
    clickClose: function () {
        $("#middlePanel").html("").addClass("d-none");

        //Unmark events:
        timeline.timelineUnmarkEvents();
        table.tableUnmarkEvents();
        ce = -1;
        leafletMap.leavePickingMode();
        util.refresh();
    }
    ,

    /**
     * Fetches the current state of the scenarios from the backend and refreshes the GUI.
     */
    refreshScenario: function () {
        const reply = controller.getScenario(currentScenario);
        reply.then(result => {
            if (!result.ok) {
                throw new Error(i18n("couldNotFetchScenarios"));
            }
            result.json().then(s => {
                scenarioRepo.set(s);
                currentScenario = s.scenarioID;
                this.refresh();
            })
        }).catch(err => util.error(err));
    }
    ,

    /**
     * Refresh the data displayed in map, timline and table.
     */
    refresh: function () {
        //Show Loading squirrel
        const loading = $('#modal-loading');
        loading.modal();

        setTimeout(() => {
            console.log("Refresh table, map and timeline.");
            document.getElementById("curConfig").innerText = `${currentConfig}`;

            table.refreshTableData();
            leafletMap.refreshMap();
            timeline.refreshTimeline();
            table.disablePhases();


            //Set slider speed
            $('#slider').slider('setValue', currentSpeed);
            $('#bouncy-cat').text(currentSpeed);

            //Hide squirrel
            loading.modal('hide');
        }, 0);
    },

    /**
     * Updates the field where the selected phases are displayed.
     */
    refreshSelectedPhases: function () {
        //Write current selected phases into phase selection field
        let text = "";
        selectedPhases.forEach(s => {
            const p = scenarioRepo.getPhase(currentScenario, s);
            text += `${p.name} (${p.phaseID}), `;
        });
        text = text.slice(0, -2);
        $('#phaseSelect').text(text);
    }
    ,

    /**
     * Returns the HTML code of one row of the phase-select table.
     *
     * @param {Phase} phase - the phase to display in this row
     * @returns {string} HTML code
     */
    createTableLine: function (phase) {
        return `<tr>
                        <td>
                            <div class="custom-control custom-checkbox">
                                <input type="checkbox" class="custom-control-input" id="${phase.phaseID}"  value="${phase.phaseID}" name="bp-p">
                                <label class="custom-control-label label-tabel" for="${phase.phaseID}"></label>
                            </div>
                        </td>
                        <td>${phase.phaseID}</td>
                        <td>${this.escape(phase.name)}</td>
                    </tr>`;
    }
    ,

    /**
     * Enables or disables all buttons that can edit the scenario, depending on the given state.
     *
     * @param {Boolean} state - when true, all buttons will be enabled
     */
    setEditingMode: function (state) {
        const ids = ["bp-btn-phaseselect", "bp-createeventbtn", "bp-phasebtn"];
        ids.forEach(n => $(`#${n}`).prop('disabled', !state));
        $('li').removeClass(state ? 'disabled' : 'active').addClass(state ? 'active' : 'disabled');
        $('a').removeClass(state ? 'disabled' : '').addClass(state ? '' : 'disabled');
    }
    ,

    /**
     * Escapes the given string for HTML text.
     *
     * @param {string} unsafe - the string that shoulb be escaped
     * @return {string} the escaped string
     */
    escape: function (unsafe) {
        if (unsafe === undefined || unsafe == null) {
            return undefined;
        }
        if (typeof unsafe !== 'string') {
            return this.escape(unsafe.toString());
        }
        return unsafe
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    },

    /**
     * Returns the media-type of the file with the given filename.
     *
     * @param {String} filename - the filename to check
     * @return {String} the media-type
     */
    getMediaType: function (filename) {
        if (filename.endsWith("mp4") || filename.endsWith("webm") || filename.endsWith("ogg")) {
            return "video";
        }
        if (filename.endsWith("mp3") || filename.endsWith("ogg") || filename.endsWith("wav")) {
            return "audio";
        }
        if (filename.endsWith("jpg") || filename.endsWith("png") || filename.endsWith("gif")) {
            return "img";
        }
        return "unknown";
    }

};