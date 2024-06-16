package com.asid.groupmateai.telegram.bot;

import com.asid.groupmateai.telegram.bot.handlers.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public final class DispatcherHandler {

    private final List<UpdateHandler> updateHandlers;

    @Autowired
    public DispatcherHandler(final List<UpdateHandler> updateHandlers) {
        this.updateHandlers = updateHandlers;
    }

    public boolean dispatch(final Update update) {
        for (final UpdateHandler updateHandler : updateHandlers) {
            if (updateHandler.canHandleUpdate(update)) {
                updateHandler.handleUpdate(update);
                return true;
            }
        }

        return false;
    }
}
