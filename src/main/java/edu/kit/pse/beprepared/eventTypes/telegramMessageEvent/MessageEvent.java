package edu.kit.pse.beprepared.eventTypes.telegramMessageEvent;

import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.Scenario;

import java.util.HashMap;

public class MessageEvent extends Event {

    /**
     * The message that should be send.
     */
    private String message;
    /**
     * The attachment (optional).
     */
    private String attachmentName;

    /**
     * Constructor
     * <p>
     * Creates a new {@link Event} with the given point in time. Automatically assigns an id.
     *
     * @param pointInTime    the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param message        the message that should be send
     * @param attachmentName the attachment (optional)
     */
    public MessageEvent(EventType eventType, long pointInTime, String message, String attachmentName) {
        super(eventType, pointInTime);
        this.message = message;
        this.attachmentName = attachmentName;
    }

    /**
     * Getter for all the attributes of this event as key-value-pairs.
     *
     * @return all the attributes of this event as key-value-pairs
     */
    @Override
    public HashMap<String, Object> getData() {
        return new HashMap<>() {{
            put("message", message);
            put("files", new HashMap<>() {{
                put("attachment", attachmentName);
            }});
        }};
    }

    /**
     * Getter for {@link this#message}.
     *
     * @return the value of {@link this#message}
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for {@link this#message}.
     *
     * @param message the new value for {@link this#message}
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for {@link this#attachmentName}.
     *
     * @return the value of {@link this#attachmentName}
     */
    public String getAttachmentName() {
        return attachmentName;
    }

    /**
     * Setter for {@link this#attachmentName}.
     *
     * @param attachmentName the new value for {@link this#attachmentName}
     */
    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    @Override
    public String toString() {
        return super.toString() + "MessageEvent{" +
                "message='" + message + '\'' +
                ", attachmentName='" + attachmentName + '\'' +
                '}';
    }
}
