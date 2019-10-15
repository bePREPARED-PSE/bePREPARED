package edu.kit.pse.beprepared.model.frontendDescriptors;

import java.util.LinkedList;
import java.util.List;

/**
 * This class models the fast-view in the frontend.
 */
public class FastViewDescriptor {

    /**
     * The keys of the values that should be displayed.
     */
    private final List<String> displayKeys;

    /**
     * Themedia-object to display.
     */
    private final String mediaKey;

    /**
     * Constructor.
     *
     * @param displayKeys the keys of the values that should be displayed
     * @param mediaKey    the key of the media object to display. If set to {@code null}, no media object will
     *                    be shown in the fast view descriptor.
     */
    public FastViewDescriptor(final List<String> displayKeys, final String mediaKey) {
        this.displayKeys = displayKeys;
        this.mediaKey = mediaKey;
    }

    /**
     * Constructor.
     *
     * @param displayKeys the keys of the values that should be displayed
     */
    public FastViewDescriptor(final List<String> displayKeys) {
        this(displayKeys, null);
    }

    /**
     * Getter for the media key.
     *
     * @return the key of the media object
     */
    public String getMediaKey() {
        return mediaKey;
    }

    /**
     * Getter for all the keys for the values to display.
     *
     * @return all the values to display
     */
    public List<String> getDisplayKeys() {
        return new LinkedList<>(displayKeys);
    }
}
