'use strict';
/* The objects that can be created with the following constructors are the representation for the JSON objects that
   are used to transfer data between the frontend and backend. Thus, this objects can be directly converted to
   a JSON object which is ready for the transfer to the backend. */

/**
 * Creates a EventType object from the supplied data.
 * @param {String} name - the unique name of the event type
 * @param {String} icon - the URL as string of an icon for the event type
 * @param {Array} inputdescr - an array of input fields that shows up in the frontend to configure an event
 * @param {FastViewDescriptor} fastview - the fastview descriptor for this event type
 * @constructor
 */
function EventType(name, icon, inputdescr, fastview) {
    this.name = name;
    this.icon = icon;
    this.inputDescriptor = inputdescr;
    this.fastViewDescriptor = fastview;
}

/**
 * Creates an Event object from the supplied data.
 *
 * @param {Number} id - the ID of the event
 * @param {String} type - name of the event type
 * @param {Number} pointInTime - point in time of the event
 * @param {Object} data - an object containing the data of the event
 * @constructor
 */
function Event(id, type, pointInTime, data) {
    this.eventID = id;
    this.type = type;
    this.pointInTime = pointInTime;
    this.data = data;
}

/**
 * Creates an EventSeries object from the supplied data.
 *
 * @param numOfEvents the number of events that belong to this series
 * @param type the type of the event
 * @param pointInTimeFunction the function for calculating the point in time
 * @param eventData the data of the event
 * @constructor
 */
function EventSeries(numOfEvents, type, pointInTimeFunction, eventData) {
    this.numOfEvents = numOfEvents;
    this.type = type;
    this.pointInTimeFunction = pointInTimeFunction;
    this.eventData = eventData;
}

/**
 * Creates a Phase object from the supplied data.
 *
 * @param {Number} id - the ID of the phase
 * @param {Array<Event>} events - an array of events
 * @param {String} name - the name of the phase
 * @constructor
 */
function Phase(id, events, name) {
    this.phaseID = id;
    this.events = events;
    this.name = name;
}

/**
 * Creates a Scenario object from the supplied data.
 *
 * @param {Number} id - the ID of the scenario
 * @param {Array<Phase>} phases - an array of phases
 * @param {String} name - the name of the phase
 * @param {Number} standardPhase - the ID of the standard phase
 * @constructor
 */
function Scenario(id, phases, name, standardPhase) {
    this.scenarioID = id;
    this.phases = phases;
    this.name = name;
    this.standardPhase = standardPhase;
}

/**
 * Creates a Configuration object from the supplied data.
 *
 * @param {Number} id - the id of the configuration
 * @param {Number} start - the start date and time for a simulation (UNIX time format)
 * @param {Object} additionalProperties - the data of the configuration
 * @constructor
 */
function Configuration(id, start, additionalProperties) {
    this.configurationID = id;
    this.scenarioStartTime = start;
    this.additionalProperties = additionalProperties;
}

/**
 * Creates a Simulation object from the supplied data.
 *
 * @param {Number} id - the id of the simulation
 * @param {string} simulationState - indicates the state of the simulation
 * @param {Configuration} config - the configuration of the simulation
 * @param {Array<Number>} phaseIDs - array containing the IDs of the selected phases
 * @param {Number} speed - the initial speed of the simulation
 * @constructor
 */
function Simulation(id, simulationState, config, phaseIDs, speed) {
    this.simulationID = id;
    this.simulationState = simulationState;
    this.configuration = config;
    this.selectedPhaseIDs = phaseIDs;
    this.speed = speed;
}

/**
 * Creates a FastViewDescriptor object from the supplied data.
 *
 * @param displayKeys - the keys of the event data object whose values should be displayed
 * @param {String} mediaKey - the key of the media object
 * @constructor
 */
function FastViewDescriptor(displayKeys, mediaKey) {
    this.displayKeys = displayKeys;
    this.mediaKey = mediaKey;
}