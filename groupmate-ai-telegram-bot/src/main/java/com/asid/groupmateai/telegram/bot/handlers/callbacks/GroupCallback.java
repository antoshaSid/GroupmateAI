package com.asid.groupmateai.telegram.bot.handlers.callbacks;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

public enum GroupCallback {

    CREATE_GROUP("create_group_callback"),
    JOIN_GROUP("join_group_callback"),
    QUERY_LIST_ON("query_list_on_callback"),
    QUERY_LIST_OFF("query_list_off_callback"),
    GROUP_SETTINGS("group_settings_callback"),
    CHANGE_GROUP_NAME("change_group_name_callback"),
    MANAGE_GROUP_FILES("manage_group_files_callback"),
    INVITE_PEOPLE("invite_people_callback"),
    LEAVE_GROUP("leave_group_callback");

    GroupCallback(final String data) {
        this.data = data;
    }

    private final String data;

    public String getData() {
        return data;
    }

    public static boolean isGroupCallback(final Update update) {
        if (update.hasCallbackQuery()) {
            final String callbackData = update.getCallbackQuery().getData();
            return Arrays.stream(values())
                .map(GroupCallback::getData)
                .anyMatch(callbackData::equals);
        } else {
            return false;
        }
    }

    public static GroupCallback getInstance(final Update update) {
        final String callbackData = update.getCallbackQuery().getData();
        return Arrays.stream(values())
            .filter(groupCallback -> groupCallback.getData().equals(callbackData))
            .findFirst()
            .orElse(null);
    }
}
