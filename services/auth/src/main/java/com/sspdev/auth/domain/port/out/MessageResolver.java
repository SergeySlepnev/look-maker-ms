package com.sspdev.auth.domain.port.out;

import java.util.Locale;

public interface MessageResolver {

    String resolve(String key, Object[] args, Locale locale);
}