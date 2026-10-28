package com.pharmacy.constants;

public final class ApiEndpointConstants {

    private ApiEndpointConstants() {
    }

    public static final String AUTH = "/api/auth";
    public static final String AUTH_ALL_PATHS = AUTH + "/*";
    public static final String AUTH_REGISTRATION = "/registration";
    public static final String AUTH_LOGIN = "/login";

    public static final String USER = "/api/user";
    public static final String USER_ALL_PATHS = USER + "/**";
    public static final String USER_BY_ID = "/{id}";

    public static final String PRODUCT = "/api/product";
    public static final String PRODUCT_SEARCH = "/search";
    public static final String PRODUCT_BY_ID = "/{id}";
    public static final String PRODUCT_CATEGORIES = "/categories";
    public static final String PRODUCT_CATEGORY = "/categories/{category}";

    public static final String PRODUCT_SEARCH_PATH = PRODUCT + "/search";
    public static final String PRODUCT_BY_ID_PATH = PRODUCT + "/*";
    public static final String PRODUCT_CATEGORIES_PATH = PRODUCT + "/categories/*";
    public static final String PRODUCT_ALL_PATHS = PRODUCT + "/**";

    public static final String CART = "/api/cart";
    public static final String CART_ALL_PATHS = CART + "/**";
    public static final String CART_MERGE = "/merge";

    public static final String CART_ITEM = "/api/cart/items";
    public static final String CART_ITEM_BY_ID = "/{id}";

    public static final String ORDERS = "/api/orders";
    public static final String ORDERS_ADMIN = "/admin";
    public static final String ORDERS_STATUS = "/{id}/status";
    public static final String ORDERS_ALL_PATHS = ORDERS + "/**";

    public static final String ORDERS_ADMIN_PATH = ORDERS + "/admin";
}
