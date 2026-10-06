package com.pharmacy.constants;

public final class ErrorMessageConstants {

    private ErrorMessageConstants() {
    }

    public static final String VALIDATION_FAILED = "Ошибка валидации";
    public static final String INVALID_CREDENTIALS = "Неверный email или пароль";

    public static final String USER_NAME_REQUIRED = "Имя обязательно для заполнения";
    public static final String USER_NAME_SIZE = "Имя не должно превышать 50 символов";
    public static final String USER_NAME_PATTERN = "Имя должно содержать только буквы, пробелы и дефис";
    public static final String USER_EMAIL_REQUIRED = "Email обязателен для заполнения";
    public static final String USER_EMAIL_VALID = "Введите корректный адрес электронной почты";
    public static final String USER_EMAIL_SIZE = "Email не должен превышать 100 символов";
    public static final String USER_PASSWORD_REQUIRED = "Пароль обязателен для заполнения";
    public static final String USER_PASSWORD_SIZE = "Пароль должен содержать минимум 8 символов";
    public static final String USER_PASSWORD_PATTERN = "Пароль должен содержать как минимум одну строчную букву, " +
            "одну заглавную букву и одну цифру";
    public static final String USER_ROLE_REQUIRED = "Роль пользователя обязательна для заполнения";

    public static final String USER_NOT_FOUND_BY_EMAIL = "User not found with email: %s";
    public static final String USER_NOT_FOUND_BY_ID = "User not found with id: %d";
    public static final String USER_EXISTS_BY_EMAIL = "Пользователь с таким email уже существует: %s";

    public static final String PRODUCT_ID_REQUIRED = "Идентификатор продукта обязателен для заполнения";
    public static final String PRODUCT_QUANTITY_REQUIRED = "Количество обязательно для заполнения";
    public static final String PRODUCT_QUANTITY_POSITIVE = "Количество должно быть больше 0";
    public static final String PRODUCT_NAME_REQUIRED = "Название товара обязательно для заполнения";
    public static final String PRODUCT_NAME_SIZE = "Название товара не должно превышать 100 символов";
    public static final String PRODUCT_PRICE_REQUIRED = "Цена обязательна для заполнения";
    public static final String PRODUCT_PRICE_MIN = "Цена должна быть не меньше 0.01";
    public static final String PRODUCT_STOCK_REQUIRED = "Количество товара обязательно для заполнения";
    public static final String PRODUCT_STOCK_POSITIVE = "Количество товара не может быть отрицательным";
    public static final String PRODUCT_CATEGORY_REQUIRED = "Категория товара обязательна для заполнения";
    public static final String PRODUCT_INSTRUCTION_REQUIRED = "Инструкция обязательна для заполнения";

    public static final String PRODUCT_NOT_FOUND_BY_ID = "Product not found with id: %d";
    public static final String PRODUCT_STOCK_EXCEEDED = "Доступно только %d единиц товара";
    public static final String PRODUCT_EXISTS = "Продукт уже существует";

    public static final String ORDER_STATUS_REQUIRED = "Статус заказа обязателен для заполнения";

    public static final String ORDER_NOT_FOUND_BY_ID = "Order not found with id: %d";

    public static final String CART_NOT_FOUND_BY_USER_ID = "Cart not found with user id: %d";
    public static final String CART_EMPTY = "Невозможно создать заказ: корзина пуста";

    public static final String CART_ITEM_NOT_FOUND_BY_ID = "Cart item not found with id: %d";
}
