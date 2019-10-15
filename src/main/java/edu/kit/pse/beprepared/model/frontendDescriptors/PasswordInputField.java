package edu.kit.pse.beprepared.model.frontendDescriptors;

/**
 * An {@link InputField} used to enter passwords.
 */
public class PasswordInputField extends InputField {

    /**
     * Constructor.
     * <p>
     * Initializes {@link this#key}.
     *
     * @param key          the unique identifier of this input field
     * @param defaultValue the default value for this field
     * @param required whether this field is required or not
     */
    public PasswordInputField(String key, String defaultValue, final boolean required) {
        super(key, defaultValue, required);
    }
}
