package com.asid.groupmateai.telegram.bot.services;

import com.asid.groupmateai.storage.entities.UserRole;
import com.asid.groupmateai.telegram.bot.handlers.callbacks.BackCallback;
import com.asid.groupmateai.telegram.bot.handlers.callbacks.GroupCallback;
import com.asid.groupmateai.telegram.bot.handlers.callbacks.HelpCallback;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

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
            .callbackData(HelpCallback.HELP.getData())
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(createGroupButton))
            .keyboardRow(new InlineKeyboardRow(joinGroupButton))
            .keyboardRow(new InlineKeyboardRow(helpButton))
            .build();
    }

    public InlineKeyboardMarkup buildGroupWelcomeKeyboard(final boolean useQueryKeyboard) {
        final InlineKeyboardButton queryListButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage(useQueryKeyboard ? "keyboard.button.query.list.on" : "keyboard.button.query.list.off"))
            .callbackData(useQueryKeyboard ? GroupCallback.QUERY_LIST_ON.getData() : GroupCallback.QUERY_LIST_OFF.getData())
            .build();
        final InlineKeyboardButton groupSettingsButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.group.settings"))
            .callbackData(GroupCallback.GROUP_SETTINGS.getData())
            .build();
        final InlineKeyboardButton helpButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.help"))
            .callbackData(HelpCallback.HELP.getData())
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

    public InlineKeyboardMarkup buildGroupSettingsKeyboard(final UserRole userRole) {
        final InlineKeyboardMarkup.InlineKeyboardMarkupBuilder<?, ?> keyboardBuilder = InlineKeyboardMarkup.builder();

        if (userRole == UserRole.ADMIN) {
            final InlineKeyboardButton changeGroupNameButton = InlineKeyboardButton.builder()
                .text(i18n.getMessage("keyboard.button.change.group.name"))
                .callbackData(GroupCallback.CHANGE_GROUP_NAME.getData())
                .build();
            final InlineKeyboardButton manageGroupFilesButton = InlineKeyboardButton.builder()
                .text(i18n.getMessage("keyboard.button.manage.group.files"))
                .callbackData(GroupCallback.MANAGE_GROUP_FILES.getData())
                .build();

            keyboardBuilder.keyboardRow(new InlineKeyboardRow(changeGroupNameButton))
                .keyboardRow(new InlineKeyboardRow(manageGroupFilesButton));
        }

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

        return keyboardBuilder
            .keyboardRow(new InlineKeyboardRow(invitePeopleButton))
            .keyboardRow(new InlineKeyboardRow(backButton, leaveGroupButton))
            .build();
    }

    public ReplyKeyboardMarkup buildQueryKeyboard() {
        final KeyboardButton scheduleForMondayQueryButton = KeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.query.schedule.for.monday"))
            .build();
        final KeyboardButton scheduleForTuesdayQueryButton = KeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.query.schedule.for.tuesday"))
            .build();
        final KeyboardButton scheduleForWednesdayQueryButton = KeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.query.schedule.for.wednesday"))
            .build();
        final KeyboardButton scheduleForWeekQueryButton = KeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.query.schedule.for.week"))
            .build();

        return ReplyKeyboardMarkup.builder()
            .keyboardRow(new KeyboardRow(scheduleForMondayQueryButton, scheduleForTuesdayQueryButton, scheduleForWednesdayQueryButton))
            .keyboardRow(new KeyboardRow(scheduleForWeekQueryButton))
            .resizeKeyboard(true)
            .build();
    }

    public ReplyKeyboardRemove removeQueryKeyboard() {
        return new ReplyKeyboardRemove(true);
    }

    public InlineKeyboardMarkup buildManageGroupFilesKeyboard(final String folderLink) {
        final InlineKeyboardButton openDriveButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.open.group.files.drive"))
            .url(folderLink)
            .build();
        final InlineKeyboardButton backButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.back"))
            .callbackData(BackCallback.BACK_MANAGE_GROUP_FILES.getData())
            .build();
        final InlineKeyboardButton updateContextButton = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.update.group.context"))
            .callbackData(GroupCallback.UPDATE_CONTEXT.getData())
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(openDriveButton))
            .keyboardRow(new InlineKeyboardRow(backButton, updateContextButton))
            .build();
    }

    public InlineKeyboardMarkup buildLeaveGroupConfirmKeyboard() {
        final InlineKeyboardButton leaveGroupYes = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.leave.group.yes"))
            .callbackData(GroupCallback.LEAVE_GROUP_YES.getData())
            .build();
        final InlineKeyboardButton leaveGroupNo = InlineKeyboardButton.builder()
            .text(i18n.getMessage("keyboard.button.leave.group.no"))
            .callbackData(GroupCallback.LEAVE_GROUP_NO.getData())
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(leaveGroupYes, leaveGroupNo))
            .build();
    }
}
