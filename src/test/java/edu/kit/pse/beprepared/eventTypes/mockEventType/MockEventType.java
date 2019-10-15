package edu.kit.pse.beprepared.eventTypes.mockEventType;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.model.frontendDescriptors.FastViewDescriptor;
import edu.kit.pse.beprepared.model.frontendDescriptors.InputFormDescriptor;
import edu.kit.pse.beprepared.simulation.RequestRunner;

import java.util.HashMap;
import java.util.LinkedList;

public class MockEventType extends EventType {

    public static final String TYPE_NAME = "MockEventType";

    /**
     * Initializes {@link this#typeName}, {@link this#ico}, {@link this#fastViewDescriptor},
     * {@link this#inputFormDescriptor}.
     */
    @Override
    protected void init() {

        this.typeName = MockEventType.TYPE_NAME;
        this.ico = null;
        this.fastViewDescriptor = new FastViewDescriptor(new LinkedList<>());
        this.inputFormDescriptor = new InputFormDescriptor(new LinkedList<>());
        this.configurationInputFields = new LinkedList<>();

    }

    /**
     * Creates a new {@link Event} with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        map with the data from the JSON Object of the event
     * @return the newly created event
     */
    @Override
    public MockEvent createEvent(long pointInTime, HashMap<String, Object> data) {

        return new MockEvent(this, pointInTime, data.get(MockEvent.PROPERTY_KEY).toString());
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
    public MockEvent editEvent(long pointInTime, HashMap<String, Object> data, Event event) {

        MockEvent e = (MockEvent) event;
        e.setPointInTime(pointInTime);
        e.setaProperty(data.get(MockEvent.PROPERTY_KEY).toString());

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
        return new MockEventRunner(event, configuration);
    }
}
