package edu.kit.pse.beprepared.model.frontendDescriptors;

import java.util.Collection;
import java.util.LinkedList;

/**
 * This class represents an {@link InputField} for a file.
 */
public class FileInputField extends InputField {

    /**
     * A collection of allowed file types.
     */
    private Collection<String> allowedFileTypes;
    /**
     * The maximum allowed file size.
     */
    private int maxFileSize;


    /**
     * Constructor.
     * <p>
     * Initializes {@link this#key}.
     *
     * @param key      the unique identifier of this input field
     * @param required whether this field is required or not
     */
    public FileInputField(final String key, final boolean required) {
        super(key, null, required);
    }

    /**
     * Constructor.
     *
     * @param key              the unique identifier of this input field
     * @param allowedFileTypes the collection of allowed file types
     * @param maxFileSize      the maximum allowed file size
     * @param required         whether this field is required or not
     */
    public FileInputField(final String key, final Collection<String> allowedFileTypes, final int maxFileSize,
                          final boolean required) {
        super(key, null, required);
        this.allowedFileTypes = allowedFileTypes;
        this.maxFileSize = maxFileSize;
    }


    /**
     * Getter for {@link this#allowedFileTypes}.
     *
     * @return a copy of {@link this#allowedFileTypes}
     */
    public Collection<String> getAllowedFileTypes() {
        return this.allowedFileTypes != null ? new LinkedList<>(this.allowedFileTypes) : new LinkedList<>() {{
            add("*/*");
        }};
    }

    /**
     * Setter for {@link this#allowedFileTypes}.
     *
     * @param allowedFileTypes the new value for {@link this#allowedFileTypes}
     */
    public void setAllowedFileTypes(Collection<String> allowedFileTypes) {
        this.allowedFileTypes = new LinkedList<>(allowedFileTypes);
    }

    /**
     * Getter for {@link this#maxFileSize}.
     *
     * @return {@link this#maxFileSize}
     */
    public int getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * Setter for {@link this#maxFileSize}.
     *
     * @param maxFileSize the new value for {@link this#maxFileSize}
     */
    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
