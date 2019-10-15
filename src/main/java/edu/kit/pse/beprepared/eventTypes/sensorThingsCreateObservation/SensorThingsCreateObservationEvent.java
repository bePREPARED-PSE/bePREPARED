package edu.kit.pse.beprepared.eventTypes.sensorThingsCreateObservation;

import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.Scenario;

import java.util.HashMap;

/**
 * This class represents an {@link Event} for the sensorthings API. Precisely it represents an {@code Observation}.
 */
public class SensorThingsCreateObservationEvent extends Event {

    /**
     * The id of the {@code Datastream} the newly created {@code Observation} should belong to.
     */
    private int dataStreamId;
    /**
     * The value for the field {@code result} of the newly created {@code Observation} (e.g. the measured temperature
     * value).
     */
    private String result;


    /**
     * Constructor
     * <p>
     * Creates a new {@link Event} with the given point in time. Automatically assigns an id.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     */
    public SensorThingsCreateObservationEvent(EventType eventType, long pointInTime, int dataStreamId, String result) {
        super(eventType, pointInTime);
        this.dataStreamId = dataStreamId;
        this.result = result;
    }

    /**
     * Getter for all the attributes of this event as key-value-pairs.
     *
     * @return all the attributes of this event as key-value-pairs
     */
    @Override
    public HashMap<String, Object> getData() {
        return new HashMap<>() {{

            put("pointInTime", getPointInTime());
            put("dataStreamId", dataStreamId);
            put("result", result);

        }};
    }

    /**
     * Getter for {@link this#dataStreamId}.
     *
     * @return {@link this#dataStreamId}
     */
    public int getDataStreamId() {
        return dataStreamId;
    }

    /**
     * Setter for {@link this#dataStreamId}.
     *
     * @param dataStreamId the new value for {@link this#dataStreamId}
     */
    public void setDataStreamId(int dataStreamId) {
        this.dataStreamId = dataStreamId;
    }

    /**
     * Getter fo {@link this#result}.
     *
     * @return {@link this#result}
     */
    public String getResult() {
        return result;
    }

    /**
     * Setter for {@link this#result}.
     *
     * @param result the new value for {@link this#result}
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Getter for a {@link String}-representation of this object
     *
     * @return a string-representation of this object
     */
    @Override
    public String toString() {
        return super.toString() + "->SensorThingsCreateObservationEvent{" +
                "dataStreamId=" + dataStreamId +
                ", result='" + result + '\'' +
                '}';
    }
}
