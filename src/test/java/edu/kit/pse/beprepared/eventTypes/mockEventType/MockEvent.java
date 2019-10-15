package edu.kit.pse.beprepared.eventTypes.mockEventType;

import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.Scenario;

import java.util.HashMap;

public class MockEvent extends Event {

    public static final String PROPERTY_KEY = "bouncyCats";

    private String aProperty;

    /**
     * Constructor
     * <p>
     * Creates a new {@link Event} with the given point in time. Automatically assigns an id.
     *
     * @param eventType   the {@link EventType} associated with this event
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     */
    public MockEvent(EventType eventType, long pointInTime, String aProperty) {
        super(eventType, pointInTime);
        this.aProperty = aProperty;
    }

    /**
     * Getter for {@link this#aProperty}.
     *
     * @return the value of {@link this#aProperty}
     */
    public String getaProperty() {
        return aProperty;
    }

    /**
     * Setter for {@link this#aProperty}.
     *
     * @param aProperty the new value for {@link this#aProperty}
     */
    public void setaProperty(String aProperty) {
        this.aProperty = aProperty;
    }

    /**
     * Getter for all the attributes of this event as key-value-pairs.
     *
     * @return all the attributes of this event as key-value-pairs
     */
    @Override
    public HashMap<String, Object> getData() {
        return new HashMap<>() {{

            put(MockEvent.PROPERTY_KEY, aProperty);

        }};
    }
}
