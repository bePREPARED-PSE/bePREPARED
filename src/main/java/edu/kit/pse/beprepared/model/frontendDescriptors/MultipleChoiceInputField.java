package edu.kit.pse.beprepared.model.frontendDescriptors;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An {@link InputField} for multiple choice inputs.
 */
public class MultipleChoiceInputField extends InputField {

    /**
     * The options that can be selected.
     */
    private Collection<String> options;
    /**
     * Whether only one or multiple options can be selected (default: {@code true}).
     */
    private boolean singleChoice;


    /**
     * Constructor.
     * <p>
     * Initializes {@link this#key}.
     *
     * @param key the unique identifier of this input field
     */
    public MultipleChoiceInputField(final String key) {
        super(key, null, true);
        this.options = new LinkedList<>();
        this.singleChoice = true;
    }

    /**
     * Constructor.
     *
     * @param key          the unique identifier of this input field
     * @param options      the options that can be selected
     * @param singleChoice whether one or multiple options can be selected
     */
    public MultipleChoiceInputField(final String key, final Collection<String>options, final boolean singleChoice) {
        super(key, null, true);
        this.options = options;
        this.singleChoice = singleChoice;
    }


    /**
     * Getter for {@link this#options}.
     *
     * @return {@link this#options}
     */
    public Collection<String> getOptions() {
        return new LinkedList<>(this.options);
    }

    /**
     * Setter for {@link this#options}.
     *
     * @param options the new options
     */
    public void setOptions(final Collection<String> options) {
        this.options = new LinkedList<>(options);
    }

    /**
     * Getter for {@link this#singleChoice}.
     *
     * @return {@link this#singleChoice}
     */
    public boolean isSingleChoice() {
        return singleChoice;
    }

    /**
     * Setter for {@link this#singleChoice}.
     *
     * @param singleChoice the new value of {@link this#singleChoice}
     */
    public void setSingleChoice(boolean singleChoice) {
        this.singleChoice = singleChoice;
    }
}
