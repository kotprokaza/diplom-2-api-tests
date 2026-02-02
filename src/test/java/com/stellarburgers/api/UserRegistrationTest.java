package com.stellarburgers.api;

import com.stellarburgers.models.UserRequest;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserRegistrationTest extends BaseTest {
    
    private String accessToken;
    private String testEmail;
    
    @Before
    public void setUpTest() {
        super.setUp();
        testEmail = "test_" + System.currentTimeMillis() + "@yandex.ru";
    }
    
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            ApiClient.deleteUser(accessToken);
        }
    }
    
    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Тест на успешную регистрацию нового пользователя")
    public void testCreateUniqueUser() {
        UserRequest user = new UserRequest(testEmail, "password123", "Test User");
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("user.email", equalTo(testEmail.toLowerCase()))
            .body("user.name", equalTo("Test User"));
            
        accessToken = response.path("accessToken");
    }
    
    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Тест на попытку регистрации уже существующего пользователя")
    public void testCreateExistingUser() {
        UserRequest user = new UserRequest(testEmail, "password123", "Test User");
        ApiClient.registerUser(user);
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", equalTo("User already exists"));
    }
    
    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Тест на регистрацию без заполнения поля email")
    public void testCreateUserWithoutEmail() {
        UserRequest user = new UserRequest(null, "password123", "Test User");
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", containsString("required"));
    }
    
    @Test
    @DisplayName("Создание пользователя без password")
    @Description("Тест на регистрацию без заполнения поля password")
    public void testCreateUserWithoutPassword() {
        UserRequest user = new UserRequest(testEmail, null, "Test User");
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", containsString("required"));
    }
    
    @Test
    @DisplayName("Создание пользователя без name")
    @Description("Тест на регистрацию без заполнения поля name")
    public void testCreateUserWithoutName() {
        UserRequest user = new UserRequest(testEmail, "password123", null);
        
        Response response = ApiClient.registerUser(user);
        
        response.then()
            .statusCode(403)
            .body("success", equalTo(false))
            .body("message", containsString("required"));
    }
}
