package edu.kit.pse.beprepared.json;

import edu.kit.pse.beprepared.model.frontendDescriptors.NumericInputField;

/**
 * This class represents a JSON object of type NumericInputField.
 */
public class NumericInputFieldJson extends XInputFieldJson {

    /*
    Properties of the JSON object:
     */
    private Number minValue;
    private Number maxValue;
    private Number stepSize;


    /**
     * Constructor.
     *
     * @param field the {@link NumericInputField} this object should map to
     */
    public NumericInputFieldJson(final NumericInputField field) {
        super(field);
        this.minValue = field.getMinVal();
        this.maxValue = field.getMaxVal();
        this.stepSize = field.getStepSize();
    }

    /**
     * Getter for {@link this#minValue}.
     *
     * @return the min value
     */
    public Number getMinValue() {
        return minValue;
    }

    /**
     * Setter for {@link this#minValue}.
     *
     * @param minValue the min value
     */
    public void setMinValue(Number minValue) {
        this.minValue = minValue;
    }

    /**
     * Getter for {@link this#maxValue}.
     *
     * @return the max value
     */
    public Number getMaxValue() {
        return maxValue;
    }

    /**
     * Setter for {@link this#maxValue}.
     *
     * @param maxValue the max value
     */
    public void setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Getter for {@link this#stepSize}.
     *
     * @return the step size
     */
    public Number getStepSize() {
        return stepSize;
    }

    /**
     * Setter for {@link this#stepSize}.
     *
     * @param stepSize the step size
     */
    public void setStepSize(Number stepSize) {
        this.stepSize = stepSize;
    }
}
