package com.pharmacy.constants;

public final class ValidationPatternConstants {

    private ValidationPatternConstants() {
    }

    public static final String USER_NAME = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$";
    public static final String USER_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
}
