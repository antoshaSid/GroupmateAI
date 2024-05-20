package com.asid.groupmateai.telegram.bot.handlers;

import com.asid.groupmateai.core.services.GroupService;
import com.asid.groupmateai.core.services.GroupUserService;
import com.asid.groupmateai.core.services.UserService;
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
    private final I18n i18n;

    @Autowired
    public GroupHandler(final TelegramService telegramService,
                        final UserService userService,
                        final KeyboardService keyboardService,
                        final GroupUserService groupUserService,
                        final GroupService groupService,
                        final I18n i18n) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.keyboardService = keyboardService;
        this.groupUserService = groupUserService;
        this.groupService = groupService;
        this.i18n = i18n;
    }

    @Override
    public boolean canHandleUpdate(final Update update) {
        return GroupCallback.isGroupCallback(update) || isUserStateGroupRelated(update);
    }

    @Override
    public void handleUpdate(final Update update) {
        if (GroupCallback.isGroupCallback(update)) {
            switch (GroupCallback.getInstance(update)) {
                case CREATE_GROUP -> this.handleCreateGroupCallback(update);
                case JOIN_GROUP -> this.handleJoinGroupCallback(update);
                case QUERY_LIST -> this.handleQueryListCallback(update);
                case GROUP_SETTINGS -> this.handleGroupSettingsCallback(update);
            }
        } else if (BackCallback.isBackCallback(update)) {
            this.handleBackCallback(update);
        } else if (telegramService.hasMessageText(update)) {
            final Long chatId = telegramService.getChatIdFromUpdate(update);
            switch (userService.getUserState(chatId)) {
                case WAIT_FOR_GROUP_NAME -> this.handleGroupCreation(update);
                case WAIT_FOR_GROUP_TOKEN -> this.handleGroupJoin(update);
            }
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
            i18n.getMessage("user.input.group.name"),
            keyboardService.buildBackKeyboard());
    }

    private void handleJoinGroupCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);
        final Map<String, String> metadata = Collections.singletonMap("messageId", String.valueOf(messageId));

        userService.updateUserState(chatId, UserState.WAIT_FOR_GROUP_TOKEN, metadata);
        telegramService.updateMessage(chatId, messageId,
            i18n.getMessage("user.input.group.token"),
            keyboardService.buildBackKeyboard());
    }

    private void handleQueryListCallback(final Update update) {

    }

    private void handleGroupSettingsCallback(final Update update) {

    }

    private void handleBackCallback(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        final Integer messageId = telegramService.getMessageIdFromUpdate(update);

        userService.updateUserState(chatId, UserState.IDLE);
        telegramService.updateMessage(chatId, messageId,
            i18n.getMessage("welcome.message"),
            keyboardService.buildWelcomeKeyboard());
    }

    private void handleGroupCreation(final Update update) {
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
            keyboardService.buildGroupWelcomeKeyboard());
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
            telegramService.sendMessage(chatId, i18n.getMessage("user.input.group.token.incorrect.format"));
            return;
        }

        if (groupService.groupExists(parsedGroupToken)) {
            final GroupUserEntity groupUser = groupUserService.addUserToGroup(parsedGroupToken, chatId);

            userService.updateUserState(chatId, UserState.IDLE);
            telegramService.deleteMessage(chatId, Integer.valueOf(messageId));
            telegramService.sendMessage(chatId,
                i18n.getMessage("group.welcome.message", groupUser.getGroup().getName()),
                keyboardService.buildGroupWelcomeKeyboard());
        } else {
            telegramService.sendMessage(chatId, i18n.getMessage("user.input.group.token.group.does.not.exist"));
        }
    }
}
