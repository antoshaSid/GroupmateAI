package com.asid.groupmateai.telegram.bot.handlers;

import com.asid.groupmateai.core.services.GoogleDriveService;
import com.asid.groupmateai.core.services.GroupService;
import com.asid.groupmateai.core.services.GroupUserService;
import com.asid.groupmateai.core.services.UserService;
import com.asid.groupmateai.storage.entities.GroupEntity;
import com.asid.groupmateai.storage.entities.GroupUserEntity;
import com.asid.groupmateai.storage.entities.UserState;
import com.asid.groupmateai.telegram.bot.handlers.callbacks.BackCallback;
import com.asid.groupmateai.telegram.bot.handlers.callbacks.GroupCallback;
import com.asid.groupmateai.telegram.bot.services.impl.KeyboardService;
import com.asid.groupmateai.telegram.bot.services.I18n;
import com.asid.groupmateai.telegram.bot.services.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
@Order(4)
@Slf4j
public class GroupHandler implements UpdateHandler {

    private final TelegramService telegramService;
    private final UserService userService;
    private final KeyboardService keyboardService;
    private final GroupUserService groupUserService;
    private final GroupService groupService;
    private final GoogleDriveService googleDriveService;
    private final I18n i18n;

    @Autowired
    public GroupHandler(final TelegramService telegramService,
                        final UserService userService,
                        final KeyboardService keyboardService,
                        final GroupUserService groupUserService,
                        final GroupService groupService,
                        final GoogleDriveService googleDriveService,
                        final I18n i18n) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.keyboardService = keyboardService;
        this.groupUserService = groupUserService;
        this.groupService = groupService;
        this.googleDriveService = googleDriveService;
        this.i18n = i18n;
    }

    @Override
    public boolean canHandleUpdate(final Update update) {
        return GroupCallback.isGroupCallback(update) ||
            isUserStateGroupRelated(update) ||
            BackCallback.isBackCallback(update);
    }

    @Override
    public void handleUpdate(final Update update) {
        try {
            if (GroupCallback.isGroupCallback(update)) {
                switch (GroupCallback.getInstance(update)) {
                    case CREATE_GROUP -> this.handleCreateGroupCallback(update);
                    case JOIN_GROUP -> this.handleJoinGroupCallback(update);
                    case QUERY_LIST_ON, QUERY_LIST_OFF -> this.handleQueryListCallback(update);
                    case GROUP_SETTINGS -> this.handleGroupSettingsCallback(update);
                    case CHANGE_GROUP_NAME -> this.handleGroupChangeNameCallback(update);
                    case MANAGE_GROUP_FILES -> this.handleManageGroupFilesCallback(update);
                }
            } else if (BackCallback.isBackCallback(update)) {
                this.handleBackCallback(update);
            } else if (telegramService.hasMessageText(update)) {
                final Long chatId = telegramService.getChatIdFromUpdate(update);
                switch (userService.getUserState(chatId)) {
                    case WAIT_FOR_GROUP_NAME -> this.handleGroupCreation(update);
                    case WAIT_FOR_GROUP_TOKEN -> this.handleGroupJoin(update);
                    case WAIT_FOR_NEW_GROUP_NAME -> this.handleGroupNameChange(update);
                }
            }
        } catch (final Exception e) {
            // TODO: handle all possible exception cases in all handlers
        }
    }

    private boolean isUserStateGroupRelated(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final UserState userState = userService.getUserState(chatId);

        return userState == UserState.WAIT_FOR_GROUP_NAME ||
            userState == UserState.WAIT_FOR_NEW_GROUP_NAME ||
            userState == UserState.WAIT_FOR_GROUP_TOKEN;
    }

    private void handleCreateGroupCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);
        final Map<String, String> metadata = Collections.singletonMap("messageId", String.valueOf(messageId));

        userService.updateUserState(chatId, UserState.WAIT_FOR_GROUP_NAME, metadata);
        telegramService.updateMessage(chatId, messageId,
            i18n.getMessage("user.input.enter.group.name.message"),
            keyboardService.buildBackKeyboard(BackCallback.BACK_CREATE_GROUP.getData()));
    }

    private void handleJoinGroupCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);
        final Map<String, String> metadata = Collections.singletonMap("messageId", String.valueOf(messageId));

        userService.updateUserState(chatId, UserState.WAIT_FOR_GROUP_TOKEN, metadata);
        telegramService.updateMessage(chatId, messageId,
            i18n.getMessage("user.input.enter.group.token.message"),
            keyboardService.buildBackKeyboard(BackCallback.BACK_JOIN_GROUP.getData()));
    }

    private void handleQueryListCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);

        final GroupUserEntity groupUser = groupUserService.getGroupUserByChatId(chatId);
        final String groupName = groupUser.getGroup().getName();
        final boolean useQueryKeyboard = Boolean.parseBoolean(groupUser.getMetadata().get("useQueryKeyboard"));

        if (!useQueryKeyboard) {

            // Query keyboard: OFF -> ON
            groupUser.getMetadata().put("useQueryKeyboard", String.valueOf(true));
            groupUserService.updateGroupUser(groupUser);

            telegramService.updateMessage(chatId, messageId,
                i18n.getMessage("group.welcome.message", groupName),
                keyboardService.buildGroupWelcomeKeyboard(true));
            telegramService.sendMessage(chatId, i18n.getMessage("query.keyboard.turned.on.message"),
                keyboardService.buildQueryKeyboard());
        } else {

            // Query keyboard: ON -> OFF
            groupUser.getMetadata().put("useQueryKeyboard", String.valueOf(false));
            groupUserService.updateGroupUser(groupUser);

            telegramService.updateMessage(chatId, messageId,
                i18n.getMessage("group.welcome.message", groupName),
                keyboardService.buildGroupWelcomeKeyboard(false));
            telegramService.sendMessage(chatId, i18n.getMessage("query.keyboard.turned.off.message"),
                keyboardService.removeQueryKeyboard());
        }
    }

    private void handleGroupSettingsCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);
        final GroupEntity group = groupUserService.getGroupUserByChatId(chatId).getGroup();
        final int groupUsersCount = groupUserService.countGroupUsersByGroupId(group.getId());

        telegramService.updateMessage(chatId, messageId,
            i18n.getMessage("group.settings.message", group.getName(), group.getId(), groupUsersCount),
            keyboardService.buildGroupSettingsKeyboard());
    }

    private void handleBackCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);
        final GroupUserEntity groupUser = groupUserService.getGroupUserByChatId(chatId);
        final GroupEntity group = groupUser.getGroup();

        userService.updateUserState(chatId, UserState.IDLE);

        switch (BackCallback.getInstance(update)) {
            case BACK_CREATE_GROUP, BACK_JOIN_GROUP -> telegramService.updateMessage(chatId, messageId,
                i18n.getMessage("welcome.message", telegramService.getFirstNameFromUpdate(update)),
                keyboardService.buildWelcomeKeyboard());
            case BACK_GROUP_SETTINGS -> {
                final boolean useQueryKeyboard = Boolean.parseBoolean(groupUser.getMetadata().get("useQueryKeyboard"));
                telegramService.updateMessage(chatId, messageId,
                    i18n.getMessage("group.welcome.message", group.getName()),
                    keyboardService.buildGroupWelcomeKeyboard(useQueryKeyboard));
            }
            case BACK_CHANGE_GROUP_NAME, BACK_MANAGE_GROUP_FILES -> {
                final int groupUsersCount = groupUserService.countGroupUsersByGroupId(group.getId());
                telegramService.updateMessage(chatId, messageId,
                    i18n.getMessage("group.settings.message", group.getName(), group.getId(), groupUsersCount),
                    keyboardService.buildGroupSettingsKeyboard());
            }
        }
    }

    private void handleGroupCreation(final Update update) throws IOException {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final String groupName = telegramService.getMessageTextFromUpdate(update);
        final String messageId = userService.getUser(chatId)
            .getMetadata()
            .get("messageId");

        final GroupUserEntity groupUser = groupUserService.addGroupWithUser(groupName, chatId);
        log.info("New group ({}) with admin user ({}) was created",
            groupUser.getGroup().getId(),
            chatId);

        userService.updateUserState(chatId, UserState.IDLE);
        telegramService.deleteMessage(chatId, Integer.valueOf(messageId));
        telegramService.sendMessage(chatId,
            i18n.getMessage("group.welcome.message", groupName),
            keyboardService.buildGroupWelcomeKeyboard(false));
    }

    private void handleGroupJoin(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final String groupToken = telegramService.getMessageTextFromUpdate(update);
        final String messageId = userService.getUser(chatId)
            .getMetadata()
            .get("messageId");

        Long parsedGroupToken;
        try {
            parsedGroupToken = Long.valueOf(groupToken);
        } catch (final NumberFormatException e) {
            telegramService.sendMessage(chatId, i18n.getMessage("user.input.group.token.incorrect.format.error.message"));
            return;
        }

        if (groupService.groupExists(parsedGroupToken)) {
            final GroupUserEntity groupUser = groupUserService.addUserToGroup(parsedGroupToken, chatId);

            userService.updateUserState(chatId, UserState.IDLE);
            telegramService.deleteMessage(chatId, Integer.valueOf(messageId));
            telegramService.sendMessage(chatId,
                i18n.getMessage("group.welcome.message", groupUser.getGroup().getName()),
                keyboardService.buildGroupWelcomeKeyboard(false));
        } else {
            telegramService.sendMessage(chatId, i18n.getMessage(
                "user.input.group.token.group.does.not.exist.error.message"));
        }
    }

    private void handleGroupChangeNameCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);
        final Map<String, String> metadata = Collections.singletonMap("messageId", String.valueOf(messageId));

        userService.updateUserState(chatId, UserState.WAIT_FOR_NEW_GROUP_NAME, metadata);
        telegramService.updateMessage(chatId, messageId,
            i18n.getMessage("user.input.enter.new.group.name.message"),
            keyboardService.buildBackKeyboard(BackCallback.BACK_CHANGE_GROUP_NAME.getData()));
    }

    private void handleGroupNameChange(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final GroupEntity group = groupUserService.getGroupUserByChatId(chatId).getGroup();
        final int groupUsersCount = groupUserService.countGroupUsersByGroupId(group.getId());
        final String newGroupName = telegramService.getMessageTextFromUpdate(update);
        final String messageId = userService.getUser(chatId)
            .getMetadata()
            .get("messageId");

        group.setName(newGroupName);
        groupService.updateGroup(group);

        userService.updateUserState(chatId, UserState.IDLE);
        telegramService.deleteMessage(chatId, Integer.valueOf(messageId));
        telegramService.sendMessage(chatId, i18n.getMessage("group.name.changed.successfully.message", newGroupName));
        telegramService.sendMessage(chatId,
            i18n.getMessage("group.settings.message", newGroupName, group.getId(), groupUsersCount),
            keyboardService.buildGroupSettingsKeyboard());
    }

    private void handleManageGroupFilesCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);
        final String folderId = groupUserService.getGroupUserByChatId(chatId)
            .getGroup()
            .getDriveFolderId();
        final String folderLink = googleDriveService.getFolderShareableLink(folderId);

        telegramService.updateMessage(chatId, messageId,
            i18n.getMessage("manage.group.files.message"),
            keyboardService.buildManageGroupFilesKeyboard(folderLink));
    }
}
