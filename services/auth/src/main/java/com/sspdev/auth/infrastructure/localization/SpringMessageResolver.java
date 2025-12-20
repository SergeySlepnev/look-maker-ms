package com.sspdev.auth.infrastructure.localization;

import com.sspdev.auth.domain.port.out.MessageResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SpringMessageResolver implements MessageResolver {

    private final MessageSource messageSource;

    @Override
    public String resolve(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }
}