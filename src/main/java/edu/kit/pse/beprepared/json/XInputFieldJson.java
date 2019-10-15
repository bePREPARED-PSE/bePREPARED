package edu.kit.pse.beprepared.json;

import edu.kit.pse.beprepared.model.frontendDescriptors.InputField;

/**
 * This class is used as super class for all xInputFields, so they can be added into the list in {@link EventTypeJson}.
 */
public class XInputFieldJson {


    /*
        Properties of the JSON object:
         */
    protected String type;
    protected String key;
    protected String defaultValue;
    protected boolean required;

    /**
     * Constructor.
     *
     * @param defaultValue the default value of the corresponding {@link InputField}
     * @param key          the key of the corresponding {@link InputField}
     * @param required     whether this field is required or not
     */
    public XInputFieldJson(String key, String defaultValue, boolean required) {
        this.defaultValue = defaultValue;
        this.key = key;
        this.required = required;
    }

    /**
     * Constructor.
     *
     * @param field the corresponding {@link InputField}
     */
    public XInputFieldJson(InputField field) {
        this.key = field.getKey();
        this.defaultValue = field.getDefaultValue();
        this.type = field.getClass().getSimpleName();
        this.required = field.isRequired();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
