package com.sspdev.auth.application.util;

import jakarta.annotation.Nullable;

import org.springframework.stereotype.Component;

@Component
public class DataMasker {

    public static final int EMAIL_VISIBLE_CHARS = 2;
    public static final int PHONE_VISIBLE_CHARS = 4;

    /**
     * Маскирует email адрес.
     * Пример: ivan.ivanov@example.com → iv********@example.com
     * @return замаскированный email адрес
     */
    @Nullable
    public String maskEmail(String email) {
        if (email == null) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= EMAIL_VISIBLE_CHARS) {
            return "***@***";
        }

        String localPart = email.substring(0, EMAIL_VISIBLE_CHARS);
        String domain = email.substring(atIndex);
        return localPart + "*".repeat(atIndex - EMAIL_VISIBLE_CHARS) + domain;
    }

    /**
     * Маскирует номер телефона.
     * Пример: +79991234567 → +7999123****67
     * @return замаскированный email адрес
     */
    @Nullable
    public String maskPhone(String phone) {
        if (phone == null) {
            return null;
        }

        if (phone.length() < PHONE_VISIBLE_CHARS) {
            return "***";
        }
        String visiblePart = phone.substring(phone.length() - PHONE_VISIBLE_CHARS);
        String maskedPart = "*".repeat(phone.length() - PHONE_VISIBLE_CHARS);
        return visiblePart + maskedPart;
    }
}