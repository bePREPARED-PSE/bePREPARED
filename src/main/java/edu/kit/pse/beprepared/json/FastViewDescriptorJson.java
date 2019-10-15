package edu.kit.pse.beprepared.json;

import edu.kit.pse.beprepared.model.frontendDescriptors.FastViewDescriptor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a JSON object of type FastViewDescriptor.
 */
public class FastViewDescriptorJson {

    /*
    Properties of the JSON object:
     */
    private List<String> displayKeys;
    private String mediaKey;


    /**
     * Constructor.
     * <p>
     * Constructs a new {@link FastViewDescriptorJson} from the supplied {@link FastViewDescriptor}.
     *
     * @param descriptor the {@link FastViewDescriptor} this object should map to
     */
    public FastViewDescriptorJson(FastViewDescriptor descriptor) {
        this(descriptor.getDisplayKeys(), descriptor.getMediaKey());
    }

    /**
     * Instantiates a new {@link FastViewDescriptorJson}.
     *
     * @param displayKeys the value keys
     * @param mediaKey    the key of the media object
     */
    public FastViewDescriptorJson(List<String> displayKeys, String mediaKey) {
        this.displayKeys = displayKeys;
        this.mediaKey = mediaKey;
    }

    /**
     * Returns the value of {@link this#mediaKey}.
     *
     * @return a boolean
     */
    public String getMediaKey() {
        return mediaKey;
    }

    /**
     * Setter for {@link this#mediaKey}.
     *
     * @param mediaKey the media key
     */
    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    /**
     * Getter for {@link this#displayKeys}.
     *
     * @return the value keys
     */
    public List<String> getDisplayKeys() {
        return displayKeys;
    }

    /**
     * Setter for {@link this#displayKeys}.
     *
     * @param displayKeys the value keys
     */
    public void setDisplayKeys(List<String> displayKeys) {
        this.displayKeys = displayKeys;
    }
}
