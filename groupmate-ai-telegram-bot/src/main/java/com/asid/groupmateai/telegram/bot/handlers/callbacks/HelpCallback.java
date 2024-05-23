package com.asid.groupmateai.telegram.bot.handlers.callbacks;

import org.telegram.telegrambots.meta.api.objects.Update;

public enum HelpCallback {

    HELP("help_callback");

    HelpCallback(final String data) {
        this.data = data;
    }

    private final String data;

    public String getData() {
        return data;
    }

    public static boolean isHelpCallback(final Update update) {
        if (update.hasCallbackQuery()) {
            final String callbackData = update.getCallbackQuery().getData();
            return HELP.getData().equals(callbackData);
        } else {
            return false;
        }
    }
}
