package com.sspdev.auth.presentation.dto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {

    public static final String PHONE_NUMBER_REGEX = "^(8|\\+7)?[\\s.-]?\\$?(\\d{3})\\$?[\\s.-]?(\\d{3})[\\s.-]?(\\d{2})[\\s.-]?(\\d{2})$";
}