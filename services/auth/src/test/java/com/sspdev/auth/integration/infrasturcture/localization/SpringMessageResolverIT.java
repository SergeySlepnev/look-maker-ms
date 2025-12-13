package com.sspdev.auth.integration.infrasturcture.localization;

import com.sspdev.auth.infrastructure.localization.SpringMessageResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public class SpringMessageResolverIT {

    private static final String HELLO_KEY = "test.hello";
    private static final String WELCOME_KEY = "test.welcome";

    private SpringMessageResolver messageResolver;

    @BeforeEach
    void setup() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageResolver = new SpringMessageResolver(messageSource);
    }

    @Test
    void resolve_ShouldFoundRussianMessage() {
        var actualMessage = messageResolver.resolve(HELLO_KEY, null, new Locale.Builder().setLanguage("ru").build());
        assertThat(actualMessage).isEqualTo("Привет!");
    }

    @Test
    void resolve_ShouldFoundEnglishMessage() {
        var actualMessage = messageResolver.resolve(HELLO_KEY, null, Locale.ENGLISH);
        assertThat(actualMessage).isEqualTo("Hello");
    }

    @Test
    void resolve_ShouldFoundRussianMessageWithArgs() {
        var actualMessage = messageResolver.resolve(WELCOME_KEY, new Object[]{"Сергей"}, new Locale.Builder().setLanguage("ru").build());
        assertThat(actualMessage).isEqualTo("Добро пожаловать, Сергей!");
    }

    @Test
    void resolve_ShouldFoundEnglishMessageWithArgs() {
        var actualMessage = messageResolver.resolve(WELCOME_KEY, new Object[]{"John"}, Locale.ENGLISH);
        assertThat(actualMessage).isEqualTo("Welcome John");
    }
}