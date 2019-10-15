package edu.kit.pse.beprepared.json;

import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.frontendDescriptors.FileInputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.GeolocationInputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.MultipleChoiceInputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.NumericInputField;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a JSON object of type EventType.
 */
public class EventTypeJson {

    /*
    Properties of the JSON object:
     */
    private String name;
    private String icon;
    private Collection<XInputFieldJson> inputDescriptor;
    private FastViewDescriptorJson fastViewDescriptor;


    /**
     * Constructor.
     * <p>
     * Constructs a new {@link EventTypeJson} from an {@link edu.kit.pse.beprepared.model.EventType} object.
     *
     * @param type the {@link EventType} this object should map to
     */
    public EventTypeJson(final EventType type) {

        this.name = type.getTypeName();
        this.icon = type.getIco();
        this.fastViewDescriptor = new FastViewDescriptorJson(type.getFastViewDescriptor());
        this.inputDescriptor = new LinkedList<>();

        type.getInputFormDescriptor().getFields().stream().map(f -> {
            if (f instanceof FileInputField) {
                return new FileInputFieldJson((FileInputField) f);
            } else if (f instanceof MultipleChoiceInputField) {
                return new MultipleChoiceInputFieldJson((MultipleChoiceInputField) f);
            } else if (f instanceof NumericInputField) {
                return new NumericInputFieldJson((NumericInputField) f);
            } else {
                return new XInputFieldJson(f);
            }

        }).forEach(this.inputDescriptor::add);

    }

    /**
     * Instantiates a new {@link EventTypeJson}.
     *
     * @param name               the typeName
     * @param icon               the icon
     * @param inputDescriptor    the input descriptor
     * @param fastViewDescriptor the fast view descriptor
     */
    public EventTypeJson(String name, String icon, List<XInputFieldJson> inputDescriptor, FastViewDescriptorJson fastViewDescriptor) {
        this.name = name;
        this.icon = icon;
        this.inputDescriptor = inputDescriptor;
        this.fastViewDescriptor = fastViewDescriptor;
    }

    /**
     * Getter for {@link this#name}.
     *
     * @return the typeName
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for {@link this#name}.
     *
     * @param name the typeName
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for {@link this#icon}.
     *
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Setter for {@link this#icon}.
     *
     * @param icon the icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Getter for {@link this#inputDescriptor}.
     *
     * @return the input descriptor
     */
    public Collection<XInputFieldJson> getInputDescriptor() {
        return inputDescriptor;
    }

    /**
     * Setter for {@link this#inputDescriptor}.
     *
     * @param inputDescriptor the input descriptor
     */
    public void setInputDescriptor(Collection<XInputFieldJson> inputDescriptor) {
        this.inputDescriptor = inputDescriptor;
    }

    /**
     * Getter for {@link this#fastViewDescriptor}.
     *
     * @return the fast view descriptor
     */
    public FastViewDescriptorJson getFastViewDescriptor() {
        return fastViewDescriptor;
    }

    /**
     * Setter for {@link this#fastViewDescriptor}.
     *
     * @param fastViewDescriptor the fast view descriptor
     */
    public void setFastViewDescriptor(FastViewDescriptorJson fastViewDescriptor) {
        this.fastViewDescriptor = fastViewDescriptor;
    }
}
