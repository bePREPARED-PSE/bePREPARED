'use strict';

/**
 * Returns a data set for the table which contains all events from the current scenario.
 *
 * @returns {[]} an Array containing the data of the table
 */
function getDataSet() {
    const scenario = scenarioRepo.get(currentScenario);
    if (scenario == null) return null;
    const phases = scenario.phases;
    if (phases == null) return null;
    let dataSet = [];
    phases.forEach(p => {
        const events = p.events;
        events.forEach(e => {
            let currentEvent = [];
            currentEvent.push(e.eventID);
            currentEvent.push(util.escape(e.type));
            currentEvent.push(convertSecondsToHHMMSS(e.pointInTime));
            currentEvent.push(`${util.escape(p.name)} (${p.phaseID})`);
            dataSet.push(currentEvent);
        });
    });
    return dataSet;
}

/*
Initialize the table:
 */

class BPTable {

    /**
     * Construct the table.
     */
    constructor() {
        this.table = $('#mydatatable').DataTable({
            "oLanguage": {
                "sSearch": i18n("Search") + ":",
                "sEmptyTable": i18n("noDataAvailableYet")
            },
            paging: false,
            info: false,
            columns: [
                {title: "ID"},
                {title: i18n("EventType")},
                {title: i18n("PointInTime")},
                {title: i18n("Phase")}
            ],
        });
    }

    /**
     * Adds the fitler function to the table.
     */
    addFilter() {
        this.table.columns([1, 3]).every(function () {
            let column = this;
            let select = $('<select class="custom-select"><option value=""></option></select>')
                .appendTo($(column.footer()).empty())
                .on('change', function () {
                    let val = $.fn.dataTable.util.escapeRegex(
                        $(this).val()
                    );
                    column
                        .search(val ? '^' + val + '$' : '', true, false)
                        .draw();
                });
            column.data().unique().sort().each(function (d, j) {
                select.append('<option value="' + d + '">' + d + '</option>')
            });
        });
    }

    /**
     * Refresh the data displayed in the table
     */
    refreshTableData() {
        const dataSet = getDataSet();
        if (Array.isArray(dataSet)) {
            if (dataSet.length === 0) {
                this.table.clear();
                this.table.draw();
                this.addFilter();
            }
            if (dataSet.length > 0) {
                this.table.clear();
                this.table.rows.add(dataSet);
                this.table.draw();
                this.addFilter();
            }
        }
    }

    /**
     * Marks the event with the given ID in the table.
     *
     * @param {number} eventID - the ID of the event to mark
     */
    tableMarkEvent(eventID) {
        this.table.rows().every(function () {
            const row = this.node();
            if (this.data()[0] === eventID) {
                $(row).addClass('markRow');
            } else {
                $(row).removeClass('markRow');
            }
        });
    }

    /**
     * Unmarks all table rows.
     */
    tableUnmarkEvents() {
        this.table.rows().every(function () {
            const row = this.node();
            $(row).removeClass('markRow');
        });
    }

    disablePhases() {
        const phases = scenarioRepo.getAllPhases(currentScenario);
        for (let i = 0; i < phases.length; i++) {
            if (selectedPhases.includes(phases[i].phaseID)) {
                this.tableEnablePhase(phases[i].phaseID);
            } else {
                this.tableDisablePhase(phases[i].phaseID);
            }
        }
    }

    /**
     * Marks all events of the phase with the given ID as disabled in the table.
     *
     * @param {number} phaseID - the ID of the phase to disable
     */
    tableDisablePhase(phaseID) {
        const events = scenarioRepo.getAllEventsFromPhase(currentScenario, phaseID);
        this.table.rows().every(function () {
            const row = this.node();
            for (let i = 0; i < events.length; i++) {
                if (this.data()[0] === events[i].eventID) {
                    $(row).addClass('disableRow');
                    break;
                }
            }
        })
    }

    /**
     * Marks all events of the phase with the given ID as enabled in the table.
     *
     * @param {number} phaseID - the ID of the phase to enable
     */
    tableEnablePhase(phaseID) {
        const events = scenarioRepo.getAllEventsFromPhase(currentScenario, phaseID);
        this.table.rows().every(function () {
            const row = this.node();
            for (let i = 0; i < events.length; i++) {
                if (this.data()[0] === events[i].eventID) {
                    $(row).removeClass('disableRow');
                    break;
                }
            }
        })
    }
}


$(document).ready(function () {
    $('#table-body').on('click', 'tr', function () {
        let data = table.table.row(this).data();
        if (data == null)
            return;
        clickEditEvent(data[0]);
    })
});
