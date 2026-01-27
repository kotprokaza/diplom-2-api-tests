package com.stellarburgers.api;

import org.apache.http.HttpStatus;

public class Endpoints {
    public static final String BASE_URL = "https://stellarburgers.education-services.ru";
    public static final String REGISTER = "/api/auth/register";
    public static final String LOGIN = "/api/auth/login";
    public static final String USER = "/api/auth/user";
    public static final String ORDERS = "/api/orders";
    public static final String INGREDIENTS = "/api/ingredients";
    
    // HTTP статусы
    public static final int SC_OK = HttpStatus.SC_OK;
    public static final int SC_CREATED = HttpStatus.SC_CREATED;
    public static final int SC_ACCEPTED = HttpStatus.SC_ACCEPTED;
    public static final int SC_BAD_REQUEST = HttpStatus.SC_BAD_REQUEST;
    public static final int SC_UNAUTHORIZED = HttpStatus.SC_UNAUTHORIZED;
    public static final int SC_FORBIDDEN = HttpStatus.SC_FORBIDDEN;
    public static final int SC_NOT_FOUND = HttpStatus.SC_NOT_FOUND;
    public static final int SC_INTERNAL_SERVER_ERROR = HttpStatus.SC_INTERNAL_SERVER_ERROR;
}
