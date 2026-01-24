package com.stellarburgers.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserLoginTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    private String password = "password123";
    
    @Before
    @Step("Создание тестового пользователя")
    public void setUpTest() {
        super.setUp();
        testEmail = "login_test_" + System.currentTimeMillis() + "@yandex.ru";
        
        // Создаем пользователя для тестов логина
        String jsonBody = String.format(
            "{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"Login Test User\"}", 
            testEmail, password
        );
        
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
        .when()
            .post("/api/auth/register");

        accessToken = response.path("accessToken");
    }
    
    @After
    @Step("Удаление тестового пользователя")
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            given()
                .header("Authorization", accessToken)
            .when()
                .delete("/api/auth/user")
            .then()
                .statusCode(202);
        }
    }
    
    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Тест на успешный вход с правильными данными")
    public void testLoginWithExistingUser() {
        String jsonBody = String.format(
            "{\"email\": \"%s\", \"password\": \"%s\"}", 
            testEmail, password
        );

        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
        .when()
            .post("/api/auth/login");

        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("user.email", equalTo(testEmail.toLowerCase()))
            .body("user.name", equalTo("Login Test User"));
    }
    
    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Тест на вход с неверным паролем")
    public void testLoginWithWrongPassword() {
        String jsonBody = String.format(
            "{\"email\": \"%s\", \"password\": \"wrongpassword\"}", 
            testEmail
        );

        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
        .when()
            .post("/api/auth/login");

        response.then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("message", equalTo("email or password are incorrect"));
    }
}
