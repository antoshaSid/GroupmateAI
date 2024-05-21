package com.asid.groupmateai.telegram.bot.services.impl;

import com.asid.groupmateai.telegram.bot.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class DefaultTelegramService implements TelegramService {

    private final TelegramClient telegramClient;

    @Autowired
    public DefaultTelegramService(final TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public boolean hasMessageText(final Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Override
    public String getMessageTextFromUpdate(final Update update) {
        return update.getMessage().getText();
    }

    @Override
    public Integer getMessageIdFromUpdate(final Update update) {
        if (update.hasMessage()) {
            return update.getMessage()
                .getMessageId();
        } else {
            return update.getCallbackQuery()
                .getMessage()
                .getMessageId();
        }
    }

    @Override
    public String getUsernameFromUpdate(final Update update) {
        if (update.hasMessage()) {
            return update.getMessage()
                .getFrom()
                .getUserName();
        } else {
            return update.getCallbackQuery()
                .getMessage()
                .getChat()
                .getUserName();
        }
    }

    @Override
    public String getFirstNameFromUpdate(final Update update) {
        if (update.hasMessage()) {
            return update.getMessage()
                .getChat()
                .getFirstName();
        } else {
            return update.getCallbackQuery()
                .getMessage()
                .getChat()
                .getFirstName();
        }
    }

    @Override
    public Long getChatIdFromUpdate(final Update update) {
        if (update.hasMessage()) {
            return update.getMessage()
                .getChatId();
        } else {
            return update.getCallbackQuery()
                .getMessage()
                .getChatId();
        }
    }

    @Override
    public void sendMessage(final Long chatId, final String text) {
        try {
            final SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .build();

            telegramClient.execute(message);
        } catch (final TelegramApiException exception) {
            exception.printStackTrace();
            // TODO LOG: exception and send to user
        }
    }

    @Override
    public void sendMessage(final Long chatId,
                               final String text,
                               final ReplyKeyboard keyboard) {
        try {
            final SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .build();

            telegramClient.execute(message);
        } catch (final TelegramApiException exception) {
            exception.printStackTrace();
            // TODO LOG: exception and send to user
        }
    }

    @Override
    public void updateMessage(final Long chatId,
                              final Integer messageId,
                              final String text) {
        try {
            final EditMessageText newTextMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .build();

            telegramClient.execute(newTextMessage);
        } catch (final TelegramApiException exception) {
            exception.printStackTrace();
            // TODO LOG: exception and send to user
        }
    }

    @Override
    public void updateMessage(final Long chatId,
                              final Integer messageId,
                              final String text,
                              final InlineKeyboardMarkup keyboard) {
        try {
            final EditMessageText newTextMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .replyMarkup(keyboard)
                .parseMode(ParseMode.HTML)
                .build();

            telegramClient.execute(newTextMessage);
        } catch (final TelegramApiException exception) {
            exception.printStackTrace();
            // TODO LOG: exception and send to user
        }
    }

    @Override
    public void updateMessage(final Long chatId,
                              final Integer messageId,
                              final InlineKeyboardMarkup keyboard) {
        try {
            final EditMessageReplyMarkup newInlineKeyboard = EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(keyboard)
                .build();

            telegramClient.execute(newInlineKeyboard);
        } catch (final TelegramApiException exception) {
            exception.printStackTrace();
            // TODO LOG: exception and send to user
        }
    }

    @Override
    public void deleteMessage(final Long chatId, final Integer messageId) {
        try {
            final DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();

            telegramClient.execute(deleteMessage);
        } catch (final TelegramApiException exception) {
            exception.printStackTrace();
            // TODO LOG: exception and send to user
        }
    }
}
