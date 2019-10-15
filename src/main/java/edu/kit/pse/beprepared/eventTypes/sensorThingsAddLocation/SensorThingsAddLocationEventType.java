package edu.kit.pse.beprepared.eventTypes.sensorThingsAddLocation;

import edu.kit.pse.beprepared.json.LocationJson;
import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.model.frontendDescriptors.*;
import edu.kit.pse.beprepared.simulation.RequestRunner;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class is an {@link EventType} to add locations to a thing via the SensorThingsAPI on a FROST server.
 */
public class SensorThingsAddLocationEventType extends EventType {

    /**
     * The {@link Logger} instance used by objects of this class.
     */
    private final Logger log = Logger.getLogger(SensorThingsAddLocationEventType.class);

    /**
     * Initializes {@link this#typeName}, {@link this#ico}, {@link this#fastViewDescriptor},
     * {@link this#inputFormDescriptor}.
     */
    @Override
    protected void init() {

        this.typeName = "sensorthings: Add Location";
        this.ico = "img/geolocation_ico.png";
        this.fastViewDescriptor = new FastViewDescriptor(new LinkedList<>() {{
            add("thingId");
        }});
        this.inputFormDescriptor = new InputFormDescriptor(new LinkedList<>() {{
            add(new InputField("name", "Fraunhofer IOSB", true));
            add(new InputField("description", "Home of bePREPARED", true));
            add(new NumericInputField("thingId", 0, 0, Integer.MAX_VALUE, 1, true));
            add(new GeolocationInputField("geo", true));
        }});
        this.configurationInputFields = new LinkedList<>(){{
           add(new UrlInputField("frostServerUrl", "https://localhost", true));
        }};

    }

    /**
     * Creates a new {@link Event} with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        map with the data from the JSON Object of the event
     * @return the newly created event
     */
    @Override
    public Event createEvent(long pointInTime, HashMap<String, Object> data) {

        String name = (String) data.get("name");
        String description = (String) data.get("description");
        int thingId = (int) data.get("thingId");

        LocationJson location = EventType.getLocationFromEventData("geo", data);

        return new SensorThingsAddLocationEvent(this, pointInTime, name, description, location.getLatitude(),
                location.getLongitude(), thingId);
    }

    /**
     * Replaces the data of the given event with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        the new data for the event
     * @param event       the event that should be edited
     * @return the edited event
     */
    @Override
    public Event editEvent(long pointInTime, HashMap<String, Object> data, Event event) {

        SensorThingsAddLocationEvent e = (SensorThingsAddLocationEvent) event;
        e.setPointInTime(pointInTime);
        e.setName((String) data.get("name"));
        e.setDescription((String) data.get("description"));
        e.setThingId((int) data.get("thingId"));

        LocationJson locationJson = EventType.getLocationFromEventData("geo", data);

        if (locationJson != null) {
            e.setLat(locationJson.getLatitude());
            e.setLon(locationJson.getLongitude());
        }

        return e;
    }

    /**
     * Returns a {@link RequestRunner} for the given event.
     *
     * @param event         the event for which the runner is required
     * @param configuration the {@link Configuration} this {@link RequestRunner} should use
     * @return the request runner for the event
     */
    @Override
    public RequestRunner getRequestRunnerFor(Event event, Configuration configuration) {
        return new SensorThingsAddLocationRequestRunner((SensorThingsAddLocationEvent) event, configuration);
    }
}
