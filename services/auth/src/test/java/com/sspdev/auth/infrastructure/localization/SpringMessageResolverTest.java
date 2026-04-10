package com.sspdev.auth.infrastructure.localization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
public class SpringMessageResolverTest {

    @Mock
    private MessageSource messageSource;
    @InjectMocks
    private SpringMessageResolver messageResolver;

    @Test
    void resolve_shouldResolveMessage_whenKeyArgsLocaleExist() {
        var key = "dummy.key";
        var args = new Object[]{};
        var locale = Locale.ENGLISH;

        when(messageSource.getMessage(key, args, locale)).thenReturn("dummy result");

        var actualResult = messageResolver.resolve(key, args, locale);
        assertThat(actualResult).isEqualTo("dummy result");
    }
}