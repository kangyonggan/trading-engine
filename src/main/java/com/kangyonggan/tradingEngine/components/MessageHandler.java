package com.kangyonggan.tradingEngine.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author kyg
 */
@Component
public class MessageHandler {

    @Autowired
    private MessageSource messageSource;

    public String getMsg(String code) {
        return messageSource.getMessage(code, null, getLocale());
    }

    public String getMsg(String code, Object... args) {
        String msg = getMsg(code);
        return args == null ? msg : String.format(msg, args);
    }

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
