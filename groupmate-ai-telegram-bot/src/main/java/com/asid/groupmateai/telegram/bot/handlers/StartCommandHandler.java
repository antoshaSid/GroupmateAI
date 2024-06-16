package com.asid.groupmateai.telegram.bot.handlers;

import com.asid.groupmateai.core.services.GroupUserService;
import com.asid.groupmateai.core.services.UserService;
import com.asid.groupmateai.storage.entities.GroupEntity;
import com.asid.groupmateai.storage.entities.GroupUserEntity;
import com.asid.groupmateai.storage.entities.UserEntity;
import com.asid.groupmateai.storage.entities.UserState;
import com.asid.groupmateai.telegram.bot.services.I18n;
import com.asid.groupmateai.telegram.bot.services.TelegramService;
import com.asid.groupmateai.telegram.bot.services.KeyboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(1)
@Slf4j
public class StartCommandHandler implements CommandHandler, UpdateHandler {

    private final TelegramService telegramService;
    private final I18n i18n;
    private final UserService userService;
    private final GroupUserService groupUserService;
    private final KeyboardService keyboardService;

    @Autowired
    public StartCommandHandler(final TelegramService telegramService,
                               final I18n i18n,
                               final UserService userService,
                               final GroupUserService groupUserService,
                               final KeyboardService keyboardService) {
        this.telegramService = telegramService;
        this.i18n = i18n;
        this.userService = userService;
        this.groupUserService = groupUserService;
        this.keyboardService = keyboardService;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public boolean canHandleUpdate(final Update update) {
        return isCommand(update);
    }

    @Override
    public void handleUpdate(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);

        try {
            final GroupUserEntity groupUser = groupUserService.getGroupUserByChatId(chatId);
            UserEntity user = userService.getUser(chatId);

            if (user == null) {
                user = userService.addUser(chatId);
                log.info("New user was registered with chat id {}", user.getChatId());
            }

            if (user.getUserState() != UserState.IDLE) {

                // START command is invoked by a user with input state
                this.handleInputUserState(user.getUserState(), update);
            } else if (groupUser != null) {

                // START command is invoked by a user in a group
                final boolean useQueryKeyboard = Boolean.parseBoolean(groupUser.getMetadata().get("useQueryKeyboard"));
                telegramService.sendMessage(chatId,
                    i18n.getMessage("group.welcome.message", groupUser.getGroup().getName()),
                    keyboardService.buildGroupWelcomeKeyboard(useQueryKeyboard));
            } else {

                // START command is invoked by a user not in a group
                telegramService.sendMessage(chatId,
                    i18n.getMessage("welcome.message", telegramService.getFirstNameFromUpdate(update)),
                    keyboardService.buildWelcomeKeyboard());
            }
        } catch (final Exception e) {
            log.error("Error occurred in StartCommandHandler with {}.", update, e);
            telegramService.sendMessage(chatId, i18n.getMessage("bot.handler.error.message"));
        }
    }

    private void handleInputUserState(final UserState userState, final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);

        userService.updateUserState(chatId, UserState.IDLE);

        if (userState == UserState.WAIT_FOR_GROUP_NAME || userState == UserState.WAIT_FOR_GROUP_TOKEN) {
            telegramService.sendMessage(chatId,
                i18n.getMessage("welcome.message", telegramService.getFirstNameFromUpdate(update)),
                keyboardService.buildWelcomeKeyboard());
        } else if (userState == UserState.WAIT_FOR_NEW_GROUP_NAME) {
            final GroupUserEntity groupUser = groupUserService.getGroupUserByChatId(chatId);
            final GroupEntity group = groupUser.getGroup();
            final int groupUsersCount = groupUserService.countGroupUsersByGroupId(group.getId());

            telegramService.sendMessage(chatId,
                i18n.getMessage("group.settings.message", group.getName(), group.getId(), groupUsersCount),
                keyboardService.buildGroupSettingsKeyboard(groupUser.getUserRole()));
        }
    }
}
