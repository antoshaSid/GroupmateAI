package com.asid.groupmateai.telegram.bot.handlers;

import com.asid.groupmateai.telegram.bot.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(3)
public class QueryListCommandHandler implements CommandHandler, UpdateHandler {

    private final TelegramService telegramService;

    @Autowired
    public QueryListCommandHandler(final TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public String command() {
        return "/queries";
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return isCommand(update);
    }

    @Override
    public void handleUpdate(Update update) {

    }
}
