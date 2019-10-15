package edu.kit.pse.beprepared.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventTypeManager;

import java.util.HashMap;

/**
 * This class represents a JSON object of type Event.
 */
public class EventJson {

    /*
    Properties of the JSON object:
     */
    private long eventID;
    private String type;
    private long pointInTime;
    private HashMap<String, Object> data;


    /**
     * Constructor.
     * <p>
     * Constructs this EventJson from the corresponding {@link Event}.
     *
     * @param event the corresponding {@link Event}
     */
    public EventJson(final Event event) {

        this.eventID = event.getId();
        this.type = event.getEventTypeInstance().getTypeName();
        this.pointInTime = event.getPointInTime();
        this.data = event.getData();

    }

    /**
     * Instantiates a new {@link EventJson}.
     *
     * @param eventID       the event id
     * @param type          the type
     * @param pointInTime   the point in time
     * @param data          the data
     */
    @JsonCreator
    public EventJson(
            @JsonProperty(value = "eventID") final long eventID,
            @JsonProperty(value = "type") final String type,
            @JsonProperty(value = "pointInTime") final long pointInTime,
            @JsonProperty(value = "data") final HashMap<String, Object> data) {
        this.eventID = eventID;
        this.type = type;
        this.pointInTime = pointInTime;
        this.data = data;
    }

    /**
     * Getter for {@link this#eventID}.
     *
     * @return the event id
     */
    public long getEventID() {
        return eventID;
    }

    /**
     * Setter for {@link this#eventID}.
     *
     * @param eventID the event id
     */
    public void setEventID(final long eventID) {
        this.eventID = eventID;
    }

    /**
     * Getter for {@link this#type}.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for {@link this#type}.
     *
     * @param type the type
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Getter for {@link this#pointInTime}.
     *
     * @return the point in time
     */
    public long getPointInTime() {
        return pointInTime;
    }

    /**
     * Setter for {@link this#pointInTime}.
     *
     * @param pointInTime the point in time
     */
    public void setPointInTime(final long pointInTime) {
        this.pointInTime = pointInTime;
    }

    /**
     * Getter for {@link this#data}.
     *
     * @return the data
     */
    public HashMap<String, Object> getData() {
        return data;
    }

    /**
     * Setter for {@link this#data}.
     *
     * @param data the data
     */
    public void setData(final HashMap<String, Object> data) {
        this.data = data;
    }

    /**
     * Getter for a {@link String} representation of this object.
     *
     * @return a {@link String} representation of this object
     */
    @Override
    public String toString() {
        return "EventJson{" +
                "eventID=" + eventID +
                ", type='" + type + '\'' +
                ", pointInTime=" + pointInTime +
                ", data=" + data +
                '}';
    }
}
