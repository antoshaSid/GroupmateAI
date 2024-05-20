package com.asid.groupmateai.telegram.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

    boolean canHandleUpdate(final Update update);

    void handleUpdate(final Update update); // TODO: handle possible exceptions in every handleUpdate method
}
