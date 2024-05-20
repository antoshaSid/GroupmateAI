package com.asid.groupmateai.telegram.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    String command();

    default boolean isCommand(final Update update) {
        return update.hasMessage() &&
            update.getMessage().isCommand() &&
            update.getMessage().getText().equals(command());
    }
}
