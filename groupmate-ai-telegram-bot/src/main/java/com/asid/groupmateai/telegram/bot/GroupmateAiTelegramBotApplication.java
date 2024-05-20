package com.asid.groupmateai.telegram.bot;

import com.asid.groupmateai.core.CoreModuleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CoreModuleConfiguration.class)
public class GroupmateAiTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupmateAiTelegramBotApplication.class, args);
    }

}
