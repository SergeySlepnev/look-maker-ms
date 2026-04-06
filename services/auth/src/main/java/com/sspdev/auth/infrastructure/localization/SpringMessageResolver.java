package com.sspdev.auth.infrastructure.localization;

import lombok.RequiredArgsConstructor;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import com.sspdev.auth.domain.port.out.MessageResolver;

@Service
@RequiredArgsConstructor
public class SpringMessageResolver implements MessageResolver {

    private final MessageSource messageSource;

    @Override
    public String resolve(String key, Object[] args, Locale locale) {
        Locale effectiveLocale = locale != null ? locale : LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, effectiveLocale);
    }
}