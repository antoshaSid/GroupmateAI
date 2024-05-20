package com.asid.groupmateai.telegram.bot.handlers.callbacks;

import org.telegram.telegrambots.meta.api.objects.Update;

public enum BackCallback {

    BACK("back_callback");

    BackCallback(final String data) {
        this.data = data;
    }

    private final String data;

    public String getData() {
        return data;
    }

    public static boolean isBackCallback(final Update update) {
        if (update.hasCallbackQuery()) {
            final String callbackData = update.getCallbackQuery().getData();
            return BACK.getData().equals(callbackData);
        } else {
            return false;
        }
    }
}
