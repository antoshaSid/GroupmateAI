package com.asid.groupmateai.telegram.bot.services.impl;

import com.asid.groupmateai.telegram.bot.services.I18n;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class I18nImpl implements I18n {

    private final MessageSource messageSource;

    public I18nImpl() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(final String code, final String... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}
