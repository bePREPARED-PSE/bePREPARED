package edu.kit.pse.beprepared.eventTypes.sensorThingsCreateObservation;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.model.frontendDescriptors.FastViewDescriptor;
import edu.kit.pse.beprepared.model.frontendDescriptors.InputFormDescriptor;
import edu.kit.pse.beprepared.model.frontendDescriptors.NumericInputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.UrlInputField;
import edu.kit.pse.beprepared.simulation.RequestRunner;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class is an {@link EventType} to create sensor events for the "SensorThins API" on a FROST-Server.
 */
public class SensorThingsCreateObservationEventType extends EventType {

    /**
     * The {@link Logger} used by objects of this type.
     */
    private final Logger log = Logger.getLogger(SensorThingsCreateObservationEventType.class);

    /**
     * Initializes {@link this#typeName}, {@link this#ico}, {@link this#fastViewDescriptor},
     * {@link this#inputFormDescriptor}.
     */
    @Override
    protected void init() {

        this.typeName = "sensorthings: Create Observation";
        this.ico = "https://cdn2.iconfinder.com/data/icons/innovation-technology-1/512/tech_0007-512.png";
        this.fastViewDescriptor = new FastViewDescriptor(
                new LinkedList<>() {{
                    add("dataStreamId");
                    add("result");
                }});

        NumericInputField dataStreamIdInputField = new NumericInputField("dataStreamId", 0, 0, Integer.MAX_VALUE, 1,
                true);
        NumericInputField resultInputField = new NumericInputField("result", 0, Integer.MIN_VALUE, Integer.MAX_VALUE,
                0.1, true);
        this.inputFormDescriptor = new InputFormDescriptor(new LinkedList<>() {{
            add(dataStreamIdInputField);
            add(resultInputField);
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
    public SensorThingsCreateObservationEvent createEvent(long pointInTime, HashMap<String, Object> data) {

        int dataStreamId = Integer.parseInt(data.get("dataStreamId").toString());
        String result = data.get("result").toString();

        return new SensorThingsCreateObservationEvent(this, pointInTime, dataStreamId, result);
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
    public SensorThingsCreateObservationEvent editEvent(long pointInTime, HashMap<String, Object> data, Event event) {

        SensorThingsCreateObservationEvent e = (SensorThingsCreateObservationEvent) event;

        e.setPointInTime(pointInTime);
        e.setDataStreamId((int) data.get("dataStreamId"));
        e.setResult(data.get("result").toString());

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

        return new SensorThingsCreateObservationRequestRunner(event, configuration);
    }
}
