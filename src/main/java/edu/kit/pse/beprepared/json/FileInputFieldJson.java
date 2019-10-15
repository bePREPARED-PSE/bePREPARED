package edu.kit.pse.beprepared.json;

import edu.kit.pse.beprepared.model.frontendDescriptors.FileInputField;
import edu.kit.pse.beprepared.model.frontendDescriptors.InputField;

import java.util.Collection;
import java.util.List;

/**
 * This class represents a JSON object of type FileInputField.
 */
public class FileInputFieldJson extends XInputFieldJson {

    /*
    Properties of the JSON object:
     */
    private Collection<String> allowedFileTypes;
    private int maxFileSize;


    public FileInputFieldJson(final FileInputField inputField) {
        super(inputField);
        this.allowedFileTypes = inputField.getAllowedFileTypes();
        this.maxFileSize = inputField.getMaxFileSize();
    }

    /**
     * Getter for {@link this#allowedFileTypes}.
     *
     * @return the allowed file types
     */
    public Collection<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    /**
     * Setter for {@link this#allowedFileTypes}.
     *
     * @param allowedFileTypes the allowed file types
     */
    public void setAllowedFileTypes(List<String> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    /**
     * Getter for {@link this#maxFileSize}.
     *
     * @return the max file size
     */
    public int getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * Setter for {@link this#maxFileSize}.
     *
     * @param maxFileSize the max file size
     */
    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
