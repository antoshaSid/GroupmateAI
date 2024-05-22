package com.asid.groupmateai.telegram.bot.handlers.callbacks;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

public enum BackCallback {

    BACK_CREATE_GROUP("back_create_group_callback"),
    BACK_JOIN_GROUP("back_join_group_callback"),
    BACK_GROUP_SETTINGS("back_group_settings_callback"),
    BACK_CHANGE_GROUP_NAME("back_change_group_name_callback"),
    BACK_MANAGE_GROUP_FILES("back_manage_group_files_callback");

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
            return Arrays.stream(values())
                .map(BackCallback::getData)
                .anyMatch(callbackData::equals);
        } else {
            return false;
        }
    }

    public static BackCallback getInstance(final Update update) {
        final String callbackData = update.getCallbackQuery().getData();
        return Arrays.stream(values())
            .filter(backCallback -> backCallback.getData().equals(callbackData))
            .findFirst()
            .orElse(null);
    }
}
