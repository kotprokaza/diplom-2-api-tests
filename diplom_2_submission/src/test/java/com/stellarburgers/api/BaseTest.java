package com.stellarburgers.api;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;

public class BaseTest {
    
    @Before
    public void setUp() {
        // Правильный URL из задания
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        
        // Включаем логирование запросов и ответов
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
