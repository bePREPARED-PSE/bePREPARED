package edu.kit.pse.beprepared.model.frontendDescriptors;

import java.util.LinkedList;

/**
 * This class represents the input form of the frontend.
 */
public class InputFormDescriptor {

    /**
     * The {@link InputField}s that should be displayed.
     */
    private final LinkedList<InputField> fields;

    /**
     * Constructor.
     * <p>
     * Initializes {@link this#fields}
     */
    public InputFormDescriptor(final LinkedList<InputField> fields) {
        if (fields == null) {
            throw new NullPointerException("fields must not be null!");
        }
        this.fields = fields;
    }

    /**
     * Getter for the list of fields.
     *
     * @return a copy of {@link this#fields}
     */
    public LinkedList<InputField> getFields() {
        return new LinkedList<>(this.fields);
    }

}
