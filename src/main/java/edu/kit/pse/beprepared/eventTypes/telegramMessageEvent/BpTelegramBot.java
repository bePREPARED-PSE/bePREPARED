package edu.kit.pse.beprepared.eventTypes.telegramMessageEvent;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

/**
 * A custom Telegram bot used for bePrepared.
 */
public class BpTelegramBot extends TelegramLongPollingBot {

    /**
     * The bot token.
     */
    private final String botToken;
    /**
     * The channel name.
     */
    private final String channelName;

    /**
     * Constructor.
     *
     * @param botToken    the bot token
     * @param channelName the channel name
     */
    public BpTelegramBot(String botToken, String channelName) {
        this.botToken = botToken;
        this.channelName = channelName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // do nothing
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    /**
     * Sends a document with a supplied caption to {@link #channelName}.
     *
     * @param caption  the caption
     * @param document the document
     */
    void sendDocument(String caption, File document) throws TelegramApiException {

        SendDocument request = new SendDocument();
        if (caption != null) {
            request.setCaption(caption);
        }
        request.setDocument(document);
        request.setChatId(this.channelName);

        execute(request);
    }

    /**
     * Sends a supplied message to {@link #channelName}.
     *
     * @param message the message to send
     */
    void sendMessage(String message) throws TelegramApiException {

        SendMessage request = new SendMessage();
        request.setChatId(this.channelName);
        request.setText(message);

        execute(request);
    }

}
