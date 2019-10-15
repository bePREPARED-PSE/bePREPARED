'use strict';

/**
 * This function is called when the time marker needs to be moved.
 */
function onUpdate() {
    timeline.currentTime += 10 * currentSpeed;
    timeline.line.setCustomTime(timeline.currentTime, 0);
}

class TimelineBP {

    /**
     * Returns the fast view descriptors content for the given event.
     *
     * @param {Event} event - the event to show the fast view descriptor for
     * @param {boolean} fastForwardEnabled - whether the fastforward-Button should be shown or not
     * @return {string[]} - the fast view descriptor as string array
     */
    static loadFastViewDescriptor(event, fastForwardEnabled) {
        const fastViewDescriptor = eventTypeRepo.get(event.type).fastViewDescriptor;
        const dpk = fastViewDescriptor.displayKeys;
        let title = `<b>${util.escape(i18n(`${event.type}.ffd.name`))}</b>`;

        const ff = currentSimulationState === simulationState.NO_SIMULATION ||
            currentSimulationState === simulationState.STOPPED ||
            event.pointInTime + configRepo.get(currentConfig).scenarioStartTime < timeline.currentTime;

        if (fastForwardEnabled) {
            title += ` <a href="#" onclick="fastForward(${event.eventID})" class="fa fa-forward" ${ff ? "disabled" : ""}></a>`;
        }
        let descriptor = "";

        if (dpk != undefined) {
            for (let key of dpk) {
                descriptor += `${i18n(`${event.type}.${key}.label`)} ${util.escape(event.data[key])}</br>`;
            }
        }

        if (fastViewDescriptor.mediaKey !== null) {
            const filename = event.data.files[fastViewDescriptor.mediaKey];
            if (filename !== null) {
                switch (util.getMediaType(filename)) {
                    case "video":
                        descriptor += `<video src="${controller.url}/files/${filename}" controls style="width: 100%">`;
                        break;
                    case "audio":
                        descriptor += `<audio src="${controller.url}/files/${filename}" controls style="width: 100%">`;
                        break;
                    case "img":
                        descriptor += `<img src="${controller.url}/files/${filename}" alt="IMG" width="100%">`;
                        break;
                    case "unknown":
                        descriptor += `<a href="${controller.url}/files/${filename}" download="${filename}" >${filename.split('-', 2)[1]}</a>`;
                        break;
                }
            }
        }

        return [title, descriptor];
    }

    /**
     * Initializes the timeline with the given ID c.
     *
     * @param {String} c - the ID of a HTML element
     */
    constructor(c) {
        this.runnerID = -1;
        this.startTime = 0;
        this.currentTime = 0;
        this.speed = 1;
        this.lastSimulationState = undefined;

        //Prepare stuff for timeline
        const container = document.getElementById(c);
        this.items = new vis.DataSet();

        //Create the Timeline
        this.line = new vis.Timeline(container, this.items, {
            showCurrentTime: false,
            height: 200,
            selectable: true,
            start: this.startTime,
            zoomMin: 10000,
            locale: moment.locale()
        });

        //Add time marker
        this.line.addCustomTime(0, 0);
        //Prevent time marker to be draggable
        this.line.customTimes[this.line.customTimes.length - 1].hammer.off("panstart panmove panend");
    }

    /**
     * Updates the state of the timline, depending on the state of the simulation.
     */
    updateSimulationState() {
        if (this.lastSimulationState !== currentSimulationState) {
            //the state has changed.
            switch (currentSimulationState) {
                case simulationState.NO_SIMULATION:
                    break;
                case simulationState.RUNNING:
                    this.runnerID = setInterval(onUpdate, 10);
                    break;
                case simulationState.PAUSED:
                    clearTimeout(this.runnerID);
                    break;
                case simulationState.STOPPED:
                    clearTimeout(this.runnerID);
                    this.currentTime = this.startTime;
                    this.line.setCustomTime(this.startTime, 0);
                    break;
            }
        }
        this.lastSimulationState = currentSimulationState;
    }

    /**
     * Refreshs the data displayed on the timeline.
     */
    refreshTimeline() {
        this.items.clear();

        scenarioRepo.getAllPhases(currentScenario).forEach(p => {
            if (selectedPhases.includes(p.phaseID)) {
                this.addNewData(p.events, true);
            } else {
                this.addNewData(p.events, false);
            }
        });

        const time = configRepo.get(currentConfig).scenarioStartTime;
        this.startTime = time;
        this.currentTime = time;
        this.line.setCustomTime(time, 0);
    }

    /**
     * Adds new events to the timeline.
     *
     * @param data {Array<Event>} - an Array of events of a phase
     * @param selected {boolean} - indicates, if the these events are in a selected phase
     */
    addNewData(data, selected) {
        if (data != null) {
            data.forEach(e => {
                this.items.add({
                    id: e.eventID,
                    content: `<img src="${eventTypeRepo.get(e.type).icon}" style="width:32px; height: 32px; ${selected ? "" : "opacity: 0.5"}" alt="EREIGNIS">`,
                    start: new Date(e.pointInTime + configRepo.get(currentConfig).scenarioStartTime),
                    className: `${selected ? "" : "unselected"}`
                });
            });
        }
    }

    /**
     * Shifts the view of the timline, so that the start and the and of the scenario can be seen.
     */
    shiftView() {
        const stime = (configRepo.get(currentConfig).scenarioStartTime);
        const levent = scenarioRepo.getLastEventOf(currentScenario);
        const etime = levent == null ? stime : stime + levent.pointInTime;
        this.line.setWindow(stime, etime, null, function () {
            timeline.line.zoomOut(0.3)
        });
    }

    /**
     * Marks the event with the given ID on the timeline.
     *
     * @param {number} eventID - the ID of the event
     */
    timelineMarkEvent(eventID) {
        this.line.setSelection(eventID);
        this.line.moveTo(scenarioRepo.getEvent(currentScenario, scenarioRepo.getPhaseIDForEventID(currentScenario, eventID), eventID).pointInTime + configRepo.get(currentConfig).scenarioStartTime);
    }

    /**
     * Unmarks all events on the timeline.
     */
    timelineUnmarkEvents() {
        this.line.setSelection();
    }
}

//Timeline object of the frontend
var timeline = new TimelineBP("visualization");
timeline.line.on('select', function (properties) {
    if (properties.items.length === 0) {
        return;
    }
    const eventID = properties.items[0];
    clickEditEvent(eventID);
});

timeline.line.on('itemover', props => {
    const ele = $(props.event.target);
    const ffd = TimelineBP.loadFastViewDescriptor(scenarioRepo.getEventFromScenario(currentScenario, props.item), true);
    const _popover = {
        placement: 'top',
        container: 'body',
        html: true,
        title: ffd[0],
        content: ffd[1],
        sanitize: false
    };

    ele.popover(_popover)
        .on('shown.bs.popover', function () {
            let _this = this;
            $('.popover').on('mouseleave', function () {
                $(_this).popover('dispose');
            });
        })
        .on('mouseleave', function () {
            let _this = this;
            setTimeout(function () {
                if (!$('.popover:hover').length) {
                    $(_this).popover('dispose');
                }
            }, 300);
        }).popover('show');
});

$('#zoomIn').on('click', () => timeline.line.zoomIn(1));
$('#zoomOut').on('click', () => timeline.line.zoomOut(1));
$('#defaultView').on('click', () => timeline.shiftView());

