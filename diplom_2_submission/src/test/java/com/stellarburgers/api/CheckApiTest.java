package com.stellarburgers.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CheckApiTest {
    
    @Before
    public void setUp() {
        // Тестируем разные URL
        String[] urls = {
            "https://stellarburgers.education-services.ru",
            "https://stellarburgers.nomoreparties.site",
            "http://localhost:3000"  // локальный, если запущен
        };
        
        for (String url : urls) {
            System.out.println("Проверяю URL: " + url);
            try {
                RestAssured.baseURI = url;
                Response response = given()
                    .when()
                    .get("/api/ingredients")
                    .then()
                    .extract().response();
                    
                if (response.getStatusCode() == 200) {
                    System.out.println("✓ URL работает: " + url);
                    System.out.println("Ответ: " + response.getBody().asString().substring(0, Math.min(100, response.getBody().asString().length())));
                    RestAssured.baseURI = url;
                    return;
                }
            } catch (Exception e) {
                System.out.println("✗ Ошибка для URL " + url + ": " + e.getMessage());
            }
        }
        
        System.out.println("Ни один URL не сработал!");
    }
    
    @Test
    public void testApiAvailable() {
        Response response = given()
            .when()
            .get("/api/ingredients");
        
        System.out.println("Статус код: " + response.getStatusCode());
        System.out.println("Тип контента: " + response.getContentType());
        System.out.println("Тело ответа (первые 500 символов):");
        System.out.println(response.getBody().asString().substring(0, Math.min(500, response.getBody().asString().length())));
        
        response.then()
            .statusCode(200)
            .contentType(containsString("json"))
            .body("success", equalTo(true));
    }
}
