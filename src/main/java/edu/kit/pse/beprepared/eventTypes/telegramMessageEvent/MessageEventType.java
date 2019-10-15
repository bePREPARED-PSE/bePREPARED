package edu.kit.pse.beprepared.eventTypes.telegramMessageEvent;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.model.frontendDescriptors.*;
import edu.kit.pse.beprepared.simulation.RequestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class MessageEventType extends EventType {

    @Override
    public void init() {

        this.typeName = "Message Event";
        this.ico = "img/msg-ico.png";
        this.fastViewDescriptor = new FastViewDescriptor(new LinkedList<>() {{
            add("message");
        }}, "attachment");

        InputField resultInputField = new InputField("message", "Hello World!", true);
        FileInputField fileInputField = new FileInputField("attachment", false);

        this.inputFormDescriptor = new InputFormDescriptor(new LinkedList<>() {
            {
                add(resultInputField);
                add(fileInputField);
            }
        });
        this.configurationInputFields = new LinkedList<>() {{
            add(new InputField("telegramAuthToken", "", true));
        }};

    }

    /**
     * Creates a new {@link Event} with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        map with the data from the JSON Object of the event
     * @return the newly created event
     */
    @SuppressWarnings("unchecked")
    @Override
    public MessageEvent createEvent(long pointInTime, HashMap<String, Object> data) {

        String message = data.get("message").toString();
        String attachmentName = getFileNameFromEventData("attachment", data);

        return new MessageEvent(this, pointInTime, message, attachmentName);
    }

    /**
     * Replaces the data of the given event with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        the new data for the event
     * @param event       the event that should be edited
     * @return the edited event
     */
    @SuppressWarnings("unchecked")
    @Override
    public MessageEvent editEvent(long pointInTime, HashMap<String, Object> data, Event event) {

        String message = data.get("message").toString();
        String attachmentName = getFileNameFromEventData("attachment", data);

        event.setPointInTime(pointInTime);

        MessageEvent me = (MessageEvent) event;
        me.setMessage(message);
        me.setAttachmentName(attachmentName);

        return (MessageEvent) event;

    }

    /**
     * Returns a {@link RequestRunner} for the given event.
     *
     * @param event         the event for which the runner is required
     * @param configuration the {@link Configuration} this {@link RequestRunner} should use
     * @return the request runner for the event
     */
    @Override
    public RequestRunner getRequestRunnerFor(Event event, Configuration configuration) {
        return new MessageEventRequestRunner(event, configuration);
    }

}
