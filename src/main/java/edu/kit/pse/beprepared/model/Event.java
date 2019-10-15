package edu.kit.pse.beprepared.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class represents the abstract construct of an event. To make specific events, extend this class.
 */
public abstract class Event implements Comparable<Event> {

    /**
     * Next, highest ID.
     */
    private static long nextID = 0;

    /**
     * ID of the event.
     */
    private final long id;
    private final EventType eventType;

    /**
     * Point in time of the event relative to the start of the {@link Scenario} in ms.
     */
    private long pointInTime;

    /**
     * Constructor
     * <p>
     * Creates a new {@link Event} with the given point in time. Automatically assigns an id.
     *
     * @param eventType   the {@link EventType} associated with this event
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     */
    public Event(final EventType eventType, final long pointInTime) {
        this.eventType = eventType;
        this.pointInTime = pointInTime;
        this.id = nextID++;
    }

    /**
     * Gets the id of the event.
     *
     * @return id of the event
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the point in time of the event.
     *
     * @return point in time of the evnt
     */
    public long getPointInTime() {
        return pointInTime;
    }

    /**
     * Sets the point in time of the event to the given value.
     *
     * @param pointInTime the new point in time of the event relative to the start of the {@link Scenario} in ms.
     */
    public void setPointInTime(final long pointInTime) {
        this.pointInTime = pointInTime;
    }

    /**
     * Getter for all the attributes of this event as key-value-pairs.
     *
     * @return all the attributes of this event as key-value-pairs
     */
    public abstract HashMap<String, Object> getData();

    /**
     * Getter for the keys of the attached files, if any.
     *
     * @return a {@link Collection<String>} containing all the keys of attached files
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getAttachedFileKeys() {

        HashMap<String, String> files = (HashMap<String, String>) getData().get("files");

        return files == null ? new LinkedList<>() : files.values();
    }

    /**
     * Return the instance of the associated EventType
     *
     * @return Instance representing the corresponding eventType
     */
    public EventType getEventTypeInstance() {
        return eventType;
    }

    ;

    /**
     * Compares this event with the given one by the point in time.
     *
     * @param e the {@link Event event} to compare with
     * @return a value < 0 if this event takes place before the event e, a value > 0 if this event takes place after
     * the event e. The value 0 if both events take place at the same time.
     */
    @Override
    public int compareTo(final Event e) {
        return Long.compare(this.pointInTime, e.pointInTime);
    }

    /**
     * Getter for a {@link String} representation of this event.
     *
     * @return a {@link String} representation of this event
     */
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", pointInTime=" + pointInTime +
                '}';
    }
}
