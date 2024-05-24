package com.asid.groupmateai.telegram.bot.configs;

import com.asid.groupmateai.core.CoreModuleConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Locale;

@Configuration
@Import(CoreModuleConfiguration.class)
@ComponentScan(basePackages = "com.asid.groupmateai.core")
public class GroupmateAiTelegramBotConfig {

    @Bean
    public TelegramClient telegramClient(@Value("${TELEGRAM_API_KEY}") final String botToken) {
        return new OkHttpTelegramClient(botToken);
    }

    @PostConstruct
    public void initDefaultLocale() {
        final Locale ukLocale = new Locale("uk");
        Locale.setDefault(ukLocale);
        LocaleContextHolder.setDefaultLocale(ukLocale);
    }
}
