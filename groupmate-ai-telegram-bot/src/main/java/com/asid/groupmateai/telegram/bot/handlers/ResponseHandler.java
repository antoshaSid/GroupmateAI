package com.asid.groupmateai.telegram.bot.handlers;

import com.asid.groupmateai.core.exceptions.ResponseGenerationException;
import com.asid.groupmateai.core.services.GroupUserService;
import com.asid.groupmateai.core.services.UserService;
import com.asid.groupmateai.core.services.UserThreadService;
import com.asid.groupmateai.storage.entities.GroupUserEntity;
import com.asid.groupmateai.storage.entities.UserState;
import com.asid.groupmateai.telegram.bot.services.I18n;
import com.asid.groupmateai.telegram.bot.services.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(4)
@Slf4j
public class ResponseHandler implements UpdateHandler {

    private final TelegramService telegramService;
    private final UserService userService;
    private final GroupUserService groupUserService;
    private final UserThreadService userThreadService;
    private final I18n i18n;

    @Autowired
    public ResponseHandler(final TelegramService telegramService,
                           final UserService userService,
                           final GroupUserService groupUserService,
                           final UserThreadService userThreadService,
                           final I18n i18n) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.groupUserService = groupUserService;
        this.userThreadService = userThreadService;
        this.i18n = i18n;
    }

    @Override
    public boolean canHandleUpdate(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);
        return userService.getUserState(chatId) == UserState.IDLE && telegramService.hasMessageText(update);
    }

    @Override
    public void handleUpdate(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);

        try {
            final GroupUserEntity groupUser = groupUserService.getGroupUserByChatId(chatId);

            if (groupUser != null) {
                final String messageText = telegramService.getMessageTextFromUpdate(update);
                final String threadId = groupUser.getThreadId();

                try {
                    final String response = userThreadService.generateResponse(threadId, messageText);
                    telegramService.sendMessage(chatId, response);
                } catch (final ResponseGenerationException e) {
                    log.error(e.getMessage(), e);
                    telegramService.sendMessage(chatId, i18n.getMessage("user.input.generate.response.error.message"));
                }
            } else {
                telegramService.sendMessage(chatId, i18n.getMessage("user.input.join.group.first.error.message"));
            }
        } catch (final Exception e) {
            log.error("Error occurred in ResponseHandler with {}.", update, e);
            telegramService.sendMessage(chatId, i18n.getMessage("bot.handler.error.message"));
        }
    }
}
