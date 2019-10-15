package edu.kit.pse.beprepared.eventTypes.telegramMessageEvent;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.services.FileManagementService;
import edu.kit.pse.beprepared.simulation.ExecutionReport;
import edu.kit.pse.beprepared.simulation.ExecutionStatus;
import edu.kit.pse.beprepared.simulation.RequestRunner;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class MessageEventRequestRunner extends RequestRunner {

    /**
     * The logger instance used by this class.
     */
    private final Logger log = Logger.getLogger(MessageEventRequestRunner.class);
    /**
     * The Telegrambot used by this runner.
     */
    private final BpTelegramBot chatBot;

    /**
     * Constructor.
     *
     * @param event         the event that should be simulated
     * @param configuration the configuration for the simulation
     */
    public MessageEventRequestRunner(Event event, Configuration configuration) {
        super(event, configuration);
        String apiKey = configuration.getPropertyValue("telegramAuthToken").toString();
        String channelName = "@bePreparedPSE2019";
        this.chatBot = new BpTelegramBot(apiKey, channelName);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public ExecutionReport call() {

        log.info("Running " + this.toString());

        if (!(this.event instanceof MessageEvent)) {
            log.error("Bad event type: " + this.event.getClass().getName());
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_EXCEPTIONALLY, null, System.currentTimeMillis());
        }

        MessageEvent messageEvent = (MessageEvent) this.event;

        try {

            if (messageEvent.getAttachmentName() != null) {
                this.chatBot.sendDocument(messageEvent.getMessage(), FileManagementService.getInstance().getFile(
                        messageEvent.getAttachmentName()));
            } else {
                this.chatBot.sendMessage(messageEvent.getMessage());
            }

        } catch (IOException | TelegramApiException e) {
            log.error(e);
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_EXCEPTIONALLY, e, System.currentTimeMillis());
        }

        return new ExecutionReport(this, ExecutionStatus.COMPLETED_NORMAL, null, System.currentTimeMillis());
    }

    /**
     * Getter for a {@link String} representation of this object.
     *
     * @return a {@link String} representation of this object
     */
    @Override
    public String toString() {
        return "MessageEventRequestRunner{" +
                "event=" + event +
                ", configuration=" + configuration +
                '}';
    }
}
