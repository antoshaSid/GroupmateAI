package com.asid.groupmateai.telegram.bot.services.impl;

import com.asid.groupmateai.telegram.bot.handlers.callbacks.BackCallback;
import com.asid.groupmateai.telegram.bot.handlers.callbacks.GroupCallback;
import com.asid.groupmateai.telegram.bot.services.I18n;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Service
public class KeyboardService {

    private final I18n i18n;

    private KeyboardService(final I18n i18n) {
        this.i18n = i18n;
    }

    public InlineKeyboardMarkup buildWelcomeKeyboard() {
        final InlineKeyboardButton createGroupButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.create.group"))
            .callbackData(GroupCallback.CREATE_GROUP.getData())
            .build();
        final InlineKeyboardButton joinGroupButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.join.group"))
            .callbackData(GroupCallback.JOIN_GROUP.getData())
            .build();
        final InlineKeyboardButton helpButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.help"))
            .callbackData("help_callback")
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(createGroupButton))
            .keyboardRow(new InlineKeyboardRow(joinGroupButton))
            .keyboardRow(new InlineKeyboardRow(helpButton))
            .build();
    }

    public InlineKeyboardMarkup buildGroupWelcomeKeyboard() {
        final InlineKeyboardButton queryListButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.query.list"))
            .callbackData(GroupCallback.QUERY_LIST.getData())
            .build();
        final InlineKeyboardButton groupSettingsButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.group.settings"))
            .callbackData(GroupCallback.GROUP_SETTINGS.getData())
            .build();
        final InlineKeyboardButton helpButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.help"))
            .callbackData("help_callback")
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(queryListButton))
            .keyboardRow(new InlineKeyboardRow(groupSettingsButton))
            .keyboardRow(new InlineKeyboardRow(helpButton))
            .build();
    }

    public InlineKeyboardMarkup buildBackKeyboard(final String callbackData) {
        final InlineKeyboardButton backButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.back"))
            .callbackData(callbackData)
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(backButton))
            .build();
    }

    public InlineKeyboardMarkup buildGroupSettingsKeyboard() {
        final InlineKeyboardButton changeGroupNameButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.change.group.name"))
            .callbackData(GroupCallback.CHANGE_GROUP_NAME.getData())
            .build();
        final InlineKeyboardButton manageGroupFilesButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.manage.group.files"))
            .callbackData(GroupCallback.MANAGE_GROUP_FILES.getData())
            .build();
        final InlineKeyboardButton invitePeopleButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.invite.people"))
            .callbackData(GroupCallback.INVITE_PEOPLE.getData())
            .build();
        final InlineKeyboardButton backButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.back"))
            .callbackData(BackCallback.BACK_GROUP_SETTINGS.getData())
            .build();
        final InlineKeyboardButton leaveGroupButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.leave.group"))
            .callbackData(GroupCallback.LEAVE_GROUP.getData())
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(changeGroupNameButton))
            .keyboardRow(new InlineKeyboardRow(manageGroupFilesButton))
            .keyboardRow(new InlineKeyboardRow(invitePeopleButton))
            .keyboardRow(new InlineKeyboardRow(backButton, leaveGroupButton))
            .build();
    }
}
