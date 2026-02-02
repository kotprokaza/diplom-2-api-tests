package com.stellarburgers.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;

public class BaseTest {
    
    @Before
    public void setUp() {
        // Базовая конфигурация URI
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        
        // ВАЖНО: AllureRestAssured должен быть ПЕРВЫМ фильтром
        RestAssured.filters(
            new AllureRestAssured(),  // Собирает данные для Allure отчетов
            new RequestLoggingFilter(),
            new ResponseLoggingFilter()
        );
        
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
