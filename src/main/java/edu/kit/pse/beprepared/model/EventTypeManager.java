package edu.kit.pse.beprepared.model;

import edu.kit.pse.beprepared.eventTypes.sensorThingsAddLocation.SensorThingsAddLocationEventType;
import edu.kit.pse.beprepared.eventTypes.sensorThingsCreateObservation.SensorThingsCreateObservationEventType;
import edu.kit.pse.beprepared.eventTypes.telegramMessageEvent.MessageEventType;
import edu.kit.pse.beprepared.eventTypes.teamPosition.TeamPositionEventType;
import edu.kit.pse.beprepared.model.frontendDescriptors.FileInputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.GeolocationInputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.InputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.InputFormDescriptor;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class manages all {@link EventType EventTypes} and provides some methods for management.
 */
public class EventTypeManager {

    /**
     * The ONLY instance of this class
     */
    private static EventTypeManager instance = new EventTypeManager();
    /**
     * A Map containing all event types, where the typeName of the type is used as key
     * and the object as value
     */
    private HashMap<String, EventType> eventTypes = new HashMap<>();

    private EventTypeManager() {
        addEventType(new SensorThingsCreateObservationEventType());
        addEventType(new SensorThingsAddLocationEventType());
        addEventType(new MessageEventType());
        addEventType(new TeamPositionEventType());

        /*
        addEventType(new CustomEventType());
         */

    }

    /**
     * Returns the instance of this class.
     *
     * @return the event type manager
     */
    public static EventTypeManager getInstance() {
        return instance;
    }

    private void addEventType(EventType eventType) {
        this.eventTypes.put(eventType.typeName, eventType);
    }

    /**
     * Returns the {@link EventType} with the given typeName.
     *
     * @param name the typeName of the event type
     * @return the object of the event type with the given typeName or {@code null} if no such event type exists
     */
    public EventType getEventTypeByName(final String name) {
        return eventTypes.get(name);
    }

    /**
     * Returns the Map which contains all event types.
     *
     * @return a {@link HashMap} containing event types
     */
    public HashMap<String, EventType> getEventTypes() {
        return eventTypes;
    }

    /**
     * Getter for an {@link InputFormDescriptor} for the configuration.
     * <p>
     * Assembles all fields from {@link EventType#getConfigurationInputFields()}.
     *
     * @return an {@link InputFormDescriptor}
     */
    public InputFormDescriptor getConfigurationDescriptor() {

        LinkedList<InputField> fields = new LinkedList<>();

        this.eventTypes.values().stream().map(EventType::getConfigurationInputFields).forEachOrdered(fieldList -> {

            for (InputField inputField : fieldList) {
                if (inputField instanceof GeolocationInputField || inputField instanceof FileInputField) {
                    throw new UnsupportedOperationException("geolocation- or fileinputfields are not allowed in" +
                            "configuration dialog!");
                }
                boolean contains = false;
                for (InputField field : fields) {
                    if (field.getKey().equals(inputField.getKey())) {
                        contains = true;
                    }
                }
                if (!contains) {
                    fields.add(inputField);
                }
            }

        });

        return new InputFormDescriptor(fields);
    }
}
