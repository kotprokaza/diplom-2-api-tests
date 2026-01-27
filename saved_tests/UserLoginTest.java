package com.stellarburgers.api;

import com.stellarburgers.models.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.stellarburgers.api.Endpoints.*;
import static org.hamcrest.Matchers.*;

public class UserLoginTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    private final String PASSWORD = "password123";
    private User testUser;
    
    @Before
    public void setUpTest() {
        super.setUp();
        testEmail = generateUniqueEmail();
        testUser = new User(testEmail, PASSWORD, generateName());
        
        // Создаем пользователя для тестов логина
        Response response = ApiClient.registerUser(testUser);
        accessToken = response.path("accessToken");
    }
    
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            ApiClient.deleteUser(accessToken);
        }
    }
    
    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Тест на успешный вход с правильными данными")
    public void testLoginWithExistingUser() {
        Response response = ApiClient.loginUser(testUser);
        
        response.then()
            .statusCode(SC_OK)
            .body("success", equalTo(true))
            .body("user.email", equalTo(testEmail.toLowerCase()))
            .body("user.name", equalTo(testUser.getName()));
    }
    
    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Тест на вход с неверным паролем")
    public void testLoginWithWrongPassword() {
        User wrongPasswordUser = new User(testEmail, "wrongpassword", testUser.getName());
        
        Response response = ApiClient.loginUser(wrongPasswordUser);
        
        response.then()
            .statusCode(SC_UNAUTHORIZED)
            .body("success", equalTo(false))
            .body("message", equalTo("email or password are incorrect"));
    }
    
    @Test
    @DisplayName("Логин с неверным email")
    @Description("Тест на вход с несуществующим email")
    public void testLoginWithWrongEmail() {
        User wrongEmailUser = new User("nonexistent@example.com", PASSWORD, "Test User");
        
        Response response = ApiClient.loginUser(wrongEmailUser);
        
        response.then()
            .statusCode(SC_UNAUTHORIZED)
            .body("success", equalTo(false))
            .body("message", equalTo("email or password are incorrect"));
    }
}
