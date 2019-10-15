package edu.kit.pse.beprepared.model;

import edu.kit.pse.beprepared.json.LocationJson;
import edu.kit.pse.beprepared.model.frontendDescriptors.FastViewDescriptor;
import edu.kit.pse.beprepared.model.frontendDescriptors.InputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.InputFormDescriptor;
import edu.kit.pse.beprepared.simulation.RequestRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class represents the abstract construct of an event type. It has at least an unique typeName and an icon.
 * To make specific event types, extend this class and override the abstract methods which define how to create
 * {@link Event} and {@link RequestRunner} objects.
 */
public abstract class EventType {

    /**
     * The unique typeName of the event type. This must mach the key of this event type in the {@link EventTypeManager}s
     * map of event types.
     */
    protected String typeName;
    /**
     * The {@link FastViewDescriptor} for this EventType.
     */
    protected FastViewDescriptor fastViewDescriptor;
    /**
     * The {@link InputFormDescriptor} of this EventType.
     */
    protected InputFormDescriptor inputFormDescriptor;
    /**
     * The resource URL of the icon for the EventType.
     */
    protected String ico;
    /**
     * A {@link Collection<InputField>} of {@link InputField}s needed to configure this event type.
     * <p>
     * Note that event types can share fields by declaring them with the same key.
     */
    protected Collection<InputField> configurationInputFields;


    /**
     * Constructor
     * <p>
     * Creates a new {@link EventType}.
     */
    public EventType() {
        this.init();
        if (this.typeName == null || this.typeName.isEmpty() || this.typeName.isBlank()) {
            throw new NullPointerException("typeName must not be null or empty or blank!");
        } else if (this.fastViewDescriptor == null) {
            throw new NullPointerException("fastViewDescriptor must not be null!");
        } else if (this.inputFormDescriptor == null) {
            throw new NullPointerException("inputFormDescriptor must not be null!");
        } else if (this.configurationInputFields == null) {
            throw new NullPointerException("configurationFormDescriptor must not be null!");
        }
    }

    /**
     * Extracts the location with the specified key from {@link Event#getData()}.
     *
     * @param key  the key of the location that should be extracted
     * @param data the data field of the {@link Event}
     * @return the location as {@link LocationJson} or {@code null}, if the location with the specified key can not be found
     */
    @SuppressWarnings("unchecked")
    protected static LocationJson getLocationFromEventData(final String key, final HashMap<String, Object> data) {

        HashMap<String, Object> location = (HashMap<String, Object>) ((HashMap<String, Object>) data.get("locations")).get(key);

        if (location == null) {
            return null;
        }

        String type = (String) location.get("type");
        double longitude = ((ArrayList<Number>) location.get("coordinates")).get(0).doubleValue();
        double latitude = ((ArrayList<Number>) location.get("coordinates")).get(1).doubleValue();

        return new LocationJson(type, new double[] {longitude, latitude});
    }

    /**
     * Extract a specific file key from the data object.
     *
     * @param key  the key of the file-key that should be extracted
     * @param data the event data
     * @return the key
     */
    @SuppressWarnings("unchecked")
    protected static String getFileNameFromEventData(final String key, final HashMap<String, Object> data) {

        HashMap<String, String> files = (HashMap<String, String>) data.get("files");

        if (files == null || files.get(key) == null) {
            return null;
        }

        return files.get(key).isEmpty() ? null : files.get(key);

    }

    /**
     * Initializes {@link this#typeName}, {@link this#ico}, {@link this#fastViewDescriptor},
     * {@link this#inputFormDescriptor}.
     */
    protected abstract void init();

    /**
     * Creates a new {@link Event} with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        map with the data from the JSON Object of the event
     * @return the newly created event
     */
    public abstract Event createEvent(final long pointInTime, final HashMap<String, Object> data);

    /**
     * Replaces the data of the given event with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        the new data for the event
     * @param event       the event that should be edited
     * @return the edited event
     */
    public abstract Event editEvent(final long pointInTime, final HashMap<String, Object> data, final Event event);

    /**
     * Returns a {@link RequestRunner} for the given event.
     *
     * @param event         the event for which the runner is required
     * @param configuration the {@link Configuration} this {@link RequestRunner} should use
     * @return the request runner for the event
     */
    public abstract RequestRunner getRequestRunnerFor(final Event event, final Configuration configuration);

    /**
     * Returns the typeName of this event type.
     *
     * @return the typeName of the event type
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Returns the URL of the icon from this event type.
     *
     * @return the URL of the icon
     */
    public String getIco() {
        return ico;
    }

    /**
     * Getter for {@link this#fastViewDescriptor}.
     *
     * @return {@link this#fastViewDescriptor}
     */
    public FastViewDescriptor getFastViewDescriptor() {
        return fastViewDescriptor;
    }

    /**
     * Getter for {@link this#inputFormDescriptor}.
     *
     * @return {@link this#inputFormDescriptor}
     */
    public InputFormDescriptor getInputFormDescriptor() {
        return inputFormDescriptor;
    }

    /**
     * Getter for {@link this#configurationInputFields}.
     *
     * @return the value of {@link this#configurationInputFields}
     */
    public Collection<InputField> getConfigurationInputFields() {
        return configurationInputFields;
    }
}
