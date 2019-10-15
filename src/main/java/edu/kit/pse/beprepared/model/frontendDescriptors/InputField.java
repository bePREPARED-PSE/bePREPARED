package edu.kit.pse.beprepared.model.frontendDescriptors;

/**
 * This class represents an input field of the frontend.
 */
public class InputField {

    /**
     * Unique identifier of this input field.
     */
    protected final String key;

    /**
     * The tooltip that should be displayed for this input field.
     */
    protected String tooltip;

    /**
     * The default value of this field.
     */
    protected String defaultValue;

    /**
     * Whether this field is required or not.
     */
    protected boolean required;

    /**
     * Constructor.
     * <p>
     * Initializes {@link this#key}.
     *
     * @param key          the unique identifier of this input field
     * @param defaultValue the default value for this field
     * @param required     whether this field is required or not
     */
    public InputField(final String key, final String defaultValue, final boolean required) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.required = required;
    }

    /**
     * Getter for {@link this#key}.
     *
     * @return {@link this#key}
     */
    public String getKey() {
        return key;
    }

    /**
     * Getter for {@link this#defaultValue}.
     *
     * @return {@link this#defaultValue}
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Setter for {@link this#defaultValue}.
     *
     * @param defaultValue the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for {@link this#required}.
     *
     * @return the value of {@link this#required}
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Setter for {@link this#required}.
     *
     * @param required the new value for {@link this#required}
     */
    public void setRequired(boolean required) {
        this.required = required;
    }
}
