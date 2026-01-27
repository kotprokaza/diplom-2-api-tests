package com.stellarburgers.api;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;

import static com.stellarburgers.api.Endpoints.*;

public class BaseTest {
    protected Faker faker;
    
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        faker = new Faker();
    }
    
    // Генерация уникального email
    protected String generateUniqueEmail() {
        return "test_" + System.currentTimeMillis() + "_" + faker.random().hex(8) + "@yandex.ru";
    }
    
    // Генерация имени
    protected String generateName() {
        return faker.name().fullName();
    }
}
