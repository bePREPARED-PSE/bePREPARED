package edu.kit.pse.beprepared.eventTypes.sensorThingsAddLocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.pse.beprepared.json.LocationJson;
import edu.kit.pse.beprepared.model.Event;

import edu.kit.pse.beprepared.model.EventType;
import java.util.HashMap;

public class SensorThingsAddLocationEvent extends Event {

    /**
     * The name of the location.
     */
    private String name;
    /**
     * The latitude.
     */
    private double lat;
    /**
     * The longitude.
     */
    private double lon;
    /**
     * The description of this location.
     */
    private String description;
    /**
     * The id of the thing this location should belong to.
     */
    private int thingId;

    /**
     * Constructor.
     *
     * @param pointInTime the relative point in time
     * @param lat         the latitude
     * @param lon         the longitude
     */
    public SensorThingsAddLocationEvent(EventType eventType, long pointInTime, String name, String description, double lat, double lon,
                                        int thingId) {
        super(eventType, pointInTime);
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.thingId = thingId;
    }

    /**
     * Getter for {@link this#thingId}.
     *
     * @return the value of {@link this#thingId}
     */
    public int getThingId() {
        return thingId;
    }

    /**
     * Setter for {@link this#thingId}.
     *
     * @param thingId the new value for {@link this#thingId}
     */
    public void setThingId(int thingId) {
        this.thingId = thingId;
    }

    /**
     * Getter for {@link this#name}.
     *
     * @return the value of {@link this#name}
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for {@link this#name}.
     *
     * @param name the new value for {@link this#name}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for {@link this#description}.
     *
     * @return the value of {@link this#description}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for {@link this#description}.
     *
     * @param description the value of {@link this#description}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for {@link this#lat}.
     *
     * @return the value of {@link this#lat}
     */
    public double getLat() {
        return lat;
    }

    /**
     * Setter for {@link this#lat}.
     *
     * @param lat the new value for {@link this#lat}
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Getter for {@link this#lon}.
     *
     * @return the value of {@link this#lon}
     */
    public double getLon() {
        return lon;
    }

    /**
     * Setter for {@link this#lon}.
     *
     * @param lon the new value for {@link this#lon}
     */
    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * Getter for all the attributes of this event as key-value-pairs.
     *
     * @return all the attributes of this event as key-value-pairs
     */
    @Override
    public HashMap<String, Object> getData() {
        return new HashMap<>() {{
            put("name", name);
            put("description", description);
            put("thingId", thingId);
            put("locations", new HashMap<String, Object>() {{
                put("geo", new LocationJson("Point", new double[] {lon, lat}));
            }});
        }};
    }

    /**
     * Getter for a {@link String} representation of this event.
     *
     * @return a {@link String} representation of this event
     */
    @Override
    public String toString() {
        return "SensorThingsAddLocationEvent{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", description='" + description + '\'' +
                ", thingId=" + thingId +
                '}';
    }
}
