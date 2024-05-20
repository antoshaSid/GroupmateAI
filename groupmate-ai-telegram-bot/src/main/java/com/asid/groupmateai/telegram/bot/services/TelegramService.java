package com.asid.groupmateai.telegram.bot.services;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface TelegramService {

    boolean hasMessageText(Update update);

    String getMessageTextFromUpdate(Update update);

    Integer getMessageIdFromUpdate(Update update);

    String getUsernameFromUpdate(Update update);

    String getFirstNameFromUpdate(Update update);

    Long getChatIdFromUpdate(Update update);

    void sendMessage(Long chatId, String text);

    void sendMessage(Long chatId, String text, ReplyKeyboard keyboard);

    void updateMessage(Long chatId, Integer messageId, String text);

    void updateMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard);

    void updateMessage(Long chatId, Integer messageId, InlineKeyboardMarkup keyboard);

    void deleteMessage(Long chatId, Integer messageId);
}
