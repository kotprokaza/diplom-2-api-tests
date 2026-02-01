package com.stellarburgers.api;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.Before;

public class BaseTest {
    
    @Before
    public void setUp() {
        // Устанавливаем дефолтный парсер для JSON
        RestAssured.defaultParser = Parser.JSON;
        // Используем правильный URL из задания
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
    }
}
