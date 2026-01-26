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

public class UserRegistrationTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    
    @Before
    public void setUpTest() {
        super.setUp(); // Вызываем родительский метод
        testEmail = "test_" + System.currentTimeMillis() + "@yandex.ru";
    }
    
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            deleteUser(accessToken);
        }
    }
    
    @Step("Удаление пользователя")
    private void deleteUser(String token) {
        given()
            .header("Authorization", token)
        .when()
            .delete("/api/auth/user")
        .then()
            .statusCode(202);
    }
    
    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Тест на успешную регистрацию нового пользователя")
    public void testCreateUniqueUser() {
        String jsonBody = String.format(
            "{\"email\": \"%s\", \"password\": \"password123\", \"name\": \"Test User\"}", 
            testEmail
        );

        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
        .when()
            .post("/api/auth/register");

        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("user.email", equalTo(testEmail.toLowerCase()))
            .body("user.name", equalTo("Test User"));
            
        // Сохраняем токен для удаления пользователя
        accessToken = response.path("accessToken");
    }
    
    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Тест на попытку регистрации уже существующего пользователя")
    public void testCreateExistingUser() {
        // Сначала создаем пользователя
        String jsonBody = String.format(
            "{\"email\": \"%s\", \"password\": \"password123\", \"name\": \"Test User\"}", 
            testEmail
        );
        
        given()
            .header("Content-type", "application/json")
            .body(jsonBody)
        .when()
            .post("/api/auth/register");

        // Пытаемся создать того же пользователя снова
        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
        .when()
            .post("/api/auth/register");

        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", equalTo("User already exists"));
    }
    
    @Test
    @DisplayName("Создание пользователя без обязательного поля")
    @Description("Тест на регистрацию без заполнения обязательного поля")
    public void testCreateUserWithoutRequiredField() {
        // Отправляем без поля "password"
        String jsonBody = String.format(
            "{\"email\": \"%s\", \"name\": \"Test User\"}", 
            testEmail
        );

        Response response = given()
            .header("Content-type", "application/json")
            .body(jsonBody)
        .when()
            .post("/api/auth/register");

        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", containsString("required"));
    }
}
