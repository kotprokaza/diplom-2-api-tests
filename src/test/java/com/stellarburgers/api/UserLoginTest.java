package com.stellarburgers.api;

import com.stellarburgers.models.LoginRequest;
import com.stellarburgers.models.UserRequest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        
        UserRequest user = new UserRequest(testEmail, password, "Login Test User");
        Response response = ApiClient.registerUser(user);
        accessToken = response.path("accessToken");
    }
    
    @After
    @Step("Удаление тестового пользователя")
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            ApiClient.deleteUser(accessToken);
        }
    }
    
    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Тест на успешный вход с правильными данными")
    public void testLoginWithExistingUser() {
        LoginRequest loginData = new LoginRequest(testEmail, password);
        
        Response response = ApiClient.loginUser(loginData);
        
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
        LoginRequest loginData = new LoginRequest(testEmail, "wrongpassword");
        
        Response response = ApiClient.loginUser(loginData);
        
        response.then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("message", equalTo("email or password are incorrect"));
    }
    
    @Test
    @DisplayName("Логин с неверным email")
    @Description("Тест на вход с несуществующим email")
    public void testLoginWithWrongEmail() {
        LoginRequest loginData = new LoginRequest("nonexistent@email.com", password);
        
        Response response = ApiClient.loginUser(loginData);
        
        response.then()
            .statusCode(401)
            .body("success", equalTo(false))
            .body("message", equalTo("email or password are incorrect"));
    }
}
