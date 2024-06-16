package com.asid.groupmateai.telegram.bot.handlers;

import com.asid.groupmateai.core.services.UserService;
import com.asid.groupmateai.storage.entities.UserState;
import com.asid.groupmateai.telegram.bot.handlers.callbacks.HelpCallback;
import com.asid.groupmateai.telegram.bot.services.I18n;
import com.asid.groupmateai.telegram.bot.services.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(2)
@Slf4j
public class HelpCommandHandler implements CommandHandler, UpdateHandler {

    private final TelegramService telegramService;
    private final UserService userService;
    private final I18n i18n;

    @Autowired
    public HelpCommandHandler(final TelegramService telegramService, final UserService userService, final I18n i18n) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.i18n = i18n;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public boolean canHandleUpdate(final Update update) {
        return isCommand(update) || HelpCallback.isHelpCallback(update);
    }

    @Override
    public void handleUpdate(final Update update) {
        final Long chatId = telegramService.getChatIdFromUpdate(update);

        try {
            final UserState userState = userService.getUserState(chatId);

            if (userState != UserState.IDLE) {
                telegramService.sendMessage(chatId, i18n.getMessage("user.input.reserved.command.error.message", command()));
            } else {
                telegramService.sendMessage(chatId, i18n.getMessage("help.message"));
            }
        } catch (final Exception e) {
            log.error("Error occurred in HelpCommandHandler with {}.", update, e);
            telegramService.sendMessage(chatId, i18n.getMessage("bot.handler.error.message"));
        }
    }
}
