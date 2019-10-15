package edu.kit.pse.beprepared.model;

import edu.kit.pse.beprepared.json.PhaseJson;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a phase.
 */
public class Phase {

    /**
     * Next ID.
     */
    private static int nextID = 0;


    /**
     * The id of this phase.
     */
    private final int id;

    /**
     * The typeName of this phase.
     */
    private String name;

    /**
     * The list of events that belong to this phase.
     */
    private LinkedList<Event> events;


    /**
     * Constructor.
     * <p>
     * Constructs a new phase and assigns an ID to it.
     */
    public Phase() {

        this.id = Phase.nextID++;
        this.events = new LinkedList<>();
    }

    /**
     * Constructor.
     * <p>
     * Constructs a new phase, assigns an ID to it and gives it the supplied typeName.
     *
     * @param name the typeName this phase should have
     */
    public Phase(final String name) {

        this();
        this.name = name;

    }

    /**
     * Constructor.
     * <p>
     * Constructs a new phase from the supplied {@link PhaseJson}. This constructor is especially useful for importing
     * phases.
     *
     * @param phaseJson the JSON representation of the phase
     */
    public Phase(PhaseJson phaseJson) {

        this(phaseJson.getName());

        phaseJson.getEvents().stream().map(eventJson -> EventTypeManager.getInstance().getEventTypeByName(
                eventJson.getType()).createEvent(eventJson.getPointInTime(), eventJson.getData()))
                .forEach(this.events::add);

    }


    /**
     * Add the supplied {@link Event} to this phase.
     *
     * @param e the event that should be added to this phase
     */
    public void addEvent(final Event e) {

        this.events.add(e);

    }

    /**
     * Add all events from the supplied collection to this phase.
     *
     * @param events the events that should be added to this phase
     */
    public void addEvents(final Collection<Event> events) {

        this.events.addAll(events);

    }

    /**
     * Remove the {@link Event} with the supplied id from this phase.
     *
     * @param id the id of the event that should be removed
     * @return the removed event
     */
    public Event removeEvent(final long id) {

        Event e = null;
        for (Event event : this.events) {
            if (event.getId() == id) {
                e = event;
                break;
            }
        }

        if (e == null) {
            // element with supplied id not found
            return null;
        }

        this.events.remove(e);

        return e;
    }

    /**
     * Get the {@link Event} with the supplied id.
     *
     * @param id the id of the requested element
     * @return the requested {@link Event} or {@code null}, if no event with the supplied id exists
     */
    public Event getEventById(final long id) {

        for (Event event : this.events) {
            if (event.getId() == id) {
                return event;
            }
        }

        return null;
    }

    /**
     * Shifts all events in this phase by the time difference t.
     *
     * @param t the time difference in milliseconds
     * @throws IllegalArgumentException if the shift would shift at least one event to have a relative time
     *                                  smaller than zero. In this case, no event-times are changed.
     */
    public void shiftEventTimes(final long t) {

        this.events.forEach(e -> {
            if (e.getPointInTime() + t < 0) {
                throw new IllegalArgumentException("Can't shift events to have a relative time smaller than zero!");
            }
        });

        this.events.forEach(e -> e.setPointInTime(e.getPointInTime() + t));

    }

    /**
     * Getter for a list of all events.
     *
     * @return a {@link List<Event>} of all events of this phase
     */
    public List<Event> getAllEvents() {

        return new LinkedList<>(this.events);

    }

    /**
     * Getter for the id of this phase.
     *
     * @return the id of this phase
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the typeName of this phase.
     *
     * @return the typeName of this phase
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for a {@link String} representation of this phase.
     *
     * @return a {@link String} representation of this phase
     */
    @Override
    public String toString() {
        return "Phase{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", events=" + events +
                '}';
    }
}
