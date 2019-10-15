package edu.kit.pse.beprepared.model.frontendDescriptors;

/**
 * i
 * This class represents an {@link InputField} for numeric values.
 */
public class NumericInputField extends InputField {

    /**
     * The minimum value this field can have.
     */
    private Number minVal;
    /**
     * The maximum value this field can have.
     */
    private Number maxVal;
    /**
     * The step size that should be applied, when increment/decrement buttons are used.
     */
    private Number stepSize;


    /**
     * Constructor.
     * <p>
     * Initializes {@link this#key}.
     *
     * @param key          the unique identifier of this input field
     * @param defaultValue the default value for this field
     * @param required     whether this field is required or not
     */
    public NumericInputField(final String key, final Number defaultValue, final boolean required) {
        super(key, String.valueOf(defaultValue), required);
    }

    /**
     * Constructor.
     *
     * @param key          the unique identifier of this input field
     * @param defaultValue the default value for this field
     * @param minVal       the minimum value this field can have
     * @param maxVal       the maximum value this field can have
     * @param stepSize     the step size that should be applied, when increment/decrement buttons are used
     */
    public NumericInputField(final String key, final Number defaultValue,
                             final Number minVal, final Number maxVal, final Number stepSize, final boolean required) {
        super(key, String.valueOf(defaultValue), required);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.stepSize = stepSize;
    }


    /**
     * Getter for {@link this#minVal}.
     *
     * @return {@link this#minVal}
     */
    public Number getMinVal() {
        return minVal;
    }

    /**
     * Setter for {@link this#minVal}.
     *
     * @param minVal the new value for {@link this#minVal}
     */
    public void setMinVal(Number minVal) {
        this.minVal = minVal;
    }

    /**
     * Getter for {@link this#maxVal}.
     *
     * @return {@link this#maxVal}
     */
    public Number getMaxVal() {
        return maxVal;
    }

    /**
     * Setter for {@link this#maxVal}.
     *
     * @param maxVal the new value for {@link this#maxVal}
     */
    public void setMaxVal(Number maxVal) {
        this.maxVal = maxVal;
    }

    /**
     * Getter for {@link this#stepSize}.
     *
     * @return {@link this#stepSize}
     */
    public Number getStepSize() {
        return stepSize;
    }

    /**
     * Setter for {@link this#stepSize}.
     *
     * @param stepSize the new value for {@link this#stepSize}
     */
    public void setStepSize(Number stepSize) {
        this.stepSize = stepSize;
    }
}
