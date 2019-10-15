'use strict';

/**
 * Creates a controller object, containing methods to send requests to the bePREPARED backend.
 *
 * @param url - the address of the backend
 * @constructor
 */
function Controller(url) {
    this.url = url;

    /**
     * An object containing all content types
     */
    this.contentTypes = {
        JSON: 'application/json'
    };

    /**
     * Different types of REST-requests
     * @type {string}
     */
    this.POST = 'POST';
    this.GET = 'GET';
    this.PUT = 'PUT';
    this.DELETE = 'DELETE';
    this.PATCH = 'PATCH';

    /**
     * Function
     * Creates a REST-request from the supplied properties.
     * Set body to null if your request does not need one, in that case the type parameter will be ignored.
     *
     * @param method - REST-Method, e.g. GET
     * @param body - the body of the request
     * @param type - Content-Type of the body
     * @returns {Request} a request object
     */
    this.createRequest = function (method, body, type) {
        return body != null ? {
            method: method,
            body: body,
            headers: {
                'Content-Type': type
            }
        } : {
            method: method
        };
    };

    /**
     * Sends a request to the given URL.
     * Set object to NULL if you don't want to send a body.
     *
     * @param {String} method - method of the request, e.g. GET
     * @param {String} path - the path of the request.
     * @param {Object} object - the JSON to send
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.sendRequest = (method, path, object) => {
        console.log(`Send "${method}" request to ${path} with data:`, object);
        //Check if caller want to send a body
        const json = object != null ? JSON.stringify(object) : null;
        //First, create the request by calling createRequest to get the Request-object
        const request = this.createRequest(method, json, this.contentTypes.JSON);
        //Then call fetch to send the request
        return fetch(this.url + path, request);
    };

    //Event-Controller: -----------------------------------------------------------------------------------------------

    /**
     * Sends a request to create a new event from the supplied data.
     *
     * @param {Number} scenarioID - scenario - the ID of the event's assigned scenario
     * @param {Number} phaseID - the ID of the event's assigned phase
     * @param {String} eventTypeName - type of the event
     * @param {Number} pointInTime - the point in time of the event
     * @param {Object} data - data of the event
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.createEvent = (scenarioID, phaseID, eventTypeName, pointInTime, data) => {
        const event = new Event(-1, eventTypeName, pointInTime, data);
        return this.sendRequest(this.POST, "/scenarios/" + scenarioID + "/phases/" + phaseID + "/events", event);
    };

    /**
     * Sends a request to create a new event series from the supplied date.
     *
     * @param {Number} scenarioId - the id of the scenario containing the phase
     * @param {Number} phaseId - the phase the series should be assigned to
     * @param {EventSeries} eventSeries - a representation of the series to generate
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.createEventSeries = (scenarioId, phaseId, eventSeries) => {
        return this.sendRequest(this.POST, "/scenarios/" + scenarioId + "/phases/" + phaseId + "/eventSeries",
            eventSeries);
    };

    /**
     * Sends a request to edit the given event.
     *
     * @param {Number} scenarioID - the ID of the event's assigned scenario
     * @param {Number} phaseID - the ID of the event's assigned phase
     * @param {Event} editedEvent - the edited event object
     * @returns {Promise<Response>} - a Promise resolving the response
     */
    this.editEvent = (scenarioID, phaseID, editedEvent) => {
        return this.sendRequest(this.PUT, "/scenarios/" + scenarioID + "/phases/" + phaseID + "/events/" +
            editedEvent.eventID, editedEvent);
    };

    /**
     * Sends a request to delete the event with the given ID.
     *
     * @param {Number} scenarioID - the ID of the event's assigned scenario
     * @param {Number} phaseID - the ID of the event's assigned phase
     * @param {Number} eventID - the ID of the event that should be deleted
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.deleteEvent = (scenarioID, phaseID, eventID) => {
        return this.sendRequest(this.DELETE, "/scenarios/" + scenarioID + "/phases/" + phaseID
            + "/events/" + eventID, null);
    };

    /**
     * Sends a request to get all event types from the backend.
     *
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.getEventTypes = () => {
        return this.sendRequest(this.GET, "/eventTypes", null);

    };

    //Phase-Controller: -----------------------------------------------------------------------------------------------

    /**
     * Sends a request to create a new phase from the supplied data.
     *
     * @param {Number} scenarioID - the ID of the phase's assigned scenario
     * @param {String} name - the name if the phase
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.createPhase = (scenarioID, name) => {
        const phase = new Phase(-1, [], name);
        return this.sendRequest(this.POST, "/scenarios/" + scenarioID + "/phases", phase);
    };

    /**
     * Sends a request to delete the phase from the scenario with the given IDs.
     *
     * @param {Number} scenarioID - the ID of the phase's assigned scenario
     * @param {Number} phaseID - the ID of the phase to delete
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.deletePhase = (scenarioID, phaseID) => {
        return this.sendRequest(this.DELETE, "/scenarios/" + scenarioID + "/phases/" + phaseID, null);
    };

    /**
     * Sends a request to discard the phase from the scenario with the given IDs.
     *
     * @param {Number} scenarioID - the ID of the phase's assigned scenario
     * @param {Number} phaseID - the ID of the phase to discard
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.discardPhase = (scenarioID, phaseID) => {
        return this.sendRequest(this.POST, "/scenarios/" + scenarioID + "/phases/" + phaseID + "/discard", null);
    };

    /**
     * Sends a request to shift the phase from the scenario with the given IDs.
     *
     * @param {Number} scenarioID - the ID of the phase's assigned scenario
     * @param {Number} phaseID - the ID of the phase to discard
     * @param {Number} time - time in s to shift the phase
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.shiftPhase = (scenarioID, phaseID, time) => {
        return this.sendRequest(this.PATCH, "/scenarios/" + scenarioID + "/phases/" + phaseID + "/shift/" + time, null);
    };

    /**
     * Sends a request to reassign the phase of the event with the given ID.
     *
     * @param {Number} scenarioID - the ID of the event's scenario
     * @param {Number} oldPhaseID - the ID of the event's old phase
     * @param {Number}newPhaseID - the ID of the event's new phase
     * @param {Number}eventID - the ID of the event
     * @return {Promise<Response>} a Promise resolving the response
     */
    this.reassignToPhase = (scenarioID, oldPhaseID, newPhaseID, eventID) => {
        return this.sendRequest(this.PATCH, `/scenarios/${scenarioID}/phases/${oldPhaseID}/events/${eventID}/reassignToPhase/${newPhaseID}`, null);
    };

    //Scenario-Controller: --------------------------------------------------------------------------------------------

    /**
     * Sends a request to create a new scenario from the supplied data.
     *
     * @param {String} name - the name of the scenario
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.createScenario = async (name) => {
        const scenario = new Scenario(-1, [], name, -1);
        return this.sendRequest(this.POST, "/scenarios", scenario);
    };

    /**
     * Sends a request to get all scenarios.
     *
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.getScenarios = () => {
        return this.sendRequest(this.GET, "/scenarios", null);
    };

    /**
     * Sends a request to get the scenario from the given ID.
     *
     * @param {Number} scenarioID - the ID of the scenario that should be get
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.getScenario = (scenarioID) => {
        return this.sendRequest(this.GET, "/scenarios/" + scenarioID, null);
    };

    /**
     * Sends a request to delete the scenario with the given ID.
     *
     * @param {Number} scenarioID - the ID of the scenario that should be deleted
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.deleteScenario = (scenarioID) => {
        return this.sendRequest(this.DELETE, "/scenarios/" + scenarioID, null);
    };

    //Configuration-Controller: ---------------------------------------------------------------------------------------

    /**
     * Sends  request to create a new configuration from the supplied data.
     *
     * @param {Number} startTime - the start time for the simulation
     * @param {Object} data - the data of the config
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.createConfiguration = (startTime, data) => {
        const config = new Configuration(-1, startTime, data);
        return this.sendRequest(this.POST, "/configs", config);
    };

    /**
     * Sends a request to get all configurations.
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.getConfigurations = () => {
        return this.sendRequest(this.GET, "/configs", null);
    };

    /**
     * Sends a request to get the configuration with the given ID.
     *
     * @param {Number} configID - the ID of the configuration to get
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.getConfiguration = (configID) => {
        return this.sendRequest(this.GET, "/configs/" + configID, null);
    };

    /**
     * Sends a request to update a configuration.
     * @param {Configuration} config - the updated configuration
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.updateConfiguration = (config) => {
        return this.sendRequest(this.PUT, "/configs/" + config.configurationID, config);
    };

    /**
     * Sends a request to delete the configuration with the given ID.
     *
     * @param {Number} configID - the ID of the configuration to delete
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.deleteConfiguration = (configID) => {
        return this.sendRequest(this.DELETE, "/configs/" + configID, null);
    };

    /**
     * Sends a request to get all config descriptors.
     *
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.getConfigDescriptors = () => {
        return this.sendRequest(this.GET, "/configurationDescriptor", null);
    };

    //Simulation-Controller: ------------------------------------------------------------------------------------------

    /**
     * Sends a request to get all simulations.
     *
     * @param {Number} scenarioID - the ID of the scenario
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.getSimulations = (scenarioID) => {
        return this.sendRequest(this.GET, `/scenarios/${scenarioID}/simulations`, null);
    };

    /**
     * Sends a request to create a simulation.
     *
     * @param {Number} scenarioID - the ID of the scenario
     * @param {Configuration} config - the configuration of the simulation
     * @param {Array<Number>} selectedPhaseIDs - an array containing all selected phase IDs
     * @param {Number} speed - the initial speed of the simulation
     */
    this.createSimulation = (scenarioID, config, selectedPhaseIDs, speed) => {
        const simulation = new Simulation(-1, undefined, config, selectedPhaseIDs, speed);
        return this.sendRequest(this.POST, `/scenarios/${scenarioID}/simulations`, simulation);
    };

    /**
     * Sends a request to start the simulation with the given ID.
     *
     * @param {Number} simulationID - the ID of the simulation
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.startSimulation = (simulationID) => {
        return this.sendRequest(this.POST, "/simulations/" + simulationID + "/play", null);
    };

    /**
     * Sends a request to pause/continue the simulation with the given ID.
     *
     * @param {Number} simulationID - the ID of the simulation to pause/continue
     */
    this.togglePauseSimulation = (simulationID) => {
        return this.sendRequest(this.POST, "/simulations/" + simulationID + "/pause", null);
    };

    /**
     * Sends a request to stop the simulation with the given ID.
     *
     * @param {Number} simulationID the ID of the simulation to stop
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.stopSimulation = (simulationID) => {
        return this.sendRequest(this.POST, "/simulations/" + simulationID + "/stop", null);
    };

    /**
     * Sends a request to forward the simulation to the event with the given IDs.
     *
     * @param {Number} simulationID - the ID of the simulation to fast forward
     * @param {Number} eventID - the ID of the event to which the simulation should be fast forwarded
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.fastForwardSimulation = (simulationID, eventID) => {
        return this.sendRequest(this.POST, "/simulations/" + simulationID + "/fastforward/" + eventID, null);
    };

    /**
     * Sends a request to update the speed of a simulation.
     *
     * @param {Number} id - the ID of the simulation
     * @param {Number} speed - the new speed of the simulation
     */
    this.updateSpeed = (id, speed) => {
        console.log("Update speed:", speed);
        const data = new FormData();
        data.append('speed', speed.toString());
        return fetch(`${this.url}/simulations/${id}/speed`, {
            method: this.POST,
            body: data
        });
    };

    //Export-Controller: ----------------------------------------------------------------------------------------------

    /**
     * Sends a request to export the scenario with the given ID.
     *
     * @param {Number} scenarioID - the ID of the scenario to export
     */
    this.exportScenario = (scenarioID) => {
        location.href = `${this.url}/scenarios/${scenarioID}/export`;
    };

    /**
     * Sends a request to export the configuration with the given ID.
     *
     * @param {Number} configID - the ID of the configuration to export
     */
    this.exportConfiguration = (configID) => {
        location.href = `${this.url}/configs/${configID}/export`;
    };

    //Import-Controller -----------------------------------------------------------------------------------------------

    /**
     * Sends a request to import the given scenario file
     * @param file - the scenario file to upload
     *
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.importScenario = (file) => {
        const data = new FormData();
        data.append('file', file);
        return fetch(`${this.url}/scenarios/import`, {
            method: this.POST,
            body: data
        });
    };

    /**
     * Sends a request to import the given config file.
     * @param file - the config file to upload
     *
     * @returns {Promise<Response>} a Promise resolving the response
     */
    this.importConfiguration = (file) => {
        const data = new FormData();
        data.append('file', file);
        return fetch(`${this.url}/configs/import`, {
            method: this.POST,
            body: data
        });
    };

    //File-Controller: ------------------------------------------------------------------------------------------------

    this.uploadFile = async (file, name) => {
        console.log("Upload file:", file);
        const myNewFile = new File([file], name, {type: file.type});
        const data = new FormData();
        data.append('file', myNewFile);
        data.append('name', name);
        const f = await fetch(`${this.url}/files`, {
            method: this.POST,
            body: data
        });
        _toUpload = null;
        return f;
    };

    this.downloadFileLink = (name) => {
        return `${this.url}/files/${name}`;
    };

}

//Creating the controller object with the URL of our backend
const controller = new Controller(window.location.origin);