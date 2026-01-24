package com.stellarburgers.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SimpleTest extends BaseTest {
    
    @Test
    @DisplayName("Проверка доступности API ингредиентов")
    @Description("Тест проверяет, что эндпоинт ингредиентов доступен")
    public void testIngredientsAvailable() {
        Response response = given()
                .when()
                .get("/api/ingredients");
        
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()));
    }
    
    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Тест на успешную регистрацию нового пользователя")
    public void testCreateUniqueUser() {
        String email = "test_" + System.currentTimeMillis() + "@yandex.ru";
        String jsonBody = String.format(
                "{\"email\": \"%s\", \"password\": \"password123\", \"name\": \"Test User\"}", 
                email
        );

        Response response = given()
                .header("Content-type", "application/json")
                .body(jsonBody)
                .when()
                .post("/api/auth/register");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo("Test User"));
    }
}
