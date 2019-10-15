package edu.kit.pse.beprepared.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

/**
 * This class represents the EventSeries JSON-object.
 */
public class EventSeriesJson {

    /*
    Properties of the JSON object
     */
    private int numOfEvents;
    private String type;
    private String pointInTimeFunction;
    private HashMap<String, Object> eventData;

    /**
     * Constructor.
     *
     * @param numOfEvents         the number of events
     * @param type                the type of the events
     * @param pointInTimeFunction the mathematics function to calculate the point in time
     * @param eventData           the event data
     */
    public EventSeriesJson(@JsonProperty(value = "numOfEvents") int numOfEvents,
                           @JsonProperty(value = "type") String type,
                           @JsonProperty(value = "pointInTimeFunction") String pointInTimeFunction,
                           @JsonProperty(value = "eventData") HashMap<String, Object> eventData) {

        this.numOfEvents = numOfEvents;
        this.type = type;
        this.pointInTimeFunction = pointInTimeFunction;
        this.eventData = eventData;

    }

    /**
     * Getter for {@link this#numOfEvents}.
     *
     * @return the value of {@link this#numOfEvents}
     */
    public int getNumOfEvents() {
        return numOfEvents;
    }

    /**
     * Setter for {@link this#numOfEvents}.
     *
     * @param numOfEvents the new value for {@link this#numOfEvents}
     */
    public void setNumOfEvents(int numOfEvents) {
        this.numOfEvents = numOfEvents;
    }

    /**
     * Getter for {@link this#type}.
     *
     * @return the value of {@link this#type}
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for {@link this#type}.
     *
     * @param type the new value for {@link this#type}
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for {@link this#pointInTimeFunction}.
     *
     * @return the value of {@link this#pointInTimeFunction}
     */
    public String getPointInTimeFunction() {
        return pointInTimeFunction;
    }

    /**
     * Setter for {@link this#pointInTimeFunction}.
     *
     * @param pointInTimeFunction the new value for {@link this#pointInTimeFunction}
     */
    public void setPointInTimeFunction(String pointInTimeFunction) {
        this.pointInTimeFunction = pointInTimeFunction;
    }

    /**
     * Getter for {@link this#eventData}.
     *
     * @return the value of {@link this#eventData}
     */
    public HashMap<String, Object> getEventData() {
        return eventData;
    }

    /**
     * Setter for {@link this#eventData}.
     *
     * @param eventData the new value for {@link this#eventData}
     */
    public void setEventData(HashMap<String, Object> eventData) {
        this.eventData = eventData;
    }

    /**
     * Getter for a {@link String} representation of this EventSeriesJson.
     *
     * @return a {@link String} representation of this EventSeriesJson
     */
    @Override
    public String toString() {
        return "EventSeriesJson{" +
                "numOfEvents=" + numOfEvents +
                ", type='" + type + '\'' +
                ", pointInTimeFunction='" + pointInTimeFunction + '\'' +
                ", eventData=" + eventData +
                '}';
    }
}
