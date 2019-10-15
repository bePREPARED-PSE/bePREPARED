package edu.kit.pse.beprepared.json;

import edu.kit.pse.beprepared.model.frontendDescriptors.MultipleChoiceInputField;

import java.util.Collection;

/**
 * This class represents a JSON object of type MultipleChocieInputField.
 */
public class MultipleChoiceInputFieldJson extends XInputFieldJson {

    /*
    Properties of the JSON object:
     */
    private boolean singleChoice;
    private Collection<String> options;


    /**
     * Constructor.
     *
     * @param field the {@link edu.kit.pse.beprepared.model.frontendDescriptors.InputField} this object should map to
     */
    public MultipleChoiceInputFieldJson(final MultipleChoiceInputField field) {
        super(field);
        this.singleChoice = field.isSingleChoice();
        this.options = field.getOptions();
    }

    /**
     * Returns the value of {@link this#singleChoice}.
     *
     * @return a boolean
     */
    public boolean isSingleChoice() {
        return singleChoice;
    }

    /**
     * Setter for {@link this#singleChoice}.
     *
     * @param singleChoice the single choice
     */
    public void setSingleChoice(boolean singleChoice) {
        this.singleChoice = singleChoice;
    }

    /**
     * Getter for {@link this#options}.
     *
     * @return the options
     */
    public Collection<String> getOptions() {
        return options;
    }

    /**
     * Setter for {@link this#options}.
     *
     * @param options the options
     */
    public void setOptions(Collection<String> options) {
        this.options = options;
    }
}
