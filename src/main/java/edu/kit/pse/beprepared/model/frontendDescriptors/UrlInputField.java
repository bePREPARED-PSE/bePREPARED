package edu.kit.pse.beprepared.model.frontendDescriptors;

/**
 * This class represents an {@link InputField} for a URL.
 */
public class UrlInputField extends InputField {

    /**
     * Constructor.
     * <p>
     * Initializes {@link this#key}.
     *
     * @param key          the unique identifier of this input field
     * @param defaultValue the default value
     * @param required     whether this field is required or not
     */
    public UrlInputField(final String key, final String defaultValue, final boolean required) {
        super(key, defaultValue, required);
    }

}
