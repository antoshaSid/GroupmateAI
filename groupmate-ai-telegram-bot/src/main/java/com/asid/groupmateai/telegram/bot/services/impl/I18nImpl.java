package com.asid.groupmateai.telegram.bot.services.impl;

import com.asid.groupmateai.telegram.bot.services.I18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class I18nImpl implements I18n {

    private final MessageSource messageSource;

    @Autowired
    public I18nImpl() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("responses/messages");
        messageSource.setDefaultEncoding("UTF-8");
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(final String code, final String... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}
