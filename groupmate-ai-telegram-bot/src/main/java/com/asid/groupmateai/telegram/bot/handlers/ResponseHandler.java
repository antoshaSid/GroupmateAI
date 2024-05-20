package com.asid.groupmateai.telegram.bot.handlers;

import com.asid.groupmateai.telegram.bot.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(5)
public class ResponseHandler implements UpdateHandler {

    private final TelegramService telegramService;

    @Autowired
    public ResponseHandler(final TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return false;
//        return telegramService.getMessageFromUpdate(update).isPresent();
    }

    @Override
    public void handleUpdate(final Update update) {

    }











//    @Override
//    public void handleUpdate(Update update) {
//        final Long chatId = telegramService.getChatIdFromUpdate(update);
//        final String message = telegramService.getMessageFromUpdate(update).get();
//
//        final ThreadMessageRequest threadMessageRequest = ThreadMessageRequest.builder()
//            .role(ThreadMessageRole.USER)
//            .content(message)
//            .build();
//        final ThreadMessage threadMessage = threadOpenAiClient.createThreadMessage(thread.getId(), threadMessageRequest).join();
//
//        final ThreadRunRequest runRequest = ThreadRunRequest.builder()
//            .assistantId(assistantId)
//            .build();
//
//        final Stream<Event> runStream = threadOpenAiClient.createThreadRun(thread.getId(), runRequest).join();
//
//        telegramService.sendMessage(chatId, handleRunEvents(runStream));
//    }
//
//    private String handleRunEvents(Stream<Event> runStream) {
//        final StringBuilder stringBuilder = new StringBuilder();
//        runStream.forEach(event -> {
//                if (event.getName()
//                    .equals(EventName.THREAD_MESSAGE_DELTA)) {
//                    final ThreadMessageDelta msgDelta = (ThreadMessageDelta) event.getData();
//                    final ThreadMessageContent content = msgDelta.getDelta()
//                        .getContent()
//                        .get(0);
//                    if (content instanceof ThreadMessageContent.TextContent textContent) {
//                        stringBuilder.append(textContent.getText()
//                            .getValue());
//                    }
//                } else if (event.getName()
//                    .equals(EventName.THREAD_RUN_FAILED)) {
//                    final ThreadRun runFailed = (ThreadRun) event.getData();
//                    final LastError error = runFailed.getLastError();
//
//                    System.out.println("Thread run failed. See stack trace for more details. " + error.getMessage());
//                }
//            });
//
//        final String generatedResponse = stringBuilder.toString();
//        System.out.println("Thread was completed with response: " + generatedResponse);
//        return generatedResponse;
//    }
}
