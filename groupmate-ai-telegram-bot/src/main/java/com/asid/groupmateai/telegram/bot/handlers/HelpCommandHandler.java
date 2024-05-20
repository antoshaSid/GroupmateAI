package com.asid.groupmateai.telegram.bot.handlers;

import com.asid.groupmateai.telegram.bot.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(2)
public class HelpCommandHandler implements CommandHandler, UpdateHandler {

    private final TelegramService telegramService;

    @Autowired
    public HelpCommandHandler(final TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return isCommand(update);
    }

    @Override
    public void handleUpdate(Update update) {

    }
}
