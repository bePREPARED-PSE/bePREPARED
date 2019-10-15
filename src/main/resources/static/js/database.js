'use strict';

/**
 * Represents a repository for objects. Provides methods to set, get and remove objects.
 */
class Repository {

    constructor() {
        this.repo = new Map();
    }

    /**
     * Adds the supplied object to the repo or updates it, if an object with the same key already exists.
     *
     * @param key - the key of the value
     * @param val - the object to add
     */
    set(key, val) {
        this.repo.set(key, val);
    }

    /**
     * Returns the object with the supplied key or returns null if no such object exists.
     *
     * @param key - the key of the object
     * @returns {Object} the object with the supplied key
     */
    get(key) {
        const val = this.repo.get(key);
        return val === undefined ? null : val;
    }

    /**
     * Removes the object with the supplied id from the repo. If no such object exists, nothing will happen.
     *
     * @param key - the key of the object that should be removed
     */
    remove(key) {
        this.repo.delete(key);
    }

    /**
     * Returns all objects, that are currently stored in the Repo.
     *
     * @returns {[]} an Array of all Repo elements
     */
    getAll() {
        return Array.from(this.repo.values());
    }
}

//---------------------------------------------------------------------------------------------------------------------

/*
 Internal, private data structures for the class ScenarioRepository:
 */

/**
 * @param {Scenario} scenario
 * @private
 */
let _Scenario = function (scenario) {
    this.phases = new Map();

    this.id = scenario.scenarioID;
    this.name = scenario.name;
    this.standardPhase = scenario.standardPhase;
    scenario.phases.forEach(p => {
        this.phases.set(p.phaseID, new _Phase(p));
    });
};
/**
 * @param {Phase} phase
 */
_Scenario.prototype.addPhase = function (phase) {
    this.phases.set(phase.phaseID, new _Phase(phase));
};
/**
 * @param {Number} id
 */
_Scenario.prototype.getPhase = function (id) {
    const phase = this.phases.get(id);
    if (phase == null) {
        return null;
    }
    return phase;
};
/**
 * @param {Number} id
 */
_Scenario.prototype.removePhase = function (id) {
    this.phases.delete(id);
};

/**
 * @param {Phase} phase
 * @private
 */
let _Phase = function (phase) {
    this.events = new Map();

    this.id = phase.phaseID;
    this.name = phase.name;
    phase.events.forEach(e => {
        this.events.set(e.eventID, e);
    });
};
/**
 * @param {Event} event
 */
_Phase.prototype.addEvent = function (event) {
    this.events.set(event.eventID, event);
};
/**
 * @param {Number} id
 */
_Phase.prototype.getEvent = function (id) {
    const event = this.events.get(id);
    if (event == null) {
        return null;
    }
    return event;
};
/**
 * @param {Number} id
 */
_Phase.prototype.removeEvent = function (id) {
    this.events.delete(id);
};

//---------------------------------------------------------------------------------------------------------------------

/**
 * Represents a repository for Scenarios. Additonaly it provides methods to manipulate the phases and/or events
 * of a specific scenario.
 */
class ScenarioRepository extends Repository {

    /**
     * Adds the given scenario to the repo.
     *
     * @param {Scenario} scenario - the scenario to add
     */
    set(scenario) {
        super.set(scenario.scenarioID, new _Scenario(scenario));
    }

    /**
     * Returns the scenario with the given ID or null if no such scenario exists.
     *
     * @param {Number} id - the ID of the scenario to get
     * @returns {Scenario} the scenario with the supplied ID
     */
    get(id) {
        const scenario = super.get(id);
        if (scenario == null) {
            return null;
        }

        const phases = [];
        scenario.phases.forEach(v => {
            phases.push(new Phase(v.id, Array.from(v.events.values()), v.name));
        });
        return new Scenario(scenario.id, phases, scenario.name, scenario.standardPhase);
    }

    /**
     * Adds the supplied phase to the scenario with the given ID.
     *
     * @param {Number} scenarioID - the ID of the scenario
     * @param {Phase} phase - the phase to add to a scenario
     * @throws {Error} an error if an ID does not exist
     */
    addPhase(scenarioID, phase) {
        if (phase == null) {
            return;
        }

        const scenario = super.get(scenarioID);
        if (scenario == null) {
            throw new Error("There is no scenario with ID: " + scenarioID);
        }
        scenario.addPhase(phase);
    }

    /**
     * Returns the phase of the scenario with the given IDs.
     *
     * @param {Number} scenarioID - the ID of the scenario to get the phase from
     * @param {Number} phaseID - the ID of the phase to get
     * @returns {Phase} the phase with the supplied ID
     * @throws {Error} an error if an ID does not exist
     */
    getPhase(scenarioID, phaseID) {
        const scenario = super.get(scenarioID);
        if (scenario == null) {
            throw new Error("There is no scenario with ID: " + scenarioID);
        }

        const phase = scenario.getPhase(phaseID);
        if (phase == null) {
            throw new Error("There is no phase with ID: " + phaseID);
        }

        return new Phase(phase.id, Array.from(phase.events.values()), phase.name);
    }

    /**
     * Removes the phase from the scenario with the given IDs.
     *
     * @param {Number} scenarioID - the ID of the scenario
     * @param {Number} phaseID - the ID of the phase to remove
     * @throws {Error} an error if an ID does not exist
     */
    removePhase(scenarioID, phaseID) {
        const scenario = super.get(scenarioID);
        if (scenario == null) {
            throw new Error("There is no scenario with ID: " + scenarioID);
        }

        scenario.removePhase(phaseID);
    }

    /**
     * Returns all phases from the scenario with the given ID.
     *
     * @param {Number} scenarioID - the ID of the scenario to get the phases from
     * @returns {Array<Phase>} an array containing all phases
     */
    getAllPhases(scenarioID) {
        const scenario = this.get(scenarioID);
        return scenario.phases;
    }

    /**
     * Adds the supplied event to the phase and scenario with the given IDs.
     *
     * @param {Number} scenarioID - the ID of the scenario
     * @param {Number} phaseID - the ID of the phase
     * @param {Event} event - the event to add
     * @throws {Error} an error if an ID does not exist
     */
    addEvent(scenarioID, phaseID, event) {
        const scenario = super.get(scenarioID);
        if (scenario == null) {
            throw new Error("There is no scenario with ID: " + scenarioID);
        }

        const phase = scenario.getPhase(phaseID);
        if (phase == null) {
            throw new Error("There is no phase with ID: " + phaseID);
        }

        phase.addEvent(event);
    }

    /**
     * Returns the event from the scenario and phase with the given IDs.
     * If the phase ID is unknow, use {@link getEventFromScenario()} instead.
     *
     * @param scenarioID - the ID of the scenario
     * @param phaseID - the ID of the phase
     * @param eventID - the ID of the event to get
     * @returns {Event} the event with the supplied ID
     * @throws {Error} an error if an ID does not exist
     */
    getEvent(scenarioID, phaseID, eventID) {
        const scenario = super.get(scenarioID);
        if (scenario == null) {
            throw new Error("There is no scenario with ID: " + scenarioID);
        }

        const phase = scenario.getPhase(phaseID);
        if (phase == null) {
            throw new Error("There is no phase with ID: " + phaseID);
        }

        const event = phase.getEvent(eventID);
        if (event == null) {
            throw new Error("There is no event with ID: " + eventID);
        }

        return event;
    }

    /**
     * Removes the event from the phase and scenario with the given IDs.
     *
     * @param scenarioID - the ID of the scenario
     * @param phaseID - the ID of the phase
     * @param eventID - the ID of the event to remove
     * @throws {Error} an error if an ID does not exist
     */
    removeEvent(scenarioID, phaseID, eventID) {
        const scenario = super.get(scenarioID);
        if (scenario == null) {
            throw new Error("There is no scenario with ID: " + scenarioID);
        }

        const phase = scenario.getPhase(phaseID);
        if (phase == null) {
            throw new Error("There is no phase with ID: " + phaseID);
        }

        phase.removeEvent(eventID);
    }

    /**
     * Returns all events from the scenario with the given ID.
     *
     * @param {Number} scenarioID the ID of the scenario to get the events from
     * @returns {Array<Event>} an array containing all events
     */
    getAllEvents(scenarioID) {
        const phases = this.getAllPhases(scenarioID);
        const arr = [];
        phases.forEach(p => p.events.forEach(e => arr.push(e)));
        return arr;
    }

    /**
     * Returns the event from the scenario with the given IDs or null if no such event exists.
     * Use this function only if the phase ID is not known. When the phase ID is known, use {@link getEvent()} instead.
     *
     * @param {number} scenarioID - the ID of the scenario
     * @param {number} eventID - the ID of the event to get
     * @return {Event} the event with the supplied ID
     * @throws {Error} an error if an ID does not exist
     */
    getEventFromScenario(scenarioID, eventID) {
        const events = this.getAllEvents(scenarioID);
        let _return = null;
        events.forEach(e => _return = e.eventID == eventID ? e : _return);
        return _return;
    }

    /**
     * Returns all events from the phase of the scenario with the given IDs.
     *
     * @param {Number} scenarioID - the ID of the scenario to get the events from
     * @param {Number} phaseID - the ID of the phase to get the events from
     * @returns {Array<Event>} an array containing all events
     */
    getAllEventsFromPhase(scenarioID, phaseID) {
        const phase = this.getPhase(scenarioID, phaseID);

        const arr = [];
        phase.events.forEach(e => arr.push(e));
        return arr;
    }

    /**
     * Returns the phase an event belongs to
     *
     * @param {Number} scenarioID - the ID of the scenario to get the events from
     * @param {Number} eventID - the ID of the event to look for
     * @returns {number} the phase the event belongs to
     * @see If you want the event for an unknown phase ID, use {@link getEventFromScenario()} instead of getting the phase with this function and then call {@link getEvent()}.
     */
    getPhaseIDForEventID(scenarioID, eventID) {
        let phase = null;
        this.getAllPhases(scenarioID).forEach(p => {
            p.events.forEach(e => {
                if (e.eventID == eventID) {
                    phase = p;
                }
            });
        });
        return phase == null ? -1 : phase.phaseID;
    }

    /**
     * Returns the last event of the sdenario with the given ID.
     *
     * @param scenarioID - the ID of the scenario
     * @returns {Event} the last event of the scenario
     */
    getLastEventOf(scenarioID) {
        const events = this.getAllEvents(scenarioID);
        let levent = null;
        let lpit = 0;
        events.forEach(e => {
            if (e.pointInTime > lpit) {
                levent = e;
                lpit = e.pointInTime;
            }
        });
        return levent;
    }
}

//Create Repos:
var scenarioRepo = new ScenarioRepository();
var configRepo = new Repository();
var simulationRepo = new Repository();
var eventTypeRepo = new Repository();

//Current scenario ID:
var currentScenario = -1;
//Currnet config ID:
var currentConfig = -1;
//Selected event type
var selectedEt = "";
//Current Simulation ID:
var currentSimulation = -1;
//SelectedPhases:
var selectedPhases = [];
//Current speed
var currentSpeed = 1;

//Additional Properties
/**
 * @type {Array|null}
 */
var configPropertyDescriptor = null;