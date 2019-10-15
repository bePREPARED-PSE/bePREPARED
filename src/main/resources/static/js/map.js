'use strict';

let ce = -1;
let lastMarker;

/**
 * Initializes and controls the map.
 */

class LeafletMap {
    /**
     * Initializes the map with the given ID c.
     *
     * @param {String} c - the ID of a HTML element
     */
    constructor(c) {
        //Prepare stuff for map
        this.mapEntries = [];

        //Create the map
        this.map = L.map(c).locate({setView: true, maxZoom: 16});

        //Add layer to the map
        this.wikimedia = L.tileLayer('https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}{r}.png', {
            attribution: '<a href="https://wikimediafoundation.org/wiki/Maps_Terms_of_Use">Wikimedia</a>',
            minZoom: 1,
            maxZoom: 19
        });

        this.wikimedia.addTo(this.map);
        this.geoJsonLayer = L.geoJSON(null, {
            pointToLayer: function (feature, latlng) {
                let e = scenarioRepo.getEvent(currentScenario, scenarioRepo.getPhaseIDForEventID(currentScenario, feature.properties.eventID), feature.properties.eventID);
                const selected = selectedPhases.includes(scenarioRepo.getPhaseIDForEventID(currentScenario, feature.properties.eventID));

                if (feature.properties.eventID === ce) {

                    lastMarker = L.marker(latlng, {
                        icon: new L.divIcon({
                            className: 'vis-item vis-box vis-selected vis-readonly',
                            iconSize: [44, 44],
                            html: `<div class="vis-item-content">
                                        <img src="${eventTypeRepo.get(e.type).icon}" style="width:32px; height: 32px; ${selected ? "" : "opacity: 0.5;"}" alt="EREIGNIS">
                                    </div>`
                        })
                    });
                    //checks if phase of event is selected
                } else if (!selected) {
                    lastMarker = L.marker(latlng, {
                        icon: new L.divIcon({
                            className: 'vis-item vis-box unselected vis-readonly',
                            iconSize: [44, 44],
                            html: `<div class="vis-item-content">
                                        <img src="${eventTypeRepo.get(e.type).icon}" style="width:32px; height: 32px; opacity: 0.5" alt="EREIGNIS">
                                    </div>`
                        })
                    });
                } else {
                    lastMarker = L.marker(latlng, {
                        icon: new L.divIcon({
                            className: 'vis-item vis-box vis-readonly',
                            iconSize: [44, 44],
                            html: `<div class="vis-item-content">
                                        <img src="${eventTypeRepo.get(e.type).icon}" style="width:32px; height: 32px;" alt="EREIGNIS">
                                    </div>`
                        })
                    });
                }
                return lastMarker;
            },
            onEachFeature: onEachFeature
        }).addTo(this.map);

        //add button to reset view
        L.easyButton('<b>&#x26F6;</b>', function (btn, map) {
            leafletMap.fitView();
        }).addTo(this.map);


        //location picker marker
        this.locationPicker = new L.marker([0, 0], {
            draggable: 'true',
            icon: new L.Icon({
                iconUrl: 'img/marker-icon-2x-red.png',
                shadowUrl: 'img/marker-shadow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34],
                shadowSize: [41, 41]
            })
        }).bindPopup(i18n("moveMeMarker"));
    }

    mapMarkEvent(eventID) {
        ce = eventID;
        this.refreshMap();

        //pan map to selected event
        let mapEntry = this.getMapEntryForEvent(eventID);
        if (mapEntry == null || mapEntry == undefined) {
            //do nothing, the event has no mapEntry
        } else {
            let bounds = L.latLngBounds(null, null);
            mapEntry.features.forEach(f => {
                bounds.extend(L.latLng(f.geometry.coordinates[1], f.geometry.coordinates[0]));
            });
            if (bounds.isValid()) {
                bounds.pad(1.5);
                this.map.panInsideBounds(bounds);
            }
        }
    }

    /**
     * Refreshs the data displayed on the map.
     */
    refreshMap() {
        this.removeAllMarkers();
        this.addNewData(scenarioRepo.getAllEvents(currentScenario));
        this.showAllMarkers();
    }

    /**
     * Adds new events to the map.
     *
     * @param data {Array<Event>} - an Array of events
     */
    addNewData(data) {
        if (data != null) {
            data.forEach(e => {
                this.mapEntries[e.eventID] = new MapEntry(e);
            });
        }
    }

    /**
     * Shows all markers for location events on the map.
     */
    showAllMarkers() {
        this.mapEntries.forEach(m => {
            m.showMarkers();
        });
    }

    /**
     * Removes all markers from the map.
     */
    removeAllMarkers() {
        this.geoJsonLayer.clearLayers();
        this.mapEntries = [];
    }

    getMapEntryForEvent(eventId) {
        return this.mapEntries[eventId];
    }

    /**
     * Fits the view of the map, so that every marker is visible.
     */
    fitView() {
        let bounds = leafletMap.geoJsonLayer.getBounds();
        if (bounds.isValid()) {
            bounds.pad(1.5);
            this.map.fitBounds(bounds);
        }
    }

    pickLocation(key) {
        function onDrag(e) {
            $("#" + leafletMap.pickingKey + "lat").val(e.latlng.lat);
            $("#" + leafletMap.pickingKey + "lon").val(e.latlng.lng);
            //leafletMap.map.off('click', onClick.bind(timeline, key));
        }

        if (this.pickingKey != key) {
            this.pickingKey = key;

            let location;
            if ($("#" + leafletMap.pickingKey + "lat").val() == "" || $("#" + leafletMap.pickingKey + "lon").val() == "") {
                location = this.map.getCenter();
            } else {
                location = {
                    lat: $("#" + leafletMap.pickingKey + "lat").val(),
                    lon: $("#" + leafletMap.pickingKey + "lon").val()
                };
            }

            this.locationPicker.setLatLng(location).addTo(this.map).on('drag', onDrag).openPopup();
        } else {
            //already in picking mode for the given key
            //do nothing
        }

    }

    leavePickingMode() {
        this.pickingKey = undefined;
        this.locationPicker.remove();
    }
}

/**
 * Creates a map entry (visible as marker) for the given event.
 *
 * @param {Event} event - the event to create an entry for
 * @constructor
 */
function MapEntry(event) {
    this.phaseID = scenarioRepo.getPhaseIDForEventID(currentScenario, event.eventID);
    this.eventID = event.eventID;
    this.features = [];
    this.markers = [];

    //build features
    if (event.data.locations != undefined) {
        for (const key of Object.keys(event.data.locations)) {
            this.features.push({
                type: "Feature",
                properties: {
                    eventID: event.eventID,
                    fastViewDescriptor: TimelineBP.loadFastViewDescriptor(event, false)
                },
                geometry: event.data.locations[key]
            });
            //alternative: L.GeoJSON.asFeature(event.data.locations[key])
        }
    }
}

MapEntry.prototype.showMarkers = function () {
    this.features.forEach(m => {
        leafletMap.geoJsonLayer.addData(m);
        this.markers.push(lastMarker);
    });
    return this;
};

MapEntry.prototype.setMarkerOpacity = function (opacity) {
    this.markers.forEach(m => {
        m.setOpacity(opacity);
    });
};

MapEntry.prototype.hideMarkers = function () {
    this.markers.forEach(m => {
        m.remove();
    });
};

/**
 * Function that is called when a marker is clicked.
 */
function clickOnMarker(e) {
    let eventID = e.target.feature.properties.eventID;
    console.log(`Click on map on event with ID: "${eventID}`);
    clickEditEvent(eventID);
}

/**
 * Function that is called when a feature (visible as marker) is added to the map.
 */
function onEachFeature(feature, layer) {
    //bind click
    layer.on('click', clickOnMarker);
    layer.bindTooltip(feature.properties.fastViewDescriptor[0] + "<br>" + feature.properties.fastViewDescriptor[1]);
}