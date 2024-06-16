package com.asid.groupmateai.telegram.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class GroupmateAiTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String token;
    private final DispatcherHandler dispatcherHandler;

    @Autowired
    public GroupmateAiTelegramBot(@Value("${TELEGRAM_API_KEY}") final String token,
                                  final DispatcherHandler dispatcherHandler) {
        this.token = token;
        this.dispatcherHandler = dispatcherHandler;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(final Update update) {
        if (dispatcherHandler.dispatch(update)) {
            log.debug("Update handled: {}", update.getUpdateId());
        } else {
            log.error("Update failed to be handled. No handlers found for update: {}", update);
        }
    }
}
