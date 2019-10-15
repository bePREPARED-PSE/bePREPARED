package edu.kit.pse.beprepared.model.frontendDescriptors;

/**
 * This class represents an {@link InputField} for a geo location.
 */
public class GeolocationInputField extends InputField {

    /**
     * Constructor.
     * <p>
     * Initializes {@link this#key}.
     *
     * @param key      the unique identifier of this input field
     * @param required whether this field is required or not
     */
    public GeolocationInputField(final String key, final boolean required) {
        super(key, null, required);
    }

}
