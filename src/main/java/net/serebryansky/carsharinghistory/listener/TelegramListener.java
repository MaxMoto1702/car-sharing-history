package net.serebryansky.carsharinghistory.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TelegramListener implements UpdatesListener {
    private static final Logger log = LoggerFactory.getLogger(TelegramListener.class);

    private final TelegramBot telegramBot;

    public TelegramListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            Message message = update.message();
            log.info("Message (from listener): {}", message);
            SendMessage request = new SendMessage(update.message().chat().id(), "You write: " + message.text());
            request.replyToMessageId(message.messageId());
            telegramBot.execute(request);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;

    }
}
